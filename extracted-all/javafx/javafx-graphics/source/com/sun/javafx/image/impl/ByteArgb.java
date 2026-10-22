package com.sun.javafx.image.impl;

import com.sun.javafx.image.AlphaType;
import com.sun.javafx.image.BytePixelAccessor;
import com.sun.javafx.image.BytePixelGetter;
import com.sun.javafx.image.BytePixelSetter;
import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;

public class ByteArgb {
   public static final BytePixelGetter getter = ByteArgb.Accessor.instance;
   public static final BytePixelSetter setter = ByteArgb.Accessor.instance;
   public static final BytePixelAccessor accessor = ByteArgb.Accessor.instance;

   static class Accessor implements BytePixelAccessor {
      static final BytePixelAccessor instance = new ByteArgb.Accessor();

      private Accessor() {
      }

      @Override
      public AlphaType getAlphaType() {
         return AlphaType.NONPREMULTIPLIED;
      }

      @Override
      public int getNumElements() {
         return 4;
      }

      @Override
      public int getArgb(byte[] var1, int var2) {
         return var1[var2] << 24 | (var1[var2 + 1] & 0xFF) << 16 | (var1[var2 + 2] & 0xFF) << 8 | var1[var2 + 3] & 0xFF;
      }

      @Override
      public int getArgbPre(byte[] var1, int var2) {
         return PixelUtils.NonPretoPre(this.getArgb(var1, var2));
      }

      public int getArgb(ByteBuffer var1, int var2) {
         return var1.get(var2) << 24 | (var1.get(var2 + 1) & 0xFF) << 16 | (var1.get(var2 + 2) & 0xFF) << 8 | var1.get(var2 + 3) & 0xFF;
      }

      public int getArgbPre(ByteBuffer var1, int var2) {
         return PixelUtils.NonPretoPre(this.getArgb(var1, var2));
      }

      @Override
      public void setArgb(byte[] var1, int var2, int var3) {
         var1[var2] = (byte)(var3 >> 24);
         var1[var2 + 1] = (byte)(var3 >> 16);
         var1[var2 + 2] = (byte)(var3 >> 8);
         var1[var2 + 3] = (byte)var3;
      }

      @Override
      public void setArgbPre(byte[] var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }

      public void setArgb(ByteBuffer var1, int var2, int var3) {
         var1.put(var2, (byte)(var3 >> 24));
         var1.put(var2 + 1, (byte)(var3 >> 16));
         var1.put(var2 + 2, (byte)(var3 >> 8));
         var1.put(var2 + 3, (byte)var3);
      }

      public void setArgbPre(ByteBuffer var1, int var2, int var3) {
         this.setArgb(var1, var2, PixelUtils.PretoNonPre(var3));
      }
   }
}
