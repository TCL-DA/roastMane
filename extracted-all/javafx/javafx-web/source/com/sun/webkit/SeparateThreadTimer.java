package com.sun.webkit;

import java.lang.Thread.State;

final class SeparateThreadTimer extends Timer implements Runnable {
   private final Invoker invoker = Invoker.getInvoker();
   private final SeparateThreadTimer.FireRunner fireRunner = new SeparateThreadTimer.FireRunner();
   private final Thread thread = new Thread(this, "WebPane-Timer");

   SeparateThreadTimer() {
      this.thread.setDaemon(true);
   }

   @Override
   synchronized void setFireTime(long var1) {
      super.setFireTime(var1);
      if (this.thread.getState() == State.NEW) {
         this.thread.start();
      }

      this.notifyAll();
   }

   @Override
   public synchronized void run() {
      while (true) {
         try {
            if (this.fireTime > 0L) {
               for (long var1 = System.currentTimeMillis(); this.fireTime > var1; var1 = System.currentTimeMillis()) {
                  this.wait(this.fireTime - var1);
               }

               if (this.fireTime > 0L) {
                  this.invoker.invokeOnEventThread(this.fireRunner.forTime(this.fireTime));
               }
            }

            this.wait();
         } catch (InterruptedException var3) {
            return;
         }
      }
   }

   @Override
   public void notifyTick() {
      assert false;
   }

   private final class FireRunner implements Runnable {
      private volatile long time;

      private Runnable forTime(long var1) {
         this.time = var1;
         return this;
      }

      @Override
      public void run() {
         SeparateThreadTimer.this.fireTimerEvent(this.time);
      }
   }
}
