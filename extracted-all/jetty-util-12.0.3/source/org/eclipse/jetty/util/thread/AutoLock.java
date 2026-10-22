package org.eclipse.jetty.util.thread;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AutoLock implements AutoCloseable, Serializable {
   private static final long serialVersionUID = 3300696774541816341L;
   private final ReentrantLock _lock = new ReentrantLock();

   public AutoLock lock() {
      this._lock.lock();
      return this;
   }

   public boolean isHeldByCurrentThread() {
      return this._lock.isHeldByCurrentThread();
   }

   public Condition newCondition() {
      return this._lock.newCondition();
   }

   boolean isLocked() {
      return this._lock.isLocked();
   }

   @Override
   public void close() {
      this._lock.unlock();
   }

   public static class WithCondition extends AutoLock {
      private final Condition _condition = this.newCondition();

      public AutoLock.WithCondition lock() {
         return (AutoLock.WithCondition)super.lock();
      }

      public void signal() {
         this._condition.signal();
      }

      public void signalAll() {
         this._condition.signalAll();
      }

      public void await() throws InterruptedException {
         this._condition.await();
      }

      public boolean await(long time, TimeUnit unit) throws InterruptedException {
         return this._condition.await(time, unit);
      }
   }
}
