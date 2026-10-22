package org.jfree.data.gantt;

import java.util.Date;
import org.jfree.chart.util.Args;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.IntervalXYDataset;

public class XYTaskDataset extends AbstractXYDataset implements IntervalXYDataset, DatasetChangeListener {
   private TaskSeriesCollection underlying;
   private double seriesWidth;
   private boolean transposed;

   public XYTaskDataset(TaskSeriesCollection tasks) {
      Args.nullNotPermitted(tasks, "tasks");
      this.underlying = tasks;
      this.seriesWidth = 0.8;
      this.underlying.addChangeListener(this);
   }

   public TaskSeriesCollection getTasks() {
      return this.underlying;
   }

   public double getSeriesWidth() {
      return this.seriesWidth;
   }

   public void setSeriesWidth(double w) {
      if (w <= 0.0) {
         throw new IllegalArgumentException("Requires 'w' > 0.0.");
      } else {
         this.seriesWidth = w;
         this.fireDatasetChanged();
      }
   }

   public boolean isTransposed() {
      return this.transposed;
   }

   public void setTransposed(boolean transposed) {
      this.transposed = transposed;
      this.fireDatasetChanged();
   }

   @Override
   public int getSeriesCount() {
      return this.underlying.getSeriesCount();
   }

   @Override
   public Comparable getSeriesKey(int series) {
      return this.underlying.getSeriesKey(series);
   }

   @Override
   public int getItemCount(int series) {
      return this.underlying.getSeries(series).getItemCount();
   }

   @Override
   public double getXValue(int series, int item) {
      return !this.transposed ? this.getSeriesValue(series) : this.getItemValue(series, item);
   }

   @Override
   public double getStartXValue(int series, int item) {
      return !this.transposed ? this.getSeriesStartValue(series) : this.getItemStartValue(series, item);
   }

   @Override
   public double getEndXValue(int series, int item) {
      return !this.transposed ? this.getSeriesEndValue(series) : this.getItemEndValue(series, item);
   }

   @Override
   public Number getX(int series, int item) {
      return new Double(this.getXValue(series, item));
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
   public double getYValue(int series, int item) {
      return !this.transposed ? this.getItemValue(series, item) : this.getSeriesValue(series);
   }

   @Override
   public double getStartYValue(int series, int item) {
      return !this.transposed ? this.getItemStartValue(series, item) : this.getSeriesStartValue(series);
   }

   @Override
   public double getEndYValue(int series, int item) {
      return !this.transposed ? this.getItemEndValue(series, item) : this.getSeriesEndValue(series);
   }

   @Override
   public Number getY(int series, int item) {
      return new Double(this.getYValue(series, item));
   }

   @Override
   public Number getStartY(int series, int item) {
      return new Double(this.getStartYValue(series, item));
   }

   @Override
   public Number getEndY(int series, int item) {
      return new Double(this.getEndYValue(series, item));
   }

   private double getSeriesValue(int series) {
      return (double)series;
   }

   private double getSeriesStartValue(int series) {
      return (double)series - this.seriesWidth / 2.0;
   }

   private double getSeriesEndValue(int series) {
      return (double)series + this.seriesWidth / 2.0;
   }

   private double getItemValue(int series, int item) {
      TaskSeries s = this.underlying.getSeries(series);
      Task t = s.get(item);
      TimePeriod duration = t.getDuration();
      Date start = duration.getStart();
      Date end = duration.getEnd();
      return (double)(start.getTime() + end.getTime()) / 2.0;
   }

   private double getItemStartValue(int series, int item) {
      TaskSeries s = this.underlying.getSeries(series);
      Task t = s.get(item);
      TimePeriod duration = t.getDuration();
      Date start = duration.getStart();
      return (double)start.getTime();
   }

   private double getItemEndValue(int series, int item) {
      TaskSeries s = this.underlying.getSeries(series);
      Task t = s.get(item);
      TimePeriod duration = t.getDuration();
      Date end = duration.getEnd();
      return (double)end.getTime();
   }

   @Override
   public void datasetChanged(DatasetChangeEvent event) {
      this.fireDatasetChanged();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof XYTaskDataset)) {
         return false;
      } else {
         XYTaskDataset that = (XYTaskDataset)obj;
         if (this.seriesWidth != that.seriesWidth) {
            return false;
         } else {
            return this.transposed != that.transposed ? false : this.underlying.equals(that.underlying);
         }
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      XYTaskDataset clone = (XYTaskDataset)super.clone();
      clone.underlying = (TaskSeriesCollection)this.underlying.clone();
      return clone;
   }
}
