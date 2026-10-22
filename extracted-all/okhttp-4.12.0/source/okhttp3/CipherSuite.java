package okhttp3

import java.util.Comparator
import java.util.LinkedHashMap
import kotlin.jvm.internal.Intrinsics
import org.jetbrains.annotations.NotNull

class CipherSuite private constructor(javaName: String) {
   @JvmStatic
   public CipherSuite.Companion Companion = new CipherSuite.Companion(null);
   @JvmStatic
   private Comparator<java.lang.String> ORDER_BY_NAME = new Comparator<java.lang.String>() {
      public int compare(@NotNull java.lang.String a, @NotNull java.lang.String b) {
         var i: Int = 4;

         for (int limit = Math.min(a.length(), b.length()); i < limit; i++) {
            var lengthA: Char = a.charAt(i);
            var lengthB: Char = b.charAt(i);
            if (lengthA != lengthB) {
               return if (Intrinsics.compare(lengthA, lengthB) < 0) -1 else 1;
            }
         }

         var var7: Int = a.length();
         var var8: Int = b.length();
         if (var7 != var8) {
            return if (var7 < var8) -1 else 1;
         } else {
            return 0;
         }
      }
   };
   @JvmStatic
   private java.util.Map<java.lang.String, CipherSuite> INSTANCES = new LinkedHashMap<>();
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_NULL_MD5 = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_NULL_MD5", 1);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_NULL_SHA", 2);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_EXPORT_WITH_RC4_40_MD5 = CipherSuite.Companion.access$init(Companion, "SSL_RSA_EXPORT_WITH_RC4_40_MD5", 3);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_RC4_128_MD5 = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_RC4_128_MD5", 4);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_RC4_128_SHA", 5);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_EXPORT_WITH_DES40_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", 8);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_DES_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_DES_CBC_SHA", 9);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_RSA_WITH_3DES_EDE_CBC_SHA", 10);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", 17);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_DES_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_DSS_WITH_DES_CBC_SHA", 18);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", 19);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 20);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_DES_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_RSA_WITH_DES_CBC_SHA", 21);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", 22);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_EXPORT_WITH_RC4_40_MD5 = CipherSuite.Companion.access$init(Companion, "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 23);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_RC4_128_MD5 = CipherSuite.Companion.access$init(Companion, "SSL_DH_anon_WITH_RC4_128_MD5", 24);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", 25);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_DES_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DH_anon_WITH_DES_CBC_SHA", 26);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 27);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_DES_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_DES_CBC_SHA", 30);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_3DES_EDE_CBC_SHA", 31);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_RC4_128_SHA", 32);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_DES_CBC_MD5 = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_DES_CBC_MD5", 34);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_3DES_EDE_CBC_MD5 = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_3DES_EDE_CBC_MD5", 35);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_WITH_RC4_128_MD5 = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_WITH_RC4_128_MD5", 36);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", 38);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_EXPORT_WITH_RC4_40_SHA = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_EXPORT_WITH_RC4_40_SHA", 40);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5 = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", 41);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_KRB5_EXPORT_WITH_RC4_40_MD5 = CipherSuite.Companion.access$init(Companion, "TLS_KRB5_EXPORT_WITH_RC4_40_MD5", 43);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_128_CBC_SHA", 47);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 50);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 51);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_128_CBC_SHA", 52);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_256_CBC_SHA", 53);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_256_CBC_SHA", 56);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 57);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_256_CBC_SHA", 58);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_NULL_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_NULL_SHA256", 59);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_128_CBC_SHA256", 60);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_256_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_256_CBC_SHA256", 61);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 64);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_CAMELLIA_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", 65);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", 68);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", 69);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 103);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_256_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 106);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_256_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 107);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_128_CBC_SHA256", 108);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_256_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_256_CBC_SHA256", 109);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_CAMELLIA_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", 132);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", 135);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", 136);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_PSK_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_PSK_WITH_RC4_128_SHA", 138);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_PSK_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_PSK_WITH_3DES_EDE_CBC_SHA", 139);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_PSK_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_PSK_WITH_AES_128_CBC_SHA", 140);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_PSK_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_PSK_WITH_AES_256_CBC_SHA", 141);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_SEED_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_SEED_CBC_SHA", 150);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_128_GCM_SHA256", 156);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_RSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_RSA_WITH_AES_256_GCM_SHA384", 157);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 158);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 159);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 162);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_DSS_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 163);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_128_GCM_SHA256", 166);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DH_anon_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_DH_anon_WITH_AES_256_GCM_SHA384", 167);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_EMPTY_RENEGOTIATION_INFO_SCSV = CipherSuite.Companion.access$init(Companion, "TLS_EMPTY_RENEGOTIATION_INFO_SCSV", 255);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_FALLBACK_SCSV = CipherSuite.Companion.access$init(Companion, "TLS_FALLBACK_SCSV", 22016);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_NULL_SHA", 49153);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_RC4_128_SHA", 49154);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", 49155);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 49156);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 49157);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_NULL_SHA", 49158);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", 49159);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", 49160);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 49161);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 49162);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_NULL_SHA", 49163);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_RC4_128_SHA", 49164);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", 49165);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 49166);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 49167);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_NULL_SHA", 49168);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_RC4_128_SHA", 49169);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", 49170);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 49171);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 49172);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_anon_WITH_NULL_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_anon_WITH_NULL_SHA", 49173);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_anon_WITH_RC4_128_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_anon_WITH_RC4_128_SHA", 49174);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", 49175);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_anon_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_anon_WITH_AES_128_CBC_SHA", 49176);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_anon_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_anon_WITH_AES_256_CBC_SHA", 49177);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 49187);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 49188);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 49189);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 49190);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 49191);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 49192);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 49193);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 49194);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 49195);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 49196);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 49197);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 49198);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 49199);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 49200);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 49201);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 49202);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", 49205);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA = CipherSuite.Companion.access$init(Companion, "TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", 49206);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256 = CipherSuite.Companion.access$init(
      Companion, "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", 52392
   );
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256 = CipherSuite.Companion.access$init(
      Companion, "TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256", 52393
   );
   @JvmField
   @JvmStatic
   public CipherSuite TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256 = CipherSuite.Companion.access$init(
      Companion, "TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256", 52394
   );
   @JvmField
   @JvmStatic
   public CipherSuite TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256 = CipherSuite.Companion.access$init(
      Companion, "TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256", 52396
   );
   @JvmField
   @JvmStatic
   public CipherSuite TLS_AES_128_GCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_AES_128_GCM_SHA256", 4865);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_AES_256_GCM_SHA384 = CipherSuite.Companion.access$init(Companion, "TLS_AES_256_GCM_SHA384", 4866);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_CHACHA20_POLY1305_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_CHACHA20_POLY1305_SHA256", 4867);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_AES_128_CCM_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_AES_128_CCM_SHA256", 4868);
   @JvmField
   @JvmStatic
   public CipherSuite TLS_AES_128_CCM_8_SHA256 = CipherSuite.Companion.access$init(Companion, "TLS_AES_128_CCM_8_SHA256", 4869);

   public final val javaName: String

   init {
      this.javaName = javaName;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "javaName", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_javaName")
   public fun javaName(): String {
      return this.javaName;
   }

   public override fun toString(): String {
      return this.javaName;
   }

   @Synchronized
   @JvmStatic
   fun forJavaName(javaName: java.lang.String): CipherSuite {
      return Companion.forJavaName(javaName);
   }

   companion object Companion private constructor() {
      private final val INSTANCES: MutableMap<String, CipherSuite>
      internal final val ORDER_BY_NAME: Comparator<String>
      public final val TLS_AES_128_CCM_8_SHA256: CipherSuite
      public final val TLS_AES_128_CCM_SHA256: CipherSuite
      public final val TLS_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_CHACHA20_POLY1305_SHA256: CipherSuite
      public final val TLS_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_256_CBC_SHA256: CipherSuite
      public final val TLS_DHE_DSS_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA: CipherSuite
      public final val TLS_DHE_DSS_WITH_DES_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_256_CBC_SHA256: CipherSuite
      public final val TLS_DHE_RSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA: CipherSuite
      public final val TLS_DHE_RSA_WITH_CHACHA20_POLY1305_SHA256: CipherSuite
      public final val TLS_DHE_RSA_WITH_DES_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_EXPORT_WITH_DES40_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_EXPORT_WITH_RC4_40_MD5: CipherSuite
      public final val TLS_DH_anon_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_DH_anon_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_DH_anon_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_WITH_AES_256_CBC_SHA256: CipherSuite
      public final val TLS_DH_anon_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_DH_anon_WITH_DES_CBC_SHA: CipherSuite
      public final val TLS_DH_anon_WITH_RC4_128_MD5: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_NULL_SHA: CipherSuite
      public final val TLS_ECDHE_ECDSA_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_PSK_WITH_CHACHA20_POLY1305_SHA256: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_NULL_SHA: CipherSuite
      public final val TLS_ECDHE_RSA_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_NULL_SHA: CipherSuite
      public final val TLS_ECDH_ECDSA_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384: CipherSuite
      public final val TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_ECDH_RSA_WITH_NULL_SHA: CipherSuite
      public final val TLS_ECDH_RSA_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_ECDH_anon_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_ECDH_anon_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_ECDH_anon_WITH_NULL_SHA: CipherSuite
      public final val TLS_ECDH_anon_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_EMPTY_RENEGOTIATION_INFO_SCSV: CipherSuite
      public final val TLS_FALLBACK_SCSV: CipherSuite
      public final val TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5: CipherSuite
      public final val TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA: CipherSuite
      public final val TLS_KRB5_EXPORT_WITH_RC4_40_MD5: CipherSuite
      public final val TLS_KRB5_EXPORT_WITH_RC4_40_SHA: CipherSuite
      public final val TLS_KRB5_WITH_3DES_EDE_CBC_MD5: CipherSuite
      public final val TLS_KRB5_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_KRB5_WITH_DES_CBC_MD5: CipherSuite
      public final val TLS_KRB5_WITH_DES_CBC_SHA: CipherSuite
      public final val TLS_KRB5_WITH_RC4_128_MD5: CipherSuite
      public final val TLS_KRB5_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_PSK_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_PSK_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_PSK_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_PSK_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_RSA_EXPORT_WITH_DES40_CBC_SHA: CipherSuite
      public final val TLS_RSA_EXPORT_WITH_RC4_40_MD5: CipherSuite
      public final val TLS_RSA_WITH_3DES_EDE_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_AES_128_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_AES_128_CBC_SHA256: CipherSuite
      public final val TLS_RSA_WITH_AES_128_GCM_SHA256: CipherSuite
      public final val TLS_RSA_WITH_AES_256_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_AES_256_CBC_SHA256: CipherSuite
      public final val TLS_RSA_WITH_AES_256_GCM_SHA384: CipherSuite
      public final val TLS_RSA_WITH_CAMELLIA_128_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_CAMELLIA_256_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_DES_CBC_SHA: CipherSuite
      public final val TLS_RSA_WITH_NULL_MD5: CipherSuite
      public final val TLS_RSA_WITH_NULL_SHA: CipherSuite
      public final val TLS_RSA_WITH_NULL_SHA256: CipherSuite
      public final val TLS_RSA_WITH_RC4_128_MD5: CipherSuite
      public final val TLS_RSA_WITH_RC4_128_SHA: CipherSuite
      public final val TLS_RSA_WITH_SEED_CBC_SHA: CipherSuite


      @Synchronized
      public fun forJavaName(javaName: String): CipherSuite {
         var result: CipherSuite = CipherSuite.access$getINSTANCES$cp().get(javaName) as CipherSuite;
         if (result == null) {
            result = CipherSuite.access$getINSTANCES$cp().get(this.secondaryName(javaName)) as CipherSuite;
            if (result == null) {
               result = new CipherSuite(javaName, null);
            }

            CipherSuite.access$getINSTANCES$cp().put(javaName, result);
         }

         return result;
      }

      private fun secondaryName(javaName: String): String {
         var var2: java.lang.String;
         if (StringsKt.startsWith$default(javaName, "TLS_", false, 2, null)) {
            var var10000: StringBuilder = new StringBuilder().append("SSL_");
            var var10001: java.lang.String = javaName.substring(4);
            var2 = var10000.append(var10001).toString();
         } else if (StringsKt.startsWith$default(javaName, "SSL_", false, 2, null)) {
            var var3: StringBuilder = new StringBuilder().append("TLS_");
            var var4: java.lang.String = javaName.substring(4);
            var2 = var3.append(var4).toString();
         } else {
            var2 = javaName;
         }

         return var2;
      }

      private fun init(javaName: String, value: Int): CipherSuite {
         var suite: CipherSuite = new CipherSuite(javaName, null);
         CipherSuite.access$getINSTANCES$cp().put(javaName, suite);
         return suite;
      }
   }
}
