package com.sun.marlin;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class MarlinProperties {
   private MarlinProperties() {
   }

   public static boolean isUseThreadLocal() {
      return getBoolean("prism.marlin.useThreadLocal", "true");
   }

   public static int getInitialEdges() {
      return align(getInteger("prism.marlin.edges", 4096, 64, 65536), 64);
   }

   public static int getInitialPixelWidth() {
      return align(getInteger("prism.marlin.pixelWidth", 4096, 64, 32768), 64);
   }

   public static int getInitialPixelHeight() {
      return align(getInteger("prism.marlin.pixelHeight", 2176, 64, 32768), 64);
   }

   public static boolean isProfileQuality() {
      String var0 = getString("prism.marlin.profile", "quality");
      if ("quality".equals(var0)) {
         return true;
      } else if ("speed".equals(var0)) {
         return false;
      } else {
         MarlinUtils.logInfo("Invalid value for prism.marlin.profile = " + var0 + "; expect value in [quality, speed] !");
         return true;
      }
   }

   public static int getSubPixel_Log2_X() {
      return getInteger("prism.marlin.subPixel_log2_X", 8, 0, 8);
   }

   public static int getSubPixel_Log2_Y() {
      int var0 = isProfileQuality() ? 3 : 2;
      return getInteger("prism.marlin.subPixel_log2_Y", var0, 0, 8);
   }

   public static int getBlockSize_Log2() {
      return getInteger("prism.marlin.blockSize_log2", 5, 3, 8);
   }

   public static boolean isForceRLE() {
      return getBoolean("prism.marlin.forceRLE", "false");
   }

   public static boolean isForceNoRLE() {
      return getBoolean("prism.marlin.forceNoRLE", "false");
   }

   public static boolean isUseTileFlags() {
      return getBoolean("prism.marlin.useTileFlags", "true");
   }

   public static boolean isUseTileFlagsWithHeuristics() {
      return isUseTileFlags() && getBoolean("prism.marlin.useTileFlags.useHeuristics", "true");
   }

   public static int getRLEMinWidth() {
      return getInteger("prism.marlin.rleMinWidth", 64, 0, Integer.MAX_VALUE);
   }

   public static boolean isUseSimplifier() {
      return getBoolean("prism.marlin.useSimplifier", "false");
   }

   public static boolean isUsePathSimplifier() {
      return getBoolean("prism.marlin.usePathSimplifier", "false");
   }

   public static float getPathSimplifierPixelTolerance() {
      return getFloat("prism.marlin.pathSimplifier.pixTol", 1.0F / MarlinConst.MIN_SUBPIXELS, 0.001F, 10.0F);
   }

   public static boolean isDoClip() {
      return getBoolean("prism.marlin.clip", "true");
   }

   public static boolean isDoClipRuntimeFlag() {
      return getBoolean("prism.marlin.clip.runtime.enable", "false");
   }

   public static boolean isDoClipAtRuntime() {
      return getBoolean("prism.marlin.clip.runtime", "true");
   }

   public static boolean isDoClipSubdivider() {
      return getBoolean("prism.marlin.clip.subdivider", "true");
   }

   public static float getSubdividerMinLength() {
      return getFloat("prism.marlin.clip.subdivider.minLength", 100.0F, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
   }

   public static boolean isDoStats() {
      return getBoolean("prism.marlin.doStats", "false");
   }

   public static boolean isDoMonitors() {
      return getBoolean("prism.marlin.doMonitors", "false");
   }

   public static boolean isDoChecks() {
      return getBoolean("prism.marlin.doChecks", "false");
   }

   public static boolean isLoggingEnabled() {
      return getBoolean("prism.marlin.log", "false");
   }

   public static boolean isUseLogger() {
      return getBoolean("prism.marlin.useLogger", "false");
   }

   public static boolean isLogCreateContext() {
      return getBoolean("prism.marlin.logCreateContext", "false");
   }

   public static boolean isLogUnsafeMalloc() {
      return getBoolean("prism.marlin.logUnsafeMalloc", "false");
   }

   public static float getCurveLengthError() {
      return getFloat("prism.marlin.curve_len_err", 0.01F, 1.0E-6F, 1.0F);
   }

   public static float getCubicDecD2() {
      float var0 = isProfileQuality() ? 1.0F : 2.5F;
      return getFloat("prism.marlin.cubic_dec_d2", var0, 1.0E-5F, 4.0F);
   }

   public static float getCubicIncD1() {
      float var0 = isProfileQuality() ? 0.2F : 0.5F;
      return getFloat("prism.marlin.cubic_inc_d1", var0, 1.0E-6F, 1.0F);
   }

   public static float getQuadDecD2() {
      float var0 = isProfileQuality() ? 0.5F : 1.0F;
      return getFloat("prism.marlin.quad_dec_d2", var0, 1.0E-5F, 4.0F);
   }

   static String getString(String var0, String var1) {
      return AccessController.doPrivileged((PrivilegedAction<String>)(() -> {
         String var2 = System.getProperty(var0);
         return var2 == null ? var1 : var2;
      }));
   }

   static boolean getBoolean(String var0, String var1) {
      return Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)(() -> {
         String var2 = System.getProperty(var0);
         return var2 == null ? var1 : var2;
      })));
   }

   static int getInteger(String var0, int var1, int var2, int var3) {
      String var4 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty(var0)));
      int var5 = var1;
      if (var4 != null) {
         try {
            var5 = Integer.decode(var4);
         } catch (NumberFormatException var7) {
            MarlinUtils.logInfo("Invalid integer value for " + var0 + " = " + var4);
         }
      }

      if (var5 < var2 || var5 > var3) {
         MarlinUtils.logInfo("Invalid value for " + var0 + " = " + var5 + "; expected value in range[" + var2 + ", " + var3 + "] !");
         var5 = var1;
      }

      return var5;
   }

   static int align(int var0, int var1) {
      int var2 = FloatMath.ceil_int((float)var0 / (float)var1);
      return var2 * var1;
   }

   public static double getDouble(String var0, double var1, double var3, double var5) {
      double var7 = var1;
      String var9 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty(var0)));
      if (var9 != null) {
         try {
            var7 = Double.parseDouble(var9);
         } catch (NumberFormatException var11) {
            MarlinUtils.logInfo("Invalid value for " + var0 + " = " + var9 + " !");
         }
      }

      if (var7 < var3 || var7 > var5) {
         MarlinUtils.logInfo("Invalid value for " + var0 + " = " + var7 + "; expect value in range[" + var3 + ", " + var5 + "] !");
         var7 = var1;
      }

      return var7;
   }

   public static float getFloat(String var0, float var1, float var2, float var3) {
      return (float)getDouble(var0, (double)var1, (double)var2, (double)var3);
   }
}
