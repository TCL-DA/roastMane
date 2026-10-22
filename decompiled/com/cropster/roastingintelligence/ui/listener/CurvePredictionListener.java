package com.cropster.roastingintelligence.ui.listener;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.cropster.roastingintelligence.RoastingManager;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CurvePredictionListener implements CurveModelListener {
   private static final long PREDICTION_INTERVAL = 1000L;
   public static final int THRESHOLD = 60;
   private final Logger logger = LoggerFactory.getLogger(CurvePredictionListener.class);
   private final RoastingManager roastingManager;
   private final RoastingModel roastingModel;
   private final CurveModel targetModel;
   private final CurveModel sourceModel;
   private volatile long lastUpdate = -1L;
   private volatile long requestInterval = 1000L;
   private volatile ScheduledFuture<Void> predictionFuture;
   private final SimpleBooleanProperty enabledProperty = new SimpleBooleanProperty(true);

   public CurvePredictionListener(RoastingManager roastingManager, RoastingModel roastingModel, CurveModel targetModel, CurveModel sourceModel) {
      this.roastingManager = roastingManager;
      this.roastingModel = roastingModel;
      this.targetModel = targetModel;
      this.sourceModel = sourceModel;
   }

   public long getRequestInterval() {
      return this.requestInterval;
   }

   public void setRequestInterval(long requestInterval) {
      this.requestInterval = requestInterval;
   }

   private synchronized void scheduleUpdate(CurveModel curveModel) {
      if (this.enabledProperty.get() && curveModel.getValues().size() >= 60) {
         long now = NanoTime.currentTimeMillis();
         if (now - (this.requestInterval - 500L) >= this.lastUpdate) {
            this.lastUpdate = now;
            Long referenceTime = this.sourceModel.getValues().lastKey() + this.requestInterval;
            if (this.predictionFuture != null && !this.predictionFuture.isDone()) {
               Iterator<Long> it = this.targetModel.getValues().keySet().iterator();

               while (it.hasNext() && it.next() <= referenceTime) {
                  it.remove();
               }

               this.targetModel.fireCurveChanged();
               return;
            }

            CurveModel beanModel = this.sourceModel;
            SortedMap<Long, Double> beanCurve = new TreeMap<>(beanModel.getValues());
            SortedMap<Long, Double> gasCurve = new TreeMap<>();
            if (this.roastingModel.getCurves().containsKey("gas")) {
               gasCurve.putAll(this.roastingModel.getCurves().get("gas").getValues());
            }

            this.predictionFuture = this.roastingManager
               .getExecutor()
               .schedule(
                  () -> {
                     try {
                        long requestStart = NanoTime.currentTimeMillis();
                        double[] predictions = this.roastingManager
                           .predictCurve(
                              this.roastingModel.getMachine(), this.roastingModel.getRoast().getStartWeight(), beanModel.getUnit(), beanCurve, gasCurve
                           );
                        if (predictions == null) {
                           return null;
                        }

                        if (Thread.interrupted()) {
                           return null;
                        }

                        Platform.runLater(() -> {
                           if (!this.logger.isDebugEnabled()) {
                              this.targetModel.getValues().clear();
                           }

                           long delay = NanoTime.currentTimeMillis() - requestStart;
                           int offset = (int)(delay / 1000L);
                           if (this.enabledProperty.get()) {
                              for (int i = offset; i < predictions.length; i++) {
                                 this.targetModel.getValues().put(Long.valueOf(referenceTime + (long)i * 1000L), Double.valueOf(predictions[i]));
                              }

                              this.targetModel.fireCurveChanged();
                           }
                        });
                     } catch (Throwable var8x) {
                        this.logger.error("Error getting curve prediction", var8x);
                     }

                     return null;
                  },
                  0L,
                  TimeUnit.SECONDS
               );
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

   @Override
   public void notifyCurveChanged(CurveModel source, SortedSet<Long> times) {
      this.scheduleUpdate(source);
   }

   public void clear() {
      while (this.predictionFuture != null && !this.predictionFuture.isDone()) {
         try {
            Thread.sleep(20L);
         } catch (InterruptedException var2) {
            this.logger.error("Error waiting for curve prediction update to finish");
         }
      }

      this.targetModel.getValues().clear();
      this.targetModel.fireCurveChanged();
   }

   public BooleanProperty getEnabledProperty() {
      return this.enabledProperty;
   }
}
