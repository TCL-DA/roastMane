package okhttp3

import java.nio.charset.Charset
import java.util.ArrayList
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSink

class FormBody internal constructor(encodedNames: List<String>, encodedValues: List<String>) : RequestBody {
   @JvmStatic
   public FormBody.Companion Companion = new FormBody.Companion(null);
   @JvmStatic
   private MediaType CONTENT_TYPE = MediaType.Companion.get("application/x-www-form-urlencoded");

   private final val encodedNames: List<String>
   private final val encodedValues: List<String>
   public final val size: Int
      public final get() {
         return this.encodedNames.size();
      }


   init {
      this.encodedNames = Util.toImmutableList(encodedNames);
      this.encodedValues = Util.toImmutableList(encodedValues);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "size", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_size")
   public fun size(): Int {
      return this.size();
   }

   public fun encodedName(index: Int): String {
      return this.encodedNames.get(index);
   }

   public fun name(index: Int): String {
      return HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedName(index), 0, 0, true, 3, null);
   }

   public fun encodedValue(index: Int): String {
      return this.encodedValues.get(index);
   }

   public fun value(index: Int): String {
      return HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedValue(index), 0, 0, true, 3, null);
   }

   public override fun contentType(): MediaType {
      return CONTENT_TYPE;
   }

   public override fun contentLength(): Long {
      return this.writeOrCountBytes(null, true);
   }

   @Throws(java/io/IOException::class)
   public override fun writeTo(sink: BufferedSink) {
      this.writeOrCountBytes(sink, false);
   }

   private fun writeOrCountBytes(sink: BufferedSink?, countBytes: Boolean): Long {
      var byteCount: Long = 0L;
      var var10000: Buffer;
      if (countBytes) {
         var10000 = new Buffer();
      } else {
         var10000 = sink.getBuffer();
      }

      var buffer: Buffer = var10000;
      var i: Int = 0;

      for (int var7 = this.encodedNames.size(); i < var7; i++) {
         if (i > 0) {
            buffer.writeByte(38);
         }

         buffer.writeUtf8(this.encodedNames.get(i));
         buffer.writeByte(61);
         buffer.writeUtf8(this.encodedValues.get(i));
      }

      if (countBytes) {
         byteCount = buffer.size();
         buffer.clear();
      }

      return byteCount;
   }

   class Builder @JvmOverloads  public constructor(charset: Charset?) {
      private final val charset: Charset?
      private final val names: MutableList<String>
      private final val values: MutableList<String>

      init {
         this.charset = charset;
         this.names = new ArrayList<>();
         this.values = new ArrayList<>();
      }

      public fun add(name: String, value: String): okhttp3.FormBody.Builder {
         var `$this$add_u24lambda_u240`: FormBody.Builder = this;
         this.names
            .add(
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, name, 0, 0, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, false, this.charset, 91, null
               )
            );
         `$this$add_u24lambda_u240`.values
            .add(
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, value, 0, 0, " \"':;<=>@[]^`{}|/\\?#&!$(),~", false, false, true, false, `$this$add_u24lambda_u240`.charset, 91, null
               )
            );
         return this;
      }

      public fun addEncoded(name: String, value: String): okhttp3.FormBody.Builder {
         var `$this$addEncoded_u24lambda_u241`: FormBody.Builder = this;
         this.names
            .add(
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, name, 0, 0, " \"':;<=>@[]^`{}|/\\?#&!$(),~", true, false, true, false, this.charset, 83, null
               )
            );
         `$this$addEncoded_u24lambda_u241`.values
            .add(
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion,
                  value,
                  0,
                  0,
                  " \"':;<=>@[]^`{}|/\\?#&!$(),~",
                  true,
                  false,
                  true,
                  false,
                  `$this$addEncoded_u24lambda_u241`.charset,
                  83,
                  null
               )
            );
         return this;
      }

      public fun build(): FormBody {
         return new FormBody(this.names, this.values);
      }

      @JvmOverloads
      fun Builder() {
         this(null, 1, null);
      }
   }

   companion object Companion private constructor() {
      private final val CONTENT_TYPE: MediaType

   }
}
