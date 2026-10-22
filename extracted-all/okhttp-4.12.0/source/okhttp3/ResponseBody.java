package okhttp3

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

@SourceDebugExtension(["SMAP\nResponseBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody\n*L\n1#1,321:1\n140#1,11:322\n140#1,11:333\n*S KotlinDebug\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody\n*L\n124#1:322,11\n134#1:333,11\n*E\n"])
abstract class ResponseBody : Closeable {
   @JvmStatic
   public ResponseBody.Companion Companion = new ResponseBody.Companion(null);

   private final var reader: Reader?

   public abstract fun contentType(): MediaType? {
   }

   public abstract fun contentLength(): Long {
   }

   public fun byteStream(): InputStream {
      return this.source().inputStream();
   }

   public abstract fun source(): BufferedSource {
   }

   @Throws(java/io/IOException::class)
   public fun bytes(): ByteArray {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: invokevirtual okhttp3/ResponseBody.contentLength ()J
      // 08: lstore 3
      // 09: lload 3
      // 0a: ldc2_w 2147483647
      // 0d: lcmp
      // 0e: ifle 2c
      // 11: new java/io/IOException
      // 14: dup
      // 15: new java/lang/StringBuilder
      // 18: dup
      // 19: invokespecial java/lang/StringBuilder.<init> ()V
      // 1c: ldc "Cannot buffer entire body for content length: "
      // 1e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 21: lload 3
      // 22: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // 25: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 28: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // 2c: aload 1
      // 2d: invokevirtual okhttp3/ResponseBody.source ()Lokio/BufferedSource;
      // 30: checkcast java/io/Closeable
      // 33: astore 5
      // 35: aconst_null
      // 36: astore 6
      // 38: nop
      // 39: aload 5
      // 3b: checkcast okio/BufferedSource
      // 3e: astore 7
      // 40: bipush 0
      // 41: istore 8
      // 43: aload 7
      // 45: invokeinterface okio/BufferedSource.readByteArray ()[B 1
      // 4a: astore 7
      // 4c: aload 5
      // 4e: aload 6
      // 50: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 53: aload 7
      // 55: goto 6d
      // 58: astore 7
      // 5a: aload 7
      // 5c: astore 6
      // 5e: aload 7
      // 60: athrow
      // 61: astore 7
      // 63: aload 5
      // 65: aload 6
      // 67: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 6a: aload 7
      // 6c: athrow
      // 6d: astore 9
      // 6f: aload 9
      // 71: astore 8
      // 73: bipush 0
      // 74: istore 10
      // 76: aload 8
      // 78: arraylength
      // 79: istore 5
      // 7b: lload 3
      // 7c: ldc2_w -1
      // 7f: lcmp
      // 80: ifeq b5
      // 83: lload 3
      // 84: iload 5
      // 86: i2l
      // 87: lcmp
      // 88: ifeq b5
      // 8b: new java/io/IOException
      // 8e: dup
      // 8f: new java/lang/StringBuilder
      // 92: dup
      // 93: invokespecial java/lang/StringBuilder.<init> ()V
      // 96: ldc "Content-Length ("
      // 98: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 9b: lload 3
      // 9c: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // 9f: ldc ") and stream length ("
      // a1: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // a4: iload 5
      // a6: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
      // a9: ldc ") disagree"
      // ab: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // ae: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // b1: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // b4: athrow
      // b5: aload 9
      // b7: areturn
   }

   @Throws(java/io/IOException::class)
   public fun byteString(): ByteString {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 00: aload 0
      // 01: astore 1
      // 02: bipush 0
      // 03: istore 2
      // 04: aload 1
      // 05: invokevirtual okhttp3/ResponseBody.contentLength ()J
      // 08: lstore 3
      // 09: lload 3
      // 0a: ldc2_w 2147483647
      // 0d: lcmp
      // 0e: ifle 2c
      // 11: new java/io/IOException
      // 14: dup
      // 15: new java/lang/StringBuilder
      // 18: dup
      // 19: invokespecial java/lang/StringBuilder.<init> ()V
      // 1c: ldc "Cannot buffer entire body for content length: "
      // 1e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 21: lload 3
      // 22: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // 25: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 28: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // 2c: aload 1
      // 2d: invokevirtual okhttp3/ResponseBody.source ()Lokio/BufferedSource;
      // 30: checkcast java/io/Closeable
      // 33: astore 5
      // 35: aconst_null
      // 36: astore 6
      // 38: nop
      // 39: aload 5
      // 3b: checkcast okio/BufferedSource
      // 3e: astore 7
      // 40: bipush 0
      // 41: istore 8
      // 43: aload 7
      // 45: invokeinterface okio/BufferedSource.readByteString ()Lokio/ByteString; 1
      // 4a: astore 7
      // 4c: aload 5
      // 4e: aload 6
      // 50: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 53: aload 7
      // 55: goto 6d
      // 58: astore 7
      // 5a: aload 7
      // 5c: astore 6
      // 5e: aload 7
      // 60: athrow
      // 61: astore 7
      // 63: aload 5
      // 65: aload 6
      // 67: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 6a: aload 7
      // 6c: athrow
      // 6d: astore 9
      // 6f: aload 9
      // 71: astore 8
      // 73: bipush 0
      // 74: istore 10
      // 76: aload 8
      // 78: invokevirtual okio/ByteString.size ()I
      // 7b: istore 5
      // 7d: lload 3
      // 7e: ldc2_w -1
      // 81: lcmp
      // 82: ifeq b7
      // 85: lload 3
      // 86: iload 5
      // 88: i2l
      // 89: lcmp
      // 8a: ifeq b7
      // 8d: new java/io/IOException
      // 90: dup
      // 91: new java/lang/StringBuilder
      // 94: dup
      // 95: invokespecial java/lang/StringBuilder.<init> ()V
      // 98: ldc "Content-Length ("
      // 9a: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 9d: lload 3
      // 9e: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // a1: ldc ") and stream length ("
      // a3: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // a6: iload 5
      // a8: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
      // ab: ldc ") disagree"
      // ad: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // b0: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // b3: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // b6: athrow
      // b7: aload 9
      // b9: areturn
   }

