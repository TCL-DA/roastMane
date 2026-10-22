package com.segment.analytics;

public interface Log {
   Log NONE = new Log() {
      @Override
      public void print(Log.Level level, String format, Object... args) {
      }

      @Override
      public void print(Log.Level level, Throwable error, String format, Object... args) {
      }
   };

   void print(Log.Level var1, String var2, Object... var3);

   void print(Log.Level var1, Throwable var2, String var3, Object... var4);

   public static enum Level {
      VERBOSE,
      DEBUG,
      ERROR;
   }
}
