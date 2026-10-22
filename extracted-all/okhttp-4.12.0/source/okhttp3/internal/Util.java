@file:JvmName(name = "Util")

@file:SourceDebugExtension(["SMAP\nUtil.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Util.kt\nokhttp3/internal/Util\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,636:1\n37#2,2:637\n1627#3,6:639\n1#4:645\n1549#5:646\n1620#5,3:647\n*S KotlinDebug\n*F\n+ 1 Util.kt\nokhttp3/internal/Util\n*L\n127#1:637,2\n167#1:639,6\n300#1:646\n300#1:647,3\n*E\n"])

package okhttp3.internal

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.lang.reflect.Field
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Comparator
import java.util.LinkedHashMap
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import okhttp3.Call
import okhttp3.EventListener
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.EventListener.Factory
import okhttp3.internal.http2.Header
import okhttp3.internal.io.FileSystem
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.Options
import okio.Sink
import okio.Source

public final val EMPTY_BYTE_ARRAY: ByteArray = new byte[0]
public final val EMPTY_HEADERS: Headers = Headers.Companion.of()
public final val EMPTY_REQUEST: RequestBody = RequestBody.Companion.create$default(RequestBody.Companion, EMPTY_BYTE_ARRAY, null, 0, 0, 7, null)
public final val EMPTY_RESPONSE: ResponseBody = ResponseBody.Companion.create$default(ResponseBody.Companion, EMPTY_BYTE_ARRAY, null, 1, null)
private final val UNICODE_BOMS: Options =
   Options.Companion
      .of(
         new ByteString[]{
            ByteString.Companion.decodeHex("efbbbf"),
            ByteString.Companion.decodeHex("feff"),
            ByteString.Companion.decodeHex("fffe"),
            ByteString.Companion.decodeHex("0000ffff"),
            ByteString.Companion.decodeHex("ffff0000")
         }
      )
      public final val UTC: TimeZone
private final val VERIFY_AS_IP_ADDRESS: Regex = new Regex("([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)")
internal final val assertionsEnabled: Boolean = OkHttpClient.class.desiredAssertionStatus()
internal final val okHttpName: String
public const val userAgent: String = "okhttp/4.12.0"

public fun checkOffsetAndCount(arrayLength: Long, offset: Long, count: Long) {
   if ((offset or count) < 0L || offset > arrayLength || arrayLength - offset < count) {
      throw new ArrayIndexOutOfBoundsException();
   }
}

public fun threadFactory(name: String, daemon: Boolean): ThreadFactory {
   return Util::threadFactory$lambda$1;
}

public fun Array<String>.intersect(other: Array<String>, comparator: Comparator<in String>): Array<String> {
   var result: java.util.List = new ArrayList();

   for (java.lang.String a : $this$intersect) {
      for (java.lang.String b : other) {
         if (comparator.compare(`thisCollection$iv`, b) == 0) {
            result.add(`thisCollection$iv`);
            break;
         }
      }
   }

   return result.toArray(new java.lang.String[0]);
}

