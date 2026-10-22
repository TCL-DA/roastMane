package okio

import java.io.Closeable
import kotlin.jvm.internal.InlineMarker

@JvmSynthetic
// $VF: Class flags could not be determined
internal class Okio__OkioKt {
   @JvmStatic
   public fun Source.buffer(): BufferedSource {
      return new RealBufferedSource(`$this$buffer`);
   }

   @JvmStatic
   public fun Sink.buffer(): BufferedSink {
      return new RealBufferedSink(`$this$buffer`);
   }

   @JvmName(name = "blackhole")
   @JvmStatic
   public fun blackholeSink(): Sink {
      return new BlackholeSink();
   }

   @JvmStatic
   public inline fun <T : Closeable?, R> T.use(block: (T) -> R): R {
      label68: {
         var thrown: java.lang.Throwable = null;

         label69: {
            label70: {
               try {
                  try {
                     break label70;
                  } catch (java.lang.Throwable var10) {
                     thrown = var10;
                  }
               } catch (java.lang.Throwable var11) {
                  InlineMarker.finallyStart(1);

                  try {
                     if (`$this$use` != null) {
                        `$this$use`.close();
                     }
                  } catch (java.lang.Throwable var7) {
                  }

                  InlineMarker.finallyEnd(1);
               }

               InlineMarker.finallyStart(1);

               try {
                  if (`$this$use` != null) {
                     `$this$use`.close();
                  }
               } catch (java.lang.Throwable var9) {
                  if (thrown == null) {
                     thrown = var9;
                  } else {
                     ExceptionsKt.addSuppressed(thrown, var9);
                  }
               }

               InlineMarker.finallyEnd(1);
               break label69;
            }

            InlineMarker.finallyStart(1);

            try {
               if (`$this$use` != null) {
                  `$this$use`.close();
               }
            } catch (java.lang.Throwable var8) {
               thrown = var8;
            }

            InlineMarker.finallyEnd(1);
         }

         if (thrown != null) {
            throw thrown;
         } else {
            return null;
         }
      }
   }
}
