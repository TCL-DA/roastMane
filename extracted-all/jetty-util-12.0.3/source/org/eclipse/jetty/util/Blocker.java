package org.eclipse.jetty.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Blocker {
   private static final Logger LOG = LoggerFactory.getLogger(Blocker.class);
   private static final Throwable ACQUIRED = new Throwable() {
      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   };
   private static final Throwable SUCCEEDED = new Throwable() {
      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   };

   public static Blocker.Runnable runnable() {
      return new Blocker.Runnable() {
         final CountDownLatch _complete = new CountDownLatch(1);

         @Override
         public void run() {
            this._complete.countDown();
         }

         @Override
         public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
         }

         @Override
         public void block() throws IOException {
            try {
               this._complete.await();
            } catch (Throwable var2) {
               throw IO.rethrow(var2);
            }
         }

         @Override
         public void close() {
            if (this._complete.getCount() != 0L) {
               if (Blocker.LOG.isDebugEnabled()) {
                  Blocker.LOG.warn("Blocking.Runnable incomplete", new Throwable());
               } else {
                  Blocker.LOG.warn("Blocking.Runnable incomplete");
               }
            }
         }
      };
   }

   public static Blocker.Callback callback() {
      return new Blocker.Callback() {
         private final CompletableFuture<Throwable> _future = new CompletableFuture<>();

         @Override
         public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
         }

         @Override
         public void succeeded() {
            this._future.complete(Blocker.SUCCEEDED);
         }

         @Override
         public void failed(Throwable x) {
            this._future.complete(x == null ? new Throwable() : x);
         }

         @Override
         public void block() throws IOException {
            Throwable result;
            try {
               result = this._future.get();
            } catch (Throwable var3) {
               result = var3;
            }

            if (result != Blocker.SUCCEEDED) {
               throw IO.rethrow(result);
            }
         }

         @Override
         public void close() {
            if (!this._future.isDone()) {
               if (Blocker.LOG.isDebugEnabled()) {
                  Blocker.LOG.warn("Blocking.Callback incomplete", new Throwable());
               } else {
                  Blocker.LOG.warn("Blocking.Callback incomplete");
               }
            }
         }
      };
   }

   public interface Callback extends org.eclipse.jetty.util.Callback, AutoCloseable, Invocable {
      void block() throws IOException;

      @Override
      void close();
   }

   public interface Runnable extends java.lang.Runnable, AutoCloseable, Invocable {
      void block() throws IOException;

      @Override
      void close();
   }

   public static class Shared {
      private final ReentrantLock _lock = new ReentrantLock();
      private final Condition _idle = this._lock.newCondition();
      private final Condition _complete = this._lock.newCondition();
      private Throwable _completed;
      private final Blocker.Callback _callback = new Blocker.Callback() {
         @Override
         public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
         }

         @Override
         public void succeeded() {
            Shared.this._lock.lock();

            try {
               if (Shared.this._completed == Blocker.ACQUIRED) {
                  Shared.this._completed = Blocker.SUCCEEDED;
                  Shared.this._complete.signalAll();
               }
            } finally {
               Shared.this._lock.unlock();
            }
         }

         @Override
         public void failed(Throwable x) {
            Shared.this._lock.lock();

            try {
               if (Shared.this._completed == Blocker.ACQUIRED) {
                  Shared.this._completed = x;
                  Shared.this._complete.signalAll();
               }
            } finally {
               Shared.this._lock.unlock();
            }
         }

         @Override
         public void block() throws IOException {
            Shared.this._lock.lock();

            Throwable result;
            try {
               while (Shared.this._completed == Blocker.ACQUIRED) {
                  Shared.this._complete.await();
               }

               result = Shared.this._completed;
            } catch (Throwable var6) {
               result = var6;
            } finally {
               Shared.this._lock.unlock();
            }

            if (result != Blocker.SUCCEEDED) {
               throw IO.rethrow(result);
            }
         }

         @Override
         public void close() {
            Shared.this._lock.lock();

            boolean completed;
            try {
               completed = Shared.this._completed != Blocker.ACQUIRED;
            } finally {
               Shared.this._completed = null;
               Shared.this._idle.signalAll();
               Shared.this._lock.unlock();
            }

            if (!completed) {
               if (Blocker.LOG.isDebugEnabled()) {
                  Blocker.LOG.warn("Blocking.Shared incomplete", new Throwable());
               } else {
                  Blocker.LOG.warn("Blocking.Shared incomplete");
               }
            }
         }
      };
      private final Blocker.Runnable _runnable = new Blocker.Runnable() {
         @Override
         public Invocable.InvocationType getInvocationType() {
            return Invocable.InvocationType.NON_BLOCKING;
         }

         @Override
         public void run() {
            Shared.this._callback.succeeded();
         }

         @Override
         public void block() throws IOException {
            Shared.this._callback.block();
         }

         @Override
         public void close() {
            Shared.this._callback.close();
         }
      };

      public Blocker.Callback callback() throws IOException {
         this._lock.lock();

         Blocker.Callback x;
         try {
            while (this._completed != null) {
               this._idle.await();
            }

            this._completed = Blocker.ACQUIRED;
            x = this._callback;
         } catch (InterruptedException var5) {
            throw new InterruptedIOException();
         } finally {
            this._lock.unlock();
         }

         return x;
      }

      public Blocker.Runnable runnable() throws IOException {
         this._lock.lock();

         Blocker.Runnable x;
         try {
            while (this._completed != null) {
               this._idle.await();
            }

            this._completed = Blocker.ACQUIRED;
            x = this._runnable;
         } catch (InterruptedException var5) {
            throw new InterruptedIOException();
         } finally {
            this._lock.unlock();
         }

         return x;
      }
   }
}
