package com.sun.media.jfxmediaimpl.platform.java;

import com.sun.media.jfxmedia.Media;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.MetadataParser;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmediaimpl.platform.Platform;

public final class JavaPlatform extends Platform {
   private static JavaPlatform globalInstance = null;

   public static synchronized Platform getPlatformInstance() {
      if (null == globalInstance) {
         globalInstance = new JavaPlatform();
      }

      return globalInstance;
   }

   JavaPlatform() {
   }

   @Override
   public boolean loadPlatform() {
      return true;
   }

   @Override
   public MetadataParser createMetadataParser(Locator var1) {
      String var2 = var1.getContentType();
      return !var2.equals("audio/mpeg") && !var2.equals("audio/mp3") ? null : new ID3MetadataParser(var1);
   }

   @Override
   public Media createMedia(Locator var1) {
      return null;
   }

   @Override
   public MediaPlayer createMediaPlayer(Locator var1) {
      return null;
   }
}
