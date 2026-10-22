@file:JvmName(name = "ByteStreamsKt")

package kotlin.io

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Reader
import java.io.Writer
import java.nio.charset.Charset
import java.util.NoSuchElementException
import kotlin.internal.InlineOnly

public operator fun BufferedInputStream.iterator(): ByteIterator {
   return new ByteIterator(`$this$iterator`) {
      private int nextByte;
      private boolean nextPrepared;
      private boolean finished;

      {
         this.$this_iterator = `$receiver`;
         this.nextByte = -1;
      }

      public final int getNextByte() {
         return this.nextByte;
      }

      public final void setNextByte(int var1) {
         this.nextByte = var1;
      }

      public final boolean getNextPrepared() {
         return this.nextPrepared;
      }

      public final void setNextPrepared(boolean var1) {
         this.nextPrepared = var1;
      }

      public final boolean getFinished() {
         return this.finished;
      }

      public final void setFinished(boolean var1) {
         this.finished = var1;
      }

      private final void prepareNext() {
         if (!this.nextPrepared && !this.finished) {
            this.nextByte = this.$this_iterator.read();
            this.nextPrepared = true;
            this.finished = this.nextByte == -1;
         }
      }

      @Override
      public boolean hasNext() {
         this.prepareNext();
         return !this.finished;
      }

      @Override
      public byte nextByte() {
         this.prepareNext();
         if (this.finished) {
            throw new NoSuchElementException("Input stream is over.");
         } else {
            var res: Byte = (byte)this.nextByte;
            this.nextPrepared = false;
            return res;
         }
      }
   };
}

@InlineOnly
public inline fun String.byteInputStream(charset: Charset): ByteArrayInputStream {
   var var10002: ByteArray = `$this$byteInputStream`.getBytes(charset);
   return new ByteArrayInputStream(var10002);
}

@JvmSynthetic
fun java.lang.String.`byteInputStream$default`(charset: Charset, var2: Int, var3: Any): ByteArrayInputStream {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   var var10002: ByteArray = `$this$byteInputStream_u24default`.getBytes(charset);
   return new ByteArrayInputStream(var10002);
}

@InlineOnly
public inline fun ByteArray.inputStream(): ByteArrayInputStream {
   return new ByteArrayInputStream(`$this$inputStream`);
}

@InlineOnly
public inline fun ByteArray.inputStream(offset: Int, length: Int): ByteArrayInputStream {
   return new ByteArrayInputStream(`$this$inputStream`, offset, length);
}

@InlineOnly
public inline fun InputStream.buffered(bufferSize: Int): BufferedInputStream {
   return if (`$this$buffered` is BufferedInputStream) `$this$buffered` as BufferedInputStream else new BufferedInputStream(`$this$buffered`, bufferSize);
}

@JvmSynthetic
fun InputStream.`buffered$default`(bufferSize: Int, var2: Int, var3: Any): BufferedInputStream {
   if ((var2 and 1) != 0) {
      bufferSize = 8192;
   }

   return if (`$this$buffered_u24default` is BufferedInputStream)
      `$this$buffered_u24default` as BufferedInputStream
      else
      new BufferedInputStream(`$this$buffered_u24default`, bufferSize);
}

@InlineOnly
public inline fun InputStream.reader(charset: Charset): InputStreamReader {
   return new InputStreamReader(`$this$reader`, charset);
}

@JvmSynthetic
fun InputStream.`reader$default`(charset: Charset, var2: Int, var3: Any): InputStreamReader {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   return new InputStreamReader(`$this$reader_u24default`, charset);
}

@InlineOnly
public inline fun InputStream.bufferedReader(charset: Charset): BufferedReader {
   var var2: Reader = new InputStreamReader(`$this$bufferedReader`, charset);
   return if (var2 is BufferedReader) var2 as BufferedReader else new BufferedReader(var2, 8192);
}

