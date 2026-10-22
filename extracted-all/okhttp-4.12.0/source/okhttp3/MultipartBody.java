package okhttp3

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSink
import okio.ByteString

class MultipartBody internal constructor(boundaryByteString: ByteString, type: MediaType, parts: List<okhttp3.MultipartBody.Part>) : RequestBody {
   @JvmStatic
   public MultipartBody.Companion Companion = new MultipartBody.Companion(null);
   @JvmField
   @JvmStatic
   public MediaType MIXED = MediaType.Companion.get("multipart/mixed");
   @JvmField
   @JvmStatic
   public MediaType ALTERNATIVE = MediaType.Companion.get("multipart/alternative");
   @JvmField
   @JvmStatic
   public MediaType DIGEST = MediaType.Companion.get("multipart/digest");
   @JvmField
   @JvmStatic
   public MediaType PARALLEL = MediaType.Companion.get("multipart/parallel");
   @JvmField
   @JvmStatic
   public MediaType FORM = MediaType.Companion.get("multipart/form-data");
   @JvmStatic
   private byte[] COLONSPACE = new byte[]{58, 32};
   @JvmStatic
   private byte[] CRLF = new byte[]{13, 10};
   @JvmStatic
   private byte[] DASHDASH = new byte[]{45, 45};

   public final val boundary: String
      public final get() {
         return this.boundaryByteString.utf8();
      }

   private final val boundaryByteString: ByteString
   private final var contentLength: Long
   private final val contentType: MediaType
   public final val parts: List<okhttp3.MultipartBody.Part>
   public final val size: Int
      public final get() {
         return this.parts.size();
      }

   public final val type: MediaType

   init {
      this.boundaryByteString = boundaryByteString;
      this.type = type;
      this.parts = parts;
      this.contentType = MediaType.Companion.get(this.type + "; boundary=" + this.boundary());
      this.contentLength = -1L;
   }

   public fun part(index: Int): okhttp3.MultipartBody.Part {
      return this.parts.get(index);
   }

