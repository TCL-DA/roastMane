package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

public class MoveToHelper extends PathElementHelper {
   private static final MoveToHelper theInstance = new MoveToHelper();
   private static MoveToHelper.MoveToAccessor moveToAccessor;

   private static MoveToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(MoveTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      moveToAccessor.doAddTo(var1, var2);
   }

   public static void setMoveToAccessor(MoveToHelper.MoveToAccessor var0) {
      if (moveToAccessor != null) {
         throw new IllegalStateException();
      } else {
         moveToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(MoveTo.class);
   }

   public interface MoveToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
