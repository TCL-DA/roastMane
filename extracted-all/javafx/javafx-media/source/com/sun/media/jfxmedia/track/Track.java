package com.sun.media.jfxmedia.track;

import java.util.Locale;

public abstract class Track {
   private boolean trackEnabled;
   private long trackID;
   private String name;
   private Locale locale;
   private Track.Encoding encoding;

   protected Track(boolean var1, long var2, String var4, Locale var5, Track.Encoding var6) {
      if (var4 == null) {
         throw new IllegalArgumentException("name == null!");
      } else if (var6 == null) {
         throw new IllegalArgumentException("encoding == null!");
      } else {
         this.trackEnabled = var1;
         this.trackID = var2;
         this.locale = var5;
         this.encoding = var6;
         this.name = var4;
      }
   }

   public Track.Encoding getEncodingType() {
      return this.encoding;
   }

   public String getName() {
      return this.name;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public long getTrackID() {
      return this.trackID;
   }

   public boolean isEnabled() {
      return this.trackEnabled;
   }

   public static enum Encoding {
      NONE,
      PCM,
      MPEG1AUDIO,
      MPEG1LAYER3,
      AAC,
      H264,
      CUSTOM;

      public static Track.Encoding toEncoding(int var0) {
         for (Track.Encoding var4 : values()) {
            if (var4.ordinal() == var0) {
               return var4;
            }
         }

         return NONE;
      }
   }
}
