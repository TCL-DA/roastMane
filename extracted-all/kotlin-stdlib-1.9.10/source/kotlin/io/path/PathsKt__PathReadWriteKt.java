package kotlin.io.path

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.OpenOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.Arrays
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nPathReadWrite.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathReadWrite.kt\nkotlin/io/path/PathsKt__PathReadWriteKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ReadWrite.kt\nkotlin/io/TextStreamsKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,326:1\n1#2:327\n1#2:329\n52#3:328\n1313#4,2:330\n*S KotlinDebug\n*F\n+ 1 PathReadWrite.kt\nkotlin/io/path/PathsKt__PathReadWriteKt\n*L\n202#1:329\n202#1:328\n202#1:330,2\n*E\n"])
// $VF: Class flags could not be determined
internal class PathsKt__PathReadWriteKt {
   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.reader(charset: Charset, vararg options: OpenOption): InputStreamReader {
      return new InputStreamReader(Files.newInputStream(`$this$reader`, Arrays.copyOf(options, options.length)), charset);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.bufferedReader(charset: Charset, bufferSize: Int, vararg options: OpenOption): BufferedReader {
      return new BufferedReader(
         new InputStreamReader(Files.newInputStream(`$this$bufferedReader`, Arrays.copyOf(options, options.length)), charset), bufferSize
      );
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.writer(charset: Charset, vararg options: OpenOption): OutputStreamWriter {
      return new OutputStreamWriter(Files.newOutputStream(`$this$writer`, Arrays.copyOf(options, options.length)), charset);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.bufferedWriter(charset: Charset, bufferSize: Int, vararg options: OpenOption): BufferedWriter {
      return new BufferedWriter(
         new OutputStreamWriter(Files.newOutputStream(`$this$bufferedWriter`, Arrays.copyOf(options, options.length)), charset), bufferSize
      );
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.readBytes(): ByteArray {
      var var10000: ByteArray = Files.readAllBytes(`$this$readBytes`);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.writeBytes(array: ByteArray, vararg options: OpenOption) {
      Files.write(`$this$writeBytes`, array, Arrays.copyOf(options, options.length));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.appendBytes(array: ByteArray) {
      Files.write(`$this$appendBytes`, array, StandardOpenOption.APPEND);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Path.readText(charset: Charset): String {
      label19: {
         var var3: Array<OpenOption> = new OpenOption[0];
         var var2: Closeable = new InputStreamReader(Files.newInputStream(`$this$readText`, Arrays.copyOf(var3, var3.length)), charset);
         var var10: java.lang.Throwable = null;

         try {
            try {
               var var11: java.lang.String = TextStreamsKt.readText(var2 as InputStreamReader);
            } catch (java.lang.Throwable var6) {
               var10 = var6;
               throw var6;
            }
         } catch (java.lang.Throwable var7) {
            CloseableKt.closeFinally(var2, var10);
         }

         CloseableKt.closeFinally(var2, null);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Path.writeText(text: CharSequence, charset: Charset, vararg options: OpenOption) {
      label19: {
         var var10000: OutputStream = Files.newOutputStream(`$this$writeText`, Arrays.copyOf(options, options.length));
         var var12: Closeable = new OutputStreamWriter(var10000, charset);
         var var5: java.lang.Throwable = null;

         try {
            try {
               var var13: Writer = (var12 as OutputStreamWriter).append(text);
            } catch (java.lang.Throwable var8) {
               var5 = var8;
               throw var8;
            }
         } catch (java.lang.Throwable var9) {
            CloseableKt.closeFinally(var12, var5);
         }

         CloseableKt.closeFinally(var12, null);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @Throws(java/io/IOException::class)
   @JvmStatic
   public fun Path.appendText(text: CharSequence, charset: Charset) {
      label19: {
         var var10000: OutputStream = Files.newOutputStream(`$this$appendText`, StandardOpenOption.APPEND);
         var var11: Closeable = new OutputStreamWriter(var10000, charset);
         var var12: java.lang.Throwable = null;

         try {
            try {
               var var13: Writer = (var11 as OutputStreamWriter).append(text);
            } catch (java.lang.Throwable var7) {
               var12 = var7;
               throw var7;
            }
         } catch (java.lang.Throwable var8) {
            CloseableKt.closeFinally(var11, var12);
         }

         CloseableKt.closeFinally(var11, null);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.forEachLine(charset: Charset, action: (String) -> Unit) {
      label28: {
         var var10000: BufferedReader = Files.newBufferedReader(`$this$forEachLine`, charset);
         var var5: Closeable = var10000 as BufferedReader;
         var var6: java.lang.Throwable = null;

         try {
            try {
               var it: Sequence;
               for (Object element$iv : it) {
                  action.invoke(`element$iv`);
               }
            } catch (java.lang.Throwable var15) {
               var6 = var15;
               throw var15;
            }
         } catch (java.lang.Throwable var16) {
            InlineMarker.finallyStart(1);
            CloseableKt.closeFinally(var5, var6);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var5, null);
         InlineMarker.finallyEnd(1);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.inputStream(vararg options: OpenOption): InputStream {
      var var10000: InputStream = Files.newInputStream(`$this$inputStream`, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.outputStream(vararg options: OpenOption): OutputStream {
      var var10000: OutputStream = Files.newOutputStream(`$this$outputStream`, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.readLines(charset: Charset): List<String> {
      var var10000: java.util.List = Files.readAllLines(`$this$readLines`, charset);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun <T> Path.useLines(charset: Charset, block: (Sequence<String>) -> T): T {
      label19: {
         var var3: Closeable = Files.newBufferedReader(`$this$useLines`, charset);
         var var4: java.lang.Throwable = null;

         try {
            try {
               var it: BufferedReader = var3 as BufferedReader;
               it = (BufferedReader)block.invoke(TextStreamsKt.lineSequence(it));
            } catch (java.lang.Throwable var7) {
               var4 = var7;
               throw var7;
            }
         } catch (java.lang.Throwable var8) {
            InlineMarker.finallyStart(1);
            CloseableKt.closeFinally(var3, var4);
            InlineMarker.finallyEnd(1);
         }

         InlineMarker.finallyStart(1);
         CloseableKt.closeFinally(var3, null);
         InlineMarker.finallyEnd(1);
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.writeLines(lines: Iterable<CharSequence>, charset: Charset, vararg options: OpenOption): Path {
      var var10000: Path = Files.write(`$this$writeLines`, lines, charset, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.writeLines(lines: Sequence<CharSequence>, charset: Charset, vararg options: OpenOption): Path {
      var var10000: Path = Files.write(`$this$writeLines`, SequencesKt.asIterable(lines), charset, Arrays.copyOf(options, options.length));
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.appendLines(lines: Iterable<CharSequence>, charset: Charset): Path {
      var var10000: Path = Files.write(`$this$appendLines`, lines, charset, StandardOpenOption.APPEND);
      return var10000;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalPathApi::class])
   @InlineOnly
   @Throws(java/io/IOException::class)
   @JvmStatic
   public inline fun Path.appendLines(lines: Sequence<CharSequence>, charset: Charset): Path {
      var var10000: Path = Files.write(`$this$appendLines`, SequencesKt.asIterable(lines), charset, StandardOpenOption.APPEND);
      return var10000;
   }

   open fun PathsKt__PathReadWriteKt() {
   }
}