   private inline fun <T : Any> consumeSource(consumer: (BufferedSource) -> T, sizeMapper: (T) -> Int): T {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 00: bipush 0
      // 01: istore 3
      // 02: aload 0
      // 03: invokevirtual okhttp3/ResponseBody.contentLength ()J
      // 06: lstore 4
      // 08: lload 4
      // 0a: ldc2_w 2147483647
      // 0d: lcmp
      // 0e: ifle 2d
      // 11: new java/io/IOException
      // 14: dup
      // 15: new java/lang/StringBuilder
      // 18: dup
      // 19: invokespecial java/lang/StringBuilder.<init> ()V
      // 1c: ldc "Cannot buffer entire body for content length: "
      // 1e: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 21: lload 4
      // 23: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // 26: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 29: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 2c: athrow
      // 2d: aload 0
      // 2e: invokevirtual okhttp3/ResponseBody.source ()Lokio/BufferedSource;
      // 31: checkcast java/io/Closeable
      // 34: astore 7
      // 36: aconst_null
      // 37: astore 8
      // 39: nop
      // 3a: aload 1
      // 3b: aload 7
      // 3d: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 42: astore 9
      // 44: bipush 1
      // 45: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
      // 48: aload 7
      // 4a: aload 8
      // 4c: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 4f: bipush 1
      // 50: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
      // 53: aload 9
      // 55: goto 75
      // 58: astore 9
      // 5a: aload 9
      // 5c: astore 8
      // 5e: aload 9
      // 60: athrow
      // 61: astore 9
      // 63: bipush 1
      // 64: invokestatic kotlin/jvm/internal/InlineMarker.finallyStart (I)V
      // 67: aload 7
      // 69: aload 8
      // 6b: invokestatic kotlin/io/CloseableKt.closeFinally (Ljava/io/Closeable;Ljava/lang/Throwable;)V
      // 6e: bipush 1
      // 6f: invokestatic kotlin/jvm/internal/InlineMarker.finallyEnd (I)V
      // 72: aload 9
      // 74: athrow
      // 75: astore 6
      // 77: aload 2
      // 78: aload 6
      // 7a: invokeinterface kotlin/jvm/functions/Function1.invoke (Ljava/lang/Object;)Ljava/lang/Object; 2
      // 7f: checkcast java/lang/Number
      // 82: invokevirtual java/lang/Number.intValue ()I
      // 85: istore 7
      // 87: lload 4
      // 89: ldc2_w -1
      // 8c: lcmp
      // 8d: ifeq c4
      // 90: lload 4
      // 92: iload 7
      // 94: i2l
      // 95: lcmp
      // 96: ifeq c4
      // 99: new java/io/IOException
      // 9c: dup
      // 9d: new java/lang/StringBuilder
      // a0: dup
      // a1: invokespecial java/lang/StringBuilder.<init> ()V
      // a4: ldc "Content-Length ("
      // a6: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // a9: lload 4
      // ab: invokevirtual java/lang/StringBuilder.append (J)Ljava/lang/StringBuilder;
      // ae: ldc ") and stream length ("
      // b0: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // b3: iload 7
      // b5: invokevirtual java/lang/StringBuilder.append (I)Ljava/lang/StringBuilder;
      // b8: ldc ") disagree"
      // ba: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // bd: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // c0: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // c3: athrow
      // c4: aload 6
      // c6: areturn
   }

   public fun charStream(): Reader {
      var var10000: Reader = this.reader;
      if (this.reader == null) {
         var var1: ResponseBody.BomAwareReader = new ResponseBody.BomAwareReader(this.source(), this.charset());
         this.reader = var1;
         var10000 = var1;
      }

      return var10000;
   }

