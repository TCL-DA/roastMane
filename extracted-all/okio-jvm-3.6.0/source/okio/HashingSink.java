package okio

import java.security.InvalidKeyException
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nHashingSink.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HashingSink.kt\nokio/HashingSink\n+ 2 Util.kt\nokio/-SegmentedByteString\n*L\n1#1,148:1\n86#2:149\n*S KotlinDebug\n*F\n+ 1 HashingSink.kt\nokio/HashingSink\n*L\n75#1:149\n*E\n"])
class HashingSink : ForwardingSink, Sink {
   @JvmStatic
   public HashingSink.Companion Companion = new HashingSink.Companion(null);

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

   internal constructor(sink: Sink, digest: MessageDigest) : super(sink) {
      this.messageDigest = digest;
      this.mac = null;
   }

   internal constructor(sink: Sink, algorithm: String)  {
      var var10002: MessageDigest = MessageDigest.getInstance(algorithm);
      this(sink, var10002);
   }

   internal constructor(sink: Sink, mac: Mac) : super(sink) {
      this.mac = mac;
      this.messageDigest = null;
   }

   internal constructor(sink: Sink, key: ByteString, algorithm: String)  {
      var var9: Sink = sink;
      var var8: HashingSink = this;

      var var14: Mac;
      var var10000: HashingSink;
      var var10001: Sink;
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
   public override fun write(source: Buffer, byteCount: Long) {
      -SegmentedByteString.checkOffsetAndCount(source.size(), 0L, byteCount);
      var hashedCount: Long = 0L;
      var var10000: Segment = source.head;
      var s: Segment = var10000;

      while (hashedCount < byteCount) {
         var toHash: Int = (int)Math.min(byteCount - hashedCount, (long)(s.limit - s.pos));
         if (this.messageDigest != null) {
            this.messageDigest.update(s.data, s.pos, toHash);
         } else {
            var var12: Mac = this.mac;
            var12.update(s.data, s.pos, toHash);
         }

         hashedCount += (long)toHash;
         var10000 = s.next;
         s = var10000;
      }

      super.write(source, byteCount);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "hash", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_hash")
   public fun hash(): ByteString {
      return this.hash();
   }

   @JvmStatic
   fun md5(sink: Sink): HashingSink {
      return Companion.md5(sink);
   }

   @JvmStatic
   fun sha1(sink: Sink): HashingSink {
      return Companion.sha1(sink);
   }

   @JvmStatic
   fun sha256(sink: Sink): HashingSink {
      return Companion.sha256(sink);
   }

   @JvmStatic
   fun sha512(sink: Sink): HashingSink {
      return Companion.sha512(sink);
   }

   @JvmStatic
   fun hmacSha1(sink: Sink, key: ByteString): HashingSink {
      return Companion.hmacSha1(sink, key);
   }

   @JvmStatic
   fun hmacSha256(sink: Sink, key: ByteString): HashingSink {
      return Companion.hmacSha256(sink, key);
   }

   @JvmStatic
   fun hmacSha512(sink: Sink, key: ByteString): HashingSink {
      return Companion.hmacSha512(sink, key);
   }

   companion object Companion private constructor() {
      public fun md5(sink: Sink): HashingSink {
         return new HashingSink(sink, "MD5");
      }

      public fun sha1(sink: Sink): HashingSink {
         return new HashingSink(sink, "SHA-1");
      }

      public fun sha256(sink: Sink): HashingSink {
         return new HashingSink(sink, "SHA-256");
      }

      public fun sha512(sink: Sink): HashingSink {
         return new HashingSink(sink, "SHA-512");
      }

      public fun hmacSha1(sink: Sink, key: ByteString): HashingSink {
         return new HashingSink(sink, key, "HmacSHA1");
      }

      public fun hmacSha256(sink: Sink, key: ByteString): HashingSink {
         return new HashingSink(sink, key, "HmacSHA256");
      }

      public fun hmacSha512(sink: Sink, key: ByteString): HashingSink {
         return new HashingSink(sink, key, "HmacSHA512");
      }
   }
}