public fun Array<String>.hasIntersection(other: Array<String>?, comparator: Comparator<in String>): Boolean {
   // $VF: Couldn't be decompiled
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   // java.lang.NullPointerException: Cannot read field "bytecode" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInstance()" is null
   //   at org.vineflower.kotlin.pass.KMergePass.matchForEach(KMergePass.java:169)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoop(KMergePass.java:53)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:39)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
   //   at org.vineflower.kotlin.pass.KMergePass.enhanceLoopsRec(KMergePass.java:34)
   //   at org.vineflower.kotlin.pass.KMergePass.run(KMergePass.java:23)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.LoopingPassBuilder$CompiledPass.run(LoopingPassBuilder.java:43)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.NamedPass.run(NamedPass.java:18)
   //   at org.jetbrains.java.decompiler.api.plugin.pass.MainPassBuilder$CompiledPass.run(MainPassBuilder.java:34)
   //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:160)
   //
   // Bytecode:
   // 00: aload 0
   // 01: ldc "<this>"
   // 03: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 06: aload 2
   // 07: ldc "comparator"
   // 09: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   // 0c: aload 0
   // 0d: arraylength
   // 0e: ifne 15
   // 11: bipush 1
   // 12: goto 16
   // 15: bipush 0
   // 16: ifne 2a
   // 19: aload 1
   // 1a: ifnull 2a
   // 1d: aload 1
   // 1e: arraylength
   // 1f: ifne 26
   // 22: bipush 1
   // 23: goto 27
   // 26: bipush 0
   // 27: ifeq 2c
   // 2a: bipush 0
   // 2b: ireturn
   // 2c: bipush 0
   // 2d: istore 3
   // 2e: aload 0
   // 2f: arraylength
   // 30: istore 4
   // 32: iload 3
   // 33: iload 4
   // 35: if_icmpge 6e
   // 38: aload 0
   // 39: iload 3
   // 3a: aaload
   // 3b: astore 5
   // 3d: aload 1
   // 3e: invokestatic kotlin/jvm/internal/ArrayIteratorKt.iterator ([Ljava/lang/Object;)Ljava/util/Iterator;
   // 41: astore 6
   // 43: aload 6
   // 45: invokeinterface java/util/Iterator.hasNext ()Z 1
   // 4a: ifeq 68
   // 4d: aload 6
   // 4f: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
   // 54: checkcast java/lang/String
   // 57: astore 7
   // 59: aload 2
   // 5a: aload 5
   // 5c: aload 7
   // 5e: invokeinterface java/util/Comparator.compare (Ljava/lang/Object;Ljava/lang/Object;)I 3
   // 63: ifne 43
   // 66: bipush 1
   // 67: ireturn
   // 68: iinc 3 1
   // 6b: goto 32
   // 6e: bipush 0
   // 6f: ireturn
}

public fun HttpUrl.toHostHeader(includeDefaultPort: Boolean): String {
   var host: java.lang.String = if (StringsKt.contains$default(`$this$toHostHeader`.host(), ":", false, 2, null))
      '[' + `$this$toHostHeader`.host() + ']'
      else
      `$this$toHostHeader`.host();
   return if (!includeDefaultPort && `$this$toHostHeader`.port() == HttpUrl.Companion.defaultPort(`$this$toHostHeader`.scheme()))
      host
      else
      host + ':' + `$this$toHostHeader`.port();
}

@JvmSynthetic
fun `toHostHeader$default`(var0: HttpUrl, var1: Boolean, var2: Int, var3: Any): java.lang.String {
   if ((var2 and 1) != 0) {
      var1 = false;
   }

   return toHostHeader(var0, var1);
}

public fun Array<String>.indexOf(value: String, comparator: Comparator<String>): Int {
   var `$this$indexOfFirst$iv`: Array<Any> = `$this$indexOf`;
   var `index$iv`: Int = 0;
   var var6: Int = `$this$indexOf`.length;

   var var10000: Int;
   while (true) {
      if (`index$iv` >= var6) {
         var10000 = -1;
         break;
      }

      if (comparator.compare(`$this$indexOfFirst$iv`[`index$iv`], value) == 0) {
         var10000 = `index$iv`;
         break;
      }

      `index$iv`++;
   }

   return var10000;
}

public fun Array<String>.concat(value: String): Array<String> {
   var var10000: Array<Any> = Arrays.copyOf(`$this$concat`, `$this$concat`.length + 1);
   var result: Array<java.lang.String> = var10000 as java.lang.String[];
   (var10000 as java.lang.String[])[ArraysKt.getLastIndex(var10000 as java.lang.String[])] = value;
   return result;
}

public fun String.indexOfFirstNonAsciiWhitespace(startIndex: Int, endIndex: Int): Int {
   for (int i = startIndex; i < endIndex; i++) {
      var var4: Char = `$this$indexOfFirstNonAsciiWhitespace`.charAt(i);
      if (var4 != '\t' && var4 != '\n' && var4 != '\f' && var4 != '\r' && var4 != ' ') {
         return i;
      }
   }

   return endIndex;
}

