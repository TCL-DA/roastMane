package com.sun.media.jfxmediaimpl.platform.gstreamer;

import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import com.sun.media.jfxmediaimpl.HostUtils;
import com.sun.media.jfxmediaimpl.MediaUtils;
import com.sun.media.jfxmediaimpl.platform.Platform;
import java.util.Arrays;

public final class GSTPlatform extends Platform {
   private static final String[] CONTENT_TYPES = new String[]{
      "audio/x-aiff", "audio/mp3", "audio/mpeg", "audio/x-wav", "video/mp4", "audio/x-m4a", "video/x-m4v", "application/vnd.apple.mpegurl", "audio/mpegurl"
   };
   private static final String[] PROTOCOLS = new String[]{"file", "http", "https", "jrt", "resource"};
   private static GSTPlatform globalInstance = null;

   @Override
   public boolean loadPlatform() {
      MediaError var1;
      try {
         var1 = MediaError.getFromCode(gstInitPlatform());
      } catch (UnsatisfiedLinkError var3) {
         var1 = MediaError.ERROR_MANAGER_ENGINEINIT_FAIL;
      }

      if (var1 != MediaError.ERROR_NONE) {
         MediaUtils.nativeError(GSTPlatform.class, var1);
      }

      return true;
   }

   public static synchronized Platform getPlatformInstance() {
      if (null == globalInstance) {
         globalInstance = new GSTPlatform();
      }

      return globalInstance;
   }

   private GSTPlatform() {
   }

   @Override
   public String[] getSupportedContentTypes() {
      return Arrays.copyOf(CONTENT_TYPES, CONTENT_TYPES.length);
   }

   @Override
   public String[] getSupportedProtocols() {
      return Arrays.copyOf(PROTOCOLS, PROTOCOLS.length);
   }

   @Override
   public Media createMedia(Locator var1) {
      return new GSTMedia(var1);
   }

   @Override
   public MediaPlayer createMediaPlayer(Locator var1) {
      GSTMediaPlayer var2;
      try {
         var2 = new GSTMediaPlayer(var1);
      } catch (Exception var15) {
         if (Logger.canLog(1)) {
            Logger.logMsg(1, "GSTPlatform caught exception while creating media player: " + var15);
         }

         return null;
      }

      if (HostUtils.isMacOSX()) {
         String var3 = var1.getContentType();
         if ("video/mp4".equals(var3) || "video/x-m4v".equals(var3) || var1.getStringLocation().endsWith(".m3u8")) {
            String var4 = var1.getURI().getScheme();
            long var5 = !var4.equals("http") && !var4.equals("https") ? 5000L : 60000L;
            long var7 = 0L;
            Object var9 = new Object();

            for (PlayerStateEvent.PlayerState var10 = var2.getState();
               var7 < var5 && (var10 == PlayerStateEvent.PlayerState.UNKNOWN || var10 == PlayerStateEvent.PlayerState.STALLED);
               var10 = var2.getState()
            ) {
               try {
                  synchronized (var9) {
                     var9.wait(50L);
                     var7 += 50L;
                  }
               } catch (InterruptedException var14) {
               }

               if (var2.isErrorEventCached()) {
                  break;
               }
            }

            if (var2.getState() != PlayerStateEvent.PlayerState.READY) {
               var2.dispose();
               var2 = null;
            }
         }
      }

      return var2;
   }

   private static native int gstInitPlatform();
}
