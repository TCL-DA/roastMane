package okhttp3.internal.http2

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import okhttp3.internal.concurrent.Task

@SourceDebugExtension(["SMAP\nTaskQueue.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TaskQueue.kt\nokhttp3/internal/concurrent/TaskQueue$execute$1\n+ 2 Http2Connection.kt\nokhttp3/internal/http2/Http2Connection$ReaderRunnable\n*L\n1#1,218:1\n759#2,2:219\n*E\n"])
public class `Http2Connection$ReaderRunnable$applyAndAckSettings$lambda$7$lambda$6$$inlined$execute$default$1` : Task {
   fun `Http2Connection$ReaderRunnable$applyAndAckSettings$lambda$7$lambda$6$$inlined$execute$default$1`(
      `$name`: java.lang.String, `$cancelable`: Boolean, var3: Http2Connection, var4: ObjectRef
   ) {
      super(`$name`, `$cancelable`);
      this.this$0 = var3;
      this.$newPeerSettings$inlined = var4;
   }

   public override fun runOnce(): Long {
      this.this$0.getListener$okhttp().onSettings(this.this$0, this.$newPeerSettings$inlined.element as Settings);
      return -1L;
   }
}
