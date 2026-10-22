package com.sun.javafx.scene.input;

import com.sun.javafx.util.Utils;
import javafx.scene.input.Clipboard;

public class ClipboardHelper {
   private static ClipboardHelper.ClipboardAccessor clipboardAccessor;

   private ClipboardHelper() {
   }

   public static boolean contentPut(Clipboard var0) {
      return clipboardAccessor.contentPut(var0);
   }

   public static void setClipboardAccessor(ClipboardHelper.ClipboardAccessor var0) {
      if (clipboardAccessor != null) {
         throw new IllegalStateException();
      } else {
         clipboardAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Clipboard.class);
   }

   public interface ClipboardAccessor {
      boolean contentPut(Clipboard var1);
   }
}
