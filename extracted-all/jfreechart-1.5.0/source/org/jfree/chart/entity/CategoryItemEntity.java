package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.Serializable;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.data.category.CategoryDataset;

public class CategoryItemEntity extends ChartEntity implements Cloneable, Serializable {
   private static final long serialVersionUID = -8657249457902337349L;
   private CategoryDataset dataset;
   private Comparable rowKey;
   private Comparable columnKey;

   public CategoryItemEntity(Shape area, String toolTipText, String urlText, CategoryDataset dataset, Comparable rowKey, Comparable columnKey) {
      super(area, toolTipText, urlText);
      Args.nullNotPermitted(dataset, "dataset");
      this.dataset = dataset;
      this.rowKey = rowKey;
      this.columnKey = columnKey;
   }

   public CategoryDataset getDataset() {
      return this.dataset;
   }

   public void setDataset(CategoryDataset dataset) {
      Args.nullNotPermitted(dataset, "dataset");
      this.dataset = dataset;
   }

   public Comparable getRowKey() {
      return this.rowKey;
   }

   public void setRowKey(Comparable rowKey) {
      this.rowKey = rowKey;
   }

   public Comparable getColumnKey() {
      return this.columnKey;
   }

   public void setColumnKey(Comparable columnKey) {
      this.columnKey = columnKey;
   }

   @Override
   public String toString() {
      return "CategoryItemEntity: rowKey=" + this.rowKey + ", columnKey=" + this.columnKey + ", dataset=" + this.dataset;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof CategoryItemEntity)) {
         return false;
      } else {
         CategoryItemEntity that = (CategoryItemEntity)obj;
         if (!this.rowKey.equals(that.rowKey)) {
            return false;
         } else if (!this.columnKey.equals(that.columnKey)) {
            return false;
         } else {
            return !ObjectUtils.equal(this.dataset, that.dataset) ? false : super.equals(obj);
         }
      }
   }
}
