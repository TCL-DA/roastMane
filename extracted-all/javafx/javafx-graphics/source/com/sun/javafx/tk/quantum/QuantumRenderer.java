package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.Application;
import com.sun.glass.ui.Screen;
import com.sun.glass.ui.View;
import com.sun.javafx.tk.CompletionListener;
import com.sun.javafx.tk.RenderJob;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.GraphicsResource;
import com.sun.prism.Presentable;
import com.sun.prism.ResourceFactory;
import com.sun.prism.impl.PrismSettings;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.prism.PrFilterContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

final class QuantumRenderer extends ThreadPoolExecutor {
   private static boolean usePurgatory = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("decora.purgatory")));
   private static final AtomicReference<QuantumRenderer> instanceReference = new AtomicReference<>(null);
   private Thread _renderer;
   private Throwable _initThrowable = null;
   private CountDownLatch initLatch = new CountDownLatch(1);

   private QuantumRenderer() {
      super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
      this.setThreadFactory(new QuantumRenderer.QuantumThreadFactory());
   }

   protected Throwable initThrowable() {
      return this._initThrowable;
   }

   private void setInitThrowable(Throwable var1) {
      this._initThrowable = var1;
   }

   protected void createResourceFactory() {
      CountDownLatch var1 = new CountDownLatch(1);
      CompletionListener var2 = var1x -> var1.countDown();
      Runnable var3 = () -> {
         ResourceFactory var0 = GraphicsPipeline.getDefaultResourceFactory();

         assert var0 != null;
      };
      RenderJob var4 = new RenderJob(var3, var2);
      this.submit(var4);

      try {
         var1.await();
      } catch (Throwable var6) {
         var6.printStackTrace(System.err);
      }
   }

   protected void disposePresentable(Presentable var1) {
      assert !Thread.currentThread().equals(this._renderer);

      if (var1 instanceof GraphicsResource) {
         GraphicsResource var2 = (GraphicsResource)var1;
         Runnable var3 = () -> var2.dispose();
         RenderJob var4 = new RenderJob(var3, null);
         this.submit(var4);
      }
   }

   protected void stopRenderer() {
      AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         this.shutdown();
         return null;
      }));
      if (PrismSettings.verbose) {
         System.out.println("QuantumRenderer: shutdown");
      }

      assert this.isShutdown();

      instanceReference.set(null);
   }

   @Override
   protected <T> RunnableFuture<T> newTaskFor(Runnable var1, T var2) {
      return (RenderJob)var1;
   }

   protected Future submitRenderJob(RenderJob var1) {
      return this.submit(var1);
   }

   @Override
   public void afterExecute(Runnable var1, Throwable var2) {
      super.afterExecute(var1, var2);
      if (usePurgatory) {
         Screen var3 = Screen.getMainScreen();
         Renderer var4 = Renderer.getRenderer(PrFilterContext.getInstance(var3));
         var4.releasePurgatory();
      }
   }

   void checkRendererIdle() {
      if (PrismSettings.threadCheck) {
         PaintCollector var1 = PaintCollector.getInstance();
         boolean var2 = ViewPainter.renderLock.isLocked() && !ViewPainter.renderLock.isHeldByCurrentThread();
         if (var2) {
            System.err.println("ERROR: PrismPen / FX threads co-running: DIRTY: " + var1.hasDirty());

            for (StackTraceElement var6 : QuantumToolkit.getFxUserThread().getStackTrace()) {
               System.err.println("FX: " + var6);
            }

            for (StackTraceElement var10 : this._renderer.getStackTrace()) {
               System.err.println("QR: " + var10);
            }
         }
      }
   }

   public static synchronized QuantumRenderer getInstance() {
      if (instanceReference.get() == null) {
         synchronized (QuantumRenderer.class) {
            QuantumRenderer var1 = null;

            try {
               var1 = new QuantumRenderer();
               var1.prestartCoreThread();
               var1.initLatch.await();
            } catch (Throwable var4) {
               if (var1 != null) {
                  var1.setInitThrowable(var4);
               }

               if (PrismSettings.verbose) {
                  var4.printStackTrace();
               }
            }

            if (var1 != null && var1.initThrowable() != null) {
               if (PrismSettings.noFallback) {
                  System.err.println("Cannot initialize a graphics pipeline, and Prism fallback is disabled");
                  throw new InternalError("Could not initialize prism toolkit, and the fallback is disabled.");
               }

               throw new RuntimeException(var1.initThrowable());
            }

            instanceReference.set(var1);
         }
      }

      return instanceReference.get();
   }

   private class PipelineRunnable implements Runnable {
      private Runnable work;

      public PipelineRunnable(Runnable var2) {
         this.work = var2;
      }

      public void init() {
         try {
            if (GraphicsPipeline.createPipeline() == null) {
               String var8 = "Error initializing QuantumRenderer: no suitable pipeline found";
               System.err.println(var8);
               throw new RuntimeException(var8);
            }

            Object var1 = GraphicsPipeline.getPipeline().getDeviceDetails();
            if (var1 == null) {
               var1 = new HashMap();
            }

            var1.put(View.Capability.kHiDPIAwareKey, PrismSettings.allowHiDPIScaling);
            Map var2 = Application.getDeviceDetails();
            if (var2 != null) {
               var1.putAll(var2);
            }

            Application.setDeviceDetails((Map)var1);
         } catch (Throwable var6) {
            QuantumRenderer.this.setInitThrowable(var6);
         } finally {
            QuantumRenderer.this.initLatch.countDown();
         }
      }

      public void cleanup() {
         GraphicsPipeline var1 = GraphicsPipeline.getPipeline();
         if (var1 != null) {
            var1.dispose();
         }
      }

      @Override
      public void run() {
         try {
            this.init();
            this.work.run();
         } finally {
            this.cleanup();
         }
      }
   }

   private class QuantumThreadFactory implements ThreadFactory {
      final AtomicInteger threadNumber = new AtomicInteger(0);

      @Override
      public Thread newThread(Runnable var1) {
         QuantumRenderer.PipelineRunnable var2 = QuantumRenderer.this.new PipelineRunnable(var1);
         QuantumRenderer.this._renderer = AccessController.doPrivileged((PrivilegedAction<Thread>)(() -> {
            Thread var2x = new Thread(var2);
            var2x.setName("QuantumRenderer-" + this.threadNumber.getAndIncrement());
            var2x.setDaemon(true);
            var2x.setUncaughtExceptionHandler((var0, var1xx) -> {
               System.err.println(var0.getName() + " uncaught: " + var1xx.getClass().getName());
               var1xx.printStackTrace();
            });
            return var2x;
         }));

         assert this.threadNumber.get() == 1;

         return QuantumRenderer.this._renderer;
      }
   }
}
