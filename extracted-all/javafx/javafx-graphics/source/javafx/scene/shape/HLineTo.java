package javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.HLineToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

public class HLineTo extends PathElement {
   private DoubleProperty x = new DoublePropertyBase() {
      public void invalidated() {
         HLineTo.this.u();
      }

      public Object getBean() {
         return HLineTo.this;
      }

      public String getName() {
         return "x";
      }
   };

   public HLineTo() {
      HLineToHelper.initHelper(this);
   }

   public HLineTo(double var1) {
      this.setX(var1);
      HLineToHelper.initHelper(this);
   }

   public final void setX(double var1) {
      this.x.set(var1);
   }

   public final double getX() {
      return this.x.get();
   }

   public final DoubleProperty xProperty() {
      return this.x;
   }

   @Override
   void addTo(NGPath var1) {
      if (this.isAbsolute()) {
         var1.addLineTo((float)this.getX(), var1.getCurrentY());
      } else {
         var1.addLineTo((float)((double)var1.getCurrentX() + this.getX()), var1.getCurrentY());
      }
   }

   private void doAddTo(Path2D var1) {
      if (this.isAbsolute()) {
         var1.lineTo((float)this.getX(), var1.getCurrentY());
      } else {
         var1.lineTo((float)((double)var1.getCurrentX() + this.getX()), var1.getCurrentY());
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("HLineTo[");
      var1.append("x=").append(this.getX());
      return var1.append("]").toString();
   }

   static {
      HLineToHelper.setHLineToAccessor(new HLineToHelper.HLineToAccessor() {
         @Override
         public void doAddTo(PathElement var1, Path2D var2) {
            ((HLineTo)var1).doAddTo(var2);
         }
      });
   }
}
