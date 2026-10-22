package com.sun.javafx.webkit.prism;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.tk.RenderJob;
import com.sun.javafx.tk.Toolkit;
import com.sun.webkit.Invoker;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

public final class PrismInvoker extends Invoker {
   private static final PlatformLogger log = PlatformLogger.getLogger(PrismInvoker.class.getName());

   @Override
   protected boolean lock(ReentrantLock var1) {
      return false;
   }

   @Override
   protected boolean unlock(ReentrantLock var1) {
      return false;
   }

   @Override
   protected boolean isEventThread() {
      return isEventThreadPrivate();
   }

   private static boolean isEventThreadPrivate() {
      return Toolkit.getToolkit().isFxUserThread();
   }

   @Override
   public void checkEventThread() {
      Toolkit.getToolkit().checkFxUserThread();
   }

   @Override
   public void invokeOnEventThread(Runnable var1) {
      if (this.isEventThread()) {
         var1.run();
      } else {
         PlatformImpl.runLater(var1);
      }
   }

   @Override
   public void postOnEventThread(Runnable var1) {
      PlatformImpl.runLater(var1);
   }

   static void invokeOnRenderThread(Runnable var0) {
      Toolkit.getToolkit().addRenderJob(new RenderJob(var0));
   }

   static void runOnRenderThread(Runnable var0) {
      if (Thread.currentThread().getName().startsWith("QuantumRenderer")) {
         var0.run();
      } else {
         FutureTask var1 = new FutureTask(var0, null);
         Toolkit.getToolkit().addRenderJob(new RenderJob(var1));

         try {
            var1.get();
         } catch (InterruptedException | ExecutionException var3) {
            log.severe("RenderJob error", var3);
         }
      }
   }
}
