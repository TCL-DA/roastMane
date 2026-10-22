package okhttp3

import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.ArrayList
import javax.net.ssl.SSLPeerUnverifiedException
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.TypeIntrinsics
import okhttp3.internal.tls.CertificateChainCleaner
import okio.ByteString
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nCertificatePinner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,370:1\n625#2,8:371\n*S KotlinDebug\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner\n*L\n216#1:371,8\n*E\n"])
class CertificatePinner internal constructor(pins: Set<okhttp3.CertificatePinner.Pin>, certificateChainCleaner: CertificateChainCleaner?) {
   @JvmStatic
   public CertificatePinner.Companion Companion = new CertificatePinner.Companion(null);
   @JvmField
   @JvmStatic
   public CertificatePinner DEFAULT = new CertificatePinner.Builder().build();

   internal final val certificateChainCleaner: CertificateChainCleaner?
   public final val pins: Set<okhttp3.CertificatePinner.Pin>

   init {
      this.pins = pins;
      this.certificateChainCleaner = certificateChainCleaner;
   }

   @Throws(javax/net/ssl/SSLPeerUnverifiedException::class)
   public fun check(hostname: String, peerCertificates: List<Certificate>) {
      this.check$okhttp(hostname, (new Function0<java.util.List<? extends X509Certificate>>(this, peerCertificates, hostname) {
         {
            super(0);
            this.this$0 = `$receiver`;
            this.$peerCertificates = `$peerCertificates`;
            this.$hostname = `$hostname`;
         }

         @NotNull
         public final java.util.List<X509Certificate> invoke() {
            var var11: java.util.List;
            label20: {
               var var10000: CertificateChainCleaner = this.this$0.getCertificateChainCleaner$okhttp();
               if (var10000 != null) {
                  var11 = var10000.clean(this.$peerCertificates, this.$hostname);
                  if (var11 != null) {
                     break label20;
                  }
               }

               var11 = this.$peerCertificates;
            }

            var `$this$map$iv`: java.lang.Iterable = var11;
            var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var11, 10));

            for (Object item$iv$iv : $this$map$iv) {
               var it: Certificate = `item$iv$iv` as Certificate;
               `destination$iv$iv`.add(it as X509Certificate);
            }

            return `destination$iv$iv` as java.util.List<X509Certificate>;
         }
      }) as Function0<? extends java.util.List<? extends X509Certificate>>);
   }

   internal fun check(hostname: String, cleanedPeerCertificatesFn: () -> List<X509Certificate>) {
      var pins: java.util.List = this.findMatchingPins(hostname);
      if (!pins.isEmpty()) {
         var peerCertificates: java.util.List = cleanedPeerCertificatesFn.invoke() as java.util.List;

         for (X509Certificate peerCertificate : peerCertificates) {
            var `$this$check_u24lambda_u240`: ByteString = null;
            var var8: ByteString = null;

            for (CertificatePinner.Pin pin : pins) {
               var var11: java.lang.String = pin.getHashAlgorithm();
               if (var11 == "sha256") {
                  if (var8 == null) {
                     var8 = Companion.sha256Hash(peerCertificate);
                  }

                  if (pin.getHash() == var8) {
                     return;
                  }
               } else {
                  if (!(var11 == "sha1")) {
                     throw new AssertionError("unsupported hashAlgorithm: " + pin.getHashAlgorithm());
                  }

                  if (`$this$check_u24lambda_u240` == null) {
                     `$this$check_u24lambda_u240` = Companion.sha1Hash(peerCertificate);
                  }

                  if (pin.getHash() == `$this$check_u24lambda_u240`) {
                     return;
                  }
               }
            }
         }

         var var13: StringBuilder = new StringBuilder();
         var var14: StringBuilder = var13;
         var13.append("Certificate pinning failure!");
         var13.append("\n  Peer certificate chain:");

         for (X509Certificate element : peerCertificates) {
            var14.append("\n    ");
            var14.append(Companion.pin(var18));
            var14.append(": ");
            var14.append(var18.getSubjectDN().getName());
         }

         var14.append("\n  Pinned certificates for ");
         var14.append(hostname);
         var14.append(":");

         for (CertificatePinner.Pin pinx : pins) {
            var14.append("\n    ");
            var14.append(pinx);
         }

         var var10000: java.lang.String = var13.toString();
         throw new SSLPeerUnverifiedException(var10000);
      }
   }

   @Deprecated(message = "replaced with {@link #check(String, List)}.", replaceWith = @ReplaceWith(expression = "check(hostname, peerCertificates.toList())", imports = []))
   @Throws(javax/net/ssl/SSLPeerUnverifiedException::class)
   public fun check(hostname: String, vararg peerCertificates: Certificate) {
      this.check(hostname, ArraysKt.toList(peerCertificates));
   }

   public fun findMatchingPins(hostname: String): List<okhttp3.CertificatePinner.Pin> {
      var `$this$filterList$iv`: java.lang.Iterable = this.pins;
      var `result$iv`: java.util.List = CollectionsKt.emptyList();

      for (Object i$iv : $this$filterList$iv) {
         if ((`i$iv` as CertificatePinner.Pin).matchesHostname(hostname)) {
            if (`result$iv`.isEmpty()) {
               `result$iv` = new ArrayList();
            }

            TypeIntrinsics.asMutableList(`result$iv`).add(`i$iv`);
         }
      }

      return `result$iv`;
   }

   internal fun withCertificateChainCleaner(certificateChainCleaner: CertificateChainCleaner): CertificatePinner {
      return if (this.certificateChainCleaner == certificateChainCleaner) this else new CertificatePinner(this.pins, certificateChainCleaner);
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is CertificatePinner
         && (other as CertificatePinner).pins == this.pins
         && (other as CertificatePinner).certificateChainCleaner == this.certificateChainCleaner;
   }

   public override fun hashCode(): Int {
      return 41 * (41 * 37 + this.pins.hashCode()) + (if (this.certificateChainCleaner != null) this.certificateChainCleaner.hashCode() else 0);
   }

   @JvmStatic
   fun X509Certificate.sha1Hash(): ByteString {
      return Companion.sha1Hash(`$this$sha1Hash`);
   }

   @JvmStatic
   fun X509Certificate.sha256Hash(): ByteString {
      return Companion.sha256Hash(`$this$sha256Hash`);
   }

   @JvmStatic
   fun pin(certificate: Certificate): java.lang.String {
      return Companion.pin(certificate);
   }

   class Builder {
      public final val pins: MutableList<okhttp3.CertificatePinner.Pin> = (new ArrayList()) as java.util.List


      public fun add(pattern: String, vararg pins: String): okhttp3.CertificatePinner.Builder {
         var `$this$add_u24lambda_u240`: CertificatePinner.Builder = this;

         for (java.lang.String pin : pins) {
            `$this$add_u24lambda_u240`.pins.add(new CertificatePinner.Pin(pattern, pin));
         }

         return this;
      }

      public fun build(): CertificatePinner {
         return new CertificatePinner(CollectionsKt.toSet(this.pins), null, 2, null);
      }
   }

   @SourceDebugExtension(["SMAP\nCertificatePinner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CertificatePinner.kt\nokhttp3/CertificatePinner$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,370:1\n1#2:371\n*E\n"])
   companion object Companion private constructor() {
      public final val DEFAULT: CertificatePinner


      public fun X509Certificate.sha1Hash(): ByteString {
         var var10000: okio.ByteString.Companion = ByteString.Companion;
         var var10001: ByteArray = `$this$sha1Hash`.getPublicKey().getEncoded();
         return okio.ByteString.Companion.of$default(var10000, var10001, 0, 0, 3, null).sha1();
      }

      public fun X509Certificate.sha256Hash(): ByteString {
         var var10000: okio.ByteString.Companion = ByteString.Companion;
         var var10001: ByteArray = `$this$sha256Hash`.getPublicKey().getEncoded();
         return okio.ByteString.Companion.of$default(var10000, var10001, 0, 0, 3, null).sha256();
      }

      public fun pin(certificate: Certificate): String {
         if (certificate !is X509Certificate) {
            throw new IllegalArgumentException("Certificate pinning requires X509 certificates".toString());
         } else {
            return "sha256/" + this.sha256Hash(certificate as X509Certificate).base64();
         }
      }
   }

   class Pin(pattern: String, pin: String) {
      public final val hash: ByteString
      public final val hashAlgorithm: String
      public final val pattern: String


      public fun matchesHostname(hostname: String): Boolean {
         var var10000: Boolean;
         if (StringsKt.startsWith$default(this.pattern, "**.", false, 2, null)) {
            var suffixLength: Int = this.pattern.length() - 3;
            var prefixLength: Int = hostname.length() - suffixLength;
            var10000 = StringsKt.regionMatches$default(hostname, hostname.length() - suffixLength, this.pattern, 3, suffixLength, false, 16, null)
               && (prefixLength == 0 || hostname.charAt(prefixLength - 1) == '.');
         } else if (StringsKt.startsWith$default(this.pattern, "*.", false, 2, null)) {
            var var4: Int = this.pattern.length() - 1;
            var10000 = StringsKt.regionMatches$default(hostname, hostname.length() - var4, this.pattern, 1, var4, false, 16, null)
               && StringsKt.lastIndexOf$default(hostname, '.', hostname.length() - var4 - 1, false, 4, null) == -1;
         } else {
            var10000 = hostname == this.pattern;
         }

         return var10000;
      }

      public fun matchesCertificate(certificate: X509Certificate): Boolean {
         var var2: java.lang.String = this.hashAlgorithm;
         return if (this.hashAlgorithm == "sha256")
            this.hash == CertificatePinner.Companion.sha256Hash(certificate)
            else
            var2 == "sha1" && this.hash == CertificatePinner.Companion.sha1Hash(certificate);
      }

      public override fun toString(): String {
         return this.hashAlgorithm + '/' + this.hash.base64();
      }

      public override operator fun equals(other: Any?): Boolean {
         if (this === other) {
            return true;
         } else if (other !is CertificatePinner.Pin) {
            return false;
         } else if (!(this.pattern == (other as CertificatePinner.Pin).pattern)) {
            return false;
         } else if (!(this.hashAlgorithm == (other as CertificatePinner.Pin).hashAlgorithm)) {
            return false;
         } else {
            return this.hash == (other as CertificatePinner.Pin).hash;
         }
      }

      public override fun hashCode(): Int {
         return 31 * (31 * this.pattern.hashCode() + this.hashAlgorithm.hashCode()) + this.hash.hashCode();
      }
   }
}