   @Throws(java/io/IOException::class)
   public fun string(): String {
      label19: {
         var var1: Closeable = this.source() as Closeable;
         var var2: java.lang.Throwable = null;

         try {
            try {
               var var9: java.lang.String = (var1 as BufferedSource).readString(Util.readBomAsCharset(var1 as BufferedSource, this.charset()));
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

   private fun charset(): Charset {
      var var10000: MediaType = this.contentType();
      if (var10000 != null) {
         var var1: Charset = var10000.charset(Charsets.UTF_8);
         if (var1 != null) {
            return var1;
         }
      }

      return Charsets.UTF_8;
   }

   public override fun close() {
      Util.closeQuietly(this.source() as Closeable);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun java.lang.String.create(contentType: MediaType?): ResponseBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun ByteArray.create(contentType: MediaType?): ResponseBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun ByteString.create(contentType: MediaType?): ResponseBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun BufferedSource.create(contentType: MediaType?, contentLength: Long): ResponseBody {
      return Companion.create(`$this$create`, contentType, contentLength);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, content: java.lang.String): ResponseBody {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteArray): ResponseBody {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteString): ResponseBody {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.asResponseBody(contentType, contentLength)", imports = ["okhttp3.ResponseBody.Companion.asResponseBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, contentLength: Long, content: BufferedSource): ResponseBody {
      return Companion.create(contentType, contentLength, content);
   }

   @SourceDebugExtension(["SMAP\nResponseBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ResponseBody.kt\nokhttp3/ResponseBody$BomAwareReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,321:1\n1#2:322\n*E\n"])
   internal class BomAwareReader(source: BufferedSource, charset: Charset) : Reader {
      private final val charset: Charset
      private final var closed: Boolean
      private final var delegate: Reader?
      private final val source: BufferedSource

      init {
         this.source = source;
         this.charset = charset;
      }

      @Throws(java/io/IOException::class)
      public override fun read(cbuf: CharArray, off: Int, len: Int): Int {
         if (this.closed) {
            throw new IOException("Stream closed");
         } else {
            var var10000: Reader = this.delegate;
            if (this.delegate == null) {
               var var5: InputStreamReader = new InputStreamReader(this.source.inputStream(), Util.readBomAsCharset(this.source, this.charset));
               this.delegate = var5;
               var10000 = var5;
            }

            return var10000.read(cbuf, off, len);
         }
      }

      @Throws(java/io/IOException::class)
      public override fun close() {
         this.closed = true;
         var var10000: Unit;
         if (this.delegate != null) {
            this.delegate.close();
            var10000 = Unit.INSTANCE;
         } else {
            var10000 = null;
         }

         if (var10000 == null) {
            this.source.close();
         }
      }
   }

   companion object Companion private constructor() {
      @JvmName(name = "create")
      public fun String.toResponseBody(contentType: MediaType?): ResponseBody {
         var charset: Charset = Charsets.UTF_8;
         var finalContentType: MediaType = contentType;
         if (contentType != null) {
            var buffer: Charset = MediaType.charset$default(contentType, null, 1, null);
            if (buffer == null) {
               charset = Charsets.UTF_8;
               finalContentType = MediaType.Companion.parse(contentType + "; charset=utf-8");
            } else {
               charset = buffer;
            }
         }

         var var6: Buffer = new Buffer().writeString(`$this$toResponseBody`, charset);
         return this.create(var6 as BufferedSource, finalContentType, var6.size());
      }

      @JvmName(name = "create")
      public fun ByteArray.toResponseBody(contentType: MediaType?): ResponseBody {
         return this.create(new Buffer().write(`$this$toResponseBody`) as BufferedSource, contentType, (long)`$this$toResponseBody`.length);
      }

      @JvmName(name = "create")
      public fun ByteString.toResponseBody(contentType: MediaType?): ResponseBody {
         return this.create(new Buffer().write(`$this$toResponseBody`) as BufferedSource, contentType, (long)`$this$toResponseBody`.size());
      }

      @JvmName(name = "create")
      public fun BufferedSource.asResponseBody(contentType: MediaType?, contentLength: Long): ResponseBody {
         return new ResponseBody(contentType, contentLength, `$this$asResponseBody`) {
            {
               this.$contentType = `$contentType`;
               this.$contentLength = `$contentLength`;
               this.$this_asResponseBody = `$receiver`;
            }

            @Nullable
            @Override
            public MediaType contentType() {
               return this.$contentType;
            }

            @Override
            public long contentLength() {
               return this.$contentLength;
            }

            @NotNull
            @Override
            public BufferedSource source() {
               return this.$this_asResponseBody;
            }
         };
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, content: String): ResponseBody {
         return this.create(content, contentType);
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, content: ByteArray): ResponseBody {
         return this.create(content, contentType);
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toResponseBody(contentType)", imports = ["okhttp3.ResponseBody.Companion.toResponseBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, content: ByteString): ResponseBody {
         return this.create(content, contentType);
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.asResponseBody(contentType, contentLength)", imports = ["okhttp3.ResponseBody.Companion.asResponseBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, contentLength: Long, content: BufferedSource): ResponseBody {
         return this.create(content, contentType, contentLength);
      }
   }
}
