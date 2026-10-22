package com.cropster.roastingintelligence.ui.listener;

import com.cropster.roastingintelligence.ui.components.FlickPredictionPanel;
import com.cropster.roastingintelligence.ui.util.Icon;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.cropster.roastingintelligence.RoastingManager;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlickPredictionListener implements CurveModelListener {
   private final Logger logger = LoggerFactory.getLogger(FlickPredictionListener.class);
   private static final long PREDICTION_INTERVAL = 30000L;
   private static final long SHOW_AHEAD_TIME = 120000L;
   private static final long START_TIME = 302000L;
   private final RoastingManager roastingManager;
   private final RoastingModel roastingModel;
   private volatile long lastUpdate = 0L;
   private final DoubleProperty flickPrediction = new SimpleDoubleProperty(0.0);
   private final SimpleBooleanProperty flickTendency = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty flickTendencyReverted = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty firstCrackUpcoming = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty dismissed = new SimpleBooleanProperty(true);
   private final FirstCrackPredictionListener firstCrackPredictionListener;
   private final SimpleBooleanProperty smartPredictionsEnabledProperty = new SimpleBooleanProperty(true);

   public FlickPredictionListener(
      RoastingManager roastingManager,
      RoastingModel roastingModel,
      FirstCrackPredictionListener firstCrackPredictionListener,
      FlickPredictionPanel flickPredictionPanel
   ) throws IllegalArgumentException {
      this.roastingManager = roastingManager;
      this.roastingModel = roastingModel;
      this.firstCrackPredictionListener = firstCrackPredictionListener;
      if (!roastingModel.getCurves().containsKey("gas")) {
         throw new IllegalArgumentException("Flick prediction disabled: no gas curve");
      } else if (roastingModel.getMachine() == null || roastingModel.getMachine().getId() == null) {
         throw new IllegalArgumentException("Flick prediction disabled: no machine id");
      } else if (roastingModel.getMachine().getCapacity() == null) {
         throw new IllegalArgumentException("Flick prediction disabled: no machine capacity");
      } else if (roastingModel.getRoast().getStartWeight() == null) {
         throw new IllegalArgumentException("Flick prediction disabled: no start weight");
      } else {
         flickPredictionPanel.visibleProperty()
            .bind(this.smartPredictionsEnabledProperty.and(this.firstCrackUpcoming).and(this.flickTendencyReverted.or(this.dismissed.not())));
         this.flickTendency.bind(this.flickPrediction.greaterThanOrEqualTo(0.5));
         this.flickTendency.addListener((v, o, n) -> {
            if ((o || !n) && (!o || n)) {
               this.flickTendencyReverted.set(false);
            } else {
               this.flickTendencyReverted.set(true);
               this.dismissed.set(false);
            }
         });
         flickPredictionPanel.getFlickTendencyLabel()
            .textProperty()
            .bind(
               Bindings.createStringBinding(
                  () -> this.flickTendency.get()
                        ? org.cropster.roastingintelligence.util.Messages.m_("Flick possibility detected")
                        : org.cropster.roastingintelligence.util.Messages.m_("Flick possibility avoided"),
                  new Observable[]{this.flickPrediction}
               )
            );
         flickPredictionPanel.getFlickParagraph().managedProperty().bind(this.flickTendency);
         flickPredictionPanel.getFlickParagraph().visibleProperty().bind(this.flickTendency);
         flickPredictionPanel.getFlickTendencyIcon()
            .graphicProperty()
            .bind(
               Bindings.createObjectBinding(
                  () -> this.flickTendency.get() ? Icon.WARNING.load("icon--warning") : Icon.SUCCESS.load("icon--success"),
                  new Observable[]{this.flickPrediction}
               )
            );
         flickPredictionPanel.getDismissButton().setOnAction(e -> {
            this.dismissed.set(true);
            this.flickTendencyReverted.set(false);
         });
      }
   }

   private synchronized void scheduleUpdate(CurveModel curveModel) {
      if (this.smartPredictionsEnabledProperty.get()) {
         if (!curveModel.getValues().isEmpty()) {
            long now = curveModel.getValues().lastKey();
            if (now >= 302000L) {
               if (!this.firstCrackUpcoming.get()) {
                  Long firstCrackPrediction = this.firstCrackPredictionListener.getLastPrediction();
                  if (firstCrackPrediction == null || firstCrackPrediction - now >= 120000L) {
                     return;
                  }

                  this.firstCrackUpcoming.set(true);
               }

               if (now - 30000L >= this.lastUpdate) {
                  this.lastUpdate = now;
                  SortedMap<Long, Double> beanCurve = new TreeMap<>(curveModel.getValues().tailMap(0L));
                  SortedMap<Long, Double> gasCurve = new TreeMap<>(this.roastingModel.getCurves().get("gas").getValues().tailMap(0L));
                  this.roastingManager
                     .getExecutor()
                     .schedule(
                        () -> {
                           try {
                              Double prediction = this.roastingManager
                                 .predictFlick(
                                    this.roastingModel.getMachine(), this.roastingModel.getRoast().getStartWeight(), curveModel.getUnit(), beanCurve, gasCurve
                                 );
                              if (prediction == null) {
                                 return null;
                              }

                              if (Thread.interrupted()) {
                                 return null;
                              }

                              this.logger.debug("Flick prediction: {}", prediction);
                              Platform.runLater(() -> this.flickPrediction.set(prediction));
                           } catch (Throwable var5x) {
                              this.logger.error("Error getting flick prediction", var5x);
                           }

                           return null;
                        },
                        0L,
                        TimeUnit.SECONDS
                     );
               }
            }
         }
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      this.scheduleUpdate(source);
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this.scheduleUpdate(source);
   }

   public BooleanProperty getSmartPredictionsEnabledProperty() {
      return this.smartPredictionsEnabledProperty;
   }
}
