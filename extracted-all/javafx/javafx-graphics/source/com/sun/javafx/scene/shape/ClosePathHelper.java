package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.PathElement;

public class ClosePathHelper extends PathElementHelper {
   private static final ClosePathHelper theInstance = new ClosePathHelper();
   private static ClosePathHelper.ClosePathAccessor closePathAccessor;

   private static ClosePathHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(ClosePath var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      closePathAccessor.doAddTo(var1, var2);
   }

   public static void setClosePathAccessor(ClosePathHelper.ClosePathAccessor var0) {
      if (closePathAccessor != null) {
         throw new IllegalStateException();
      } else {
         closePathAccessor = var0;
      }
   }

   static {
      Utils.forceInit(ClosePath.class);
   }

   public interface ClosePathAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
