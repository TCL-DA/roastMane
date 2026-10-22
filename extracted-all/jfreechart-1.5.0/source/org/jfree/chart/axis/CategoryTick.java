package org.jfree.chart.axis;

import org.jfree.chart.text.TextBlock;
import org.jfree.chart.text.TextBlockAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.ObjectUtils;

public class CategoryTick extends Tick {
   private Comparable category;
   private TextBlock label;
   private TextBlockAnchor labelAnchor;

   public CategoryTick(Comparable category, TextBlock label, TextBlockAnchor labelAnchor, TextAnchor rotationAnchor, double angle) {
      super("", TextAnchor.CENTER, rotationAnchor, angle);
      this.category = category;
      this.label = label;
      this.labelAnchor = labelAnchor;
   }

   public Comparable getCategory() {
      return this.category;
   }

   public TextBlock getLabel() {
      return this.label;
   }

   public TextBlockAnchor getLabelAnchor() {
      return this.labelAnchor;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof CategoryTick && super.equals(obj)) {
         CategoryTick that = (CategoryTick)obj;
         if (!ObjectUtils.equal(this.category, that.category)) {
            return false;
         } else {
            return !ObjectUtils.equal(this.label, that.label) ? false : ObjectUtils.equal(this.labelAnchor, that.labelAnchor);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = 41;
      result = 37 * result + this.category.hashCode();
      result = 37 * result + this.label.hashCode();
      return 37 * result + this.labelAnchor.hashCode();
   }
}
