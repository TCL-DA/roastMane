package com.sun.javafx.font;

import com.sun.glass.utils.NativeLibLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

class MacFontFinder {
   private static final int SystemFontType = 2;
   private static final int MonospacedFontType = 1;

   private static native String getFont(int var0);

   public static String getSystemFont() {
      return getFont(2);
   }

   public static String getMonospacedFont() {
      return getFont(1);
   }

   static native float getSystemFontSize();

   public static boolean populateFontFileNameMap(
      HashMap<String, String> var0, HashMap<String, String> var1, HashMap<String, ArrayList<String>> var2, Locale var3
   ) {
      if (var0 != null && var1 != null && var2 != null) {
         if (var3 == null) {
            var3 = Locale.ENGLISH;
         }

         String[] var4 = getFontData();
         if (var4 == null) {
            return false;
         } else {
            int var5 = 0;

            while (var5 < var4.length) {
               String var6 = var4[var5++];
               String var7 = var4[var5++];
               String var8 = var4[var5++];
               if (PrismFontFactory.debugFonts) {
                  System.err.println("[MacFontFinder] Name=" + var6);
                  System.err.println("\tFamily=" + var7);
                  System.err.println("\tFile=" + var8);
               }

               if (var6 != null && var7 != null && var8 != null) {
                  String var9 = var6.toLowerCase(var3);
                  String var10 = var7.toLowerCase(var3);
                  var0.put(var9, var8);
                  var1.put(var9, var7);
                  ArrayList var11 = (ArrayList)var2.get(var10);
                  if (var11 == null) {
                     var11 = new ArrayList();
                     var2.put(var10, var11);
                  }

                  var11.add(var6);
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static native String[] getFontData();

   static {
      Void var0 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         NativeLibLoader.loadLibrary("javafx_font");
         return null;
      }));
   }
}
