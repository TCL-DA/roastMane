package com.cropster.roastingintelligence.ui.util;

public class OnceRunnable implements Runnable {
   private final Runnable delegate;
   private boolean wasRun = false;

   public OnceRunnable(Runnable delegate) {
      this.delegate = delegate;
   }

   @Override
   public synchronized void run() {
      if (!this.wasRun) {
         this.wasRun = true;
         this.delegate.run();
      }
   }
}
