package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.VLineTo;

public class VLineToHelper extends PathElementHelper {
   private static final VLineToHelper theInstance = new VLineToHelper();
   private static VLineToHelper.VLineToAccessor vLineToAccessor;

   private static VLineToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(VLineTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      vLineToAccessor.doAddTo(var1, var2);
   }

   public static void setVLineToAccessor(VLineToHelper.VLineToAccessor var0) {
      if (vLineToAccessor != null) {
         throw new IllegalStateException();
      } else {
         vLineToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(VLineTo.class);
   }

   public interface VLineToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
