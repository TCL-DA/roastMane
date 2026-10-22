@file:JvmName(name = "-GzipSinkExtensions")

package okio

public inline fun Sink.gzip(): GzipSink {
   return new GzipSink(`$this$gzip`);
}