@JvmSynthetic
fun InputStream.`bufferedReader$default`(charset: Charset, var2: Int, var3: Any): BufferedReader {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   var var4: Reader = new InputStreamReader(`$this$bufferedReader_u24default`, charset);
   return if (var4 is BufferedReader) var4 as BufferedReader else new BufferedReader(var4, 8192);
}

@InlineOnly
public inline fun OutputStream.buffered(bufferSize: Int): BufferedOutputStream {
   return if (`$this$buffered` is BufferedOutputStream) `$this$buffered` as BufferedOutputStream else new BufferedOutputStream(`$this$buffered`, bufferSize);
}

@JvmSynthetic
fun OutputStream.`buffered$default`(bufferSize: Int, var2: Int, var3: Any): BufferedOutputStream {
   if ((var2 and 1) != 0) {
      bufferSize = 8192;
   }

   return if (`$this$buffered_u24default` is BufferedOutputStream)
      `$this$buffered_u24default` as BufferedOutputStream
      else
      new BufferedOutputStream(`$this$buffered_u24default`, bufferSize);
}

@InlineOnly
public inline fun OutputStream.writer(charset: Charset): OutputStreamWriter {
   return new OutputStreamWriter(`$this$writer`, charset);
}

@JvmSynthetic
fun OutputStream.`writer$default`(charset: Charset, var2: Int, var3: Any): OutputStreamWriter {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   return new OutputStreamWriter(`$this$writer_u24default`, charset);
}

@InlineOnly
public inline fun OutputStream.bufferedWriter(charset: Charset): BufferedWriter {
   var var2: Writer = new OutputStreamWriter(`$this$bufferedWriter`, charset);
   return if (var2 is BufferedWriter) var2 as BufferedWriter else new BufferedWriter(var2, 8192);
}

@JvmSynthetic
fun OutputStream.`bufferedWriter$default`(charset: Charset, var2: Int, var3: Any): BufferedWriter {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   var var4: Writer = new OutputStreamWriter(`$this$bufferedWriter_u24default`, charset);
   return if (var4 is BufferedWriter) var4 as BufferedWriter else new BufferedWriter(var4, 8192);
}

public fun InputStream.copyTo(out: OutputStream, bufferSize: Int): Long {
   var bytesCopied: Long = 0L;
   var buffer: ByteArray = new byte[bufferSize];

   for (int bytes = $this$copyTo.read(buffer); bytes >= 0; bytes = $this$copyTo.read(buffer)) {
      out.write(buffer, 0, bytes);
      bytesCopied += (long)bytes;
   }

   return bytesCopied;
}

@JvmSynthetic
fun `copyTo$default`(var0: InputStream, var1: OutputStream, var2: Int, var3: Int, var4: Any): Long {
   if ((var3 and 2) != 0) {
      var2 = 8192;
   }

   return copyTo(var0, var1, var2);
}

@Deprecated(message = "Use readBytes() overload without estimatedSize parameter", replaceWith = @ReplaceWith(expression = "readBytes()", imports = []))
@DeprecatedSinceKotlin(warningSince = "1.3", errorSince = "1.5")
public fun InputStream.readBytes(estimatedSize: Int): ByteArray {
   var buffer: ByteArrayOutputStream = new ByteArrayOutputStream(Math.max(estimatedSize, `$this$readBytes`.available()));
   copyTo$default(`$this$readBytes`, buffer, 0, 2, null);
   var var10000: ByteArray = buffer.toByteArray();
   return var10000;
}

/** @deprecated */
@JvmSynthetic
fun `readBytes$default`(var0: InputStream, var1: Int, var2: Int, var3: Any): ByteArray {
   if ((var2 and 1) != 0) {
      var1 = 8192;
   }

   return readBytes(var0, var1);
}

@SinceKotlin(version = "1.3")
public fun InputStream.readBytes(): ByteArray {
   var buffer: ByteArrayOutputStream = new ByteArrayOutputStream(Math.max(8192, `$this$readBytes`.available()));
   copyTo$default(`$this$readBytes`, buffer, 0, 2, null);
   var var10000: ByteArray = buffer.toByteArray();
   return var10000;
}

