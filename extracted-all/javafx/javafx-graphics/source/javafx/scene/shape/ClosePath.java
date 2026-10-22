package javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.ClosePathHelper;
import com.sun.javafx.sg.prism.NGPath;

public class ClosePath extends PathElement {
   public ClosePath() {
      ClosePathHelper.initHelper(this);
   }

   @Override
   void addTo(NGPath var1) {
      var1.addClosePath();
   }

   private void doAddTo(Path2D var1) {
      var1.closePath();
   }

   @Override
   public String toString() {
      return "ClosePath";
   }

   static {
      ClosePathHelper.setClosePathAccessor(new ClosePathHelper.ClosePathAccessor() {
         @Override
         public void doAddTo(PathElement var1, Path2D var2) {
            ((ClosePath)var1).doAddTo(var2);
         }
      });
   }
}
