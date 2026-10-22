package com.sun.javafx.scene.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.PathElement;

public class ArcToHelper extends PathElementHelper {
   private static final ArcToHelper theInstance = new ArcToHelper();
   private static ArcToHelper.ArcToAccessor arcToAccessor;

   private static ArcToHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(ArcTo var0) {
      setHelper(var0, getInstance());
   }

   @Override
   protected void addToImpl(PathElement var1, Path2D var2) {
      arcToAccessor.doAddTo(var1, var2);
   }

   public static void setArcToAccessor(ArcToHelper.ArcToAccessor var0) {
      if (arcToAccessor != null) {
         throw new IllegalStateException();
      } else {
         arcToAccessor = var0;
      }
   }

   static {
      Utils.forceInit(ArcTo.class);
   }

   public interface ArcToAccessor {
      void doAddTo(PathElement var1, Path2D var2);
   }
}
