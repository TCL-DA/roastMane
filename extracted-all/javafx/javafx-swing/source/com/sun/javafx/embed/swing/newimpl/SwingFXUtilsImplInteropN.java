package com.sun.javafx.embed.swing.newimpl;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.Toolkit;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import jdk.swing.interop.DispatcherWrapper;

public class SwingFXUtilsImplInteropN {
   public void setFwDispatcher(EventQueue var1) {
      DispatcherWrapper.setFwDispatcher(var1, new SwingFXUtilsImplInteropN.FXDispatcher());
   }

   private static class FXDispatcher extends DispatcherWrapper {
      @Override
      public boolean isDispatchThread() {
         return Platform.isFxApplicationThread();
      }

      @Override
      public void scheduleDispatch(Runnable var1) {
         Platform.runLater(var1);
      }

      @Override
      public SecondaryLoop createSecondaryLoop() {
         return new SwingFXUtilsImplInteropN.FwSecondaryLoop();
      }
   }

   private static class FwSecondaryLoop implements SecondaryLoop {
      private final AtomicBoolean isRunning = new AtomicBoolean(false);

      @Override
      public boolean enter() {
         if (this.isRunning.compareAndSet(false, true)) {
            PlatformImpl.runAndWait(() -> Toolkit.getToolkit().enterNestedEventLoop(this));
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean exit() {
         if (this.isRunning.compareAndSet(true, false)) {
            PlatformImpl.runAndWait(() -> Toolkit.getToolkit().exitNestedEventLoop(this, null));
            return true;
         } else {
            return false;
         }
      }
   }
}
