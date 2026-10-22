package org.glassfish.jersey.internal.guava;

public abstract class Ticker {
   private static final Ticker SYSTEM_TICKER = new Ticker() {
      @Override
      public long read() {
         return Platform.systemNanoTime();
      }
   };

   Ticker() {
   }

   public static Ticker systemTicker() {
      return SYSTEM_TICKER;
   }

   public abstract long read();
}