@JvmSynthetic
fun `indexOfFirstNonAsciiWhitespace$default`(var0: java.lang.String, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 1) != 0) {
      var1 = 0;
   }

   if ((var3 and 2) != 0) {
      var2 = var0.length();
   }

   return indexOfFirstNonAsciiWhitespace(var0, var1, var2);
}

public fun String.indexOfLastNonAsciiWhitespace(startIndex: Int, endIndex: Int): Int {
   var i: Int = endIndex - 1;
   if (startIndex <= endIndex - 1) {
      while (true) {
         var var4: Char = `$this$indexOfLastNonAsciiWhitespace`.charAt(i);
         if (var4 != '\t' && var4 != '\n' && var4 != '\f' && var4 != '\r' && var4 != ' ') {
            return i + 1;
         }

         if (i == startIndex) {
            break;
         }

         i--;
      }
   }

   return startIndex;
}

@JvmSynthetic
fun `indexOfLastNonAsciiWhitespace$default`(var0: java.lang.String, var1: Int, var2: Int, var3: Int, var4: Any): Int {
   if ((var3 and 1) != 0) {
      var1 = 0;
   }

   if ((var3 and 2) != 0) {
      var2 = var0.length();
   }

   return indexOfLastNonAsciiWhitespace(var0, var1, var2);
}

public fun String.trimSubstring(startIndex: Int, endIndex: Int): String {
   var start: Int = indexOfFirstNonAsciiWhitespace(`$this$trimSubstring`, startIndex, endIndex);
   var var10000: java.lang.String = `$this$trimSubstring`.substring(start, indexOfLastNonAsciiWhitespace(`$this$trimSubstring`, start, endIndex));
   return var10000;
}

@JvmSynthetic
fun `trimSubstring$default`(var0: java.lang.String, var1: Int, var2: Int, var3: Int, var4: Any): java.lang.String {
   if ((var3 and 1) != 0) {
      var1 = 0;
   }

   if ((var3 and 2) != 0) {
      var2 = var0.length();
   }

   return trimSubstring(var0, var1, var2);
}

public fun String.delimiterOffset(delimiters: String, startIndex: Int, endIndex: Int): Int {
   for (int i = startIndex; i < endIndex; i++) {
      if (StringsKt.contains$default(delimiters, `$this$delimiterOffset`.charAt(i), false, 2, null)) {
         return i;
      }
   }

   return endIndex;
}

@JvmSynthetic
fun `delimiterOffset$default`(var0: java.lang.String, var1: java.lang.String, var2: Int, var3: Int, var4: Int, var5: Any): Int {
   if ((var4 and 2) != 0) {
      var2 = 0;
   }

   if ((var4 and 4) != 0) {
      var3 = var0.length();
   }

   return delimiterOffset(var0, var1, var2, var3);
}

public fun String.delimiterOffset(delimiter: Char, startIndex: Int, endIndex: Int): Int {
   for (int i = startIndex; i < endIndex; i++) {
      if (`$this$delimiterOffset`.charAt(i) == delimiter) {
         return i;
      }
   }

   return endIndex;
}

@JvmSynthetic
fun `delimiterOffset$default`(var0: java.lang.String, var1: Char, var2: Int, var3: Int, var4: Int, var5: Any): Int {
   if ((var4 and 2) != 0) {
      var2 = 0;
   }

   if ((var4 and 4) != 0) {
      var3 = var0.length();
   }

   return delimiterOffset(var0, var1, var2, var3);
}

public fun String.indexOfControlOrNonAscii(): Int {
   var i: Int = 0;

   for (int var2 = $this$indexOfControlOrNonAscii.length(); i < var2; i++) {
      var c: Char = `$this$indexOfControlOrNonAscii`.charAt(i);
      if (Intrinsics.compare(c, 31) <= 0 || Intrinsics.compare(c, 127) >= 0) {
         return i;
      }
   }

   return -1;
}

