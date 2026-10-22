package com.sun.javafx.iio.common;

import com.sun.javafx.iio.ImageFormatDescription;
import com.sun.javafx.iio.ImageLoadListener;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageMetadata;
import java.util.HashSet;

public abstract class ImageLoaderImpl implements ImageLoader {
   protected ImageFormatDescription formatDescription;
   protected HashSet<ImageLoadListener> listeners;
   protected int lastPercentDone = -1;

   protected ImageLoaderImpl(ImageFormatDescription var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("formatDescription == null!");
      } else {
         this.formatDescription = var1;
      }
   }

   @Override
   public final ImageFormatDescription getFormatDescription() {
      return this.formatDescription;
   }

   @Override
   public final void addListener(ImageLoadListener var1) {
      if (this.listeners == null) {
         this.listeners = new HashSet<>();
      }

      this.listeners.add(var1);
   }

   @Override
   public final void removeListener(ImageLoadListener var1) {
      if (this.listeners != null) {
         this.listeners.remove(var1);
      }
   }

   protected void emitWarning(String var1) {
      if (this.listeners != null && !this.listeners.isEmpty()) {
         for (ImageLoadListener var3 : this.listeners) {
            var3.imageLoadWarning(this, var1);
         }
      }
   }

   protected void updateImageProgress(float var1) {
      if (this.listeners != null && !this.listeners.isEmpty()) {
         int var2 = (int)var1;
         byte var3 = 5;
         if (var3 * var2 / var3 % var3 == 0 && var2 != this.lastPercentDone) {
            this.lastPercentDone = var2;

            for (ImageLoadListener var5 : this.listeners) {
               var5.imageLoadProgress(this, (float)var2);
            }
         }
      }
   }

   protected void updateImageMetadata(ImageMetadata var1) {
      if (this.listeners != null && !this.listeners.isEmpty()) {
         for (ImageLoadListener var3 : this.listeners) {
            var3.imageLoadMetaData(this, var1);
         }
      }
   }
}
