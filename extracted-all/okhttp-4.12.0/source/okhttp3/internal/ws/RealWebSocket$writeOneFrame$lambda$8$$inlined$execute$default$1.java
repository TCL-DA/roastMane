package okhttp3.internal.ws

import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 RealWebSocket.kt\nokhttp3/internal/ws/RealWebSocket\n*L\n1#1,218:1\n513#2,2:219\n*E\n"])
public class `RealWebSocket$writeOneFrame$lambda$8$$inlined$execute$default$1` : Task {
   fun `RealWebSocket$writeOneFrame$lambda$8$$inlined$execute$default$1`(`$name`: java.lang.String, `$cancelable`: Boolean, var3: RealWebSocket) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
   }

   public override fun runOnce(): Long {
      this.this$0.cancel();
      return -1L;
   }
}