public fun String.canParseAsIpAddress(): Boolean {
   return VERIFY_AS_IP_ADDRESS.matches(`$this$canParseAsIpAddress`);
}

public fun isSensitiveHeader(name: String): Boolean {
   return StringsKt.equals(name, "Authorization", true)
      || StringsKt.equals(name, "Cookie", true)
      || StringsKt.equals(name, "Proxy-Authorization", true)
      || StringsKt.equals(name, "Set-Cookie", true);
}

public fun format(format: String, vararg args: Any): String {
   var var2: Locale = Locale.US;
   var var3: Array<Any> = Arrays.copyOf(args, args.length);
   var var10000: java.lang.String = java.lang.String.format(var2, format, Arrays.copyOf(var3, var3.length));
   return var10000;
}

@Throws(java/io/IOException::class)
public fun BufferedSource.readBomAsCharset(default: Charset): Charset {
   var var10000: Charset;
   switch ($this$readBomAsCharset.select(UNICODE_BOMS)) {
      case -1:
         var10000 = var1;
         break;
      case 0:
         var10000 = StandardCharsets.UTF_8;
         break;
      case 1:
         var10000 = StandardCharsets.UTF_16BE;
         break;
      case 2:
         var10000 = StandardCharsets.UTF_16LE;
         break;
      case 3:
         var10000 = Charsets.INSTANCE.UTF32_BE();
         break;
      case 4:
         var10000 = Charsets.INSTANCE.UTF32_LE();
         break;
      default:
         throw new AssertionError();
   }

   return var10000;
}

public fun checkDuration(name: String, duration: Long, unit: TimeUnit?): Int {
   if (duration < 0L) {
      throw new IllegalStateException((name + " < 0").toString());
   } else if (unit == null) {
      throw new IllegalStateException("unit == null".toString());
   } else {
      var millis: Long = unit.toMillis(duration);
      if (millis > 2147483647L) {
         throw new IllegalArgumentException((name + " too large.").toString());
      } else if (millis == 0L && duration > 0L) {
         throw new IllegalArgumentException((name + " too small.").toString());
      } else {
         return (int)millis;
      }
   }
}

public fun Char.parseHexDigit(): Int {
   return if (48 <= `$this$parseHexDigit` && `$this$parseHexDigit` < 58)
      `$this$parseHexDigit` - 48
      else
      (
         if (97 <= `$this$parseHexDigit` && `$this$parseHexDigit` < 103)
            `$this$parseHexDigit` - 97 + 10
            else
            (if (65 <= `$this$parseHexDigit` && `$this$parseHexDigit` < 71) `$this$parseHexDigit` - 65 + 10 else -1)
      );
}

public fun List<Header>.toHeaders(): Headers {
   var builder: Headers.Builder = new Headers.Builder();

   for (Header var3 : $this$toHeaders) {
      builder.addLenient$okhttp(var3.component1().utf8(), var3.component2().utf8());
   }

   return builder.build();
}

public fun Headers.toHeaderList(): List<Header> {
   var `$this$map$iv`: java.lang.Iterable = RangesKt.until(0, `$this$toHeaderList`.size()) as java.lang.Iterable;
   var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));
   var var6: java.util.Iterator = `$this$map$iv`.iterator();

   while (var6.hasNext()) {
      var `item$iv$iv`: Int = (var6 as IntIterator).nextInt();
      `destination$iv$iv`.add(new Header(`$this$toHeaderList`.name(`item$iv$iv`), `$this$toHeaderList`.value(`item$iv$iv`)));
   }

   return `destination$iv$iv` as java.util.List<Header>;
}

public fun HttpUrl.canReuseConnectionFor(other: HttpUrl): Boolean {
   return `$this$canReuseConnectionFor`.host() == other.host()
      && `$this$canReuseConnectionFor`.port() == other.port()
      && `$this$canReuseConnectionFor`.scheme() == other.scheme();
}

