package javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.VLineToHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

public class VLineTo extends PathElement {
   private DoubleProperty y = new DoublePropertyBase() {
      public void invalidated() {
         VLineTo.this.u();
      }

      public Object getBean() {
         return VLineTo.this;
      }

      public String getName() {
         return "y";
      }
   };

   public VLineTo() {
      VLineToHelper.initHelper(this);
   }

   public VLineTo(double var1) {
      this.setY(var1);
      VLineToHelper.initHelper(this);
   }

   public final void setY(double var1) {
      this.y.set(var1);
   }

   public final double getY() {
      return this.y.get();
   }

   public final DoubleProperty yProperty() {
      return this.y;
   }

   @Override
   void addTo(NGPath var1) {
      if (this.isAbsolute()) {
         var1.addLineTo(var1.getCurrentX(), (float)this.getY());
      } else {
         var1.addLineTo(var1.getCurrentX(), (float)((double)var1.getCurrentY() + this.getY()));
      }
   }

   private void doAddTo(Path2D var1) {
      if (this.isAbsolute()) {
         var1.lineTo(var1.getCurrentX(), (float)this.getY());
      } else {
         var1.lineTo(var1.getCurrentX(), (float)((double)var1.getCurrentY() + this.getY()));
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("VLineTo[");
      var1.append("y=").append(this.getY());
      return var1.append("]").toString();
   }

   static {
      VLineToHelper.setVLineToAccessor(new VLineToHelper.VLineToAccessor() {
         @Override
         public void doAddTo(PathElement var1, Path2D var2) {
            ((VLineTo)var1).doAddTo(var2);
         }
      });
   }
}
