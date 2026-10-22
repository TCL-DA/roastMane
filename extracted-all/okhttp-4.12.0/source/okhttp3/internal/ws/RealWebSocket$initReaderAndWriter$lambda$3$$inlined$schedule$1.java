package okhttp3.internal.ws

import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$schedule$2\n+ 2 RealWebSocket.kt\nokhttp3/internal/ws/RealWebSocket\n*L\n1#1,218:1\n270#2,2:219\n*E\n"])
public class `RealWebSocket$initReaderAndWriter$lambda$3$$inlined$schedule$1` : Task {
   fun `RealWebSocket$initReaderAndWriter$lambda$3$$inlined$schedule$1`(`$name`: java.lang.String, var2: RealWebSocket, var3: Long) {
      super(`$name`, false, 2, null);
      this.this$0 = var2;
      this.$pingIntervalNanos$inlined = var3;
   }

   public override fun runOnce(): Long {
      this.this$0.writePingFrame$okhttp();
      return this.$pingIntervalNanos$inlined;
   }
}
