@file:JvmName(name = "TextStreamsKt")

@file:SourceDebugExtension(["SMAP\nReadWrite.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReadWrite.kt\nkotlin/io/TextStreamsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,151:1\n52#1:152\n1#2:153\n1#2:156\n1313#3,2:154\n*S KotlinDebug\n*F\n+ 1 ReadWrite.kt\nkotlin/io/TextStreamsKt\n*L\n33#1:152\n33#1:153\n33#1:154,2\n*E\n"])

package kotlin.io

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.InputStream
import java.io.Reader
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@InlineOnly
public inline fun Reader.buffered(bufferSize: Int): BufferedReader {
   return if (`$this$buffered` is BufferedReader) `$this$buffered` as BufferedReader else new BufferedReader(`$this$buffered`, bufferSize);
}

@JvmSynthetic
fun Reader.`buffered$default`(bufferSize: Int, var2: Int, var3: Any): BufferedReader {
   if ((var2 and 1) != 0) {
      bufferSize = 8192;
   }

   return if (`$this$buffered_u24default` is BufferedReader)
      `$this$buffered_u24default` as BufferedReader
      else
      new BufferedReader(`$this$buffered_u24default`, bufferSize);
}

@InlineOnly
public inline fun Writer.buffered(bufferSize: Int): BufferedWriter {
   return if (`$this$buffered` is BufferedWriter) `$this$buffered` as BufferedWriter else new BufferedWriter(`$this$buffered`, bufferSize);
}

@JvmSynthetic
fun Writer.`buffered$default`(bufferSize: Int, var2: Int, var3: Any): BufferedWriter {
   if ((var2 and 1) != 0) {
      bufferSize = 8192;
   }

   return if (`$this$buffered_u24default` is BufferedWriter)
      `$this$buffered_u24default` as BufferedWriter
      else
      new BufferedWriter(`$this$buffered_u24default`, bufferSize);
}

public fun Reader.forEachLine(action: (String) -> Unit) {
   label33: {
      var var4: Closeable = if (`$this$forEachLine` is BufferedReader) `$this$forEachLine` as BufferedReader else new BufferedReader(`$this$forEachLine`, 8192);
      var var18: java.lang.Throwable = null;

      try {
         try {
            var it: Sequence;
            for (Object element$iv : it) {
               action.invoke(`element$iv`);
            }
         } catch (java.lang.Throwable var14) {
            var18 = var14;
            throw var14;
         }
      } catch (java.lang.Throwable var15) {
         CloseableKt.closeFinally(var4, var18);
      }

      CloseableKt.closeFinally(var4, null);
   }
}

public fun Reader.readLines(): List<String> {
   var result: ArrayList = new ArrayList();
   forEachLine(`$this$readLines`, (new Function1<java.lang.String, Unit>(result) {
      {
         super(1);
         this.$result = `$result`;
      }

      public final void invoke(@NotNull java.lang.String it) {
         this.$result.add(it);
      }
   }) as Function1<? super java.lang.String, Unit>);
   return result;
}

public inline fun <T> Reader.useLines(block: (Sequence<String>) -> T): T {
   label24: {
      var var3: Closeable = if (`$this$useLines` is BufferedReader) `$this$useLines` as BufferedReader else new BufferedReader(`$this$useLines`, 8192);
      var var11: java.lang.Throwable = null;

      try {
         try {
            var var12: Any = block.invoke(lineSequence(var3 as BufferedReader));
         } catch (java.lang.Throwable var7) {
            var11 = var7;
            throw var7;
         }
      } catch (java.lang.Throwable var8) {
         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var3, var11);
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      CloseableKt.closeFinally(var3, null);
      InlineMarker.finallyEnd(1);
   }
}

@InlineOnly
public inline fun String.reader(): StringReader {
   return new StringReader(`$this$reader`);
}

public fun BufferedReader.lineSequence(): Sequence<String> {
   return SequencesKt.constrainOnce(new LinesSequence(`$this$lineSequence`));
}

public fun Reader.readText(): String {
   var buffer: StringWriter = new StringWriter();
   copyTo$default(`$this$readText`, buffer, 0, 2, null);
   var var10000: java.lang.String = buffer.toString();
   return var10000;
}

public fun Reader.copyTo(out: Writer, bufferSize: Int): Long {
   var charsCopied: Long = 0L;
   var buffer: CharArray = new char[bufferSize];

   for (int chars = $this$copyTo.read(buffer); chars >= 0; chars = $this$copyTo.read(buffer)) {
      out.write(buffer, 0, chars);
      charsCopied += (long)chars;
   }

   return charsCopied;
}

@JvmSynthetic
fun `copyTo$default`(var0: Reader, var1: Writer, var2: Int, var3: Int, var4: Any): Long {
   if ((var3 and 2) != 0) {
      var2 = 8192;
   }

   return copyTo(var0, var1, var2);
}

@InlineOnly
public inline fun URL.readText(charset: Charset): String {
   return new java.lang.String(readBytes(`$this$readText`), charset);
}

@JvmSynthetic
fun URL.`readText$default`(charset: Charset, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      charset = Charsets.UTF_8;
   }

   return new java.lang.String(readBytes(`$this$readText_u24default`), charset);
}

public fun URL.readBytes(): ByteArray {
   label19: {
      var var1: Closeable = `$this$readBytes`.openStream();
      var var2: java.lang.Throwable = null;

      try {
         try {
            var it: InputStream = var1 as InputStream;
            var var9: ByteArray = ByteStreamsKt.readBytes(it);
         } catch (java.lang.Throwable var5) {
            var2 = var5;
            throw var5;
         }
      } catch (java.lang.Throwable var6) {
         CloseableKt.closeFinally(var1, var2);
      }

      CloseableKt.closeFinally(var1, null);
   }
}

