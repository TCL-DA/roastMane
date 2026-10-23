package com.cropster.roastingintelligence.task;

import com.cropster.roastingintelligence.task.control.WriteCommand;
import com.cropster.util.model.Unit;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoastReplayer implements Runnable {
   static final String TEMPERATURE_MEASURE = "beanTemperature";
   private final Logger logger = LoggerFactory.getLogger(RoastReplayer.class);
   private long lastChangeTime = 0L;
   private final String controlMeasurement;
   private final String replayMeasurement;
   private final MeasurementReaderWriter source;
   private final RoastingModel roastingModel;
   private final SortedMap<Long, Double> changes = new TreeMap<>();
   private final Queue<Triple<Double, Long, Double>> temperatureTriggers = new LinkedList<>();
   private final Queue<Triple<Double, Long, Double>> timeTriggers = new LinkedList<>();
   private final Consumer<WriteCommand> commandConsumer;
   private volatile boolean paused;
   private volatile boolean turningPointDetected = false;
   private volatile boolean wasPaused = false;
   private Unit sourceUnit;

   public RoastReplayer(
      String replayMeasurement,
      String controlMeasurement,
      Unit sourceUnit,
      MeasurementReaderWriter source,
      RoastingModel roastingModel,
      Roast replayRoast,
      Consumer<WriteCommand> commandConsumer
   ) {
      if (this.logger.isDebugEnabled()) {
         Long turningPoint = replayRoast.getEventTimes().get("turningPoint");
         this.logger
            .debug(
               "Initializing Roast Replayer...\nTEMPERATURE_MEASURE: {}\n  replayMeasurement: {}\n controlMeasurement: {}\n replayTurningPoint: {}",
               new Object[]{"beanTemperature", replayMeasurement, controlMeasurement, String.format("%1$tM:%1$tS", turningPoint)}
            );
      }

      this.replayMeasurement = replayMeasurement;
      this.controlMeasurement = controlMeasurement;
      this.source = source;
      this.roastingModel = roastingModel;
      this.commandConsumer = commandConsumer;
      this.sourceUnit = sourceUnit;
      Map<String, Curve> curves = replayRoast.getCurves();
      Curve temperatureCurve = curves.get("beanTemperature");
      Curve replayCurve = curves.get(replayMeasurement);
      Double lastValue = null;

      for (Entry<Long, Double> entry : replayCurve.entrySet()) {
         if (!entry.getValue().equals(lastValue)) {
            this.changes.put(entry.getKey(), entry.getValue());
         }

         lastValue = entry.getValue();
      }

      lastValue = null;

      for (Entry<Long, Double> entry : this.changes.entrySet()) {
         Long beanTime = CollectionUtil.getProximateKey(temperatureCurve, entry.getKey(), 500L);
         Double currentValue = entry.getValue();
         if (lastValue == null) {
            lastValue = currentValue;
         }

         if (beanTime != null) {
            Double beanTemperature = temperatureCurve.get(beanTime);
            ImmutableTriple<Double, Long, Double> trigger = ImmutableTriple.of(beanTemperature, beanTime, currentValue);
            if (currentValue < lastValue) {
               if (this.logger.isDebugEnabled()) {
                  this.logger
                     .debug(
                        "Adding temperature trigger {}{} @ {}{} ({})",
                        new Object[]{
                           currentValue,
                           org.cropster.roastingintelligence.util.Messages.m_(replayCurve.getUnit()),
                           beanTemperature,
                           org.cropster.roastingintelligence.util.Messages.m_(temperatureCurve.getUnit()),
                           String.format("%1$tM:%1$tS", beanTime)
                        }
                     );
               }

               this.temperatureTriggers.add(trigger);
            } else if (currentValue > lastValue) {
               if (this.logger.isDebugEnabled()) {
                  this.logger
                     .debug(
                        "Adding time trigger {}{} @ {}{} ({})",
                        new Object[]{
                           currentValue,
                           org.cropster.roastingintelligence.util.Messages.m_(replayCurve.getUnit()),
                           beanTemperature,
                           org.cropster.roastingintelligence.util.Messages.m_(temperatureCurve.getUnit()),
                           String.format("%1$tM:%1$tS", beanTime)
                        }
                     );
               }

               this.timeTriggers.add(trigger);
            } else if (this.logger.isDebugEnabled()) {
               this.logger
                  .debug(
                     "Skipped trigger {}{} @ {}{} ({})",
                     new Object[]{
                        currentValue,
                        org.cropster.roastingintelligence.util.Messages.m_(replayCurve.getUnit()),
                        beanTemperature,
                        org.cropster.roastingintelligence.util.Messages.m_(temperatureCurve.getUnit()),
                        String.format("%1$tM:%1$tS", beanTime)
                     }
                  );
            }
         }

         lastValue = currentValue;
      }
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void setPaused(boolean paused) {
      this.paused = paused;
      if (paused) {
         this.wasPaused = true;
      }

      this.logger.info("Replaying was {} at {}", paused ? "paused" : "restarted", String.format("%1$tM:%1$tS", this.roastingModel.getDuration()));
   }

   @Override
   public void run() {
      if (!this.paused) {
         if (this.source.getFactory().supportsAutoMode(this.controlMeasurement)) {
            Boolean autoMode = this.source.getAutoMode(this.controlMeasurement);
            if (autoMode == null
               || (
                  autoMode
                     ? !this.controlMeasurement.equals(this.source.getFactory().getAutoModeMeasurement(this.controlMeasurement))
                     : !this.controlMeasurement.equals(this.source.getFactory().getManualModeMeasurement(this.controlMeasurement))
               )) {
               return;
            }
         }

         try {
            Map<String, ? extends CurveModel> curves = this.roastingModel.getCurves();
            CurveModel controlCurveModel = curves.get(this.replayMeasurement);
            String logMessage = "";
            long currentTime = this.roastingModel.getDuration();
            long nextTime = currentTime + 1000L;
            Double value = null;
            if (this.roastingModel.getTurningPoint() == null) {
               Long key = CollectionUtil.getProximateKey(this.changes, nextTime, 500L);
               if (key != null) {
                  this.lastChangeTime = nextTime;
                  value = this.changes.get(key);
                  logMessage = String.format(
                     "Replaying %4$s change to %1$s%3$s at %2$tM:%2$tS",
                     value,
                     currentTime,
                     org.cropster.roastingintelligence.util.Messages.m_(controlCurveModel.getUnit()),
                     this.controlMeasurement
                  );
               }

               if (this.wasPaused) {
                  this.wasPaused = false;
               }
            } else {
               if (!this.turningPointDetected) {
                  if (this.logger.isDebugEnabled()) {
                     this.logger.debug("Turning point detected at {}", String.format("%1$tM:%1$tS", currentTime));
                  }

                  this.flushQueues(currentTime);
                  this.turningPointDetected = true;
               }

               CurveModel temperatureCurveModel = curves.get("beanTemperature");
               Double currentTemperature = temperatureCurveModel.getValue(currentTime);
               if (this.wasPaused) {
                  this.flushQueues(currentTime);
                  this.flushTemperatureQueue(currentTemperature);
                  this.wasPaused = false;
               }

               Double currentControl = controlCurveModel.getValue(currentTime);
               Double nextControl = Optional.ofNullable(CollectionUtil.nextValue(this.changes, nextTime))
                  .orElseGet(() -> this.changes.get(this.changes.lastKey()));
               if (currentTemperature != null && currentControl != null && nextControl != null) {
                  while (!this.temperatureTriggers.isEmpty() && currentTemperature >= this.temperatureTriggers.peek().getLeft()) {
                     Triple<Double, Long, Double> trigger = this.temperatureTriggers.poll();
                     if (this.logger.isDebugEnabled()) {
                        this.logger.debug("Temperature trigger was consumed [{} remaining]", this.temperatureTriggers.size());
                     }

                     this.lastChangeTime = (Long)trigger.getMiddle();
                     value = (Double)trigger.getRight();
                     logMessage = String.format(
                        "Replaying %6$s change to %1$s%4$s triggered by reaching %2$s%5$s at %3$tM:%3$tS",
                        value,
                        trigger.getLeft(),
                        currentTime,
                        org.cropster.roastingintelligence.util.Messages.m_(controlCurveModel.getUnit()),
                        org.cropster.roastingintelligence.util.Messages.m_(temperatureCurveModel.getUnit()),
                        this.controlMeasurement
                     );
                     this.flushQueue(this.timeTriggers, this.lastChangeTime);
                  }

                  if (value == null && !this.timeTriggers.isEmpty() && nextTime > this.lastChangeTime) {
                     Triple<Double, Long, Double> trigger = this.timeTriggers.peek();
                     Long nextIncreaseTime = (Long)trigger.getMiddle();
                     if (currentTime >= nextIncreaseTime) {
                        this.timeTriggers.poll();
                        if (this.logger.isDebugEnabled()) {
                           this.logger.debug("Time trigger was consumed [{} remaining]", this.timeTriggers.size());
                        }

                        this.lastChangeTime = currentTime;
                        value = (Double)trigger.getRight();
                        logMessage = String.format(
                           "Replaying %4$s change to %1$s%3$s at %2$tM:%2$tS",
                           value,
                           currentTime,
                           org.cropster.roastingintelligence.util.Messages.m_(controlCurveModel.getUnit()),
                           this.controlMeasurement
                        );
                        this.flushQueue(this.temperatureTriggers, this.lastChangeTime);
                     }
                  }
               }
            }

            if (value != null) {
               this.logger.info("[{}] {}", this.controlMeasurement, logMessage);
               value = this.sourceUnit.isCompatible(Unit.CELSIUS)
                  ? this.sourceUnit.from(BigDecimal.valueOf(value), controlCurveModel.getUnit()).doubleValue()
                  : value;
               this.commandConsumer.accept(new WriteCommand(this.source, this.controlMeasurement, value));
            }
         } catch (Exception var15) {
            this.logger.error("Error replaying curve", var15);
         }
      }
   }

   private void flushTemperatureQueue(Double currentTemperature) {
      int queueSize = this.temperatureTriggers.size();

      while (!this.temperatureTriggers.isEmpty() && this.temperatureTriggers.peek().getLeft() < currentTemperature) {
         this.temperatureTriggers.poll();
      }

      if (this.logger.isDebugEnabled() && queueSize != this.temperatureTriggers.size()) {
         this.logger.debug("Flushed temperature triggers with temperature less than {} [{} remaining]", currentTemperature, this.temperatureTriggers.size());
      }
   }

   private void flushQueues(long upToTime) {
      this.flushQueue(this.timeTriggers, upToTime);
      this.flushQueue(this.temperatureTriggers, upToTime);
   }

   private void flushQueue(Queue<Triple<Double, Long, Double>> queue, long upToTime) {
      int queueSize = queue.size();

      while (!queue.isEmpty() && queue.peek().getMiddle() < upToTime) {
         queue.poll();
      }

      if (this.logger.isDebugEnabled() && queueSize != queue.size()) {
         String triggersName = queue == this.timeTriggers ? "time" : "temperature";
         this.logger.debug("Flushed {} triggers up to {} [{} remaining]", new Object[]{triggersName, upToTime, queue.size()});
      }
   }
}
