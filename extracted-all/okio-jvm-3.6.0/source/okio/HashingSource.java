package okio

import java.security.InvalidKeyException
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HashingSource : ForwardingSource, Source {
   @JvmStatic
   public HashingSource.Companion Companion = new HashingSource.Companion(null);

   public final val hash: ByteString
      public final get() {
         var var10000: ByteArray;
         if (this.messageDigest != null) {
            var10000 = this.messageDigest.digest();
         } else {
            var var2: Mac = this.mac;
            var10000 = var2.doFinal();
         }

         return new ByteString(var10000);
      }

   private final val mac: Mac?
   private final val messageDigest: MessageDigest?

   internal constructor(source: Source, digest: MessageDigest) : super(source) {
      this.messageDigest = digest;
      this.mac = null;
   }

   internal constructor(source: Source, algorithm: String)  {
      var var10002: MessageDigest = MessageDigest.getInstance(algorithm);
      this(source, var10002);
   }

   internal constructor(source: Source, mac: Mac) : super(source) {
      this.mac = mac;
      this.messageDigest = null;
   }

   internal constructor(source: Source, key: ByteString, algorithm: String)  {
      var var9: Source = source;
      var var8: HashingSource = this;

      var var14: Mac;
      var var10000: HashingSource;
      var var10001: Source;
      try {
         var14 = Mac.getInstance(algorithm);
         var14.init(new SecretKeySpec(key.toByteArray(), algorithm));
         var10000 = var8;
         var10001 = var9;
         var14 = var14;
      } catch (InvalidKeyException var13) {
         throw new IllegalArgumentException(var13);
      }

      var10000./* $VF: Unable to resugar constructor */<init>(var10001, var14);
   }

   @Throws(java/io/IOException::class)
   public override fun read(sink: Buffer, byteCount: Long): Long {
      var result: Long = super.read(sink, byteCount);
      if (result != -1L) {
         var start: Long = sink.size() - result;
         var offset: Long = sink.size();
         var var10000: Segment = sink.head;

         var s: Segment;
         for (s = var10000; offset > start; offset -= (long)(var10000.limit - var10000.pos)) {
            var10000 = s.prev;
            s = var10000;
         }

         while (offset < sink.size()) {
            var pos: Int = (int)((long)s.pos + start - offset);
            if (this.messageDigest != null) {
               this.messageDigest.update(s.data, pos, s.limit - pos);
            } else {
               var var13: Mac = this.mac;
               var13.update(s.data, pos, s.limit - pos);
            }

            offset += (long)(s.limit - s.pos);
            start = offset;
            var10000 = s.next;
            s = var10000;
         }
      }

      return result;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "hash", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_hash")
   public fun hash(): ByteString {
      return this.hash();
   }

   @JvmStatic
   fun md5(source: Source): HashingSource {
      return Companion.md5(source);
   }

   @JvmStatic
   fun sha1(source: Source): HashingSource {
      return Companion.sha1(source);
   }

   @JvmStatic
   fun sha256(source: Source): HashingSource {
      return Companion.sha256(source);
   }

   @JvmStatic
   fun sha512(source: Source): HashingSource {
      return Companion.sha512(source);
   }

   @JvmStatic
   fun hmacSha1(source: Source, key: ByteString): HashingSource {
      return Companion.hmacSha1(source, key);
   }

   @JvmStatic
   fun hmacSha256(source: Source, key: ByteString): HashingSource {
      return Companion.hmacSha256(source, key);
   }

   @JvmStatic
   fun hmacSha512(source: Source, key: ByteString): HashingSource {
      return Companion.hmacSha512(source, key);
   }

   companion object Companion private constructor() {
      public fun md5(source: Source): HashingSource {
         return new HashingSource(source, "MD5");
      }

      public fun sha1(source: Source): HashingSource {
         return new HashingSource(source, "SHA-1");
      }

      public fun sha256(source: Source): HashingSource {
         return new HashingSource(source, "SHA-256");
      }

      public fun sha512(source: Source): HashingSource {
         return new HashingSource(source, "SHA-512");
      }

      public fun hmacSha1(source: Source, key: ByteString): HashingSource {
         return new HashingSource(source, key, "HmacSHA1");
      }

      public fun hmacSha256(source: Source, key: ByteString): HashingSource {
         return new HashingSource(source, key, "HmacSHA256");
      }

      public fun hmacSha512(source: Source, key: ByteString): HashingSource {
         return new HashingSource(source, key, "HmacSHA512");
      }
   }
}
