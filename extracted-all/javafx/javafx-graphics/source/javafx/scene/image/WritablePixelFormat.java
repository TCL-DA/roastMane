package javafx.scene.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public abstract class WritablePixelFormat<T extends Buffer> extends PixelFormat<T> {
   WritablePixelFormat(PixelFormat.Type var1) {
      super(var1);
   }

   @Override
   public boolean isWritable() {
      return true;
   }

   public abstract void setArgb(T var1, int var2, int var3, int var4, int var5);

   static class ByteBgra extends WritablePixelFormat<ByteBuffer> {
      static final WritablePixelFormat.ByteBgra INSTANCE = new WritablePixelFormat.ByteBgra();

      private ByteBgra() {
         super(PixelFormat.Type.BYTE_BGRA);
      }

      @Override
      public boolean isPremultiplied() {
         return false;
      }

      public int getArgb(ByteBuffer var1, int var2, int var3, int var4) {
         int var5 = var3 * var4 + var2 * 4;
         int var6 = var1.get(var5) & 255;
         int var7 = var1.get(var5 + 1) & 255;
         int var8 = var1.get(var5 + 2) & 255;
         int var9 = var1.get(var5 + 3) & 255;
         return var9 << 24 | var8 << 16 | var7 << 8 | var6;
      }

      public void setArgb(ByteBuffer var1, int var2, int var3, int var4, int var5) {
         int var6 = var3 * var4 + var2 * 4;
         var1.put(var6, (byte)var5);
         var1.put(var6 + 1, (byte)(var5 >> 8));
         var1.put(var6 + 2, (byte)(var5 >> 16));
         var1.put(var6 + 3, (byte)(var5 >> 24));
      }
   }

   static class ByteBgraPre extends WritablePixelFormat<ByteBuffer> {
      static final WritablePixelFormat.ByteBgraPre INSTANCE = new WritablePixelFormat.ByteBgraPre();

      private ByteBgraPre() {
         super(PixelFormat.Type.BYTE_BGRA_PRE);
      }

      @Override
      public boolean isPremultiplied() {
         return true;
      }

      public int getArgb(ByteBuffer var1, int var2, int var3, int var4) {
         int var5 = var3 * var4 + var2 * 4;
         int var6 = var1.get(var5) & 255;
         int var7 = var1.get(var5 + 1) & 255;
         int var8 = var1.get(var5 + 2) & 255;
         int var9 = var1.get(var5 + 3) & 255;
         if (var9 > 0 && var9 < 255) {
            int var10 = var9 >> 1;
            var8 = var8 >= var9 ? 255 : (var8 * 255 + var10) / var9;
            var7 = var7 >= var9 ? 255 : (var7 * 255 + var10) / var9;
            var6 = var6 >= var9 ? 255 : (var6 * 255 + var10) / var9;
         }

         return var9 << 24 | var8 << 16 | var7 << 8 | var6;
      }

      public void setArgb(ByteBuffer var1, int var2, int var3, int var4, int var5) {
         int var6 = var3 * var4 + var2 * 4;
         int var7 = var5 >>> 24;
         int var8;
         int var9;
         int var10;
         if (var7 > 0) {
            var8 = var5 >> 16 & 0xFF;
            var9 = var5 >> 8 & 0xFF;
            var10 = var5 & 0xFF;
            if (var7 < 255) {
               var8 = (var8 * var7 + 127) / 255;
               var9 = (var9 * var7 + 127) / 255;
               var10 = (var10 * var7 + 127) / 255;
            }
         } else {
            var10 = 0;
            var9 = 0;
            var8 = 0;
            var7 = 0;
         }

         var1.put(var6, (byte)var10);
         var1.put(var6 + 1, (byte)var9);
         var1.put(var6 + 2, (byte)var8);
         var1.put(var6 + 3, (byte)var7);
      }
   }

   static class IntArgb extends WritablePixelFormat<IntBuffer> {
      static final WritablePixelFormat.IntArgb INSTANCE = new WritablePixelFormat.IntArgb();

      private IntArgb() {
         super(PixelFormat.Type.INT_ARGB);
      }

      @Override
      public boolean isPremultiplied() {
         return false;
      }

      public int getArgb(IntBuffer var1, int var2, int var3, int var4) {
         return var1.get(var3 * var4 + var2);
      }

      public void setArgb(IntBuffer var1, int var2, int var3, int var4, int var5) {
         var1.put(var3 * var4 + var2, var5);
      }
   }

   static class IntArgbPre extends WritablePixelFormat<IntBuffer> {
      static final WritablePixelFormat.IntArgbPre INSTANCE = new WritablePixelFormat.IntArgbPre();

      private IntArgbPre() {
         super(PixelFormat.Type.INT_ARGB_PRE);
      }

      @Override
      public boolean isPremultiplied() {
         return true;
      }

      public int getArgb(IntBuffer var1, int var2, int var3, int var4) {
         return PretoNonPre(var1.get(var3 * var4 + var2));
      }

      public void setArgb(IntBuffer var1, int var2, int var3, int var4, int var5) {
         var1.put(var3 * var4 + var2, NonPretoPre(var5));
      }
   }
}