public fun EventListener.asFactory(): Factory {
   return Util::asFactory$lambda$8;
}

public infix fun Byte.and(mask: Int): Int {
   return `$this$and` and mask;
}

public infix fun Short.and(mask: Int): Int {
   return `$this$and` and mask;
}

public infix fun Int.and(mask: Long): Long {
   return (long)`$this$and` and mask;
}

@Throws(java/io/IOException::class)
public fun BufferedSink.writeMedium(medium: Int) {
   `$this$writeMedium`.writeByte(medium ushr 16 and 255);
   `$this$writeMedium`.writeByte(medium ushr 8 and 255);
   `$this$writeMedium`.writeByte(medium and 255);
}

@Throws(java/io/IOException::class)
public fun BufferedSource.readMedium(): Int {
   return and(`$this$readMedium`.readByte(), 255) shl 16 or and(`$this$readMedium`.readByte(), 255) shl 8 or and(`$this$readMedium`.readByte(), 255);
}

@Throws(java/io/IOException::class)
public fun Source.skipAll(duration: Int, timeUnit: TimeUnit): Boolean {
   label77: {
      var nowNs: Long = System.nanoTime();
      var originalDurationNs: Long = if (`$this$skipAll`.timeout().hasDeadline())
         `$this$skipAll`.timeout().deadlineNanoTime() - nowNs
         else
         java.lang.Long.MAX_VALUE;
      `$this$skipAll`.timeout().deadlineNanoTime(nowNs + Math.min(originalDurationNs, timeUnit.toNanos((long)duration)));

      try {
         try {
            var var13: Buffer = new Buffer();

            while ($this$skipAll.read(skipBuffer, 8192L) != -1L) {
               var13.clear();
            }
         } catch (InterruptedIOException var9) {
         }
      } catch (java.lang.Throwable var10) {
         if (originalDurationNs == java.lang.Long.MAX_VALUE) {
            `$this$skipAll`.timeout().clearDeadline();
         } else {
            `$this$skipAll`.timeout().deadlineNanoTime(nowNs + originalDurationNs);
         }
      }

      if (originalDurationNs == java.lang.Long.MAX_VALUE) {
         `$this$skipAll`.timeout().clearDeadline();
      } else {
         `$this$skipAll`.timeout().deadlineNanoTime(nowNs + originalDurationNs);
      }

      if (originalDurationNs == java.lang.Long.MAX_VALUE) {
         `$this$skipAll`.timeout().clearDeadline();
      } else {
         `$this$skipAll`.timeout().deadlineNanoTime(nowNs + originalDurationNs);
      }
   }
}

public fun Source.discard(timeout: Int, timeUnit: TimeUnit): Boolean {
   var var3: Boolean;
   try {
      var3 = skipAll(`$this$discard`, timeout, timeUnit);
   } catch (IOException var5) {
      var3 = false;
   }

   return var3;
}

public fun Socket.peerName(): String {
   var address: SocketAddress = `$this$peerName`.getRemoteSocketAddress();
   var var10000: java.lang.String;
   if (address is InetSocketAddress) {
      var10000 = (address as InetSocketAddress).getHostName();
   } else {
      var10000 = address.toString();
   }

   return var10000;
}

public fun Socket.isHealthy(source: BufferedSource): Boolean {
   label30: {
      var readTimeout: Int;
      try {
         readTimeout = `$this$isHealthy`.getSoTimeout();

         try {
            `$this$isHealthy`.setSoTimeout(1);
            var _: Boolean = !source.exhausted();
         } catch (java.lang.Throwable var5) {
            `$this$isHealthy`.setSoTimeout(readTimeout);
         }

         `$this$isHealthy`.setSoTimeout(readTimeout);
      } catch (SocketTimeoutException var6) {
         readTimeout = 1;
      } catch (IOException var7) {
         readTimeout = 0;
      }

      return (boolean)readTimeout;
   }
}

public inline fun ignoreIoExceptions(block: () -> Unit) {
   try {
      block.invoke();
   } catch (IOException var3) {
   }
}

