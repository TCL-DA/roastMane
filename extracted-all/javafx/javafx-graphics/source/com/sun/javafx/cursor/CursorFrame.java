package com.sun.javafx.cursor;

import java.util.HashMap;
import java.util.Map;

public abstract class CursorFrame {
   private Class<?> firstPlatformCursorClass;
   private Object firstPlatformCursor;
   private Map<Class<?>, Object> otherPlatformCursors;

   public abstract CursorType getCursorType();

   public <T> T getPlatformCursor(Class<T> var1) {
      if (this.firstPlatformCursorClass == var1) {
         return (T)this.firstPlatformCursor;
      } else {
         return (T)(this.otherPlatformCursors != null ? this.otherPlatformCursors.get(var1) : null);
      }
   }

   public <T> void setPlatforCursor(Class<T> var1, T var2) {
      if (this.firstPlatformCursorClass != null && this.firstPlatformCursorClass != var1) {
         if (this.otherPlatformCursors == null) {
            this.otherPlatformCursors = new HashMap<>();
         }

         this.otherPlatformCursors.put(var1, var2);
      } else {
         this.firstPlatformCursorClass = var1;
         this.firstPlatformCursor = var2;
      }
   }
}
