package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.PathElement;

public class CubicCurveToHelper extends PathElementHelper {
   private static final CubicCurveToHelper theInstance = new CubicCurveToHelper();
   private static CubicCurveToHelper.CubicCurveToAccessor cubicCurveToAccessor;

   private static CubicCurveToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(CubicCurveTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      cubicCurveToAccessor.doAddTo(var1, var2);
   }

   public static void setCubicCurveToAccessor(CubicCurveToHelper.CubicCurveToAccessor var0) {
      if (cubicCurveToAccessor != null) {
         throw new IllegalStateException();
      } else {
         cubicCurveToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(CubicCurveTo.class);
   }

   public interface CubicCurveToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