public inline fun threadName(name: String, block: () -> Unit) {
   label15: {
      var currentThread: Thread = Thread.currentThread();
      var oldName: java.lang.String = currentThread.getName();
      currentThread.setName(name);

      try {
         block.invoke();
      } catch (java.lang.Throwable var6) {
         InlineMarker.finallyStart(1);
         currentThread.setName(oldName);
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      currentThread.setName(oldName);
      InlineMarker.finallyEnd(1);
   }
}

public fun Buffer.skipAll(b: Byte): Int {
   var count: Int = 0;

   while (!$this$skipAll.exhausted() && $this$skipAll.getByte(0L) == b) {
      count++;
      `$this$skipAll`.readByte();
   }

   return count;
}

public fun String.indexOfNonWhitespace(startIndex: Int): Int {
   var i: Int = startIndex;

   for (int var3 = $this$indexOfNonWhitespace.length(); i < var3; i++) {
      var c: Char = `$this$indexOfNonWhitespace`.charAt(i);
      if (c != ' ' && c != '\t') {
         return i;
      }
   }

   return `$this$indexOfNonWhitespace`.length();
}

@JvmSynthetic
fun `indexOfNonWhitespace$default`(var0: java.lang.String, var1: Int, var2: Int, var3: Any): Int {
   if ((var2 and 1) != 0) {
      var1 = 0;
   }

   return indexOfNonWhitespace(var0, var1);
}

public fun Response.headersContentLength(): Long {
   var var10000: java.lang.String = `$this$headersContentLength`.headers().get("Content-Length");
   return if (var10000 != null) toLongOrDefault(var10000, -1L) else -1L;
}

public fun String.toLongOrDefault(defaultValue: Long): Long {
   var var3: Long;
   try {
      var3 = java.lang.Long.parseLong(`$this$toLongOrDefault`);
   } catch (NumberFormatException var6) {
      var3 = defaultValue;
   }

   return var3;
}

public fun String?.toNonNegativeInt(defaultValue: Int): Int {
   try {
      if (`$this$toNonNegativeInt` != null) {
         var _: Long = java.lang.Long.parseLong(`$this$toNonNegativeInt`);
         return if (_ > 2147483647L) Integer.MAX_VALUE else (if (_ < 0L) 0 else (int)_);
      } else {
         return defaultValue;
      }
   } catch (NumberFormatException var4) {
      return defaultValue;
   }
}

public fun <T> List<T>.toImmutableList(): List<T> {
   var var10000: java.util.List = Collections.unmodifiableList(CollectionsKt.toMutableList(`$this$toImmutableList`));
   return var10000;
}

@SafeVarargs
public fun <T> immutableListOf(vararg elements: T): List<T> {
   var var1: Array<Any> = elements.clone() as Object[];
   var var10000: java.util.List = Collections.unmodifiableList(CollectionsKt.listOf(Arrays.copyOf(var1, var1.length)));
   return var10000;
}

public fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> {
   var var10000: java.util.Map;
   if (`$this$toImmutableMap`.isEmpty()) {
      var10000 = MapsKt.emptyMap();
   } else {
      var10000 = Collections.unmodifiableMap(new LinkedHashMap(`$this$toImmutableMap`));
   }

   return var10000;
}

public fun Closeable.closeQuietly() {
   try {
      `$this$closeQuietly`.close();
   } catch (RuntimeException var2) {
      throw var2;
   } catch (Exception var3) {
   }
}

public fun Socket.closeQuietly() {
   try {
      `$this$closeQuietly`.close();
   } catch (AssertionError var2) {
      throw var2;
   } catch (RuntimeException var3) {
      if (var3.getMessage() == "bio == null") {
         return;
      }

      throw var3;
   } catch (Exception var4) {
   }
}

public fun ServerSocket.closeQuietly() {
   try {
      `$this$closeQuietly`.close();
   } catch (RuntimeException var2) {
      throw var2;
   } catch (Exception var3) {
   }
}

public fun FileSystem.isCivilized(file: File): Boolean {
   label30: {
      var var2: Closeable = `$this$isCivilized`.sink(file) as Closeable;
      var var3: java.lang.Throwable = null;

      label27: {
         try {
            try {
               var it: Sink = var2 as Sink;

               try {
                  `$this$isCivilized`.delete(file);
                  break label27;
               } catch (IOException var8) {
               }
            } catch (java.lang.Throwable var9) {
               var3 = var9;
               throw var9;
            }
         } catch (java.lang.Throwable var10) {
            CloseableKt.closeFinally(var2, var3);
         }

         CloseableKt.closeFinally(var2, null);
      }

      CloseableKt.closeFinally(var2, null);
   }
}

public fun Long.toHexString(): String {
   var var10000: java.lang.String = java.lang.Long.toHexString(`$this$toHexString`);
   return var10000;
}

public fun Int.toHexString(): String {
   var var10000: java.lang.String = Integer.toHexString(`$this$toHexString`);
   return var10000;
}

public inline fun Any.wait() {
   `$this$wait`.wait();
}

public inline fun Any.notify() {
   `$this$notify`.notify();
}

public inline fun Any.notifyAll() {
   `$this$notifyAll`.notifyAll();
}

public fun <T> readFieldOrNull(instance: Any, fieldType: Class<T>, fieldName: String): T? {
   var c: Class = instance.getClass();

   while (!(c == Object::class.java)) {
      try {
         var delegate: Field = c.getDeclaredField(fieldName);
         delegate.setAccessible(true);
         var value: Any = delegate.get(instance);
         return (T)(if (!fieldType.isInstance(value)) null else fieldType.cast(value));
      } catch (NoSuchFieldException var6) {
         var var10000: Class = c.getSuperclass();
         c = var10000;
      }
   }

   if (!(fieldName == "delegate")) {
      var var7: Any = readFieldOrNull(instance, Object.class, "delegate");
      if (var7 != null) {
         return (T)readFieldOrNull(var7, fieldType, fieldName);
      }
   }

   return null;
}

internal fun <E> MutableList<E>.addIfAbsent(element: E) {
   if (!`$this$addIfAbsent`.contains(element)) {
      `$this$addIfAbsent`.add(element);
   }
}

internal inline fun Any.assertThreadHoldsLock() {
   if (assertionsEnabled && !Thread.holdsLock(`$this$assertThreadHoldsLock`)) {
      throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + `$this$assertThreadHoldsLock`);
   }
}

