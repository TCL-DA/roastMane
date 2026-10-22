package okhttp3.internal.platform.android

import java.util.logging.Level
import java.util.logging.LogRecord

private final val androidLevel: Int
   private final get() {
      return if (`$this$androidLevel`.getLevel().intValue() > Level.INFO.intValue())
         5
         else
         (if (`$this$androidLevel`.getLevel().intValue() == Level.INFO.intValue()) 4 else 3);
   }

@JvmSynthetic
fun `access$getAndroidLevel`(`$receiver`: LogRecord): Int {
   return getAndroidLevel(`$receiver`);
}

