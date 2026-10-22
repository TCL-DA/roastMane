package com.sun.javafx.tk.quantum;

import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.iio.ImageLoadListener;
import com.sun.javafx.iio.ImageMetadata;
import com.sun.javafx.iio.ImageStorage;
import com.sun.javafx.iio.ImageStorageException;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.runtime.async.AbstractRemoteResource;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
import com.sun.prism.Image;
import com.sun.prism.impl.PrismSettings;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class PrismImageLoader2 implements ImageLoader {
   private static PlatformLogger imageioLogger = null;
   private Image[] images;
   private int[] delayTimes;
   private int loopCount;
   private double width;
   private double height;
   private float pixelScale;
   private Exception exception;

   public PrismImageLoader2(String var1, double var2, double var4, boolean var6, float var7, boolean var8) {
      this.loadAll(var1, var2, var4, var6, var7, var8);
   }

   public PrismImageLoader2(InputStream var1, double var2, double var4, boolean var6, boolean var7) {
      this.loadAll(var1, var2, var4, var6, var7);
   }

   @Override
   public double getWidth() {
      return this.width;
   }

   @Override
   public double getHeight() {
      return this.height;
   }

   @Override
   public int getFrameCount() {
      return this.images == null ? 0 : this.images.length;
   }

   @Override
   public PlatformImage getFrame(int var1) {
      return this.images == null ? null : this.images[var1];
   }

   @Override
   public int getFrameDelay(int var1) {
      return this.images == null ? 0 : this.delayTimes[var1];
   }

   @Override
   public int getLoopCount() {
      return this.images == null ? 0 : this.loopCount;
   }

   @Override
   public Exception getException() {
      return this.exception;
   }

   private void loadAll(String var1, double var2, double var4, boolean var6, float var7, boolean var8) {
      PrismImageLoader2.PrismLoadListener var9 = new PrismImageLoader2.PrismLoadListener();

      try {
         ImageFrame[] var10 = ImageStorage.loadAll(var1, var9, var2, var4, var6, var7, var8);
         this.convertAll(var10);
      } catch (ImageStorageException var11) {
         this.handleException(var11);
      } catch (Exception var12) {
         this.handleException(var12);
      }
   }

   private void loadAll(InputStream var1, double var2, double var4, boolean var6, boolean var7) {
      PrismImageLoader2.PrismLoadListener var8 = new PrismImageLoader2.PrismLoadListener();

      try {
         ImageFrame[] var9 = ImageStorage.loadAll(var1, var8, var2, var4, var6, 1.0F, var7);
         this.convertAll(var9);
      } catch (ImageStorageException var10) {
         this.handleException(var10);
      } catch (Exception var11) {
         this.handleException(var11);
      }
   }

   private void handleException(ImageStorageException var1) {
      Throwable var2 = var1.getCause();
      if (var2 instanceof Exception) {
         this.handleException((Exception)var2);
      } else {
         this.handleException((Exception)var1);
      }
   }

   private void handleException(Exception var1) {
      if (PrismSettings.verbose) {
         var1.printStackTrace(System.err);
      }

      this.exception = var1;
   }

   private void convertAll(ImageFrame[] var1) {
      int var2 = var1.length;
      this.images = new Image[var2];
      this.delayTimes = new int[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         ImageFrame var4 = var1[var3];
         this.images[var3] = Image.convertImageFrame(var4);
         ImageMetadata var5 = var4.getMetadata();
         if (var5 != null) {
            Integer var6 = var5.delayTime;
            if (var6 != null) {
               this.delayTimes[var3] = var6;
            }

            Integer var7 = var5.loopCount;
            if (var7 != null) {
               this.loopCount = var7;
            }
         }

         if (var3 == 0) {
            this.width = (double)var4.getWidth();
            this.height = (double)var4.getHeight();
         }
      }
   }

   private static synchronized PlatformLogger getImageioLogger() {
      if (imageioLogger == null) {
         imageioLogger = PlatformLogger.getLogger("javafx.scene.image");
      }

      return imageioLogger;
   }

   static final class AsyncImageLoader extends AbstractRemoteResource<PrismImageLoader2> {
      private static final ExecutorService BG_LOADING_EXECUTOR = createExecutor();
      private final AccessControlContext acc;
      double width;
      double height;
      boolean preserveRatio;
      boolean smooth;

      public AsyncImageLoader(AsyncOperationListener<PrismImageLoader2> var1, String var2, double var3, double var5, boolean var7, boolean var8) {
         super(var2, var1);
         this.width = var3;
         this.height = var5;
         this.preserveRatio = var7;
         this.smooth = var8;
         this.acc = AccessController.getContext();
      }

      protected PrismImageLoader2 processStream(InputStream var1) throws IOException {
         return new PrismImageLoader2(var1, this.width, this.height, this.preserveRatio, this.smooth);
      }

      public PrismImageLoader2 call() throws IOException {
         try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<PrismImageLoader2>)(() -> (PrismImageLoader2)access$001(this)), this.acc);
         } catch (PrivilegedActionException var3) {
            Throwable var2 = var3.getCause();
            if (var2 instanceof IOException) {
               throw (IOException)var2;
            } else {
               throw new UndeclaredThrowableException(var2);
            }
         }
      }

      @Override
      public void start() {
         BG_LOADING_EXECUTOR.execute(this.future);
      }

      private static ExecutorService createExecutor() {
         ThreadGroup var0 = AccessController.doPrivileged(
            (PrivilegedAction<ThreadGroup>)(() -> new ThreadGroup(QuantumToolkit.getFxUserThread().getThreadGroup(), "Background image loading thread pool"))
         );
         ThreadFactory var1 = var1x -> AccessController.doPrivileged((PrivilegedAction<Thread>)(() -> {
               Thread var2x = new Thread(var0, var1x);
               var2x.setPriority(1);
               return var2x;
            }));
         ExecutorService var2 = Executors.newCachedThreadPool(var1);
         ((ThreadPoolExecutor)var2).setKeepAliveTime(1L, TimeUnit.SECONDS);
         return var2;
      }
   }

   private class PrismLoadListener implements ImageLoadListener {
      @Override
      public void imageLoadWarning(com.sun.javafx.iio.ImageLoader var1, String var2) {
         PrismImageLoader2.getImageioLogger().warning(var2);
      }

      @Override
      public void imageLoadProgress(com.sun.javafx.iio.ImageLoader var1, float var2) {
      }

      @Override
      public void imageLoadMetaData(com.sun.javafx.iio.ImageLoader var1, ImageMetadata var2) {
      }
   }
}
