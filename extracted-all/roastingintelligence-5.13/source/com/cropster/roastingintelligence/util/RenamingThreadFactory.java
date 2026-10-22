package com.cropster.roastingintelligence.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RenamingThreadFactory implements ThreadFactory {
   private final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();
   private final AtomicInteger counter = new AtomicInteger();
   private final String prefix;

   public RenamingThreadFactory(String prefix) {
      this.prefix = prefix;
   }

   @Override
   public Thread newThread(Runnable r) {
      Thread thread = this.defaultThreadFactory.newThread(r);
      thread.setName(this.prefix + "-" + this.counter.getAndIncrement());
      return thread;
   }
}
