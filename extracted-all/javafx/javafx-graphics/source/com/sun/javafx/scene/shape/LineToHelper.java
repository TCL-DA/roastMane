package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.PathElement;

public class LineToHelper extends PathElementHelper {
   private static final LineToHelper theInstance = new LineToHelper();
   private static LineToHelper.LineToAccessor lineToAccessor;

   private static LineToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(LineTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      lineToAccessor.doAddTo(var1, var2);
   }

   public static void setLineToAccessor(LineToHelper.LineToAccessor var0) {
      if (lineToAccessor != null) {
         throw new IllegalStateException();
      } else {
         lineToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(LineTo.class);
   }

   public interface LineToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
