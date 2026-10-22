package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.PathElement;

public abstract class PathElementHelper {
   private static PathElementHelper.PathElementAccessor pathElementAccessor;

   protected PathElementHelper() {
   }

   private static PathElementHelper getHelper(PathElement var0) {
      return pathElementAccessor.getHelper(var0);
   }

   protected static void setHelper(PathElement var0, PathElementHelper var1) {
      pathElementAccessor.setHelper(var0, var1);
   }

   public static void addTo(PathElement var0, Path2D var1) {
      getHelper(var0).addToImpl(var0, var1);
   }

   protected abstract void addToImpl(PathElement var1, Path2D var2);

   public static void setPathElementAccessor(PathElementHelper.PathElementAccessor var0) {
      if (pathElementAccessor != null) {
         throw new IllegalStateException();
      } else {
         pathElementAccessor = var0;
      }
   }

   static {
      Utils.forceInit(PathElement.class);
   }

   public interface PathElementAccessor {
      PathElementHelper getHelper(PathElement var1);

      void setHelper(PathElement var1, PathElementHelper var2);
   }
}
