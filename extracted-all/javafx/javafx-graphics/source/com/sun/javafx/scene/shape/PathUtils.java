package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import java.util.Collection;
import javafx.scene.shape.PathElement;

public final class PathUtils {
   private PathUtils() {
   }

   public static Path2D configShape(Collection<PathElement> var0, boolean var1) {
      Path2D var2 = new Path2D(var1 ? 0 : 1, var0.size());

      for (PathElement var4 : var0) {
         PathElementHelper.addTo(var4, var2);
      }

      return var2;
   }
}
