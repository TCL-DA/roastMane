package com.sun.javafx.animation;

import com.sun.javafx.util.Utils;
import javafx.animation.KeyValue;

public class KeyValueHelper {
   private static KeyValueHelper.KeyValueAccessor keyValueAccessor;

   private KeyValueHelper() {
   }

   public static KeyValueType getType(KeyValue var0) {
      return keyValueAccessor.getType(var0);
   }

   public static void setKeyValueAccessor(KeyValueHelper.KeyValueAccessor var0) {
      if (keyValueAccessor != null) {
         throw new IllegalStateException();
      } else {
         keyValueAccessor = var0;
      }
   }

   static {
      Utils.forceInit(KeyValue.class);
   }

   public interface KeyValueAccessor {
      KeyValueType getType(KeyValue var1);
   }
}
