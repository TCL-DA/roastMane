package com.sun.media.jfxmediaimpl.platform;

import com.sun.javafx.PlatformUtil;
import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.MetadataParser;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmediaimpl.platform.java.JavaPlatform;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class PlatformManager {
   private static String enabledPlatforms;
   private final List<Platform> platforms = new ArrayList<>();

   private static void getPlatformSettings() {
      enabledPlatforms = System.getProperty("jfxmedia.platforms", "").toLowerCase();
   }

   private static boolean isPlatformEnabled(String var0) {
      return null != enabledPlatforms && enabledPlatforms.length() != 0 ? enabledPlatforms.indexOf(var0.toLowerCase()) != -1 : true;
   }

   public static PlatformManager getManager() {
      return PlatformManager.PlatformManagerInitializer.globalInstance;
   }

   private PlatformManager() {
      if (isPlatformEnabled("JavaPlatform")) {
         Platform var1 = JavaPlatform.getPlatformInstance();
         if (null != var1) {
            this.platforms.add(var1);
         }
      }

      if (!HostUtils.isIOS() && isPlatformEnabled("GSTPlatform")) {
         Platform var5 = this.getPlatformInstance("com.sun.media.jfxmediaimpl.platform.gstreamer.GSTPlatform");
         if (null != var5) {
            this.platforms.add(var5);
         }
      }

      if (HostUtils.isMacOSX() && isPlatformEnabled("OSXPlatform")) {
         Platform var6 = this.getPlatformInstance("com.sun.media.jfxmediaimpl.platform.osx.OSXPlatform");
         if (null != var6) {
            this.platforms.add(var6);
         }
      }

      if (HostUtils.isIOS() && isPlatformEnabled("IOSPlatform")) {
         Platform var7 = this.getPlatformInstance("com.sun.media.jfxmediaimpl.platform.ios.IOSPlatform");
         if (null != var7) {
            this.platforms.add(var7);
         }
      }

      if (Logger.canLog(1)) {
         StringBuilder var2 = new StringBuilder("Enabled JFXMedia platforms: ");

         for (Platform var4 : this.platforms) {
            var2.append("\n   - ");
            var2.append(var4.getClass().getName());
         }

         Logger.logMsg(1, var2.toString());
      }
   }

   private Platform getPlatformInstance(String var1) {
      try {
         Class var2 = Class.forName(var1);
         Method var3 = var2.getDeclaredMethod("getPlatformInstance", (Class<?>[])null);
         Object var4 = var3.invoke(null, (Object[])null);
         return (Platform)var4;
      } catch (Exception var5) {
         if (Logger.canLog(1)) {
            Logger.logMsg(1, "Failed to get platform instance for " + var1 + ". Exception: " + var5.getMessage());
         }

         return null;
      }
   }

   public synchronized void loadPlatforms() {
      Iterator var1 = this.platforms.iterator();

      while (var1.hasNext()) {
         Platform var2 = (Platform)var1.next();
         if (!var2.loadPlatform()) {
            if (Logger.canLog(1)) {
               Logger.logMsg(1, "Failed to load platform: " + var2);
            }

            var1.remove();
         }
      }
   }

   public List<String> getSupportedContentTypes() {
      ArrayList var1 = new ArrayList();
      if (!this.platforms.isEmpty()) {
         for (Platform var3 : this.platforms) {
            if (Logger.canLog(1)) {
               Logger.logMsg(1, "Getting content types from platform: " + var3);
            }

            String[] var4 = var3.getSupportedContentTypes();
            if (var4 != null) {
               for (String var8 : var4) {
                  if (!var1.contains(var8)) {
                     var1.add(var8);
                  }
               }
            }
         }
      }

      return var1;
   }

   public List<String> getSupportedProtocols() {
      ArrayList var1 = new ArrayList();
      if (!this.platforms.isEmpty()) {
         for (Platform var3 : this.platforms) {
            String[] var4 = var3.getSupportedProtocols();
            if (var4 != null) {
               for (String var8 : var4) {
                  if (!var1.contains(var8)) {
                     var1.add(var8);
                  }
               }
            }
         }
      }

      if (PlatformUtil.isStaticBuild()) {
         var1.add("resource");
      }

      return var1;
   }

   public MetadataParser createMetadataParser(Locator var1) {
      for (Platform var3 : this.platforms) {
         MetadataParser var4 = var3.createMetadataParser(var1);
         if (var4 != null) {
            return var4;
         }
      }

      return null;
   }

   public Media createMedia(Locator var1) {
      String var2 = var1.getContentType();
      String var3 = var1.getProtocol();

      for (Platform var5 : this.platforms) {
         if (var5.canPlayContentType(var2) && var5.canPlayProtocol(var3)) {
            Media var6 = var5.createMedia(var1);
            if (null != var6) {
               return var6;
            }
         }
      }

      return null;
   }

   public MediaPlayer createMediaPlayer(Locator var1) {
      String var2 = var1.getContentType();
      String var3 = var1.getProtocol();

      for (Platform var5 : this.platforms) {
         if (var5.canPlayContentType(var2) && var5.canPlayProtocol(var3)) {
            MediaPlayer var6 = var5.createMediaPlayer(var1);
            if (null != var6) {
               return var6;
            }
         }
      }

      return null;
   }

   static {
      Object var0 = AccessController.doPrivileged((PrivilegedAction)(() -> {
         getPlatformSettings();
         return null;
      }));
   }

   private static final class PlatformManagerInitializer {
      private static final PlatformManager globalInstance = new PlatformManager();
   }
}
