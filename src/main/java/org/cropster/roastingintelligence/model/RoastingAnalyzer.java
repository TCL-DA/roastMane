package org.cropster.roastingintelligence.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.SortedMap;
import java.util.TreeSet;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.RoastingModel;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoastingAnalyzer implements CurveModelListener, RoastingListener {
   private static final Logger LOGGER = LoggerFactory.getLogger(RoastingAnalyzer.class);
   private double _startThreshold = Defaults.TEMPERATURE_DROP_START.doubleValue();
   private double _turningPointThreshold = Defaults.TEMPERATURE_RISE_TURNINGPOINT.doubleValue();
   private double _endThreshold = Defaults.TEMPERATURE_DROP_END.doubleValue();
   private boolean _turningPointOnly;
   private Date _startDate;
   private Date _turningPoint;
   private Date _endDate;
   private final RoastingModel _roastingModel;
   private final RoastingAnalyzerListener[] _listeners;

   public RoastingAnalyzer(RoastingModel roastingModel, RoastingAnalyzerListener... listeners) {
      if (listeners == null) {
         throw new NullPointerException("listeners is null");
      } else {
         this._roastingModel = roastingModel;
         this._listeners = listeners;
      }
   }

   public void setTurningPointOnly(boolean turningPointOnly) {
      this._turningPointOnly = turningPointOnly;
   }

   public void setStartThreshold(double startThreshold) {
      this._startThreshold = startThreshold;
   }

   public void setTurningPointThreshold(double turningPointThreshold) {
      this._turningPointThreshold = turningPointThreshold;
   }

   public void setEndThreshold(double endThreshold) {
      this._endThreshold = endThreshold;
   }

   public Double calculateTemperatureChange(SortedMap<Long, Double> curve, long searchInterval) {
      if (curve.size() < 2) {
         return null;
      } else {
         Iterator<Long> it = new TreeSet<>(curve.keySet()).descendingIterator();
         long searchStart = curve.lastKey();
         Boolean curveRising = null;
         Double diff = null;
         double startValue = curve.get(Long.valueOf(searchStart));
         Double previousValue = null;

         long current;
         do {
            current = it.next();
            Double currentValue = curve.get(Long.valueOf(current));
            if (curveRising == null) {
               if (currentValue + 0.01 < startValue) {
                  curveRising = Boolean.TRUE;
               } else if (currentValue - 0.01 > startValue) {
                  curveRising = Boolean.FALSE;
               }
            }

            if (Boolean.TRUE.equals(curveRising) && currentValue - 0.01 > previousValue
               || Boolean.FALSE.equals(curveRising) && currentValue + 0.01 < previousValue) {
               break;
            }

            diff = startValue - currentValue;
            previousValue = currentValue;
         } while (it.hasNext() && searchStart - current < searchInterval);

         return diff;
      }
   }

   public Long searchTrendStartKey(SortedMap<Long, Double> curve, long interval, double minChange, boolean includeEqualValues) {
      Long trendStart = null;
      Double diff = this.calculateTemperatureChange(curve, interval);
      if (LOGGER.isTraceEnabled()) {
         LOGGER.trace("{} / {} ms ({} required)", new Object[]{diff, interval, minChange});
      }

      if (diff != null && (minChange > 0.0 && diff > minChange || minChange < 0.0 && diff < minChange)) {
         ArrayList<Long> list = new ArrayList<>(curve.keySet());
         ListIterator<Long> it = list.listIterator(list.size());
         Long searchStart = it.previous();
         double extremum = minChange < 0.0 ? 0.0 : (double)searchStart.longValue();

         while (it.hasPrevious()) {
            Long current = it.previous();
            if (Math.abs(searchStart - current) > 60000L) {
               break;
            }

            double currentTemp = curve.get(current);
            if (!includeEqualValues || (!(minChange >= 0.0) || !(currentTemp <= extremum)) && (!(minChange < 0.0) || !(currentTemp >= extremum))) {
               if (includeEqualValues || (!(minChange >= 0.0) || !(currentTemp < extremum)) && (!(minChange < 0.0) || !(currentTemp > extremum))) {
                  if (includeEqualValues || currentTemp != extremum) {
                     break;
                  }
               } else {
                  trendStart = current;
                  extremum = currentTemp;
               }
            } else {
               trendStart = current;
               extremum = currentTemp;
            }
         }
      }

      return trendStart;
   }

   private void analyze(CurveModel curveModel) {
      if (this._roastingModel.getEndDate() == null) {
         if (this._startDate == null) {
            SortedMap<Long, Double> values = curveModel.getValues();
            Long startKey = this.searchTrendStartKey(values, 30000L, this._startThreshold, true);
            if (startKey == null && !values.isEmpty() && Math.abs(values.lastKey() - values.firstKey()) >= 60000L) {
               this._startDate = new Date(curveModel.getZeroPoint().getTime() + values.firstKey());
               this.fireStartDetected(this._startDate);
            } else if (startKey != null) {
               this._startDate = new Date(curveModel.getZeroPoint().getTime() + startKey);
               this.fireStartDetected(this._startDate);
            }
         } else if (this._turningPoint == null) {
            if (this._turningPointOnly && this._roastingModel.getDuration() < 20000L) {
               return;
            }

            Long tpKey = this.searchTrendStartKey(curveModel.getValues(), 30000L, this._turningPointThreshold, true);
            if (tpKey != null) {
               Date turningPoint = new Date(curveModel.getZeroPoint().getTime() + tpKey);
               if (turningPoint.after(this._startDate)) {
                  this._turningPoint = turningPoint;
                  this.fireTurningPointDetected(this._turningPoint);
               }
            }
         } else if (!this._turningPointOnly && this._endDate == null) {
            Long endKey = this.searchTrendStartKey(curveModel.getValues(), 5000L, this._endThreshold, false);
            if (endKey != null) {
               this._endDate = new Date(curveModel.getZeroPoint().getTime() + endKey);
               this.fireEndDetected(this._endDate);
            }
         }
      }
   }

   private void fireStartDetected(Date startDate) {
      for (RoastingAnalyzerListener listener : this._listeners) {
         listener.startDetected(this._roastingModel, startDate);
      }
   }

   private void fireTurningPointDetected(Date turningPoint) {
      for (RoastingAnalyzerListener listener : this._listeners) {
         listener.truningPointDetected(this._roastingModel, turningPoint);
      }
   }

   private void fireEndDetected(Date endDate) {
      for (RoastingAnalyzerListener listener : this._listeners) {
         listener.endDetected(this._roastingModel, endDate);
      }
   }

   @Override
   public void roastingStarted(RoastingModel roastingModel) {
   }

   @Override
   public void roastingForced(RoastingModel roastingModel) {
      if (this._startDate == null && roastingModel.getStartDate() != null) {
         LOGGER.debug("ROAST START SET");
         this._startDate = roastingModel.getStartDate();
      }
   }

   @Override
   public void roastingAborted(RoastingModel roastingModel) {
   }

   @Override
   public void roastingStopped(RoastingModel roastingModel) {
   }

   @Override
   public void roastingFinalized(RoastingModel roastingModel) {
   }

   @Override
   public void notifyCurveChanged(CurveModel source) {
      this.analyze(source);
   }

   @Override
   public void notifyCurveChanged(CurveModel source, long time) {
      this.analyze(source);
   }
}
