package okhttp3

import java.util.ArrayList
import java.util.Arrays
import java.util.Objects
import javax.net.ssl.SSLSocket
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util

@SourceDebugExtension(["SMAP\nConnectionSpec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,350:1\n11065#2:351\n11400#2,3:352\n11065#2:355\n11400#2,3:356\n*S KotlinDebug\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec\n*L\n59#1:351\n59#1:352,3\n75#1:355\n75#1:356,3\n*E\n"])
class ConnectionSpec internal constructor(isTls: Boolean, supportsTlsExtensions: Boolean, vararg cipherSuitesAsString: Any, vararg tlsVersionsAsString: Any) {
   @JvmStatic
   public ConnectionSpec.Companion Companion = new ConnectionSpec.Companion(null);
   private java.lang.String[] cipherSuitesAsString;
   private java.lang.String[] tlsVersionsAsString;
   @JvmStatic
   private CipherSuite[] RESTRICTED_CIPHER_SUITES = new CipherSuite[]{
      CipherSuite.TLS_AES_128_GCM_SHA256,
      CipherSuite.TLS_AES_256_GCM_SHA384,
      CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
      CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256
   };
   @JvmStatic
   private CipherSuite[] APPROVED_CIPHER_SUITES = new CipherSuite[]{
      CipherSuite.TLS_AES_128_GCM_SHA256,
      CipherSuite.TLS_AES_256_GCM_SHA384,
      CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
      CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
      CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
      CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
      CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
      CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
      CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
      CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
      CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA
   };
   @JvmField
   @JvmStatic
   public ConnectionSpec RESTRICTED_TLS = new ConnectionSpec.Builder(true)
      .cipherSuites(Arrays.copyOf(RESTRICTED_CIPHER_SUITES, RESTRICTED_CIPHER_SUITES.length))
      .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
      .supportsTlsExtensions(true)
      .build();
   @JvmField
   @JvmStatic
   public ConnectionSpec MODERN_TLS = new ConnectionSpec.Builder(true)
      .cipherSuites(Arrays.copyOf(APPROVED_CIPHER_SUITES, APPROVED_CIPHER_SUITES.length))
      .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
      .supportsTlsExtensions(true)
      .build();
   @JvmField
   @JvmStatic
   public ConnectionSpec COMPATIBLE_TLS = new ConnectionSpec.Builder(true)
      .cipherSuites(Arrays.copyOf(APPROVED_CIPHER_SUITES, APPROVED_CIPHER_SUITES.length))
      .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
      .supportsTlsExtensions(true)
      .build();
   @JvmField
   @JvmStatic
   public ConnectionSpec CLEARTEXT = new ConnectionSpec.Builder(false).build();

   public final val cipherSuites: List<CipherSuite>?
      public final get() {
         var var10000: java.util.List;
         if (this.cipherSuitesAsString != null) {
            var `$this$map$iv`: Array<Any> = this.cipherSuitesAsString;
            var `destination$iv$iv`: java.util.Collection = new ArrayList(this.cipherSuitesAsString.length);

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(CipherSuite.Companion.forJavaName((java.lang.String)`item$iv$iv`));
            }

            var10000 = CollectionsKt.toList(`destination$iv$iv` as java.util.List);
         } else {
            var10000 = null;
         }

