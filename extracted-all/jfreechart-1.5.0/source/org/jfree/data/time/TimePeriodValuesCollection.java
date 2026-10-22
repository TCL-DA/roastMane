package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class TimePeriodValuesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, DomainInfo, Serializable {
   private static final long serialVersionUID = -3077934065236454199L;
   private List data = new ArrayList();
   private TimePeriodAnchor xPosition = TimePeriodAnchor.MIDDLE;

   public TimePeriodValuesCollection() {
      this((TimePeriodValues)null);
   }

   public TimePeriodValuesCollection(TimePeriodValues series) {
      if (series != null) {
         this.data.add(series);
         series.addChangeListener(this);
      }
   }

   public TimePeriodAnchor getXPosition() {
      return this.xPosition;
   }

   public void setXPosition(TimePeriodAnchor position) {
      Args.nullNotPermitted(position, "position");
      this.xPosition = position;
   }

   @Override
   public int getSeriesCount() {
      return this.data.size();
   }

   public TimePeriodValues getSeries(int series) {
      if (series >= 0 && series < this.getSeriesCount()) {
         return (TimePeriodValues)this.data.get(series);
      } else {
         throw new IllegalArgumentException("Index 'series' out of range.");
      }
   }

   @Override
   public Comparable getSeriesKey(int series) {
      return this.getSeries(series).getKey();
   }

   public void addSeries(TimePeriodValues series) {
      Args.nullNotPermitted(series, "series");
      this.data.add(series);
      series.addChangeListener(this);
      this.fireDatasetChanged();
   }

   public void removeSeries(TimePeriodValues series) {
      Args.nullNotPermitted(series, "series");
      this.data.remove(series);
      series.removeChangeListener(this);
      this.fireDatasetChanged();
   }

   public void removeSeries(int index) {
      TimePeriodValues series = this.getSeries(index);
      if (series != null) {
         this.removeSeries(series);
      }
   }

   @Override
   public int getItemCount(int series) {
      return this.getSeries(series).getItemCount();
   }

   @Override
   public Number getX(int series, int item) {
      TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
      TimePeriodValue dp = ts.getDataItem(item);
      TimePeriod period = dp.getPeriod();
      return new Long(this.getX(period));
   }

   private long getX(TimePeriod period) {
      if (this.xPosition == TimePeriodAnchor.START) {
         return period.getStart().getTime();
      } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
         return period.getStart().getTime() / 2L + period.getEnd().getTime() / 2L;
      } else if (this.xPosition == TimePeriodAnchor.END) {
         return period.getEnd().getTime();
      } else {
         throw new IllegalStateException("TimePeriodAnchor unknown.");
      }
   }

   @Override
   public Number getStartX(int series, int item) {
      TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
      TimePeriodValue dp = ts.getDataItem(item);
      return new Long(dp.getPeriod().getStart().getTime());
   }

   @Override
   public Number getEndX(int series, int item) {
      TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
      TimePeriodValue dp = ts.getDataItem(item);
      return new Long(dp.getPeriod().getEnd().getTime());
   }

   @Override
   public Number getY(int series, int item) {
      TimePeriodValues ts = (TimePeriodValues)this.data.get(series);
      TimePeriodValue dp = ts.getDataItem(item);
      return dp.getValue();
   }

   @Override
   public Number getStartY(int series, int item) {
      return this.getY(series, item);
   }

   @Override
   public Number getEndY(int series, int item) {
      return this.getY(series, item);
   }

   @Override
   public double getDomainLowerBound(boolean includeInterval) {
      double result = Double.NaN;
      Range r = this.getDomainBounds(includeInterval);
      if (r != null) {
         result = r.getLowerBound();
      }

      return result;
   }

   @Override
   public double getDomainUpperBound(boolean includeInterval) {
      double result = Double.NaN;
      Range r = this.getDomainBounds(includeInterval);
      if (r != null) {
         result = r.getUpperBound();
      }

      return result;
   }

   @Override
   public Range getDomainBounds(boolean includeInterval) {
      boolean interval = includeInterval;
      Range result = null;
      Range temp = null;

      for (TimePeriodValues series : this.data) {
         int count = series.getItemCount();
         if (count > 0) {
            TimePeriod start = series.getTimePeriod(series.getMinStartIndex());
            TimePeriod end = series.getTimePeriod(series.getMaxEndIndex());
            if (!interval) {
               if (this.xPosition == TimePeriodAnchor.START) {
                  TimePeriod maxStart = series.getTimePeriod(series.getMaxStartIndex());
                  temp = new Range((double)start.getStart().getTime(), (double)maxStart.getStart().getTime());
               } else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
                  TimePeriod minMiddle = series.getTimePeriod(series.getMinMiddleIndex());
                  long s1 = minMiddle.getStart().getTime();
                  long e1 = minMiddle.getEnd().getTime();
                  TimePeriod maxMiddle = series.getTimePeriod(series.getMaxMiddleIndex());
                  long s2 = maxMiddle.getStart().getTime();
                  long e2 = maxMiddle.getEnd().getTime();
                  temp = new Range((double)(s1 + (e1 - s1) / 2L), (double)(s2 + (e2 - s2) / 2L));
               } else if (this.xPosition == TimePeriodAnchor.END) {
                  TimePeriod minEnd = series.getTimePeriod(series.getMinEndIndex());
                  temp = new Range((double)minEnd.getEnd().getTime(), (double)end.getEnd().getTime());
               }
            } else {
               temp = new Range((double)start.getStart().getTime(), (double)end.getEnd().getTime());
            }

            result = Range.combine(result, temp);
         }
      }

      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TimePeriodValuesCollection)) {
         return false;
      } else {
         TimePeriodValuesCollection that = (TimePeriodValuesCollection)obj;
         return this.xPosition != that.xPosition ? false : ObjectUtils.equal(this.data, that.data);
      }
   }
}
