package kotlin.io

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.io.Reader
import java.io.Writer
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Arrays
import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nFileReadWrite.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileReadWrite.kt\nkotlin/io/FilesKt__FileReadWriteKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,232:1\n231#1:234\n1#2:233\n1#2:235\n*S KotlinDebug\n*F\n+ 1 FileReadWrite.kt\nkotlin/io/FilesKt__FileReadWriteKt\n*L\n230#1:234\n230#1:235\n*E\n"])
// $VF: Class flags could not be determined
internal class FilesKt__FileReadWriteKt : FilesKt__FilePathComponentsKt {
   @InlineOnly
   @JvmStatic
   public inline fun File.reader(charset: Charset): InputStreamReader {
      return new InputStreamReader(new FileInputStream(`$this$reader`), charset);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.bufferedReader(charset: Charset, bufferSize: Int): BufferedReader {
      var var3: Reader = new InputStreamReader(new FileInputStream(`$this$bufferedReader`), charset);
      return if (var3 is BufferedReader) var3 as BufferedReader else new BufferedReader(var3, bufferSize);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.writer(charset: Charset): OutputStreamWriter {
      return new OutputStreamWriter(new FileOutputStream(`$this$writer`), charset);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.bufferedWriter(charset: Charset, bufferSize: Int): BufferedWriter {
      var var3: Writer = new OutputStreamWriter(new FileOutputStream(`$this$bufferedWriter`), charset);
      return if (var3 is BufferedWriter) var3 as BufferedWriter else new BufferedWriter(var3, bufferSize);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.printWriter(charset: Charset): PrintWriter {
      var var4: Writer = new OutputStreamWriter(new FileOutputStream(`$this$printWriter`), charset);
      return new PrintWriter(if (var4 is BufferedWriter) var4 as BufferedWriter else new BufferedWriter(var4, 8192));
   }

   @JvmStatic
   public fun File.readBytes(): ByteArray {
      label47: {
         var var1: Closeable = new FileInputStream(`$this$readBytes`);
         var var2: java.lang.Throwable = null;

         try {
            try {
               var input: FileInputStream = var1 as FileInputStream;
               var offset: Int = 0;
               var result: Long = `$this$readBytes`.length();
               if (result > 2147483647L) {
                  throw new OutOfMemoryError("File " + `$this$readBytes` + " is too big (" + result + " bytes) to fit in memory.");
               }

               var remaining: Int = (int)result;
               var var18: ByteArray = new byte[(int)result];

               while (remaining > 0) {
                  var extraByte: Int = input.read(var18, offset, remaining);
                  if (extraByte < 0) {
                     break;
                  }

                  remaining -= extraByte;
                  offset += extraByte;
               }

               if (remaining > 0) {
                  ;
               } else {
                  var var20: Int = input.read();
                  if (var20 != -1) {
                     var extra: ExposingBufferByteArrayOutputStream = new ExposingBufferByteArrayOutputStream(8193);
                     extra.write(var20);
                     ByteStreamsKt.copyTo$default(input, extra, 0, 2, null);
                     var var19: Int = var18.length + extra.size();
                     if (var19 < 0) {
                        throw new OutOfMemoryError("File " + `$this$readBytes` + " is too big to fit in memory.");
                     }

                     var var10000: ByteArray = extra.getBuffer();
                     var var10001: ByteArray = Arrays.copyOf(var18, var19);
                     ArraysKt.copyInto(var10000, var10001, var18.length, 0, extra.size());
                  }
               }
            } catch (java.lang.Throwable var14) {
               var2 = var14;
               throw var14;
            }
         } catch (java.lang.Throwable var15) {
            CloseableKt.closeFinally(var1, var2);
         }

         CloseableKt.closeFinally(var1, null);
      }
   }

   @JvmStatic
   public fun File.writeBytes(array: ByteArray) {
      label19: {
         var var2: Closeable = new FileOutputStream(`$this$writeBytes`);
         var var3: java.lang.Throwable = null;

         try {
            try {
               (var2 as FileOutputStream).write(array);
            } catch (java.lang.Throwable var6) {
               var3 = var6;
               throw var6;
            }
         } catch (java.lang.Throwable var7) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @JvmStatic
   public fun File.appendBytes(array: ByteArray) {
      label19: {
         var var2: Closeable = new FileOutputStream(`$this$appendBytes`, true);
         var var3: java.lang.Throwable = null;

         try {
            try {
               (var2 as FileOutputStream).write(array);
            } catch (java.lang.Throwable var6) {
               var3 = var6;
               throw var6;
            }
         } catch (java.lang.Throwable var7) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @JvmStatic
   public fun File.readText(charset: Charset): String {
      label19: {
         var var2: Closeable = new InputStreamReader(new FileInputStream(`$this$readText`), charset);
         var var3: java.lang.Throwable = null;

         try {
            try {
               var var10: java.lang.String = TextStreamsKt.readText(var2 as InputStreamReader);
            } catch (java.lang.Throwable var6) {
               var3 = var6;
               throw var6;
            }
         } catch (java.lang.Throwable var7) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @JvmStatic
   public fun File.writeText(text: String, charset: Charset) {
      var var10001: ByteArray = text.getBytes(charset);
      FilesKt.writeBytes(`$this$writeText`, var10001);
   }

   @JvmStatic
   public fun File.appendText(text: String, charset: Charset) {
      var var10001: ByteArray = text.getBytes(charset);
      FilesKt.appendBytes(`$this$appendText`, var10001);
   }

   @JvmStatic
   public fun File.forEachBlock(action: (ByteArray, Int) -> Unit) {
      FilesKt.forEachBlock(`$this$forEachBlock`, 4096, action);
   }

   @JvmStatic
   public fun File.forEachBlock(blockSize: Int, action: (ByteArray, Int) -> Unit) {
      label28: {
         var arr: ByteArray = new byte[RangesKt.coerceAtLeast(blockSize, 512)];
         var var4: Closeable = new FileInputStream(`$this$forEachBlock`);
         var var5: java.lang.Throwable = null;

         try {
            try {
               var input: FileInputStream = var4 as FileInputStream;

               while (true) {
                  var size: Int = input.read(arr);
                  if (size <= 0) {
                     break;
                  }

                  action.invoke(arr, size);
               }
            } catch (java.lang.Throwable var9) {
               var5 = var9;
               throw var9;
            }
         } catch (java.lang.Throwable var10) {
            CloseableKt.closeFinally(var4, var5);
         }

         CloseableKt.closeFinally(var4, null);
      }
   }

   @JvmStatic
   public fun File.forEachLine(charset: Charset, action: (String) -> Unit) {
      TextStreamsKt.forEachLine(new BufferedReader(new InputStreamReader(new FileInputStream(`$this$forEachLine`), charset)), action);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.inputStream(): FileInputStream {
      return new FileInputStream(`$this$inputStream`);
   }

   @InlineOnly
   @JvmStatic
   public inline fun File.outputStream(): FileOutputStream {
      return new FileOutputStream(`$this$outputStream`);
   }

   @JvmStatic
   public fun File.readLines(charset: Charset): List<String> {
      var result: ArrayList = new ArrayList();
      FilesKt.forEachLine(`$this$readLines`, charset, (new Function1<java.lang.String, Unit>(result) {
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

   @JvmStatic
   public inline fun <T> File.useLines(charset: Charset, block: (Sequence<String>) -> T): T {
      label24: {
         var it: Reader = new InputStreamReader(new FileInputStream(`$this$useLines`), charset);
         var var4: Closeable = if (it is BufferedReader) it as BufferedReader else new BufferedReader(it, 8192);
         var var12: java.lang.Throwable = null;

         try {
            try {
               it = (Reader)block.invoke(TextStreamsKt.lineSequence(var4 as BufferedReader));
            } catch (java.lang.Throwable var8) {
               var12 = var8;
               throw var8;
            }
         } catch (java.lang.Throwable var9) {
            InlineMarker.finallyStart(1);
            CloseableKt.closeFinally(var4, var12);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var4, null);
         InlineMarker.finallyEnd(1);
      }
   }

   open fun FilesKt__FileReadWriteKt() {
   }
}
