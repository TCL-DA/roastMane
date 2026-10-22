package com.sun.javafx.scene.text;

import com.sun.javafx.util.Utils;
import javafx.scene.text.Font;

public class FontHelper {
   private static FontHelper.FontAccessor fontAccessor;

   private FontHelper() {
   }

   public static Object getNativeFont(Font var0) {
      return fontAccessor.getNativeFont(var0);
   }

   public static void setNativeFont(Font var0, Object var1, String var2, String var3, String var4) {
      fontAccessor.setNativeFont(var0, var1, var2, var3, var4);
   }

   public static Font nativeFont(Object var0, String var1, String var2, String var3, double var4) {
      return fontAccessor.nativeFont(var0, var1, var2, var3, var4);
   }

   public static void setFontAccessor(FontHelper.FontAccessor var0) {
      if (fontAccessor != null) {
         throw new IllegalStateException();
      } else {
         fontAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Font.class);
   }

   public interface FontAccessor {
      Object getNativeFont(Font var1);

      void setNativeFont(Font var1, Object var2, String var3, String var4, String var5);

      Font nativeFont(Object var1, String var2, String var3, String var4, double var5);
   }
}
