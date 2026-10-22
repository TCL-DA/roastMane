package okhttp3

import java.io.Closeable
import java.io.File
import java.nio.charset.Charset
import okhttp3.internal.Util
import okio.BufferedSink
import okio.ByteString
import okio.Okio
import okio.Source
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

abstract class RequestBody {
   @JvmStatic
   public RequestBody.Companion Companion = new RequestBody.Companion(null);

   public abstract fun contentType(): MediaType? {
   }

   @Throws(java/io/IOException::class)
   public open fun contentLength(): Long {
      return -1L;
   }

   @Throws(java/io/IOException::class)
   public abstract fun writeTo(sink: BufferedSink) {
   }

   public open fun isDuplex(): Boolean {
      return false;
   }

   public open fun isOneShot(): Boolean {
      return false;
   }

   @JvmName(name = "create")
   @JvmStatic
   fun java.lang.String.create(contentType: MediaType?): RequestBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun ByteString.create(contentType: MediaType?): RequestBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmOverloads
   @JvmName(name = "create")
   @JvmStatic
   fun ByteArray.create(contentType: MediaType?, offset: Int, byteCount: Int): RequestBody {
      return Companion.create(`$this$create`, contentType, offset, byteCount);
   }

   @JvmName(name = "create")
   @JvmStatic
   fun File.create(contentType: MediaType?): RequestBody {
      return Companion.create(`$this$create`, contentType);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, content: java.lang.String): RequestBody {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteString): RequestBody {
      return Companion.create(contentType, content);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmOverloads
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteArray, offset: Int, byteCount: Int): RequestBody {
      return Companion.create(contentType, content, offset, byteCount);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'file' argument first to fix Java", replaceWith = @ReplaceWith(expression = "file.asRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.asRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmStatic
   fun create(contentType: MediaType?, file: File): RequestBody {
      return Companion.create(contentType, file);
   }

   @JvmOverloads
   @JvmName(name = "create")
   @JvmStatic
   fun ByteArray.create(contentType: MediaType?, offset: Int): RequestBody {
      return Companion.create(`$this$create`, contentType, offset);
   }

   @JvmOverloads
   @JvmName(name = "create")
   @JvmStatic
   fun ByteArray.create(contentType: MediaType?): RequestBody {
      return Companion.create(`$this$create`, contentType);
   }

   @JvmOverloads
   @JvmName(name = "create")
   @JvmStatic
   fun ByteArray.create(): RequestBody {
      return Companion.create(`$this$create`);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmOverloads
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteArray, offset: Int): RequestBody {
      return Companion.create(contentType, content, offset);
   }

   /** @deprecated */
   @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
   @JvmOverloads
   @JvmStatic
   fun create(contentType: MediaType?, content: ByteArray): RequestBody {
      return Companion.create(contentType, content);
   }

   companion object Companion private constructor() {
      @JvmName(name = "create")
      public fun String.toRequestBody(contentType: MediaType?): RequestBody {
         var charset: Charset = Charsets.UTF_8;
         var finalContentType: MediaType = contentType;
         if (contentType != null) {
            var bytes: Charset = MediaType.charset$default(contentType, null, 1, null);
            if (bytes == null) {
               charset = Charsets.UTF_8;
               finalContentType = MediaType.Companion.parse(contentType + "; charset=utf-8");
            } else {
               charset = bytes;
            }
         }

         var var10000: ByteArray = `$this$toRequestBody`.getBytes(charset);
         return this.create(var10000, finalContentType, 0, var10000.length);
      }

      @JvmName(name = "create")
      public fun ByteString.toRequestBody(contentType: MediaType?): RequestBody {
         return new RequestBody(contentType, `$this$toRequestBody`) {
            {
               this.$contentType = `$contentType`;
               this.$this_toRequestBody = `$receiver`;
            }

            @Nullable
            @Override
            public MediaType contentType() {
               return this.$contentType;
            }

            @Override
            public long contentLength() {
               return (long)this.$this_toRequestBody.size();
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) {
               sink.write(this.$this_toRequestBody);
            }
         };
      }

      @JvmOverloads
      @JvmName(name = "create")
      public fun ByteArray.toRequestBody(contentType: MediaType?, offset: Int, byteCount: Int): RequestBody {
         Util.checkOffsetAndCount((long)`$this$toRequestBody`.length, (long)offset, (long)byteCount);
         return new RequestBody(contentType, byteCount, `$this$toRequestBody`, offset) {
            {
               this.$contentType = `$contentType`;
               this.$byteCount = `$byteCount`;
               this.$this_toRequestBody = `$receiver`;
               this.$offset = `$offset`;
            }

            @Nullable
            @Override
            public MediaType contentType() {
               return this.$contentType;
            }

            @Override
            public long contentLength() {
               return (long)this.$byteCount;
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) {
               sink.write(this.$this_toRequestBody, this.$offset, this.$byteCount);
            }
         };
      }

      @JvmName(name = "create")
      public fun File.asRequestBody(contentType: MediaType?): RequestBody {
         return new RequestBody(contentType, `$this$asRequestBody`) {
            {
               this.$contentType = `$contentType`;
               this.$this_asRequestBody = `$receiver`;
            }

            @Nullable
            @Override
            public MediaType contentType() {
               return this.$contentType;
            }

            @Override
            public long contentLength() {
               return this.$this_asRequestBody.length();
            }

            @Override
            public void writeTo(@NotNull BufferedSink sink) {
               label19: {
                  var var2: Closeable = Okio.source(this.$this_asRequestBody) as Closeable;
                  var var3: java.lang.Throwable = null;

                  try {
                     try {
                        var var11: Long = sink.writeAll(var2 as Source);
                     } catch (java.lang.Throwable var7) {
                        var3 = var7;
                        throw var7;
                     }
                  } catch (java.lang.Throwable var8) {
                     CloseableKt.closeFinally(var2, var3);
                  }

                  CloseableKt.closeFinally(var2, null);
               }
            }
         };
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, content: String): RequestBody {
         return this.create(content, contentType);
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, content: ByteString): RequestBody {
         return this.create(content, contentType);
      }

      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
      @JvmOverloads
      public fun create(contentType: MediaType?, content: ByteArray, offset: Int, byteCount: Int): RequestBody {
         return this.create(content, contentType, offset, byteCount);
      }

      @Deprecated(message = "Moved to extension function. Put the 'file' argument first to fix Java", replaceWith = @ReplaceWith(expression = "file.asRequestBody(contentType)", imports = ["okhttp3.RequestBody.Companion.asRequestBody"]), level = DeprecationLevel.WARNING)
      public fun create(contentType: MediaType?, file: File): RequestBody {
         return this.create(file, contentType);
      }

      @JvmOverloads
      @JvmName(name = "create")
      fun ByteArray.create(contentType: MediaType?, offset: Int): RequestBody {
         return create$default(this, `$this$toRequestBody`, contentType, offset, 0, 4, null);
      }

      @JvmOverloads
      @JvmName(name = "create")
      fun ByteArray.create(contentType: MediaType?): RequestBody {
         return create$default(this, `$this$toRequestBody`, contentType, 0, 0, 6, null);
      }

      @JvmOverloads
      @JvmName(name = "create")
      fun ByteArray.create(): RequestBody {
         return create$default(this, `$this$toRequestBody`, null, 0, 0, 7, null);
      }

      /** @deprecated */
      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
      @JvmOverloads
      fun create(contentType: MediaType?, content: ByteArray, offset: Int): RequestBody {
         return create$default(this, contentType, content, offset, 0, 8, null);
      }

      /** @deprecated */
      @Deprecated(message = "Moved to extension function. Put the 'content' argument first to fix Java", replaceWith = @ReplaceWith(expression = "content.toRequestBody(contentType, offset, byteCount)", imports = ["okhttp3.RequestBody.Companion.toRequestBody"]), level = DeprecationLevel.WARNING)
      @JvmOverloads
      fun create(contentType: MediaType?, content: ByteArray): RequestBody {
         return create$default(this, contentType, content, 0, 0, 12, null);
      }
   }
}
