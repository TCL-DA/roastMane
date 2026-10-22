package okhttp3

import java.io.IOException
import java.security.Principal
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.Arrays
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSession
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nHandshake.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,200:1\n1549#2:201\n1620#2,3:202\n1549#2:205\n1620#2,3:206\n*S KotlinDebug\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake\n*L\n129#1:201\n129#1:202,3\n134#1:205\n134#1:206,3\n*E\n"])
class Handshake internal constructor(tlsVersion: TlsVersion,
   cipherSuite: CipherSuite,
   localCertificates: List<Certificate>,
   peerCertificatesFn: () -> List<Certificate>
) {
   @JvmStatic
   public Handshake.Companion Companion = new Handshake.Companion(null);

   public final val cipherSuite: CipherSuite
   public final val localCertificates: List<Certificate>
   public final val localPrincipal: Principal?
      public final get() {
         var var1: Any = CollectionsKt.firstOrNull(this.localCertificates);
         return if ((var1 as? X509Certificate) != null) (var1 as? X509Certificate).getSubjectX500Principal() else null;
      }

   public final val peerCertificates: List<Certificate>
      public final get() {
         return this.peerCertificates$delegate.getValue() as java.util.List<Certificate>;
      }

   public final val peerPrincipal: Principal?
      public final get() {
         var var1: Any = CollectionsKt.firstOrNull(this.peerCertificates());
         return if ((var1 as? X509Certificate) != null) (var1 as? X509Certificate).getSubjectX500Principal() else null;
      }

   public final val tlsVersion: TlsVersion
   private final val name: String
      private final get() {
         var var10000: java.lang.String;
         if (`$this$name` is X509Certificate) {
            var10000 = (`$this$name` as X509Certificate).getSubjectDN().toString();
         } else {
            var10000 = `$this$name`.getType();
         }

         return var10000;
      }


   init {
      this.tlsVersion = tlsVersion;
      this.cipherSuite = cipherSuite;
      this.localCertificates = localCertificates;
      this.peerCertificates$delegate = LazyKt.lazy((new Function0<java.util.List<? extends Certificate>>(peerCertificatesFn) {
         {
            super(0);
            this.$peerCertificatesFn = `$peerCertificatesFn`;
         }

         @NotNull
         public final java.util.List<Certificate> invoke() {
            var var1: java.util.List;
            try {
               var1 = this.$peerCertificatesFn.invoke() as java.util.List;
            } catch (SSLPeerUnverifiedException var3) {
               var1 = CollectionsKt.emptyList();
            }

            return var1;
         }
      }) as Function0);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "tlsVersion", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_tlsVersion")
   public fun tlsVersion(): TlsVersion {
      return this.tlsVersion;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "cipherSuite", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_cipherSuite")
   public fun cipherSuite(): CipherSuite {
      return this.cipherSuite;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "peerCertificates", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_peerCertificates")
   public fun peerCertificates(): List<Certificate> {
      return this.peerCertificates();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "peerPrincipal", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_peerPrincipal")
   public fun peerPrincipal(): Principal? {
      return this.peerPrincipal();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "localCertificates", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_localCertificates")
   public fun localCertificates(): List<Certificate> {
      return this.localCertificates;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "localPrincipal", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_localPrincipal")
   public fun localPrincipal(): Principal? {
      return this.localPrincipal();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Handshake
         && (other as Handshake).tlsVersion === this.tlsVersion
         && (other as Handshake).cipherSuite == this.cipherSuite
         && (other as Handshake).peerCertificates() == this.peerCertificates()
         && (other as Handshake).localCertificates == this.localCertificates;
   }

   public override fun hashCode(): Int {
      return 31 * (31 * (31 * (31 * 17 + this.tlsVersion.hashCode()) + this.cipherSuite.hashCode()) + this.peerCertificates().hashCode())
         + this.localCertificates.hashCode();
   }

   public override fun toString(): String {
      var `$this$map$iv`: java.lang.Iterable = this.peerCertificates();
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(this.getName(`item$iv$iv` as Certificate));
      }

      var var10000: StringBuilder = new StringBuilder()
         .append("Handshake{tlsVersion=")
         .append(this.tlsVersion)
         .append(" cipherSuite=")
         .append(this.cipherSuite)
         .append(" peerCertificates=")
         .append((`destination$iv$iv` as java.util.List).toString())
         .append(" localCertificates=");
      `$this$map$iv` = this.localCertificates;
      `destination$iv$iv` = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.localCertificates, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(this.getName(var18 as Certificate));
      }

      return var10000.append(`destination$iv$iv` as java.util.List).append('}').toString();
   }

   @JvmName(name = "get")
   @Throws(java/io/IOException::class)
   @JvmStatic
   fun SSLSession.get(): Handshake {
      return Companion.get(`$this$get`);
   }

   @JvmStatic
   fun get(tlsVersion: TlsVersion, cipherSuite: CipherSuite, peerCertificates: MutableList<Certificate>, localCertificates: MutableList<Certificate>): Handshake {
      return Companion.get(tlsVersion, cipherSuite, peerCertificates, localCertificates);
   }

   @SourceDebugExtension(["SMAP\nHandshake.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Handshake.kt\nokhttp3/Handshake$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,200:1\n1#2:201\n*E\n"])
   companion object Companion private constructor() {
      @JvmName(name = "get")
      @Throws(java/io/IOException::class)
      public fun SSLSession.handshake(): Handshake {
         var var10000: java.lang.String = `$this$handshake`.getCipherSuite();
         if (var10000 == null) {
            throw new IllegalStateException("cipherSuite == null".toString());
         } else if (var10000 == "TLS_NULL_WITH_NULL_NULL" || var10000 == "SSL_NULL_WITH_NULL_NULL") {
            throw new IOException("cipherSuite == " + var10000);
         } else {
            var cipherSuite: CipherSuite = CipherSuite.Companion.forJavaName(var10000);
            var10000 = `$this$handshake`.getProtocol();
            if (var10000 == null) {
               throw new IllegalStateException("tlsVersion == null".toString());
            } else if ("NONE" == var10000) {
               throw new IOException("tlsVersion == NONE");
            } else {
               var var7: java.util.List;
               try {
                  var7 = this.toImmutableList(`$this$handshake`.getPeerCertificates());
               } catch (SSLPeerUnverifiedException var9) {
                  var7 = CollectionsKt.emptyList();
               }

               return new Handshake(
                  TlsVersion.Companion.forJavaName(var10000),
                  cipherSuite,
                  this.toImmutableList(`$this$handshake`.getLocalCertificates()),
                  (new Function0<java.util.List<? extends Certificate>>(var7) {
                     {
                        super(0);
                        this.$peerCertificatesCopy = `$peerCertificatesCopy`;
                     }

                     @NotNull
                     public final java.util.List<Certificate> invoke() {
                        return this.$peerCertificatesCopy;
                     }
                  }) as Function0<? extends java.util.List<? extends Certificate>>
               );
            }
         }
      }

      private fun Array<out Certificate>?.toImmutableList(): List<Certificate> {
         return if (`$this$toImmutableList` != null)
            Util.immutableListOf(Arrays.copyOf(`$this$toImmutableList`, `$this$toImmutableList`.length))
            else
            CollectionsKt.emptyList();
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "sslSession.handshake()", imports = []), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_get")
      @Throws(java/io/IOException::class)
      public fun get(sslSession: SSLSession): Handshake {
         return this.get(sslSession);
      }

      public fun get(tlsVersion: TlsVersion, cipherSuite: CipherSuite, peerCertificates: List<Certificate>, localCertificates: List<Certificate>): Handshake {
         var peerCertificatesCopy: java.util.List = Util.toImmutableList(peerCertificates);
         return new Handshake(
            tlsVersion, cipherSuite, Util.toImmutableList(localCertificates), (new Function0<java.util.List<? extends Certificate>>(peerCertificatesCopy) {
               {
                  super(0);
                  this.$peerCertificatesCopy = `$peerCertificatesCopy`;
               }

               @NotNull
               public final java.util.List<Certificate> invoke() {
                  return this.$peerCertificatesCopy;
               }
            }) as Function0<? extends java.util.List<? extends Certificate>>
         );
      }
   }
}
