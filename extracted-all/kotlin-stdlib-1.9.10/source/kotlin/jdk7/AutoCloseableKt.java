@file:JvmName(name = "AutoCloseableKt")

package kotlin.jdk7

import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker

@SinceKotlin(version = "1.2")
@InlineOnly
public inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   label18: {
      var exception: java.lang.Throwable = null;

      try {
         try {
            var e: Any = block.invoke(`$this$use`);
         } catch (java.lang.Throwable var4) {
            exception = var4;
            throw var4;
         }
      } catch (java.lang.Throwable var5) {
         InlineMarker.finallyStart(1);
         closeFinally(`$this$use`, exception);
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      closeFinally(`$this$use`, null);
      InlineMarker.finallyEnd(1);
   }
}

@SinceKotlin(version = "1.2")
@PublishedApi
internal fun AutoCloseable?.closeFinally(cause: Throwable?) {
   if (`$this$closeFinally` != null) {
      if (cause == null) {
         `$this$closeFinally`.close();
      } else {
         try {
            `$this$closeFinally`.close();
         } catch (java.lang.Throwable var3) {
            ExceptionsKt.addSuppressed(cause, var3);
         }
      }
   }
}

