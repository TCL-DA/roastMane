package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.PathElement;

public class HLineToHelper extends PathElementHelper {
   private static final HLineToHelper theInstance = new HLineToHelper();
   private static HLineToHelper.HLineToAccessor hLineToAccessor;

   private static HLineToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(HLineTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      hLineToAccessor.doAddTo(var1, var2);
   }

   public static void setHLineToAccessor(HLineToHelper.HLineToAccessor var0) {
      if (hLineToAccessor != null) {
         throw new IllegalStateException();
      } else {
         hLineToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(HLineTo.class);
   }

   public interface HLineToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
