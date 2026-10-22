package org.jfree.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jfree.chart.util.ObjectUtils;

public class LegendItemCollection implements Cloneable, Serializable {
   private static final long serialVersionUID = 1365215565589815953L;
   private List items = new ArrayList();

   public void add(LegendItem item) {
      this.items.add(item);
   }

   public void addAll(LegendItemCollection collection) {
      this.items.addAll(collection.items);
   }

   public LegendItem get(int index) {
      return (LegendItem)this.items.get(index);
   }

   public int getItemCount() {
      return this.items.size();
   }

   public Iterator iterator() {
      return this.items.iterator();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof LegendItemCollection)) {
         return false;
      } else {
         LegendItemCollection that = (LegendItemCollection)obj;
         return this.items.equals(that.items);
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      LegendItemCollection clone = (LegendItemCollection)super.clone();
      clone.items = (List)ObjectUtils.deepClone(this.items);
      return clone;
   }
}
