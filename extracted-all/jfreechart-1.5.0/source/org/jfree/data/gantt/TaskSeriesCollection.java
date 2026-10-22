package org.jfree.data.gantt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.general.AbstractSeriesDataset;
import org.jfree.data.general.SeriesChangeEvent;
import org.jfree.data.time.TimePeriod;

public class TaskSeriesCollection extends AbstractSeriesDataset implements GanttCategoryDataset, Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -2065799050738449903L;
   private List keys = new ArrayList();
   private List data = new ArrayList();

   public TaskSeries getSeries(Comparable key) {
      if (key == null) {
         throw new NullPointerException("Null 'key' argument.");
      } else {
         TaskSeries result = null;
         int index = this.getRowIndex(key);
         if (index >= 0) {
            result = this.getSeries(index);
         }

         return result;
      }
   }

   public TaskSeries getSeries(int series) {
      if (series >= 0 && series < this.getSeriesCount()) {
         return (TaskSeries)this.data.get(series);
      } else {
         throw new IllegalArgumentException("Series index out of bounds");
      }
   }

   @Override
   public int getSeriesCount() {
      return this.getRowCount();
   }

   @Override
   public Comparable getSeriesKey(int series) {
      TaskSeries ts = (TaskSeries)this.data.get(series);
      return ts.getKey();
   }

   @Override
   public int getRowCount() {
      return this.data.size();
   }

   @Override
   public List getRowKeys() {
      return this.data;
   }

   @Override
   public int getColumnCount() {
      return this.keys.size();
   }

   @Override
   public List getColumnKeys() {
      return this.keys;
   }

   @Override
   public Comparable getColumnKey(int index) {
      return (Comparable)this.keys.get(index);
   }

   @Override
   public int getColumnIndex(Comparable columnKey) {
      Args.nullNotPermitted(columnKey, "columnKey");
      return this.keys.indexOf(columnKey);
   }

   @Override
   public int getRowIndex(Comparable rowKey) {
      int result = -1;
      int count = this.data.size();

      for (int i = 0; i < count; i++) {
         TaskSeries s = (TaskSeries)this.data.get(i);
         if (s.getKey().equals(rowKey)) {
            result = i;
            break;
         }
      }

      return result;
   }

   @Override
   public Comparable getRowKey(int index) {
      TaskSeries series = (TaskSeries)this.data.get(index);
      return series.getKey();
   }

   public void add(TaskSeries series) {
      Args.nullNotPermitted(series, "series");
      this.data.add(series);
      series.addChangeListener(this);

      for (Task task : series.getTasks()) {
         String key = task.getDescription();
         int index = this.keys.indexOf(key);
         if (index < 0) {
            this.keys.add(key);
         }
      }

      this.fireDatasetChanged();
   }

   public void remove(TaskSeries series) {
      Args.nullNotPermitted(series, "series");
      if (this.data.contains(series)) {
         series.removeChangeListener(this);
         this.data.remove(series);
         this.fireDatasetChanged();
      }
   }

   public void remove(int series) {
      if (series >= 0 && series < this.getSeriesCount()) {
         TaskSeries ts = (TaskSeries)this.data.get(series);
         ts.removeChangeListener(this);
         this.data.remove(series);
         this.fireDatasetChanged();
      } else {
         throw new IllegalArgumentException("TaskSeriesCollection.remove(): index outside valid range.");
      }
   }

   public void removeAll() {
      for (TaskSeries series : this.data) {
         series.removeChangeListener(this);
      }

      this.data.clear();
      this.fireDatasetChanged();
   }

   @Override
   public Number getValue(Comparable rowKey, Comparable columnKey) {
      return this.getStartValue(rowKey, columnKey);
   }

   @Override
   public Number getValue(int row, int column) {
      return this.getStartValue(row, column);
   }

   @Override
   public Number getStartValue(Comparable rowKey, Comparable columnKey) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         TimePeriod duration = task.getDuration();
         if (duration != null) {
            result = new Long(duration.getStart().getTime());
         }
      }

      return result;
   }

   @Override
   public Number getStartValue(int row, int column) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getStartValue(rowKey, columnKey);
   }

   @Override
   public Number getEndValue(Comparable rowKey, Comparable columnKey) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         TimePeriod duration = task.getDuration();
         if (duration != null) {
            result = new Long(duration.getEnd().getTime());
         }
      }

      return result;
   }

   @Override
   public Number getEndValue(int row, int column) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getEndValue(rowKey, columnKey);
   }

   @Override
   public Number getPercentComplete(int row, int column) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getPercentComplete(rowKey, columnKey);
   }

   @Override
   public Number getPercentComplete(Comparable rowKey, Comparable columnKey) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         result = task.getPercentComplete();
      }

      return result;
   }

   @Override
   public int getSubIntervalCount(int row, int column) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getSubIntervalCount(rowKey, columnKey);
   }

   @Override
   public int getSubIntervalCount(Comparable rowKey, Comparable columnKey) {
      int result = 0;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         result = task.getSubtaskCount();
      }

      return result;
   }

   @Override
   public Number getStartValue(int row, int column, int subinterval) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getStartValue(rowKey, columnKey, subinterval);
   }

   @Override
   public Number getStartValue(Comparable rowKey, Comparable columnKey, int subinterval) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         Task sub = task.getSubtask(subinterval);
         if (sub != null) {
            TimePeriod duration = sub.getDuration();
            result = new Long(duration.getStart().getTime());
         }
      }

      return result;
   }

   @Override
   public Number getEndValue(int row, int column, int subinterval) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getEndValue(rowKey, columnKey, subinterval);
   }

   @Override
   public Number getEndValue(Comparable rowKey, Comparable columnKey, int subinterval) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         Task sub = task.getSubtask(subinterval);
         if (sub != null) {
            TimePeriod duration = sub.getDuration();
            result = new Long(duration.getEnd().getTime());
         }
      }

      return result;
   }

   @Override
   public Number getPercentComplete(int row, int column, int subinterval) {
      Comparable rowKey = this.getRowKey(row);
      Comparable columnKey = this.getColumnKey(column);
      return this.getPercentComplete(rowKey, columnKey, subinterval);
   }

   @Override
   public Number getPercentComplete(Comparable rowKey, Comparable columnKey, int subinterval) {
      Number result = null;
      int row = this.getRowIndex(rowKey);
      TaskSeries series = (TaskSeries)this.data.get(row);
      Task task = series.get(columnKey.toString());
      if (task != null) {
         Task sub = task.getSubtask(subinterval);
         if (sub != null) {
            result = sub.getPercentComplete();
         }
      }

      return result;
   }

   @Override
   public void seriesChanged(SeriesChangeEvent event) {
      this.refreshKeys();
      this.fireDatasetChanged();
   }

   private void refreshKeys() {
      this.keys.clear();

      for (int i = 0; i < this.getSeriesCount(); i++) {
         TaskSeries series = (TaskSeries)this.data.get(i);

         for (Task task : series.getTasks()) {
            String key = task.getDescription();
            int index = this.keys.indexOf(key);
            if (index < 0) {
               this.keys.add(key);
            }
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TaskSeriesCollection)) {
         return false;
      } else {
         TaskSeriesCollection that = (TaskSeriesCollection)obj;
         return ObjectUtils.equal(this.data, that.data);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      TaskSeriesCollection clone = (TaskSeriesCollection)super.clone();
      clone.data = (List)ObjectUtils.deepClone(this.data);
      clone.keys = new ArrayList(this.keys);
      return clone;
   }
}
