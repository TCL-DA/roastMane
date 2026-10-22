package org.jfree.data.gantt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.general.Series;

public class TaskSeries extends Series {
   private List tasks = new ArrayList();

   public TaskSeries(String name) {
      super(name);
   }

   public void add(Task task) {
      Args.nullNotPermitted(task, "task");
      this.tasks.add(task);
      this.fireSeriesChanged();
   }

   public void remove(Task task) {
      this.tasks.remove(task);
      this.fireSeriesChanged();
   }

   public void removeAll() {
      this.tasks.clear();
      this.fireSeriesChanged();
   }

   @Override
   public int getItemCount() {
      return this.tasks.size();
   }

   public Task get(int index) {
      return (Task)this.tasks.get(index);
   }

   public Task get(String description) {
      Task result = null;
      int count = this.tasks.size();

      for (int i = 0; i < count; i++) {
         Task t = (Task)this.tasks.get(i);
         if (t.getDescription().equals(description)) {
            result = t;
            break;
         }
      }

      return result;
   }

   public List getTasks() {
      return Collections.unmodifiableList(this.tasks);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof TaskSeries)) {
         return false;
      } else if (!super.equals(obj)) {
         return false;
      } else {
         TaskSeries that = (TaskSeries)obj;
         return this.tasks.equals(that.tasks);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      TaskSeries clone = (TaskSeries)super.clone();
      clone.tasks = (List)ObjectUtils.deepClone(this.tasks);
      return clone;
   }
}
