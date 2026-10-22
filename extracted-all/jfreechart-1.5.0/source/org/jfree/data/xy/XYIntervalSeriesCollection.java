package org.jfree.data.xy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;

public class XYIntervalSeriesCollection extends AbstractIntervalXYDataset implements IntervalXYDataset, PublicCloneable, Serializable {
   private List data = new ArrayList();

   public void addSeries(XYIntervalSeries series) {
      Args.nullNotPermitted(series, "series");
      this.data.add(series);
      series.addChangeListener(this);
      this.fireDatasetChanged();
   }

   @Override
   public int getSeriesCount() {
      return this.data.size();
   }

   public XYIntervalSeries getSeries(int series) {
      if (series >= 0 && series < this.getSeriesCount()) {
         return (XYIntervalSeries)this.data.get(series);
      } else {
         throw new IllegalArgumentException("Series index out of bounds");
      }
   }

   @Override
   public Comparable getSeriesKey(int series) {
      return this.getSeries(series).getKey();
   }

   @Override
   public int getItemCount(int series) {
      return this.getSeries(series).getItemCount();
   }

   @Override
   public Number getX(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getX(item);
   }

   @Override
   public double getStartXValue(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getXLowValue(item);
   }

   @Override
   public double getEndXValue(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getXHighValue(item);
   }

   @Override
   public double getYValue(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getYValue(item);
   }

   @Override
   public double getStartYValue(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getYLowValue(item);
   }

   @Override
   public double getEndYValue(int series, int item) {
      XYIntervalSeries s = (XYIntervalSeries)this.data.get(series);
      return s.getYHighValue(item);
   }

   @Override
   public Number getY(int series, int item) {
      return new Double(this.getYValue(series, item));
   }

   @Override
   public Number getStartX(int series, int item) {
      return new Double(this.getStartXValue(series, item));
   }

   @Override
   public Number getEndX(int series, int item) {
      return new Double(this.getEndXValue(series, item));
   }

   @Override
   public Number getStartY(int series, int item) {
      return new Double(this.getStartYValue(series, item));
   }

   @Override
   public Number getEndY(int series, int item) {
      return new Double(this.getEndYValue(series, item));
   }

   public void removeSeries(int series) {
      if (series >= 0 && series < this.getSeriesCount()) {
         XYIntervalSeries ts = (XYIntervalSeries)this.data.get(series);
         ts.removeChangeListener(this);
         this.data.remove(series);
         this.fireDatasetChanged();
      } else {
         throw new IllegalArgumentException("Series index out of bounds.");
      }
   }

   public void removeSeries(XYIntervalSeries series) {
      Args.nullNotPermitted(series, "series");
      if (this.data.contains(series)) {
         series.removeChangeListener(this);
         this.data.remove(series);
         this.fireDatasetChanged();
      }
   }

   public void removeAllSeries() {
      for (int i = 0; i < this.data.size(); i++) {
         XYIntervalSeries series = (XYIntervalSeries)this.data.get(i);
         series.removeChangeListener(this);
      }

      this.data.clear();
      this.fireDatasetChanged();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof XYIntervalSeriesCollection)) {
         return false;
      } else {
         XYIntervalSeriesCollection that = (XYIntervalSeriesCollection)obj;
         return ObjectUtils.equal(this.data, that.data);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      XYIntervalSeriesCollection clone = (XYIntervalSeriesCollection)super.clone();
      int seriesCount = this.getSeriesCount();
      clone.data = new ArrayList(seriesCount);

      for (int i = 0; i < this.data.size(); i++) {
         clone.data.set(i, this.getSeries(i).clone());
      }

      return clone;
   }
}
