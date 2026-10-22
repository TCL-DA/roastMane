package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import org.jfree.chart.HashUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.DataUtils;
import org.jfree.data.category.CategoryDataset;

public abstract class AbstractCategoryItemLabelGenerator implements PublicCloneable, Cloneable, Serializable {
   private static final long serialVersionUID = -7108591260223293197L;
   private String labelFormat;
   private String nullValueString;
   private NumberFormat numberFormat;
   private DateFormat dateFormat;
   private NumberFormat percentFormat;

   protected AbstractCategoryItemLabelGenerator(String labelFormat, NumberFormat formatter) {
      this(labelFormat, formatter, NumberFormat.getPercentInstance());
   }

   protected AbstractCategoryItemLabelGenerator(String labelFormat, NumberFormat formatter, NumberFormat percentFormatter) {
      Args.nullNotPermitted(labelFormat, "labelFormat");
      Args.nullNotPermitted(formatter, "formatter");
      Args.nullNotPermitted(percentFormatter, "percentFormatter");
      this.labelFormat = labelFormat;
      this.numberFormat = formatter;
      this.percentFormat = percentFormatter;
      this.dateFormat = null;
      this.nullValueString = "-";
   }

   protected AbstractCategoryItemLabelGenerator(String labelFormat, DateFormat formatter) {
      Args.nullNotPermitted(labelFormat, "labelFormat");
      Args.nullNotPermitted(formatter, "formatter");
      this.labelFormat = labelFormat;
      this.numberFormat = null;
      this.percentFormat = NumberFormat.getPercentInstance();
      this.dateFormat = formatter;
      this.nullValueString = "-";
   }

   public String generateRowLabel(CategoryDataset dataset, int row) {
      return dataset.getRowKey(row).toString();
   }

   public String generateColumnLabel(CategoryDataset dataset, int column) {
      return dataset.getColumnKey(column).toString();
   }

   public String getLabelFormat() {
      return this.labelFormat;
   }

   public NumberFormat getNumberFormat() {
      return this.numberFormat;
   }

   public DateFormat getDateFormat() {
      return this.dateFormat;
   }

   protected String generateLabelString(CategoryDataset dataset, int row, int column) {
      Args.nullNotPermitted(dataset, "dataset");
      Object[] items = this.createItemArray(dataset, row, column);
      return MessageFormat.format(this.labelFormat, items);
   }

   protected Object[] createItemArray(CategoryDataset dataset, int row, int column) {
      Object[] result = new Object[]{dataset.getRowKey(row).toString(), dataset.getColumnKey(column).toString(), null, null};
      Number value = dataset.getValue(row, column);
      if (value != null) {
         if (this.numberFormat != null) {
            result[2] = this.numberFormat.format(value);
         } else if (this.dateFormat != null) {
            result[2] = this.dateFormat.format(value);
         }
      } else {
         result[2] = this.nullValueString;
      }

      if (value != null) {
         double total = DataUtils.calculateColumnTotal(dataset, column);
         double percent = value.doubleValue() / total;
         result[3] = this.percentFormat.format(percent);
      }

      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof AbstractCategoryItemLabelGenerator)) {
         return false;
      } else {
         AbstractCategoryItemLabelGenerator that = (AbstractCategoryItemLabelGenerator)obj;
         if (!this.labelFormat.equals(that.labelFormat)) {
            return false;
         } else {
            return !ObjectUtils.equal(this.dateFormat, that.dateFormat) ? false : ObjectUtils.equal(this.numberFormat, that.numberFormat);
         }
      }
   }

   @Override
   public int hashCode() {
      int result = 127;
      result = HashUtils.hashCode(result, this.labelFormat);
      result = HashUtils.hashCode(result, this.nullValueString);
      result = HashUtils.hashCode(result, this.dateFormat);
      result = HashUtils.hashCode(result, this.numberFormat);
      return HashUtils.hashCode(result, this.percentFormat);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      AbstractCategoryItemLabelGenerator clone = (AbstractCategoryItemLabelGenerator)super.clone();
      if (this.numberFormat != null) {
         clone.numberFormat = (NumberFormat)this.numberFormat.clone();
      }

      if (this.dateFormat != null) {
         clone.dateFormat = (DateFormat)this.dateFormat.clone();
      }

      return clone;
   }
}
