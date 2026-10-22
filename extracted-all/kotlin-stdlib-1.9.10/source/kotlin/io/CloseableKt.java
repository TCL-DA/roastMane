@file:JvmName(name = "CloseableKt")

package kotlin.io

import java.io.Closeable
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.internal.PlatformImplementationsKt
import kotlin.jvm.internal.InlineMarker

@InlineOnly
public inline fun <T : Closeable?, R> T.use(block: (T) -> R): R {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   label41: {
      var exception: java.lang.Throwable = null;

      try {
         try {
            ;
         } catch (java.lang.Throwable var6) {
            exception = var6;
            throw var6;
         }
      } catch (java.lang.Throwable var7) {
         InlineMarker.finallyStart(1);
         if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0)) {
            closeFinally(`$this$use`, exception);
         } else if (`$this$use` != null) {
            if (exception == null) {
               `$this$use`.close();
            } else {
               try {
                  `$this$use`.close();
               } catch (java.lang.Throwable var5) {
               }
            }
         }

         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      if (PlatformImplementationsKt.apiVersionIsAtLeast(1, 1, 0)) {
         closeFinally(`$this$use`, null);
      } else if (`$this$use` != null) {
         `$this$use`.close();
      }

      InlineMarker.finallyEnd(1);
      var e: Any;
      return (R)e;
   }
}

@SinceKotlin(version = "1.1")
@PublishedApi
internal fun Closeable?.closeFinally(cause: Throwable?) {
   if (`$this$closeFinally` != null) {
      if (cause == null) {
         `$this$closeFinally`.close();
      } else {
         try {
            `$this$closeFinally`.close();
         } catch (java.lang.Throwable var3) {
            kotlin.ExceptionsKt.addSuppressed(cause, var3);
         }
      }
   }
}

