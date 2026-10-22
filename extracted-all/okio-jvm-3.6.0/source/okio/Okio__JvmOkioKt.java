package okio

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.nio.file.Files
import java.nio.file.OpenOption
import java.security.MessageDigest
import java.util.Arrays
import java.util.logging.Logger
import javax.crypto.Cipher
import javax.crypto.Mac
import okio.internal.ResourceFileSystem
import okio.internal.ZipFilesKt

@JvmSynthetic
// $VF: Class flags could not be determined
internal class Okio__JvmOkioKt {
   private final val logger: Logger = Logger.getLogger("okio.Okio")
   internal final val isAndroidGetsocknameError: Boolean
      internal final get() {
         if (`$this$isAndroidGetsocknameError`.getCause() != null) {
            var var10000: java.lang.String = `$this$isAndroidGetsocknameError`.getMessage();
            if (var10000 != null && StringsKt.contains$default(var10000, "getsockname failed", false, 2, null)) {
               return true;
            }
         }

         return false;
      }


   @JvmStatic
   public fun OutputStream.sink(): Sink {
      return new OutputStreamSink(`$this$sink`, new Timeout());
   }

   @JvmStatic
   public fun InputStream.source(): Source {
      return new InputStreamSource(`$this$source`, new Timeout());
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Socket.sink(): Sink {
      var timeout: SocketAsyncTimeout = new SocketAsyncTimeout(`$this$sink`);
      var var10002: OutputStream = `$this$sink`.getOutputStream();
      return timeout.sink(new OutputStreamSink(var10002, timeout));
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Socket.source(): Source {
      var timeout: SocketAsyncTimeout = new SocketAsyncTimeout(`$this$source`);
      var var10002: InputStream = `$this$source`.getInputStream();
      return timeout.source(new InputStreamSource(var10002, timeout));
   }

   @JvmOverloads
   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   public fun File.sink(append: Boolean): Sink {
      return Okio.sink(new FileOutputStream(`$this$sink`, append));
   }

   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   public fun File.appendingSink(): Sink {
      return Okio.sink(new FileOutputStream(`$this$appendingSink`, true));
   }

   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   public fun File.source(): Source {
      return new InputStreamSource(new FileInputStream(`$this$source`), Timeout.NONE);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun java.nio.file.Path.sink(vararg options: OpenOption): Sink {
      var var10000: OutputStream = Files.newOutputStream(`$this$sink`, Arrays.copyOf(options, options.length));
      return Okio.sink(var10000);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun java.nio.file.Path.source(vararg options: OpenOption): Source {
      var var10000: InputStream = Files.newInputStream(`$this$source`, Arrays.copyOf(options, options.length));
      return Okio.source(var10000);
   }

   @JvmStatic
   public fun Sink.cipherSink(cipher: Cipher): CipherSink {
      return new CipherSink(Okio.buffer(`$this$cipherSink`), cipher);
   }

   @JvmStatic
   public fun Source.cipherSource(cipher: Cipher): CipherSource {
      return new CipherSource(Okio.buffer(`$this$cipherSource`), cipher);
   }

   @JvmStatic
   public fun Sink.hashingSink(mac: Mac): HashingSink {
      return new HashingSink(`$this$hashingSink`, mac);
   }

   @JvmStatic
   public fun Source.hashingSource(mac: Mac): HashingSource {
      return new HashingSource(`$this$hashingSource`, mac);
   }

   @JvmStatic
   public fun Sink.hashingSink(digest: MessageDigest): HashingSink {
      return new HashingSink(`$this$hashingSink`, digest);
   }

   @JvmStatic
   public fun Source.hashingSource(digest: MessageDigest): HashingSource {
      return new HashingSource(`$this$hashingSource`, digest);
   }

   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun FileSystem.openZip(zipPath: Path): FileSystem {
      return ZipFilesKt.openZip$default(zipPath, `$this$openZip`, null, 4, null);
   }

   @JvmStatic
   public fun ClassLoader.asResourceFileSystem(): FileSystem {
      return new ResourceFileSystem(`$this$asResourceFileSystem`, true, null, 4, null);
   }

   @JvmOverloads
   @Throws(java/io/FileNotFoundException::class)
   @JvmStatic
   fun File.sink(): Sink {
      return Okio.sink$default(`$this$sink`, false, 1, null);
   }
}
