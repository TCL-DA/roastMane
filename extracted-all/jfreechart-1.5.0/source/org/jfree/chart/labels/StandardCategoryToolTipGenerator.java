package org.jfree.chart.labels;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.NumberFormat;
import org.jfree.data.category.CategoryDataset;

public class StandardCategoryToolTipGenerator extends AbstractCategoryItemLabelGenerator implements CategoryToolTipGenerator, Serializable {
   private static final long serialVersionUID = -6768806592218710764L;
   public static final String DEFAULT_TOOL_TIP_FORMAT_STRING = "({0}, {1}) = {2}";

   public StandardCategoryToolTipGenerator() {
      super("({0}, {1}) = {2}", NumberFormat.getInstance());
   }

   public StandardCategoryToolTipGenerator(String labelFormat, NumberFormat formatter) {
      super(labelFormat, formatter);
   }

   public StandardCategoryToolTipGenerator(String labelFormat, DateFormat formatter) {
      super(labelFormat, formatter);
   }

   @Override
   public String generateToolTip(CategoryDataset dataset, int row, int column) {
      return this.generateLabelString(dataset, row, column);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else {
         return !(obj instanceof StandardCategoryToolTipGenerator) ? false : super.equals(obj);
      }
   }
}
