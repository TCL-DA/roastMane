package com.lmax.disruptor.util;

import java.util.concurrent.ThreadFactory;

public enum DaemonThreadFactory implements ThreadFactory {
   INSTANCE;

   @Override
   public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      return t;
   }
}