internal inline fun Any.assertThreadDoesntHoldLock() {
   if (assertionsEnabled && Thread.holdsLock(`$this$assertThreadDoesntHoldLock`)) {
      throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST NOT hold lock on " + `$this$assertThreadDoesntHoldLock`);
   }
}

public fun Exception.withSuppressed(suppressed: List<Exception>): Throwable {
   var `$this$withSuppressed_u24lambda_u2410`: Exception = `$this$withSuppressed`;

   for (Exception e : suppressed) {
      ExceptionsKt.addSuppressed(`$this$withSuppressed_u24lambda_u2410`, e);
   }

   return `$this$withSuppressed`;
}

public inline fun <T> Iterable<T>.filterList(predicate: (T) -> Boolean): List<T> {
   var result: java.util.List = CollectionsKt.emptyList();

   for (Object i : $this$filterList) {
      if (predicate.invoke(i) as java.lang.Boolean) {
         if (result.isEmpty()) {
            result = new ArrayList();
         }

         TypeIntrinsics.asMutableList(result).add(i);
      }
   }

   return result;
}

fun `threadFactory$lambda$1`(`$name`: java.lang.String, `$daemon`: Boolean, runnable: Runnable): Thread {
   var var3: Thread = new Thread(runnable, `$name`);
   var3.setDaemon(`$daemon`);
   return var3;
}

fun `asFactory$lambda$8`(`$this_asFactory`: EventListener, it: Call): EventListener {
   return `$this_asFactory`;
}

