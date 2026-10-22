package org.glassfish.jersey.server.internal;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;

public class JerseyRequestTimeoutHandler {
   private static final Logger LOGGER = Logger.getLogger(JerseyRequestTimeoutHandler.class.getName());
   private ScheduledFuture<?> timeoutTask = null;
   private ContainerResponseWriter.TimeoutHandler timeoutHandler = null;
   private boolean suspended = false;
   private final Object runtimeLock = new Object();
   private final ContainerResponseWriter containerResponseWriter;
   private final ScheduledExecutorService executor;

   public JerseyRequestTimeoutHandler(ContainerResponseWriter containerResponseWriter, ScheduledExecutorService timeoutTaskExecutor) {
      this.containerResponseWriter = containerResponseWriter;
      this.executor = timeoutTaskExecutor;
   }

   public boolean suspend(long timeOut, TimeUnit unit, ContainerResponseWriter.TimeoutHandler handler) {
      synchronized (this.runtimeLock) {
         if (this.suspended) {
            return false;
         } else {
            this.suspended = true;
            this.timeoutHandler = handler;
            this.containerResponseWriter.setSuspendTimeout(timeOut, unit);
            return true;
         }
      }
   }

   public void setSuspendTimeout(long timeOut, TimeUnit unit) throws IllegalStateException {
      synchronized (this.runtimeLock) {
         if (!this.suspended) {
            throw new IllegalStateException(LocalizationMessages.SUSPEND_NOT_SUSPENDED());
         } else {
            this.close(true);
            if (timeOut > 0L) {
               try {
                  this.timeoutTask = this.executor.schedule(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           synchronized (JerseyRequestTimeoutHandler.this.runtimeLock) {
                              JerseyRequestTimeoutHandler.this.timeoutHandler.onTimeout(JerseyRequestTimeoutHandler.this.containerResponseWriter);
                           }
                        } catch (Throwable var4) {
                           JerseyRequestTimeoutHandler.LOGGER.log(Level.WARNING, LocalizationMessages.SUSPEND_HANDLER_EXECUTION_FAILED(), var4);
                        }
                     }
                  }, timeOut, unit);
               } catch (IllegalStateException var7) {
                  LOGGER.log(Level.WARNING, LocalizationMessages.SUSPEND_SCHEDULING_ERROR(), (Throwable)var7);
               }
            }
         }
      }
   }

   public void close() {
      this.close(false);
   }

   private synchronized void close(boolean interruptIfRunning) {
      if (this.timeoutTask != null) {
         this.timeoutTask.cancel(interruptIfRunning);
         this.timeoutTask = null;
      }
   }
}
