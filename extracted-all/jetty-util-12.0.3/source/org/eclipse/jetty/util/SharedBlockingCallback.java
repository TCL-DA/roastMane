package org.eclipse.jetty.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class SharedBlockingCallback {
   private static final Logger LOG = LoggerFactory.getLogger(SharedBlockingCallback.class);
   private static final Throwable IDLE = new ConstantThrowable("IDLE");
   private static final Throwable SUCCEEDED = new ConstantThrowable("SUCCEEDED");
   private static final Throwable FAILED = new ConstantThrowable("FAILED");
   private final ReentrantLock _lock = new ReentrantLock();
   private final Condition _idle = this._lock.newCondition();
   private final Condition _complete = this._lock.newCondition();
   private SharedBlockingCallback.Blocker _blocker = new SharedBlockingCallback.Blocker();

   public SharedBlockingCallback.Blocker acquire() throws IOException {
      this._lock.lock();

      SharedBlockingCallback.Blocker x;
      try {
         while (this._blocker._state != IDLE) {
            this._idle.await();
         }

         this._blocker._state = null;
         x = this._blocker;
      } catch (InterruptedException var5) {
         throw new InterruptedIOException();
      } finally {
         this._lock.unlock();
      }

      return x;
   }

   public boolean fail(Throwable cause) {
      Objects.requireNonNull(cause);
      this._lock.lock();

      try {
         if (this._blocker._state == null) {
            this._blocker._state = new SharedBlockingCallback.BlockerFailedException(cause);
            this._complete.signalAll();
            return true;
         }
      } finally {
         this._lock.unlock();
      }

      return false;
   }

   protected void notComplete(SharedBlockingCallback.Blocker blocker) {
      LOG.warn("Blocker not complete {}", blocker);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Blocker not complete stacktrace", new Throwable());
      }
   }

   public class Blocker implements Callback, Closeable, Runnable {
      private Throwable _state = SharedBlockingCallback.IDLE;

      protected Blocker() {
      }

      @Override
      public Invocable.InvocationType getInvocationType() {
         return Invocable.InvocationType.NON_BLOCKING;
      }

      @Override
      public void run() {
         this.succeeded();
      }

      @Override
      public void succeeded() {
         SharedBlockingCallback.this._lock.lock();

         try {
            if (this._state == null) {
               this._state = SharedBlockingCallback.SUCCEEDED;
               SharedBlockingCallback.this._complete.signalAll();
            } else {
               SharedBlockingCallback.LOG.warn("Succeeded after {}", this._state.toString());
               if (SharedBlockingCallback.LOG.isDebugEnabled()) {
                  SharedBlockingCallback.LOG.debug("State", this._state);
               }
            }
         } finally {
            SharedBlockingCallback.this._lock.unlock();
         }
      }

      @Override
      public void failed(Throwable cause) {
         SharedBlockingCallback.this._lock.lock();

         try {
            if (this._state == null) {
               if (cause == null) {
                  this._state = SharedBlockingCallback.FAILED;
               } else if (cause instanceof SharedBlockingCallback.BlockerTimeoutException) {
                  this._state = new IOException(cause);
               } else {
                  this._state = cause;
               }

               SharedBlockingCallback.this._complete.signalAll();
            } else if (!(this._state instanceof SharedBlockingCallback.BlockerTimeoutException)
               && !(this._state instanceof SharedBlockingCallback.BlockerFailedException)) {
               String msg = String.format("Failed after %s: %s", this._state, cause);
               SharedBlockingCallback.LOG.warn(msg);
               if (SharedBlockingCallback.LOG.isDebugEnabled()) {
                  SharedBlockingCallback.LOG.debug(msg, this._state);
                  SharedBlockingCallback.LOG.debug(msg, cause);
               }
            } else if (SharedBlockingCallback.LOG.isDebugEnabled()) {
               SharedBlockingCallback.LOG.debug("Failed after {}", this._state);
            }
         } finally {
            SharedBlockingCallback.this._lock.unlock();
         }
      }

      public void block() throws IOException {
         SharedBlockingCallback.this._lock.lock();

         try {
            while (this._state == null) {
               SharedBlockingCallback.this._complete.await();
            }

            if (this._state != SharedBlockingCallback.SUCCEEDED) {
               if (this._state == SharedBlockingCallback.IDLE) {
                  throw new IllegalStateException("IDLE");
               }

               if (this._state instanceof IOException) {
                  throw (IOException)this._state;
               }

               if (this._state instanceof CancellationException) {
                  throw (CancellationException)this._state;
               }

               if (this._state instanceof RuntimeException) {
                  throw (RuntimeException)this._state;
               }

               if (this._state instanceof Error) {
                  throw (Error)this._state;
               }

               throw new IOException(this._state);
            }
         } catch (InterruptedException var5) {
            this._state = var5;
            throw (InterruptedIOException)new InterruptedIOException().initCause(var5);
         } finally {
            SharedBlockingCallback.this._lock.unlock();
         }
      }

      @Override
      public void close() {
         SharedBlockingCallback.this._lock.lock();

         try {
            if (this._state == SharedBlockingCallback.IDLE) {
               throw new IllegalStateException("IDLE");
            }

            if (this._state == null) {
               SharedBlockingCallback.this.notComplete(this);
            }
         } finally {
            try {
               if (this._state != null && this._state != SharedBlockingCallback.SUCCEEDED) {
                  SharedBlockingCallback.this._blocker = SharedBlockingCallback.this.new Blocker();
               } else {
                  this._state = SharedBlockingCallback.IDLE;
               }

               SharedBlockingCallback.this._idle.signalAll();
               SharedBlockingCallback.this._complete.signalAll();
            } finally {
               SharedBlockingCallback.this._lock.unlock();
            }
         }
      }

      @Override
      public String toString() {
         SharedBlockingCallback.this._lock.lock();

         String var1;
         try {
            var1 = String.format("%s@%x{%s}", SharedBlockingCallback.Blocker.class.getSimpleName(), this.hashCode(), this._state == null ? "WAIT" : this._state);
         } finally {
            SharedBlockingCallback.this._lock.unlock();
         }

         return var1;
      }
   }

   private static class BlockerFailedException extends Exception {
      public BlockerFailedException(Throwable cause) {
         super(cause);
      }
   }

   private static class BlockerTimeoutException extends TimeoutException {
   }
}