         return var10000;
      }

   private final val cipherSuitesAsString: Array<String>?
   public final val isTls: Boolean
   public final val supportsTlsExtensions: Boolean
   public final val tlsVersions: List<TlsVersion>?
      public final get() {
         var var10000: java.util.List;
         if (this.tlsVersionsAsString != null) {
            var `$this$map$iv`: Array<Any> = this.tlsVersionsAsString;
            var `destination$iv$iv`: java.util.Collection = new ArrayList(this.tlsVersionsAsString.length);

            for (Object item$iv$iv : $this$map$iv) {
               `destination$iv$iv`.add(TlsVersion.Companion.forJavaName((java.lang.String)`item$iv$iv`));
            }

            var10000 = CollectionsKt.toList(`destination$iv$iv` as java.util.List);
         } else {
            var10000 = null;
         }

         return var10000;
      }

   private final val tlsVersionsAsString: Array<String>?

   init {
      this.isTls = isTls;
      this.supportsTlsExtensions = supportsTlsExtensions;
      this.cipherSuitesAsString = cipherSuitesAsString;
      this.tlsVersionsAsString = tlsVersionsAsString;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cipherSuites", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cipherSuites")
   public fun cipherSuites(): List<CipherSuite>? {
      return this.cipherSuites();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "tlsVersions", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_tlsVersions")
   public fun tlsVersions(): List<TlsVersion>? {
      return this.tlsVersions();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "supportsTlsExtensions", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_supportsTlsExtensions")
   public fun supportsTlsExtensions(): Boolean {
      return this.supportsTlsExtensions;
   }

   internal fun apply(sslSocket: SSLSocket, isFallback: Boolean) {
      var specToApply: ConnectionSpec = this.supportedSpec(sslSocket, isFallback);
      if (specToApply.tlsVersions() != null) {
         sslSocket.setEnabledProtocols(specToApply.tlsVersionsAsString);
      }

      if (specToApply.cipherSuites() != null) {
         sslSocket.setEnabledCipherSuites(specToApply.cipherSuitesAsString);
      }
   }

   private fun supportedSpec(sslSocket: SSLSocket, isFallback: Boolean): ConnectionSpec {
      var var8: Array<java.lang.String>;
      if (this.cipherSuitesAsString != null) {
         var8 = sslSocket.getEnabledCipherSuites();
         var8 = Util.intersect(var8, this.cipherSuitesAsString, CipherSuite.Companion.getORDER_BY_NAME$okhttp());
      } else {
         var8 = sslSocket.getEnabledCipherSuites();
      }

      var cipherSuitesIntersection: Array<java.lang.String> = var8;
      if (this.tlsVersionsAsString != null) {
         var8 = sslSocket.getEnabledProtocols();
         var8 = Util.intersect(var8, this.tlsVersionsAsString, ComparisonsKt.naturalOrder());
      } else {
         var8 = sslSocket.getEnabledProtocols();
      }

      var supportedCipherSuites: Array<java.lang.String> = sslSocket.getSupportedCipherSuites();
      var indexOfFallbackScsv: Int = Util.indexOf(supportedCipherSuites, "TLS_FALLBACK_SCSV", CipherSuite.Companion.getORDER_BY_NAME$okhttp());
      if (isFallback && indexOfFallbackScsv != -1) {
         var var10001: java.lang.String = supportedCipherSuites[indexOfFallbackScsv];
         cipherSuitesIntersection = Util.concat(var8, var10001);
      }

      var var11: ConnectionSpec.Builder = new ConnectionSpec.Builder(this);
      var var12: ConnectionSpec.Builder = var11.cipherSuites(Arrays.copyOf(cipherSuitesIntersection, cipherSuitesIntersection.length));
      return var12.tlsVersions(Arrays.copyOf(var8, var8.length)).build();
   }

   public fun isCompatible(socket: SSLSocket): Boolean {
      if (!this.isTls) {
         return false;
      } else if (this.tlsVersionsAsString != null
         && !Util.hasIntersection(this.tlsVersionsAsString, socket.getEnabledProtocols(), ComparisonsKt.naturalOrder())) {
         return false;
      } else {
         return this.cipherSuitesAsString == null
            || Util.hasIntersection(this.cipherSuitesAsString, socket.getEnabledCipherSuites(), CipherSuite.Companion.getORDER_BY_NAME$okhttp());
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      if (other !is ConnectionSpec) {
         return false;
      } else if (other === this) {
         return true;
      } else if (this.isTls != (other as ConnectionSpec).isTls) {
         return false;
      } else {
         if (this.isTls) {
            if (!Arrays.equals((Object[])this.cipherSuitesAsString, (Object[])(other as ConnectionSpec).cipherSuitesAsString)) {
               return false;
            }

            if (!Arrays.equals((Object[])this.tlsVersionsAsString, (Object[])(other as ConnectionSpec).tlsVersionsAsString)) {
               return false;
            }

            if (this.supportsTlsExtensions != (other as ConnectionSpec).supportsTlsExtensions) {
               return false;
            }
         }

         return true;
      }
   }

   public override fun hashCode(): Int {
      var result: Int = 17;
      if (this.isTls) {
         result = 31
               * (
                  31 * (31 * 17 + (if (this.cipherSuitesAsString != null) Arrays.hashCode((Object[])this.cipherSuitesAsString) else 0))
                     + (if (this.tlsVersionsAsString != null) Arrays.hashCode((Object[])this.tlsVersionsAsString) else 0)
               )
            + (if (this.supportsTlsExtensions) 0 else 1);
      }

      return result;
   }

   public override fun toString(): String {
      return if (!this.isTls)
         "ConnectionSpec()"
         else
         "ConnectionSpec(cipherSuites="
            + Objects.toString(this.cipherSuites(), "[all enabled]")
            + ", tlsVersions="
            + Objects.toString(this.tlsVersions(), "[all enabled]")
            + ", supportsTlsExtensions="
            + this.supportsTlsExtensions
            + ')';
   }

   @SourceDebugExtension(["SMAP\nConnectionSpec.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,350:1\n1#2:351\n11065#3:352\n11400#3,3:353\n11065#3:358\n11400#3,3:359\n37#4,2:356\n37#4,2:362\n*S KotlinDebug\n*F\n+ 1 ConnectionSpec.kt\nokhttp3/ConnectionSpec$Builder\n*L\n225#1:352\n225#1:353,3\n244#1:358\n244#1:359,3\n225#1:356,2\n244#1:362,2\n*E\n"])
   class Builder {
      private java.lang.String[] cipherSuites;
      private java.lang.String[] tlsVersions;

      internal final var cipherSuites: Array<String>?
      internal final var supportsTlsExtensions: Boolean
      internal final var tls: Boolean
      internal final var tlsVersions: Array<String>?

      internal constructor(tls: Boolean)  {
         this.tls = tls;
      }

      public constructor(connectionSpec: ConnectionSpec)  {
         this.tls = connectionSpec.isTls();
         this.cipherSuites = ConnectionSpec.access$getCipherSuitesAsString$p(connectionSpec);
         this.tlsVersions = ConnectionSpec.access$getTlsVersionsAsString$p(connectionSpec);
         this.supportsTlsExtensions = connectionSpec.supportsTlsExtensions();
      }

      public fun allEnabledCipherSuites(): okhttp3.ConnectionSpec.Builder {
         var `$this$allEnabledCipherSuites_u24lambda_u241`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no cipher suites for cleartext connections".toString());
         } else {
            `$this$allEnabledCipherSuites_u24lambda_u241`.cipherSuites = null;
            return this;
         }
      }

      public fun cipherSuites(vararg cipherSuites: CipherSuite): okhttp3.ConnectionSpec.Builder {
         var `$this$cipherSuites_u24lambda_u244`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no cipher suites for cleartext connections".toString());
         } else {
            var `destination$iv$iv`: java.util.Collection = new ArrayList(cipherSuites.length);

            for (Object item$iv$iv : cipherSuites) {
               `destination$iv$iv`.add(((CipherSuite)`item$iv$iv`).javaName());
            }

            var strings: Array<java.lang.String> = (`destination$iv$iv` as java.util.List).toArray(new java.lang.String[0]);
            return `$this$cipherSuites_u24lambda_u244`.cipherSuites(Arrays.copyOf(strings, strings.length));
         }
      }

      public fun cipherSuites(vararg cipherSuites: String): okhttp3.ConnectionSpec.Builder {
         var `$this$cipherSuites_u24lambda_u247`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no cipher suites for cleartext connections".toString());
         } else if (cipherSuites.length == 0) {
            throw new IllegalArgumentException("At least one cipher suite is required".toString());
         } else {
            `$this$cipherSuites_u24lambda_u247`.cipherSuites = cipherSuites.clone() as java.lang.String[];
            return this;
         }
      }

      public fun allEnabledTlsVersions(): okhttp3.ConnectionSpec.Builder {
         var `$this$allEnabledTlsVersions_u24lambda_u249`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no TLS versions for cleartext connections".toString());
         } else {
            `$this$allEnabledTlsVersions_u24lambda_u249`.tlsVersions = null;
            return this;
         }
      }

      public fun tlsVersions(vararg tlsVersions: TlsVersion): okhttp3.ConnectionSpec.Builder {
         var `$this$tlsVersions_u24lambda_u2412`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no TLS versions for cleartext connections".toString());
         } else {
            var `destination$iv$iv`: java.util.Collection = new ArrayList(tlsVersions.length);

            for (Object item$iv$iv : tlsVersions) {
               `destination$iv$iv`.add(((TlsVersion)`item$iv$iv`).javaName());
            }

            var strings: Array<java.lang.String> = (`destination$iv$iv` as java.util.List).toArray(new java.lang.String[0]);
            return `$this$tlsVersions_u24lambda_u2412`.tlsVersions(Arrays.copyOf(strings, strings.length));
         }
      }

      public fun tlsVersions(vararg tlsVersions: String): okhttp3.ConnectionSpec.Builder {
         var `$this$tlsVersions_u24lambda_u2415`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no TLS versions for cleartext connections".toString());
         } else if (tlsVersions.length == 0) {
            throw new IllegalArgumentException("At least one TLS version is required".toString());
         } else {
            `$this$tlsVersions_u24lambda_u2415`.tlsVersions = tlsVersions.clone() as java.lang.String[];
            return this;
         }
      }

      @Deprecated(message = "since OkHttp 3.13 all TLS-connections are expected to support TLS extensions.\nIn a future release setting this to true will be unnecessary and setting it to false\nwill have no effect.")
      public fun supportsTlsExtensions(supportsTlsExtensions: Boolean): okhttp3.ConnectionSpec.Builder {
         var `$this$supportsTlsExtensions_u24lambda_u2417`: ConnectionSpec.Builder = this;
         if (!this.tls) {
            throw new IllegalArgumentException("no TLS extensions for cleartext connections".toString());
         } else {
            `$this$supportsTlsExtensions_u24lambda_u2417`.supportsTlsExtensions = supportsTlsExtensions;
            return this;
         }
      }

      public fun build(): ConnectionSpec {
         return new ConnectionSpec(this.tls, this.supportsTlsExtensions, this.cipherSuites, this.tlsVersions);
      }
   }

   companion object Companion private constructor() {
      private final val APPROVED_CIPHER_SUITES: Array<CipherSuite>
      public final val CLEARTEXT: ConnectionSpec
      public final val COMPATIBLE_TLS: ConnectionSpec
      public final val MODERN_TLS: ConnectionSpec
      private final val RESTRICTED_CIPHER_SUITES: Array<CipherSuite>
      public final val RESTRICTED_TLS: ConnectionSpec

   }
}
