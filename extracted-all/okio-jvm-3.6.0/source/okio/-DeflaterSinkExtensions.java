@file:JvmName(name = "-DeflaterSinkExtensions")

package okio

import java.util.zip.Deflater

public inline fun Sink.deflate(deflater: Deflater): DeflaterSink {
   return new DeflaterSink(`$this$deflate`, deflater);
}

@JvmSynthetic
fun Sink.`deflate$default`(deflater: Deflater, `$i$f$deflate`: Int, var3: Any): DeflaterSink {
   if ((`$i$f$deflate` and 1) != 0) {
      deflater = new Deflater();
   }

   return new DeflaterSink(`$this$deflate_u24default`, deflater);
}