   public override fun contentType(): MediaType {
      return this.contentType;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "type", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_type")
   public fun type(): MediaType {
      return this.type;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "boundary", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_boundary")
   public fun boundary(): String {
      return this.boundary();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "size", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_size")
   public fun size(): Int {
      return this.size();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "parts", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_parts")
   public fun parts(): List<okhttp3.MultipartBody.Part> {
      return this.parts;
   }

   @Throws(java/io/IOException::class)
   public override fun contentLength(): Long {
      var result: Long = this.contentLength;
      if (this.contentLength == -1L) {
         result = this.writeOrCountBytes(null, true);
         this.contentLength = result;
      }

      return result;
   }

   @Throws(java/io/IOException::class)
   public override fun writeTo(sink: BufferedSink) {
      this.writeOrCountBytes(sink, false);
   }

   @Throws(java/io/IOException::class)
   private fun writeOrCountBytes(sink: BufferedSink?, countBytes: Boolean): Long {
      var sinkx: BufferedSink = sink;
      var byteCount: Long = 0L;
      var byteCountBuffer: Buffer = null;
      if (countBytes) {
         byteCountBuffer = new Buffer();
         sinkx = byteCountBuffer as BufferedSink;
      }

      var p: Int = 0;

      for (int var8 = this.parts.size(); p < var8; p++) {
         var part: MultipartBody.Part = this.parts.get(p);
         var headers: Headers = part.headers();
         var body: RequestBody = part.body();
         sinkx.write(DASHDASH);
         sinkx.write(this.boundaryByteString);
         sinkx.write(CRLF);
         if (headers != null) {
            var contentType: Int = 0;

            for (int contentLength = headers.size(); h < contentLength; h++) {
               sinkx.writeUtf8(headers.name(contentType)).write(COLONSPACE).writeUtf8(headers.value(contentType)).write(CRLF);
            }
         }

         var var15: MediaType = body.contentType();
         if (var15 != null) {
            sinkx.writeUtf8("Content-Type: ").writeUtf8(var15.toString()).write(CRLF);
         }

         var var16: Long = body.contentLength();
         if (var16 != -1L) {
            sinkx.writeUtf8("Content-Length: ").writeDecimalLong(var16).write(CRLF);
         } else if (countBytes) {
            byteCountBuffer.clear();
            return -1L;
         }

         sinkx.write(CRLF);
         if (countBytes) {
            byteCount += var16;
         } else {
            body.writeTo(sinkx);
         }

         sinkx.write(CRLF);
      }

      sinkx.write(DASHDASH);
      sinkx.write(this.boundaryByteString);
      sinkx.write(DASHDASH);
      sinkx.write(CRLF);
      if (countBytes) {
         byteCount += byteCountBuffer.size();
         byteCountBuffer.clear();
      }

      return byteCount;
   }

   @SourceDebugExtension(["SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,345:1\n1#2:346\n*E\n"])
   class Builder @JvmOverloads  public constructor(boundary: String) {
      private final val boundary: ByteString
      private final val parts: MutableList<okhttp3.MultipartBody.Part>
      private final var type: MediaType

      init {
         this.boundary = ByteString.Companion.encodeUtf8(boundary);
         this.type = MultipartBody.MIXED;
         this.parts = new ArrayList<>();
      }

      public fun setType(type: MediaType): okhttp3.MultipartBody.Builder {
         var `$this$setType_u24lambda_u241`: MultipartBody.Builder = this;
         if (!(type.type() == "multipart")) {
            throw new IllegalArgumentException(("multipart != " + type).toString());
         } else {
            `$this$setType_u24lambda_u241`.type = type;
            return this;
         }
      }

      public fun addPart(body: RequestBody): okhttp3.MultipartBody.Builder {
         this.addPart(MultipartBody.Part.Companion.create(body));
         return this;
      }

      public fun addPart(headers: Headers?, body: RequestBody): okhttp3.MultipartBody.Builder {
         this.addPart(MultipartBody.Part.Companion.create(headers, body));
         return this;
      }

      public fun addFormDataPart(name: String, value: String): okhttp3.MultipartBody.Builder {
         this.addPart(MultipartBody.Part.Companion.createFormData(name, value));
         return this;
      }

      public fun addFormDataPart(name: String, filename: String?, body: RequestBody): okhttp3.MultipartBody.Builder {
         this.addPart(MultipartBody.Part.Companion.createFormData(name, filename, body));
         return this;
      }

      public fun addPart(part: okhttp3.MultipartBody.Part): okhttp3.MultipartBody.Builder {
         this.parts.add(part);
         return this;
      }

      public fun build(): MultipartBody {
         if (this.parts.isEmpty()) {
            throw new IllegalStateException("Multipart body must have at least one part.".toString());
         } else {
            return new MultipartBody(this.boundary, this.type, Util.toImmutableList(this.parts));
         }
      }

      @JvmOverloads
      fun Builder() {
         this(null, 1, null);
      }
   }

   companion object Companion private constructor() {
      public final val ALTERNATIVE: MediaType
      private final val COLONSPACE: ByteArray
      private final val CRLF: ByteArray
      private final val DASHDASH: ByteArray
      public final val DIGEST: MediaType
      public final val FORM: MediaType
      public final val MIXED: MediaType
      public final val PARALLEL: MediaType


      internal fun StringBuilder.appendQuotedString(key: String) {
         `$this$appendQuotedString`.append('"');
         var i: Int = 0;

         for (int var4 = key.length(); i < var4; i++) {
            var ch: Char = key.charAt(i);
            if (ch == '\n') {
               `$this$appendQuotedString`.append("%0A");
            } else if (ch == '\r') {
               `$this$appendQuotedString`.append("%0D");
            } else if (ch == '"') {
               `$this$appendQuotedString`.append("%22");
            } else {
               `$this$appendQuotedString`.append(ch);
            }
         }

         `$this$appendQuotedString`.append('"');
      }
   }

   class Part private constructor(headers: Headers?, body: RequestBody) {
      @JvmStatic
      public MultipartBody.Part.Companion Companion = new MultipartBody.Part.Companion(null);

      public final val body: RequestBody
      public final val headers: Headers?

      init {
         this.headers = headers;
         this.body = body;
      }

      @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "headers", imports = []), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_headers")
      public fun headers(): Headers? {
         return this.headers;
      }

      @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "body", imports = []), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_body")
      public fun body(): RequestBody {
         return this.body;
      }

      @JvmStatic
      fun create(body: RequestBody): MultipartBody.Part {
         return Companion.create(body);
      }

      @JvmStatic
      fun create(headers: Headers?, body: RequestBody): MultipartBody.Part {
         return Companion.create(headers, body);
      }

      @JvmStatic
      fun createFormData(name: java.lang.String, value: java.lang.String): MultipartBody.Part {
         return Companion.createFormData(name, value);
      }

      @JvmStatic
      fun createFormData(name: java.lang.String, filename: java.lang.String?, body: RequestBody): MultipartBody.Part {
         return Companion.createFormData(name, filename, body);
      }

      @SourceDebugExtension(["SMAP\nMultipartBody.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartBody.kt\nokhttp3/MultipartBody$Part$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,345:1\n1#2:346\n*E\n"])
      companion object Companion private constructor() {
         public fun create(body: RequestBody): okhttp3.MultipartBody.Part {
            return this.create(null, body);
         }

         public fun create(headers: Headers?, body: RequestBody): okhttp3.MultipartBody.Part {
            if ((if (headers != null) headers.get("Content-Type") else null) != null) {
               throw new IllegalArgumentException("Unexpected header: Content-Type".toString());
            } else if ((if (headers != null) headers.get("Content-Length") else null) != null) {
               throw new IllegalArgumentException("Unexpected header: Content-Length".toString());
            } else {
               return new MultipartBody.Part(headers, body, null);
            }
         }

         public fun createFormData(name: String, value: String): okhttp3.MultipartBody.Part {
            return this.createFormData(name, null, RequestBody.Companion.create$default(RequestBody.Companion, value, null, 1, null));
         }

         public fun createFormData(name: String, filename: String?, body: RequestBody): okhttp3.MultipartBody.Part {
            var headers: StringBuilder = new StringBuilder();
            headers.append("form-data; name=");
            MultipartBody.Companion.appendQuotedString$okhttp(headers, name);
            if (filename != null) {
               headers.append("; filename=");
               MultipartBody.Companion.appendQuotedString$okhttp(headers, filename);
            }

            var var10000: java.lang.String = headers.toString();
            return this.create(new Headers.Builder().addUnsafeNonAscii("Content-Disposition", var10000).build(), body);
         }
      }
   }
}
