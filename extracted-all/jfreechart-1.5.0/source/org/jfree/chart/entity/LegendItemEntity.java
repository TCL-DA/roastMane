package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.general.Dataset;

public class LegendItemEntity extends ChartEntity implements Cloneable, Serializable {
   private static final long serialVersionUID = -7435683933545666702L;
   private Dataset dataset;
   private Comparable seriesKey;
   private int seriesIndex;

   public LegendItemEntity(Shape area) {
      super(area);
   }

   public Dataset getDataset() {
      return this.dataset;
   }

   public void setDataset(Dataset dataset) {
      this.dataset = dataset;
   }

   public Comparable getSeriesKey() {
      return this.seriesKey;
   }

   public void setSeriesKey(Comparable key) {
      this.seriesKey = key;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof LegendItemEntity)) {
         return false;
      } else {
         LegendItemEntity that = (LegendItemEntity)obj;
         if (!ObjectUtils.equal(this.seriesKey, that.seriesKey)) {
            return false;
         } else if (this.seriesIndex != that.seriesIndex) {
            return false;
         } else {
            return !ObjectUtils.equal(this.dataset, that.dataset) ? false : super.equals(obj);
         }
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   @Override
   public String toString() {
      return "LegendItemEntity: seriesKey=" + this.seriesKey + ", dataset=" + this.dataset;
   }
}
