package okio

import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.file.OpenOption
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.Mac

// $VF: Class flags could not be determined
internal class Okio {
   @JvmStatic
   fun OutputStream.sink(): Sink {
      return Okio__JvmOkioKt.sink(`$this$sink`);
   }

   @JvmStatic
   fun InputStream.source(): Source {
      return Okio__JvmOkioKt.source(`$this$source`);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun Socket.sink(): Sink {
      return Okio__JvmOkioKt.sink(`$this$sink`);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun Socket.source(): Source {
      return Okio__JvmOkioKt.source(`$this$source`);
   }

   @JvmOverloads
   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   fun File.sink(append: Boolean): Sink {
      return Okio__JvmOkioKt.sink(`$this$sink`, append);
   }

   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   fun File.appendingSink(): Sink {
      return Okio__JvmOkioKt.appendingSink(`$this$appendingSink`);
   }

   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   fun File.source(): Source {
      return Okio__JvmOkioKt.source(`$this$source`);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun java.nio.file.Path.sink(vararg options: OpenOption): Sink {
      return Okio__JvmOkioKt.sink(`$this$sink`, options);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun java.nio.file.Path.source(vararg options: OpenOption): Source {
      return Okio__JvmOkioKt.source(`$this$source`, options);
   }

   @JvmStatic
   fun Sink.cipherSink(cipher: Cipher): CipherSink {
      return Okio__JvmOkioKt.cipherSink(`$this$cipherSink`, cipher);
   }

   @JvmStatic
   fun Source.cipherSource(cipher: Cipher): CipherSource {
      return Okio__JvmOkioKt.cipherSource(`$this$cipherSource`, cipher);
   }

   @JvmStatic
   fun Sink.hashingSink(mac: Mac): HashingSink {
      return Okio__JvmOkioKt.hashingSink(`$this$hashingSink`, mac);
   }

   @JvmStatic
   fun Source.hashingSource(mac: Mac): HashingSource {
      return Okio__JvmOkioKt.hashingSource(`$this$hashingSource`, mac);
   }

   @JvmStatic
   fun Sink.hashingSink(digest: MessageDigest): HashingSink {
      return Okio__JvmOkioKt.hashingSink(`$this$hashingSink`, digest);
   }

   @JvmStatic
   fun Source.hashingSource(digest: MessageDigest): HashingSource {
      return Okio__JvmOkioKt.hashingSource(`$this$hashingSource`, digest);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   fun FileSystem.openZip(zipPath: Path): FileSystem {
      return Okio__JvmOkioKt.openZip(`$this$openZip`, zipPath);
   }

   @JvmStatic
   fun ClassLoader.asResourceFileSystem(): FileSystem {
      return Okio__JvmOkioKt.asResourceFileSystem(`$this$asResourceFileSystem`);
   }

   @JvmStatic
   fun AssertionError.isAndroidGetsocknameError(): Boolean {
      return Okio__JvmOkioKt.isAndroidGetsocknameError(`$this$isAndroidGetsocknameError`);
   }

   @JvmOverloads
   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   fun File.sink(): Sink {
      return Okio__JvmOkioKt.sink(`$this$sink`);
   }

   @JvmStatic
   fun Source.buffer(): BufferedSource {
      return Okio__OkioKt.buffer(`$this$buffer`);
   }

   @JvmStatic
   fun Sink.buffer(): BufferedSink {
      return Okio__OkioKt.buffer(`$this$buffer`);
   }

   @JvmName(name = "blackhole")
   @JvmStatic
   fun blackhole(): Sink {
      return Okio__OkioKt.blackhole();
   }

   @JvmStatic
   fun <T extends Closeable, R> T.use(block: (T?) -> R): R {
      return Okio__OkioKt.use(`$this$use`, block);
   }
}
