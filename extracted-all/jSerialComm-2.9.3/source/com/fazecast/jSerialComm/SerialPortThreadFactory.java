package com.fazecast.jSerialComm;

import java.util.concurrent.ThreadFactory;

public class SerialPortThreadFactory {
   private static ThreadFactory instance = new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
         return new Thread(r);
      }
   };

   public static ThreadFactory get() {
      return instance;
   }

   public static void set(ThreadFactory threadFactory) {
      instance = threadFactory;
   }
}
