package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;

public class QuadCurveToHelper extends PathElementHelper {
   private static final QuadCurveToHelper theInstance = new QuadCurveToHelper();
   private static QuadCurveToHelper.QuadCurveToAccessor quadCurveToAccessor;

   private static QuadCurveToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(QuadCurveTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      quadCurveToAccessor.doAddTo(var1, var2);
   }

   public static void setQuadCurveToAccessor(QuadCurveToHelper.QuadCurveToAccessor var0) {
      if (quadCurveToAccessor != null) {
         throw new IllegalStateException();
      } else {
         quadCurveToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(QuadCurveTo.class);
   }

   public interface QuadCurveToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
