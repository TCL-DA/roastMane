package com.ghgande.j2mod.modbus.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPool {
   private static final Logger logger = LoggerFactory.getLogger(ThreadPool.class);
   private final LinkedBlockingQueue<Runnable> taskPool;
   private final List<ThreadPool.PoolThread> threadPool = new ArrayList<>();
   private final int size;
   private boolean running;

   public ThreadPool(int size) {
      this.size = size;
      this.taskPool = new LinkedBlockingQueue<>();
   }

   public synchronized void execute(Runnable task) {
      if (this.running) {
         try {
            this.taskPool.put(task);
         } catch (InterruptedException var3) {
         }
      }
   }

   public void initPool(String name) {
      this.running = true;
      int i = this.size;

      while (--i >= 0) {
         ThreadPool.PoolThread thread = new ThreadPool.PoolThread();
         this.threadPool.add(thread);
         thread.setName(String.format("%s Handler", name));
         thread.start();
      }
   }

   public void close() {
      if (this.running) {
         this.taskPool.clear();
         this.running = false;

         for (ThreadPool.PoolThread thread : this.threadPool) {
            thread.interrupt();
         }
      }
   }

   private class PoolThread extends Thread {
      private PoolThread() {
      }

      @Override
      public void run() {
         ThreadPool.logger.debug("Running PoolThread");

         do {
            try {
               ThreadPool.logger.debug("{}", this);
               ThreadPool.this.taskPool.take().run();
            } catch (Exception var2) {
               if (ThreadPool.this.running) {
                  ThreadPool.logger.error("Problem starting receiver thread", var2);
               }
            }
         } while (ThreadPool.this.running);
      }
   }
}
