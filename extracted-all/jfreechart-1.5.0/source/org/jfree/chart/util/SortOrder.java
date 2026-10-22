package org.jfree.chart.util;

public enum SortOrder {
   ASCENDING("SortOrder.ASCENDING"),
   DESCENDING("SortOrder.DESCENDING");

   private final String name;

   private SortOrder(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return this.name;
   }
}
