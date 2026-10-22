package com.sun.media.jfxmedia.control;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum VideoFormat {
   ARGB(1),
   BGRA_PRE(2),
   YCbCr_420p(100),
   YCbCr_422(101);

   private int nativeType;
   private static final Map<Integer, VideoFormat> lookupMap = new HashMap<>();

   private VideoFormat(int var3) {
      this.nativeType = var3;
   }

   public int getNativeType() {
      return this.nativeType;
   }

   public boolean isRGB() {
      return this == ARGB || this == BGRA_PRE;
   }

   public boolean isEqualTo(int var1) {
      return this.nativeType == var1;
   }

   public static VideoFormat formatForType(int var0) {
      return lookupMap.get(var0);
   }

   static {
      for (VideoFormat var1 : EnumSet.allOf(VideoFormat.class)) {
         lookupMap.put(var1.getNativeType(), var1);
      }
   }

   public static class FormatTypes {
      public static final int FORMAT_TYPE_ARGB = 1;
      public static final int FORMAT_TYPE_BGRA_PRE = 2;
      public static final int FORMAT_TYPE_YCBCR_420P = 100;
      public static final int FORMAT_TYPE_YCBCR_422 = 101;
   }
}
