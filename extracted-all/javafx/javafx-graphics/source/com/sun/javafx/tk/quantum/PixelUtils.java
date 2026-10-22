package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Pixels;
import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.image.impl.ByteRgb;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

class PixelUtils {
   private static ImageFormatDescription[] supportedFormats = ImageStorage.getSupportedDescriptions();

   private PixelUtils() {
   }

   protected static boolean supportedFormatType(String var0) {
      for (ImageFormatDescription var4 : supportedFormats) {
         for (String var6 : var4.getExtensions()) {
            if (var0.endsWith(var6)) {
               return true;
            }
         }
      }

      return false;
   }

   public static Pixels imageToPixels(Image var0) {
      PixelFormat.DataType var1 = var0.getDataType();
      Application var2 = Application.GetApplication();
      int var3 = Pixels.getNativeFormat();
      if (var1 == PixelFormat.DataType.BYTE) {
         ByteBuffer var10 = (ByteBuffer)var0.getPixelBuffer();
         int var6 = var0.getWidth();
         int var7 = var0.getHeight();
         int var8 = var0.getScanlineStride();
         if (var0.getBytesPerPixelUnit() == 3) {
            switch (var3) {
               case 1:
                  byte[] var11 = new byte[var6 * var7 * 4];
                  ByteRgb.ToByteBgraPreConverter().convert(var10, 0, var8, var11, 0, var6 * 4, var6, var7);
                  var10 = ByteBuffer.wrap(var11);
                  break;
               case 2:
                  byte[] var9 = new byte[var6 * var7 * 4];
                  ByteRgb.ToByteArgbConverter().convert(var10, 0, var8, var9, 0, var6 * 4, var6, var7);
                  var10 = ByteBuffer.wrap(var9);
                  break;
               default:
                  throw new IllegalArgumentException("unhandled native format: " + var3);
            }
         } else if (var0.getPixelFormat() != PixelFormat.BYTE_BGRA_PRE) {
            throw new IllegalArgumentException("non-RGB image format");
         }

         return var2.createPixels(var0.getWidth(), var0.getHeight(), var10);
      } else if (var1 == PixelFormat.DataType.INT) {
         if (ByteOrder.nativeOrder() != ByteOrder.LITTLE_ENDIAN) {
            throw new UnsupportedOperationException("INT_ARGB_PRE only supported for LITTLE_ENDIAN machines");
         } else {
            IntBuffer var5 = (IntBuffer)var0.getPixelBuffer();
            return var2.createPixels(var0.getWidth(), var0.getHeight(), var5);
         }
      } else {
         throw new IllegalArgumentException("unhandled image type: " + var1);
      }
   }

   public static Image pixelsToImage(Pixels var0) {
      Buffer var1 = var0.getPixels();
      if (var0.getBytesPerComponent() == 1) {
         ByteBuffer var3 = ByteBuffer.allocateDirect(var1.capacity());
         var3.put((ByteBuffer)var1);
         var3.rewind();
         return Image.fromByteBgraPreData(var3, var0.getWidth(), var0.getHeight());
      } else if (var0.getBytesPerComponent() == 4) {
         IntBuffer var2 = IntBuffer.allocate(var1.capacity());
         var2.put((IntBuffer)var1);
         var2.rewind();
         return Image.fromIntArgbPreData((IntBuffer)var1, var0.getWidth(), var0.getHeight());
      } else {
         throw new IllegalArgumentException("unhandled pixel buffer: " + var1.getClass().getName());
      }
   }
}
