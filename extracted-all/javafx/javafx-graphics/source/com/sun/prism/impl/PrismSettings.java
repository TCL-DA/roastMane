package com.sun.prism.impl;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.util.Utils;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public final class PrismSettings {
   public static final boolean verbose;
   public static final boolean debug;
   public static final boolean trace;
   public static final boolean printAllocs;
   public static final boolean isVsyncEnabled;
   public static final boolean dirtyOptsEnabled;
   public static final boolean occlusionCullingEnabled;
   public static final boolean scrollCacheOpt;
   public static final boolean threadCheck;
   public static final boolean cacheSimpleShapes;
   public static final boolean cacheComplexShapes;
   public static final boolean useNewImageLoader;
   public static final List<String> tryOrder;
   public static final int prismStatFrequency;
   public static final PrismSettings.RasterizerType rasterizerSpec;
   public static final String refType;
   public static final boolean forceRepaint;
   public static final boolean noFallback;
   public static final boolean showDirtyRegions;
   public static final boolean showOverdraw;
   public static final boolean printRenderGraph;
   public static final int minRTTSize;
   public static final int dirtyRegionCount;
   public static final boolean disableBadDriverWarning;
   public static final boolean forceGPU;
   public static final int maxTextureSize;
   public static final int primTextureSize;
   public static final boolean disableRegionCaching;
   public static final boolean forcePow2;
   public static final boolean noClampToZero;
   public static final boolean disableD3D9Ex;
   public static final boolean allowHiDPIScaling;
   public static final long maxVram;
   public static final long targetVram;
   public static final boolean poolStats;
   public static final boolean poolDebug;
   public static final boolean disableEffects;
   public static final int glyphCacheWidth;
   public static final int glyphCacheHeight;
   public static final String perfLog;
   public static final boolean perfLogExitFlush;
   public static final boolean perfLogFirstPaintFlush;
   public static final boolean perfLogFirstPaintExit;
   public static final boolean superShader;
   public static final boolean forceUploadingPainter;
   public static final boolean forceAlphaTestShader;
   public static final boolean forceNonAntialiasedShape;

   private PrismSettings() {
   }

   private static void printBooleanOption(boolean var0, String var1) {
      if (var0) {
         System.out.println(var1);
      } else {
         System.out.print("Not ");
         System.out.print(Character.toLowerCase(var1.charAt(0)));
         System.out.println(var1.substring(1));
      }
   }

   private static int parseInt(String var0, int var1, int var2, String var3) {
      return "true".equalsIgnoreCase(var0) ? var2 : parseInt(var0, var1, var3);
   }

   private static int parseInt(String var0, int var1, String var2) {
      if (var0 != null) {
         try {
            return Integer.parseInt(var0);
         } catch (Exception var4) {
            if (var2 != null) {
               System.err.println(var2);
            }
         }
      }

      return var1;
   }

   private static long parseLong(String var0, long var1, long var3, String var5) {
      if (var0 != null && var0.length() > 0) {
         long var6 = 1L;
         if (var0.endsWith("%")) {
            if (var3 > 0L) {
               try {
                  var0 = var0.substring(0, var0.length() - 1);
                  double var8 = Double.parseDouble(var0);
                  if (var8 >= 0.0 && var8 <= 100.0) {
                     return Math.round((double)var3 * var8 / 100.0);
                  }
               } catch (Exception var10) {
               }
            }

            if (var5 != null) {
               System.err.println(var5);
            }

            return var1;
         }

         if (var0.endsWith("k") || var0.endsWith("K")) {
            var6 = 1024L;
         } else if (var0.endsWith("m") || var0.endsWith("M")) {
            var6 = 1048576L;
         } else if (var0.endsWith("g") || var0.endsWith("G")) {
            var6 = 1073741824L;
         }

         if (var6 > 1L) {
            var0 = var0.substring(0, var0.length() - 1);
         }

         try {
            return Long.parseLong(var0) * var6;
         } catch (Exception var11) {
            if (var5 != null) {
               System.err.println(var5);
            }
         }
      }

      return var1;
   }

   private static String[] split(String var0, String var1) {
      StringTokenizer var2 = new StringTokenizer(var0, var1);
      String[] var3 = new String[var2.countTokens()];
      int var4 = 0;

      while (var2.hasMoreTokens()) {
         var3[var4++] = var2.nextToken();
      }

      return var3;
   }

   private static boolean getBoolean(Properties var0, String var1, boolean var2) {
      String var3 = var0.getProperty(var1);
      return var3 != null ? Boolean.parseBoolean(var3) : var2;
   }

   private static boolean getBoolean(Properties var0, String var1, boolean var2, boolean var3) {
      String var4 = var0.getProperty(var1);
      if (var4 != null && var4.length() == 0) {
         return var3;
      } else {
         return var4 != null ? Boolean.parseBoolean(var4) : var2;
      }
   }

   private static int getInt(Properties var0, String var1, int var2, int var3, String var4) {
      return parseInt(var0.getProperty(var1), var2, var3, var4);
   }

   private static int getInt(Properties var0, String var1, int var2, String var3) {
      return parseInt(var0.getProperty(var1), var2, var3);
   }

   private static long getLong(Properties var0, String var1, long var2, String var4) {
      return parseLong(var0.getProperty(var1), var2, 0L, var4);
   }

   private static long getLong(Properties var0, String var1, long var2, long var4, String var6) {
      return parseLong(var0.getProperty(var1), var2, var4, var6);
   }

   static {
      Properties var0 = AccessController.doPrivileged((PrivilegedAction<Properties>)(() -> System.getProperties()));
      isVsyncEnabled = getBoolean(var0, "prism.vsync", true) && !getBoolean(var0, "javafx.animation.fullspeed", false);
      dirtyOptsEnabled = getBoolean(var0, "prism.dirtyopts", true);
      occlusionCullingEnabled = dirtyOptsEnabled && getBoolean(var0, "prism.occlusion.culling", true);
      dirtyRegionCount = Utils.clamp(0, getInt(var0, "prism.dirtyregioncount", 6, null), 15);
      scrollCacheOpt = getBoolean(var0, "prism.scrollcacheopt", false);
      threadCheck = getBoolean(var0, "prism.threadcheck", false);
      showDirtyRegions = getBoolean(var0, "prism.showdirty", false);
      showOverdraw = getBoolean(var0, "prism.showoverdraw", false);
      printRenderGraph = getBoolean(var0, "prism.printrendergraph", false);
      forceRepaint = getBoolean(var0, "prism.forcerepaint", false);
      noFallback = getBoolean(var0, "prism.noFallback", false);
      String var1 = var0.getProperty("prism.cacheshapes", "complex");
      if ("all".equals(var1) || "true".equals(var1)) {
         cacheSimpleShapes = true;
         cacheComplexShapes = true;
      } else if ("complex".equals(var1)) {
         cacheSimpleShapes = false;
         cacheComplexShapes = true;
      } else {
         cacheSimpleShapes = false;
         cacheComplexShapes = false;
      }

      useNewImageLoader = getBoolean(var0, "prism.newiio", true);
      verbose = getBoolean(var0, "prism.verbose", false);
      prismStatFrequency = getInt(var0, "prism.printStats", 0, 1, "Try -Dprism.printStats=<true or number>");
      debug = getBoolean(var0, "prism.debug", false);
      trace = getBoolean(var0, "prism.trace", false);
      printAllocs = getBoolean(var0, "prism.printallocs", false);
      disableBadDriverWarning = getBoolean(var0, "prism.disableBadDriverWarning", false);
      forceGPU = getBoolean(var0, "prism.forceGPU", false);
      String var2 = var0.getProperty("prism.order");
      String[] var3;
      if (var2 != null) {
         var3 = split(var2, ",");
      } else if (PlatformUtil.isWindows()) {
         var3 = new String[]{"d3d", "sw"};
      } else if (PlatformUtil.isMac()) {
         var3 = new String[]{"es2", "sw"};
      } else if (PlatformUtil.isIOS()) {
         var3 = new String[]{"es2"};
      } else if (PlatformUtil.isAndroid()) {
         var3 = new String[]{"es2"};
      } else if (PlatformUtil.isLinux()) {
         var3 = new String[]{"es2", "sw"};
      } else {
         var3 = new String[]{"sw"};
      }

      tryOrder = List.of(var3);
      PrismSettings.RasterizerType var4 = null;
      String var5 = var0.getProperty("prism.rasterizerorder");
      if (var5 != null) {
         label120:
         for (String var9 : split(var5.toLowerCase(), ",")) {
            switch (var9) {
               case "marlin":
               case "doublemarlin":
                  var4 = PrismSettings.RasterizerType.DoubleMarlin;
                  break label120;
            }
         }
      }

      if (var4 == null) {
         var4 = PrismSettings.RasterizerType.DoubleMarlin;
      }

      rasterizerSpec = var4;
      String var12 = var0.getProperty("prism.primtextures");
      if (var12 == null) {
         primTextureSize = PlatformUtil.isEmbedded() ? -1 : 0;
      } else if (var12.equals("true")) {
         primTextureSize = -1;
      } else if (var12.equals("false")) {
         primTextureSize = 0;
      } else {
         primTextureSize = parseInt(var12, 0, "Try -Dprism.primtextures=[true|false|<number>]");
      }

      refType = var0.getProperty("prism.reftype");
      forcePow2 = getBoolean(var0, "prism.forcepowerof2", false);
      noClampToZero = getBoolean(var0, "prism.noclamptozero", false);
      allowHiDPIScaling = getBoolean(var0, "prism.allowhidpi", true);
      maxVram = getLong(var0, "prism.maxvram", 536870912L, "Try -Dprism.maxvram=<long>[kKmMgG]");
      targetVram = getLong(var0, "prism.targetvram", maxVram / 8L, maxVram, "Try -Dprism.targetvram=<long>[kKmMgG]|<double(0,100)>%");
      poolStats = getBoolean(var0, "prism.poolstats", false);
      poolDebug = getBoolean(var0, "prism.pooldebug", false);
      if (verbose) {
         System.out.print("Prism pipeline init order: ");

         for (String var15 : tryOrder) {
            System.out.print(var15 + " ");
         }

         System.out.println("");
         if (var5 != null) {
            System.out.println("Requested rasterizer preference order: " + var5);
         }

         System.out.println("Using " + var4);
         printBooleanOption(dirtyOptsEnabled, "Using dirty region optimizations");
         if (primTextureSize == 0) {
            System.out.println("Not using texture mask for primitives");
         } else if (primTextureSize < 0) {
            System.out.println("Using system sized mask for primitives");
         } else {
            System.out.println("Using " + primTextureSize + " sized mask for primitives");
         }

         printBooleanOption(forcePow2, "Forcing power of 2 sizes for textures");
         printBooleanOption(!noClampToZero, "Using hardware CLAMP_TO_ZERO mode");
         printBooleanOption(allowHiDPIScaling, "Opting in for HiDPI pixel scaling");
      }

      int var14 = getInt(var0, "prism.maxTextureSize", 4096, "Try -Dprism.maxTextureSize=<number>");
      if (var14 <= 0) {
         var14 = Integer.MAX_VALUE;
      }

      maxTextureSize = var14;
      minRTTSize = getInt(var0, "prism.minrttsize", PlatformUtil.isEmbedded() ? 16 : 0, "Try -Dprism.minrttsize=<number>");
      disableRegionCaching = getBoolean(var0, "prism.disableRegionCaching", false);
      disableD3D9Ex = getBoolean(var0, "prism.disableD3D9Ex", false);
      disableEffects = getBoolean(var0, "prism.disableEffects", false);
      glyphCacheWidth = getInt(var0, "prism.glyphCacheWidth", 1024, "Try -Dprism.glyphCacheWidth=<number>");
      glyphCacheHeight = getInt(var0, "prism.glyphCacheHeight", 1024, "Try -Dprism.glyphCacheHeight=<number>");
      perfLog = var0.getProperty("sun.perflog");
      perfLogExitFlush = getBoolean(var0, "sun.perflog.fx.exitflush", false, true);
      perfLogFirstPaintFlush = getBoolean(var0, "sun.perflog.fx.firstpaintflush", false, true);
      perfLogFirstPaintExit = getBoolean(var0, "sun.perflog.fx.firstpaintexit", false, true);
      superShader = getBoolean(var0, "prism.supershader", true);
      forceUploadingPainter = getBoolean(var0, "prism.forceUploadingPainter", false);
      forceAlphaTestShader = getBoolean(var0, "prism.forceAlphaTestShader", false);
      forceNonAntialiasedShape = getBoolean(var0, "prism.forceNonAntialiasedShape", false);
   }

   public static enum RasterizerType {
      DoubleMarlin("Double Precision Marlin Rasterizer");

      private String publicName;

      private RasterizerType(String var3) {
         this.publicName = var3;
      }

      @Override
      public String toString() {
         return this.publicName;
      }
   }
}
