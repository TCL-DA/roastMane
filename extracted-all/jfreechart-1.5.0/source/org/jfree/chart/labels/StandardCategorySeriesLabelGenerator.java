package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.MessageFormat;
import org.jfree.chart.HashUtils;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.data.category.CategoryDataset;

public class StandardCategorySeriesLabelGenerator implements CategorySeriesLabelGenerator, Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = 4630760091523940820L;
   public static final String DEFAULT_LABEL_FORMAT = "{0}";
   private String formatPattern;

   public StandardCategorySeriesLabelGenerator() {
      this("{0}");
   }

   public StandardCategorySeriesLabelGenerator(String format) {
      Args.nullNotPermitted(format, "format");
      this.formatPattern = format;
   }

   @Override
   public String generateLabel(CategoryDataset dataset, int series) {
      Args.nullNotPermitted(dataset, "dataset");
      return MessageFormat.format(this.formatPattern, this.createItemArray(dataset, series));
   }

   protected Object[] createItemArray(CategoryDataset dataset, int series) {
      return new Object[]{dataset.getRowKey(series).toString()};
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof StandardCategorySeriesLabelGenerator)) {
         return false;
      } else {
         StandardCategorySeriesLabelGenerator that = (StandardCategorySeriesLabelGenerator)obj;
         return this.formatPattern.equals(that.formatPattern);
      }
   }

   @Override
   public int hashCode() {
      int result = 127;
      return HashUtils.hashCode(result, this.formatPattern);
   }
}
