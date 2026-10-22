package okhttp3.internal.platform.android

import android.util.Log
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.concurrent.CopyOnWriteArraySet
import java.util.logging.Level
import java.util.logging.Logger
import okhttp3.OkHttpClient
import okhttp3.internal.SuppressSignatureCheck
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.http2.Http2

@SuppressSignatureCheck
object AndroidLog private constructor() {
   @JvmStatic
   public AndroidLog INSTANCE = new AndroidLog();

   private const val MAX_LOG_LENGTH: Int = 4000
   private final val configuredLoggers: CopyOnWriteArraySet<Logger> = new CopyOnWriteArraySet()
   private final val knownLoggers: Map<String, String>


   internal fun androidLog(loggerName: String, logLevel: Int, message: String, t: Throwable?) {
      var tag: java.lang.String = this.loggerTag(loggerName);
      if (Log.isLoggable(tag, logLevel)) {
         var logMessage: java.lang.String = message;
         if (t != null) {
            logMessage = message + '\n' + Log.getStackTraceString(t);
         }

         var i: Int = 0;
         var length: Int = logMessage.length();

         while (i < length) {
            var newline: Int = StringsKt.indexOf$default(logMessage, '\n', i, false, 4, null);
            newline = if (newline != -1) newline else length;

            var end: Int;
            do {
               end = Math.min(newline, i + 4000);
               var var10002: java.lang.String = logMessage.substring(i, end);
               Log.println(logLevel, tag, var10002);
               i = end;
            } while (end < newline);

            i = end + 1;
         }
      }
   }

   private fun loggerTag(loggerName: String): String {
      var var10000: java.lang.String = knownLoggers.get(loggerName);
      if (var10000 == null) {
         var10000 = StringsKt.take(loggerName, 23);
      }

      return var10000;
   }

   public fun enable() {
      for (Entry var2 : knownLoggers.entrySet()) {
         this.enableLogging(var2.getKey() as java.lang.String, var2.getValue() as java.lang.String);
      }
   }

   private fun enableLogging(logger: String, tag: String) {
      var loggerx: Logger = Logger.getLogger(logger);
      if (configuredLoggers.add(loggerx)) {
         loggerx.setUseParentHandlers(false);
         loggerx.setLevel(if (Log.isLoggable(tag, 3)) Level.FINE else (if (Log.isLoggable(tag, 4)) Level.INFO else Level.WARNING));
         loggerx.addHandler(AndroidLogHandler.INSTANCE);
      }
   }

   @JvmStatic
   fun {
      var var0: LinkedHashMap = new LinkedHashMap();
      var var10000: Package = OkHttpClient.class.getPackage();
      var packageName: java.lang.String = if (var10000 != null) var10000.getName() else null;
      if (packageName != null) {
         var0.put(packageName, "OkHttp");
      }

      var var4: java.util.Map = var0;
      var var13: java.lang.String = OkHttpClient.class.getName();
      var4.put(var13, "okhttp.OkHttpClient");
      var4 = var0;
      var var14: java.lang.String = Http2.class.getName();
      var4.put(var14, "okhttp.Http2");
      var4 = var0;
      var var15: java.lang.String = TaskRunner.class.getName();
      var4.put(var15, "okhttp.TaskRunner");
      var0.put("okhttp3.mockwebserver.MockWebServer", "okhttp.MockWebServer");
      knownLoggers = MapsKt.toMap(var0);
   }
}
