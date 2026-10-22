package com.sun.javafx.scene.input;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.KeyCode;

public final class KeyCodeMap {
   private static final Map<Integer, KeyCode> charMap = new HashMap<>(KeyCode.values().length);

   KeyCodeMap() {
   }

   public static KeyCode valueOf(int var0) {
      return charMap.get(var0);
   }

   static {
      for (KeyCode var3 : KeyCode.values()) {
         charMap.put(var3.getCode(), var3);
      }
   }
}
