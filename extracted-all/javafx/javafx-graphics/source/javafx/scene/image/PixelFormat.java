package javafx.scene.image;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public abstract class PixelFormat<T extends Buffer> {
   private PixelFormat.Type type;

   PixelFormat(PixelFormat.Type var1) {
      this.type = var1;
   }

   public static WritablePixelFormat<IntBuffer> getIntArgbInstance() {
      return WritablePixelFormat.IntArgb.INSTANCE;
   }

   public static WritablePixelFormat<IntBuffer> getIntArgbPreInstance() {
      return WritablePixelFormat.IntArgbPre.INSTANCE;
   }

   public static WritablePixelFormat<ByteBuffer> getByteBgraInstance() {
      return WritablePixelFormat.ByteBgra.INSTANCE;
   }

   public static WritablePixelFormat<ByteBuffer> getByteBgraPreInstance() {
      return WritablePixelFormat.ByteBgraPre.INSTANCE;
   }

   public static PixelFormat<ByteBuffer> getByteRgbInstance() {
      return PixelFormat.ByteRgb.instance;
   }

   public static PixelFormat<ByteBuffer> createByteIndexedPremultipliedInstance(int[] var0) {
      return PixelFormat.IndexedPixelFormat.createByte(var0, true);
   }

   public static PixelFormat<ByteBuffer> createByteIndexedInstance(int[] var0) {
      return PixelFormat.IndexedPixelFormat.createByte(var0, false);
   }

   public PixelFormat.Type getType() {
      return this.type;
   }

   public abstract boolean isWritable();

   public abstract boolean isPremultiplied();

   static int NonPretoPre(int var0) {
      int var1 = var0 >>> 24;
      if (var1 == 255) {
         return var0;
      } else if (var1 == 0) {
         return 0;
      } else {
         int var2 = var0 >> 16 & 0xFF;
         int var3 = var0 >> 8 & 0xFF;
         int var4 = var0 & 0xFF;
         var2 = (var2 * var1 + 127) / 255;
         var3 = (var3 * var1 + 127) / 255;
         var4 = (var4 * var1 + 127) / 255;
         return var1 << 24 | var2 << 16 | var3 << 8 | var4;
      }
   }

   static int PretoNonPre(int var0) {
      int var1 = var0 >>> 24;
      if (var1 != 255 && var1 != 0) {
         int var2 = var0 >> 16 & 0xFF;
         int var3 = var0 >> 8 & 0xFF;
         int var4 = var0 & 0xFF;
         int var5 = var1 >> 1;
         var2 = var2 >= var1 ? 255 : (var2 * 255 + var5) / var1;
         var3 = var3 >= var1 ? 255 : (var3 * 255 + var5) / var1;
         var4 = var4 >= var1 ? 255 : (var4 * 255 + var5) / var1;
         return var1 << 24 | var2 << 16 | var3 << 8 | var4;
      } else {
         return var0;
      }
   }

   public abstract int getArgb(T var1, int var2, int var3, int var4);

   static class ByteRgb extends PixelFormat<ByteBuffer> {
      static final PixelFormat.ByteRgb instance = new PixelFormat.ByteRgb();

      private ByteRgb() {
         super(PixelFormat.Type.BYTE_RGB);
      }

      @Override
      public boolean isWritable() {
         return true;
      }

      @Override
      public boolean isPremultiplied() {
         return false;
      }

      public int getArgb(ByteBuffer var1, int var2, int var3, int var4) {
         int var5 = var3 * var4 + var2 * 3;
         int var6 = var1.get(var5) & 255;
         int var7 = var1.get(var5 + 1) & 255;
         int var8 = var1.get(var5 + 2) & 255;
         return 0xFF000000 | var6 << 16 | var7 << 8 | var8;
      }
   }

   static class IndexedPixelFormat extends PixelFormat<ByteBuffer> {
      int[] precolors;
      int[] nonprecolors;
      boolean premult;

      static PixelFormat createByte(int[] var0, boolean var1) {
         return new PixelFormat.IndexedPixelFormat(PixelFormat.Type.BYTE_INDEXED, var1, Arrays.copyOf(var0, 256));
      }

      private IndexedPixelFormat(PixelFormat.Type var1, boolean var2, int[] var3) {
         super(var1);
         if (var2) {
            this.precolors = var3;
         } else {
            this.nonprecolors = var3;
         }

         this.premult = var2;
      }

      @Override
      public boolean isWritable() {
         return false;
      }

      @Override
      public boolean isPremultiplied() {
         return this.premult;
      }

      int[] getPreColors() {
         if (this.precolors == null) {
            int[] var1 = new int[this.nonprecolors.length];

            for (int var2 = 0; var2 < var1.length; var2++) {
               var1[var2] = NonPretoPre(this.nonprecolors[var2]);
            }

            this.precolors = var1;
         }

         return this.precolors;
      }

      int[] getNonPreColors() {
         if (this.nonprecolors == null) {
            int[] var1 = new int[this.precolors.length];

            for (int var2 = 0; var2 < var1.length; var2++) {
               var1[var2] = PretoNonPre(this.precolors[var2]);
            }

            this.nonprecolors = var1;
         }

         return this.nonprecolors;
      }

      public int getArgb(ByteBuffer var1, int var2, int var3, int var4) {
         return this.getNonPreColors()[var1.get(var3 * var4 + var2) & 0xFF];
      }
   }

   public static enum Type {
      INT_ARGB_PRE,
      INT_ARGB,
      BYTE_BGRA_PRE,
      BYTE_BGRA,
      BYTE_RGB,
      BYTE_INDEXED;
   }
}
