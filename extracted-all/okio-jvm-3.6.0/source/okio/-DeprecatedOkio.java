package okio

import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.file.OpenOption
import java.util.Arrays

/** @deprecated */
@Deprecated(message = "changed in Okio 2.x")
object `-DeprecatedOkio` private constructor() {
   @JvmStatic
   public -DeprecatedOkio INSTANCE = new -DeprecatedOkio();


   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "file.appendingSink()", imports = ["okio.appendingSink"]), level = DeprecationLevel.ERROR)
   public fun appendingSink(file: File): Sink {
      return Okio.appendingSink(file);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "sink.buffer()", imports = ["okio.buffer"]), level = DeprecationLevel.ERROR)
   public fun buffer(sink: Sink): BufferedSink {
      return Okio.buffer(sink);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "source.buffer()", imports = ["okio.buffer"]), level = DeprecationLevel.ERROR)
   public fun buffer(source: Source): BufferedSource {
      return Okio.buffer(source);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "file.sink()", imports = ["okio.sink"]), level = DeprecationLevel.ERROR)
   public fun sink(file: File): Sink {
      return Okio.sink$default(file, false, 1, null);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "outputStream.sink()", imports = ["okio.sink"]), level = DeprecationLevel.ERROR)
   public fun sink(outputStream: OutputStream): Sink {
      return Okio.sink(outputStream);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "path.sink(*options)", imports = ["okio.sink"]), level = DeprecationLevel.ERROR)
   public fun sink(path: java.nio.file.Path, vararg options: OpenOption): Sink {
      return Okio.sink(path, Arrays.copyOf(options, options.length));
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "socket.sink()", imports = ["okio.sink"]), level = DeprecationLevel.ERROR)
   public fun sink(socket: Socket): Sink {
      return Okio.sink(socket);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "file.source()", imports = ["okio.source"]), level = DeprecationLevel.ERROR)
   public fun source(file: File): Source {
      return Okio.source(file);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "inputStream.source()", imports = ["okio.source"]), level = DeprecationLevel.ERROR)
   public fun source(inputStream: InputStream): Source {
      return Okio.source(inputStream);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "path.source(*options)", imports = ["okio.source"]), level = DeprecationLevel.ERROR)
   public fun source(path: java.nio.file.Path, vararg options: OpenOption): Source {
      return Okio.source(path, Arrays.copyOf(options, options.length));
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "socket.source()", imports = ["okio.source"]), level = DeprecationLevel.ERROR)
   public fun source(socket: Socket): Source {
      return Okio.source(socket);
   }

   @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "blackholeSink()", imports = ["okio.blackholeSink"]), level = DeprecationLevel.ERROR)
   public fun blackhole(): Sink {
      return Okio.blackhole();
   }
}
