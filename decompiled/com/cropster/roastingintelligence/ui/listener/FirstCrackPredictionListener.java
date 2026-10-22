package com.cropster.roastingintelligence.ui.listener;

import java.lang.ref.WeakReference;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import org.cropster.roastingintelligence.RoastingManager;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstCrackPredictionListener implements CurveModelListener, CommentListener {
   private final Logger logger = LoggerFactory.getLogger(FirstCrackPredictionListener.class);
   private static final long PREDICTION_INTERVAL = 10000L;
   private static final long SHOW_AHEAD_TIME = 180000L;
   private static final long START_TIME = 120000L;
   private volatile long lastUpdate = 0L;
   private volatile long[] lastPredictions;
   private final SimpleBooleanProperty firstCrackMarked = new SimpleBooleanProperty(false);
   private final SimpleBooleanProperty firstCrackUpcoming = new SimpleBooleanProperty(false);
   private final BooleanBinding enabledProperty;
   private final BooleanBinding visibleProperty;
   private final SimpleBooleanProperty smartPredictionsEnabledProperty = new SimpleBooleanProperty(true);
   private RoastingManager roastingManager;
   private RoastingModel roastingModel;
   private WeakReference<CurveChart> roastingChart;
   private CurveModel targetModel;
   private final ChangeListener<Boolean> visibilityListener = (v, o, n) -> {
      if (!n) {
         this.roastingChart.get().removeFirstCrackPredictionAnnotation();
      } else {
         this.show();
      }
   };

   public FirstCrackPredictionListener(RoastingManager roastingManager, RoastingModel roastingModel, CurveChart roastingChart, CurveModel targetModel) {
      this.roastingManager = roastingManager;
      this.roastingModel = roastingModel;
      this.roastingChart = new WeakReference<>(roastingChart);
      this.targetModel = targetModel;
      this.enabledProperty = this.firstCrackMarked.not().and(this.smartPredictionsEnabledProperty);
      this.visibleProperty = this.enabledProperty.and(this.firstCrackUpcoming);
      this.visibleProperty.addListener(this.visibilityListener);
      this.roastingModel.addCommentListener(this);
   }

   private synchronized void scheduleUpdate(CurveModel curveModel) {
      if (this.enabledProperty.get()) {
         long now = curveModel.getValues().lastKey();
         if (now > 0L) {
            if (now - 10000L >= this.lastUpdate) {
               this.lastUpdate = now / 10000L * 10000L;
               SortedMap<Long, Double> beanCurve = new TreeMap<>(curveModel.getValues().tailMap(0L));
               this.roastingManager
                  .getExecutor()
                  .schedule(
                     () -> {
                        try {
                           long[] predictions = this.roastingManager
                              .predictFirstCrack(
                                 this.roastingModel.getMachine(), this.roastingModel.getRoast().getStartWeight(), curveModel.getUnit(), beanCurve
                              );
                           if (predictions == null) {
                              return null;
                           }

                           if (Thread.interrupted()) {
                              return null;
                           }

                           long referenceTime = (long)(beanCurve.size() / 10 * 10) * 1000L;

                           for (int i = 0; i < predictions.length; i++) {
                              predictions[i] += referenceTime;
                           }

                           this.lastPredictions = predictions;
                           Platform.runLater(() -> {
                              if (predictions[1] - now < 180000L) {
                                 this.firstCrackUpcoming.set(true);
                              }

                              if (this.visibleProperty.get()) {
                                 this.roastingChart.get().setFirstCrackPredictionAnnotation(predictions[0], predictions[1], predictions[2]);
                              }
                           });
                        } catch (Throwable var9) {
                           this.logger.error("Error getting first crack prediction", var9);
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

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      if (!source.getValues().isEmpty() && source.getValues().lastKey() >= 120000L) {
         this.scheduleUpdate(source);
      }
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      if (!source.getValues().isEmpty() && source.getValues().lastKey() >= 120000L) {
         this.scheduleUpdate(source);
      }
   }

   public void show() {
      long now = this.targetModel.getValues().lastKey();
      if (now > 0L) {
         if (now - 10000L < this.lastUpdate && this.lastPredictions != null) {
            this.roastingChart.get().setFirstCrackPredictionAnnotation(this.lastPredictions[0], this.lastPredictions[1], this.lastPredictions[2]);
         } else {
            this.scheduleUpdate(this.targetModel);
         }
      }
   }

   public BooleanProperty getSmartPredictionsEnabledProperty() {
      return this.smartPredictionsEnabledProperty;
   }

   public Long getLastPrediction() {
      return this.lastPredictions == null ? null : this.lastPredictions[1];
   }

   @Override
   public void commentAdded(RoastingComment comment) {
      if ("firstCrack".equals(comment.getEvent())) {
         Platform.runLater(() -> this.firstCrackMarked.set(true));
      }
   }

   @Override
   public void commentUpdated(RoastingComment comment) {
   }

   @Override
   public void commentRemoved(RoastingComment comment) {
   }
}
