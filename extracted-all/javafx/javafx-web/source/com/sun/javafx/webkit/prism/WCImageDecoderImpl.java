package com.sun.javafx.webkit.prism;

import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageLoadListener;
import com.sun.javafx.iio.ImageLoader;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.ImageStorageException;
import com.sun.javafx.iio.ImageStorage.ImageType;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCImage;
import com.sun.webkit.graphics.WCImageDecoder;
import com.sun.webkit.graphics.WCImageFrame;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

final class WCImageDecoderImpl extends WCImageDecoder {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCImageDecoderImpl.class.getName());
   private Service<ImageFrame[]> loader;
   private int imageWidth = 0;
   private int imageHeight = 0;
   private ImageFrame[] frames;
   private int frameCount = 0;
   private boolean fullDataReceived = false;
   private boolean framesDecoded = false;
   private PrismImage[] images;
   private volatile byte[] data;
   private volatile int dataSize = 0;
   private String fileNameExtension;
   private final ImageLoadListener readerListener = new ImageLoadListener() {
      public void imageLoadProgress(ImageLoader var1, float var2) {
      }

      public void imageLoadWarning(ImageLoader var1, String var2) {
      }

      public void imageLoadMetaData(ImageLoader var1, ImageMetadata var2) {
         if (WCImageDecoderImpl.log.isLoggable(Level.FINE)) {
            WCImageDecoderImpl.log.fine(String.format("%X Image size %dx%d", this.hashCode(), var2.imageWidth, var2.imageHeight));
         }

         if (WCImageDecoderImpl.this.imageWidth < var2.imageWidth) {
            WCImageDecoderImpl.this.imageWidth = var2.imageWidth;
         }

         if (WCImageDecoderImpl.this.imageHeight < var2.imageHeight) {
            WCImageDecoderImpl.this.imageHeight = var2.imageHeight;
         }

         WCImageDecoderImpl.this.fileNameExtension = (String)var1.getFormatDescription().getExtensions().get(0);
      }
   };
   private static final ThreadLocal<int[]> THREAD_LOCAL_SIZE_ARRAY = new ThreadLocal<int[]>() {
      protected int[] initialValue() {
         return new int[2];
      }
   };

   @Override
   protected synchronized void destroy() {
      if (log.isLoggable(Level.FINE)) {
         log.fine(String.format("%X Destroy image decoder", this.hashCode()));
      }

      this.destroyLoader();
      this.frames = null;
      this.images = null;
      this.framesDecoded = false;
   }

   @Override
   protected String getFilenameExtension() {
      return "." + this.fileNameExtension;
   }

   private boolean imageSizeAvilable() {
      return this.imageWidth > 0 && this.imageHeight > 0;
   }

   @Override
   protected void addImageData(byte[] var1) {
      if (var1 != null) {
         this.fullDataReceived = false;
         if (this.data == null) {
            this.data = Arrays.copyOf(var1, var1.length * 2);
            this.dataSize = var1.length;
         } else {
            int var2 = this.dataSize + var1.length;
            if (var2 > this.data.length) {
               this.resizeDataArray(Math.max(var2, this.data.length * 2));
            }

            System.arraycopy(var1, 0, this.data, this.dataSize, var1.length);
            this.dataSize = var2;
         }

         if (!this.imageSizeAvilable()) {
            this.loadFrames();
         }
      } else if (this.data != null && !this.fullDataReceived) {
         if (this.data.length > this.dataSize) {
            this.resizeDataArray(this.dataSize);
         }

         this.fullDataReceived = true;
      }
   }

   private void destroyLoader() {
      if (this.loader != null) {
         this.loader.cancel();
         this.loader = null;
      }
   }

   private void startLoader() {
      if (this.loader == null) {
         this.loader = new Service<ImageFrame[]>() {
            protected Task<ImageFrame[]> createTask() {
               return new Task<ImageFrame[]>() {
                  protected ImageFrame[] call() throws Exception {
                     return WCImageDecoderImpl.this.loadFrames();
                  }
               };
            }
         };
         this.loader.valueProperty().addListener((var1, var2, var3) -> {
            if (var3 != null && this.loader != null) {
               this.setFrames(var3);
            }
         });
      }

      if (!this.loader.isRunning()) {
         this.loader.restart();
      }
   }

   private void resizeDataArray(int var1) {
      byte[] var2 = new byte[var1];
      System.arraycopy(this.data, 0, var2, 0, this.dataSize);
      this.data = var2;
   }

   @Override
   protected void loadFromResource(String var1) {
      if (log.isLoggable(Level.FINE)) {
         log.fine(String.format("%X Load image from resource '%s'", this.hashCode(), var1));
      }

      String var2 = WCGraphicsManager.getResourceName(var1);
      InputStream var3 = this.getClass().getResourceAsStream(var2);
      if (var3 == null) {
         if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("%X Unable to open resource '%s'", this.hashCode(), var2));
         }
      } else {
         this.setFrames(this.loadFrames(var3));
      }
   }

   private synchronized ImageFrame[] loadFrames(InputStream var1) {
      if (log.isLoggable(Level.FINE)) {
         log.fine(String.format("%X Decoding frames", this.hashCode()));
      }

      Object var3;
      try {
         return ImageStorage.loadAll(var1, this.readerListener, 0.0, 0.0, true, 1.0F, false);
      } catch (ImageStorageException var13) {
         var3 = null;
      } finally {
         try {
            var1.close();
         } catch (IOException var12) {
         }
      }

      return (ImageFrame[])var3;
   }

   private ImageFrame[] loadFrames() {
      return this.loadFrames(new ByteArrayInputStream(this.data, 0, this.dataSize));
   }

   @Override
   protected int[] getImageSize() {
      int[] var1 = THREAD_LOCAL_SIZE_ARRAY.get();
      var1[0] = this.imageWidth;
      var1[1] = this.imageHeight;
      if (log.isLoggable(Level.FINE)) {
         log.fine(String.format("%X image size = %dx%d", this.hashCode(), var1[0], var1[1]));
      }

      return var1;
   }

   private synchronized void setFrames(ImageFrame[] var1) {
      this.frames = var1;
      this.images = null;
      this.frameCount = var1 == null ? 0 : var1.length;
   }

   @Override
   protected int getFrameCount() {
      if (this.fullDataReceived) {
         this.getImageFrame(0);
      }

      return this.frameCount;
   }

   @Override
   protected synchronized WCImageFrame getFrame(int var1) {
      ImageFrame var2 = this.getImageFrame(var1);
      if (var2 != null) {
         if (log.isLoggable(Level.FINE)) {
            ImageType var3 = var2.getImageType();
            log.fine(String.format("%X getFrame(%d): image type = %s", this.hashCode(), var1, var3));
         }

         PrismImage var4 = this.getPrismImage(var1, var2);
         return new WCImageDecoderImpl.Frame(var4, this.fileNameExtension);
      } else {
         if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("%X FAILED getFrame(%d)", this.hashCode(), var1));
         }

         return null;
      }
   }

   private synchronized ImageMetadata getFrameMetadata(int var1) {
      return this.frames != null && this.frames.length > var1 && this.frames[var1] != null ? this.frames[var1].getMetadata() : null;
   }

   @Override
   protected int getFrameDuration(int var1) {
      ImageMetadata var2 = this.getFrameMetadata(var1);
      int var3 = var2 != null && var2.delayTime != null ? var2.delayTime : 0;
      if (var3 < 11) {
         var3 = 100;
      }

      return var3;
   }

   @Override
   protected int[] getFrameSize(int var1) {
      ImageMetadata var2 = this.getFrameMetadata(var1);
      if (var2 == null) {
         return null;
      } else {
         int[] var3 = THREAD_LOCAL_SIZE_ARRAY.get();
         var3[0] = var2.imageWidth;
         var3[1] = var2.imageHeight;
         return var3;
      }
   }

   @Override
   protected synchronized boolean getFrameCompleteStatus(int var1) {
      return this.getFrameMetadata(var1) != null && this.framesDecoded;
   }

   private synchronized ImageFrame getImageFrame(int var1) {
      if (!this.fullDataReceived) {
         this.startLoader();
      } else if (this.fullDataReceived && !this.framesDecoded) {
         this.destroyLoader();
         this.setFrames(this.loadFrames());
         this.framesDecoded = true;
      }

      return var1 >= 0 && this.frames != null && this.frames.length > var1 ? this.frames[var1] : null;
   }

   private synchronized PrismImage getPrismImage(int var1, ImageFrame var2) {
      if (this.images == null) {
         this.images = new PrismImage[this.frames.length];
      }

      if (this.images[var1] == null) {
         this.images[var1] = new WCImageImpl(var2);
      }

      return this.images[var1];
   }

   private static final class Frame extends WCImageFrame {
      private WCImage image;

      private Frame(WCImage var1, String var2) {
         this.image = var1;
         this.image.setFileExtension(var2);
      }

      @Override
      public WCImage getFrame() {
         return this.image;
      }

      @Override
      public int[] getSize() {
         int[] var1 = WCImageDecoderImpl.THREAD_LOCAL_SIZE_ARRAY.get();
         var1[0] = this.image.getWidth();
         var1[1] = this.image.getHeight();
         return var1;
      }

      @Override
      protected void destroyDecodedData() {
         this.image = null;
      }
   }
}
