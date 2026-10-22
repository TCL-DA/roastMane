package com.sun.javafx.iio;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.iio.bmp.BMPImageLoaderFactory;
import com.sun.javafx.iio.common.ImageTools;
import com.sun.javafx.iio.gif.GIFImageLoaderFactory;
import com.sun.javafx.iio.ios.IosImageLoaderFactory;
import com.sun.javafx.iio.jpeg.JPEGImageLoaderFactory;
import com.sun.javafx.iio.png.PNGImageLoaderFactory;
import com.sun.javafx.util.DataURI;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class ImageStorage {
   private static final HashMap<ImageFormatDescription.Signature, ImageLoaderFactory> loaderFactoriesBySignature;
   private static final ImageLoaderFactory[] loaderFactories;
   private static final boolean isIOS = PlatformUtil.isIOS();
   private static int maxSignatureLength;

   public static ImageFormatDescription[] getSupportedDescriptions() {
      ImageFormatDescription[] var0 = new ImageFormatDescription[loaderFactories.length];

      for (int var1 = 0; var1 < loaderFactories.length; var1++) {
         var0[var1] = loaderFactories[var1].getFormatDescription();
      }

      return var0;
   }

   public static int getNumBands(ImageStorage.ImageType var0) {
      byte var1 = -1;
      switch (var0) {
         case GRAY:
         case PALETTE:
         case PALETTE_ALPHA:
         case PALETTE_ALPHA_PRE:
         case PALETTE_TRANS:
            var1 = 1;
            break;
         case GRAY_ALPHA:
         case GRAY_ALPHA_PRE:
            var1 = 2;
            break;
         case RGB:
            var1 = 3;
            break;
         case RGBA:
         case RGBA_PRE:
            var1 = 4;
            break;
         default:
            throw new IllegalArgumentException("Unknown ImageType " + var0);
      }

      return var1;
   }

   public static void addImageLoaderFactory(ImageLoaderFactory var0) {
      ImageFormatDescription var1 = var0.getFormatDescription();

      for (ImageFormatDescription.Signature var3 : var1.getSignatures()) {
         loaderFactoriesBySignature.put(var3, var0);
      }

      synchronized (ImageStorage.class) {
         maxSignatureLength = -1;
      }
   }

   public static ImageFrame[] loadAll(InputStream var0, ImageLoadListener var1, double var2, double var4, boolean var6, float var7, boolean var8) throws ImageStorageException {
      ImageLoader var9 = null;
      ImageFrame var10 = null;

      try {
         if (isIOS) {
            var9 = IosImageLoaderFactory.getInstance().createImageLoader(var0);
         } else {
            var9 = getLoaderBySignature(var0, var1);
         }

         if (var9 == null) {
            throw new ImageStorageException("No loader for image data");
         }

         var10 = loadAll(var9, var2, var4, var6, var7, var8);
      } catch (ImageStorageException var16) {
         throw var16;
      } catch (IOException var17) {
         throw new ImageStorageException(var17.getMessage(), var17);
      } finally {
         if (var9 != null) {
            var9.dispose();
         }
      }

      return var10;
   }

   public static ImageFrame[] loadAll(String var0, ImageLoadListener var1, double var2, double var4, boolean var6, float var7, boolean var8) throws ImageStorageException {
      if (var0 != null && !var0.isEmpty()) {
         ImageFrame var9 = null;
         Object var10 = null;
         ImageLoader var11 = null;

         try {
            float var12 = 1.0F;

            try {
               if (var7 >= 1.5F) {
                  try {
                     String var13 = ImageTools.getScaledImageName(var0);
                     var10 = ImageTools.createInputStream(var13);
                     var12 = 2.0F;
                  } catch (IOException var25) {
                  }
               }

               if (var10 == null) {
                  try {
                     var10 = ImageTools.createInputStream(var0);
                  } catch (IOException var26) {
                     DataURI var14 = DataURI.tryParse(var0);
                     if (var14 == null) {
                        throw var26;
                     }

                     String var15 = var14.getMimeType();
                     if (var15 != null && !"image".equalsIgnoreCase(var14.getMimeType())) {
                        throw new IllegalArgumentException("Unexpected MIME type: " + var14.getMimeType());
                     }

                     var10 = new ByteArrayInputStream(var14.getData());
                  }
               }

               if (isIOS) {
                  var11 = IosImageLoaderFactory.getInstance().createImageLoader((InputStream)var10);
               } else {
                  var11 = getLoaderBySignature((InputStream)var10, var1);
               }
            } catch (Exception var27) {
               throw new ImageStorageException(var27.getMessage(), var27);
            }

            if (var11 == null) {
               throw new ImageStorageException("No loader for image data");
            }

            var9 = loadAll(var11, var2, var4, var6, var12, var8);
         } finally {
            if (var11 != null) {
               var11.dispose();
            }

            try {
               if (var10 != null) {
                  var10.close();
               }
            } catch (IOException var24) {
            }
         }

         return var9;
      } else {
         throw new ImageStorageException("URL can't be null or empty");
      }
   }

   private static synchronized int getMaxSignatureLength() {
      if (maxSignatureLength < 0) {
         maxSignatureLength = 0;

         for (ImageFormatDescription.Signature var1 : loaderFactoriesBySignature.keySet()) {
            int var2 = var1.getLength();
            if (maxSignatureLength < var2) {
               maxSignatureLength = var2;
            }
         }
      }

      return maxSignatureLength;
   }

   private static ImageFrame[] loadAll(ImageLoader var0, double var1, double var3, boolean var5, float var6, boolean var7) throws ImageStorageException {
      ImageFrame[] var8 = null;
      ArrayList var9 = new ArrayList();
      int var10 = 0;
      ImageFrame var11 = null;
      int var12 = (int)Math.round(var1 * (double)var6);
      int var13 = (int)Math.round(var3 * (double)var6);

      while (true) {
         try {
            var11 = var0.load(var10++, var12, var13, var5, var7);
         } catch (Exception var15) {
            if (var10 > 1) {
               break;
            }

            throw new ImageStorageException(var15.getMessage(), var15);
         }

         if (var11 == null) {
            break;
         }

         var11.setPixelScale(var6);
         var9.add(var11);
      }

      int var14 = var9.size();
      if (var14 > 0) {
         var8 = new ImageFrame[var14];
         var9.toArray(var8);
      }

      return var8;
   }

   private static ImageLoader getLoaderBySignature(InputStream var0, ImageLoadListener var1) throws IOException {
      byte[] var2 = new byte[getMaxSignatureLength()];
      ImageTools.readFully(var0, var2);

      for (Entry var4 : loaderFactoriesBySignature.entrySet()) {
         if (((ImageFormatDescription.Signature)var4.getKey()).matches(var2)) {
            ByteArrayInputStream var5 = new ByteArrayInputStream(var2);
            SequenceInputStream var6 = new SequenceInputStream(var5, var0);
            ImageLoader var7 = ((ImageLoaderFactory)var4.getValue()).createImageLoader(var6);
            if (var1 != null) {
               var7.addListener(var1);
            }

            return var7;
         }
      }

      return null;
   }

   private ImageStorage() {
   }

   static {
      if (isIOS) {
         loaderFactories = new ImageLoaderFactory[]{IosImageLoaderFactory.getInstance()};
      } else {
         loaderFactories = new ImageLoaderFactory[]{
            GIFImageLoaderFactory.getInstance(), JPEGImageLoaderFactory.getInstance(), PNGImageLoaderFactory.getInstance(), BMPImageLoaderFactory.getInstance()
         };
      }

      loaderFactoriesBySignature = new HashMap<>(loaderFactories.length);

      for (int var0 = 0; var0 < loaderFactories.length; var0++) {
         addImageLoaderFactory(loaderFactories[var0]);
      }
   }

   public static enum ImageType {
      GRAY,
      GRAY_ALPHA,
      GRAY_ALPHA_PRE,
      PALETTE,
      PALETTE_ALPHA,
      PALETTE_ALPHA_PRE,
      PALETTE_TRANS,
      RGB,
      RGBA,
      RGBA_PRE;
   }
}
