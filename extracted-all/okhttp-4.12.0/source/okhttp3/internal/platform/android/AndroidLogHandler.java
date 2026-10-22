package okhttp3.internal.platform.android

import java.util.logging.Handler
import java.util.logging.LogRecord

object AndroidLogHandler private constructor() : Handler {
   @JvmStatic
   public AndroidLogHandler INSTANCE = new AndroidLogHandler();


   public override fun publish(record: LogRecord) {
      var var10000: AndroidLog = AndroidLog.INSTANCE;
      var var10001: java.lang.String = record.getLoggerName();
      var var10002: Int = AndroidLogKt.access$getAndroidLevel(record);
      var var10003: java.lang.String = record.getMessage();
      var10000.androidLog$okhttp(var10001, var10002, var10003, record.getThrown());
   }

   public override fun flush() {
   }

   public override fun close() {
   }
}
