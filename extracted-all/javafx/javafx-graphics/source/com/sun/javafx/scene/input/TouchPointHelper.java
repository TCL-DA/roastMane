package com.sun.javafx.scene.input;

import com.sun.javafx.util.Utils;
import javafx.scene.input.TouchPoint;

public class TouchPointHelper {
   private static TouchPointHelper.TouchPointAccessor touchPointAccessor;

   private TouchPointHelper() {
   }

   public static void reset(TouchPoint var0) {
      touchPointAccessor.reset(var0);
   }

   public static void setTouchPointAccessor(TouchPointHelper.TouchPointAccessor var0) {
      if (touchPointAccessor != null) {
         throw new IllegalStateException();
      } else {
         touchPointAccessor = var0;
      }
   }

   static {
      Utils.forceInit(TouchPoint.class);
   }

   public interface TouchPointAccessor {
      void reset(TouchPoint var1);
   }
}
