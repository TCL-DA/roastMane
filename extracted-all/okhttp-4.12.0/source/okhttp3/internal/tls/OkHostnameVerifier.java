package okhttp3.internal.tls

import java.security.cert.Certificate
import java.security.cert.CertificateParsingException
import java.security.cert.X509Certificate
import java.util.ArrayList
import java.util.Locale
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLException
import javax.net.ssl.SSLSession
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.HostnamesKt
import okhttp3.internal.Util
import okio.Utf8

@SourceDebugExtension(["SMAP\nOkHostnameVerifier.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OkHostnameVerifier.kt\nokhttp3/internal/tls/OkHostnameVerifier\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,208:1\n1747#2,3:209\n1747#2,3:212\n*S KotlinDebug\n*F\n+ 1 OkHostnameVerifier.kt\nokhttp3/internal/tls/OkHostnameVerifier\n*L\n63#1:209,3\n71#1:212,3\n*E\n"])
object OkHostnameVerifier private constructor() : HostnameVerifier {
   @JvmStatic
   public OkHostnameVerifier INSTANCE = new OkHostnameVerifier();

   private const val ALT_DNS_NAME: Int = 2
   private const val ALT_IPA_NAME: Int = 7


   public override fun verify(host: String, session: SSLSession): Boolean {
      var var10000: Boolean;
      if (!this.isAscii(host)) {
         var10000 = false;
      } else {
         var var3: Boolean;
         try {
            var var10002: Certificate = session.getPeerCertificates()[0];
            var3 = this.verify(host, var10002 as X509Certificate);
         } catch (SSLException var5) {
            var3 = false;
         }

         var10000 = var3;
      }

      return var10000;
   }

   public fun verify(host: String, certificate: X509Certificate): Boolean {
      return if (Util.canParseAsIpAddress(host)) this.verifyIpAddress(host, certificate) else this.verifyHostname(host, certificate);
   }

   private fun verifyIpAddress(ipAddress: String, certificate: X509Certificate): Boolean {
      var canonicalIpAddress: java.lang.String = HostnamesKt.toCanonicalHost(ipAddress);
      var `$this$any$iv`: java.lang.Iterable = this.getSubjectAltNames(certificate, 7);
      var var10000: Boolean;
      if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
         var10000 = false;
      } else {
         var var6: java.util.Iterator = `$this$any$iv`.iterator();

         while (true) {
            if (!var6.hasNext()) {
               var10000 = false;
               break;
            }

            if (canonicalIpAddress == HostnamesKt.toCanonicalHost(var6.next() as java.lang.String)) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private fun verifyHostname(hostname: String, certificate: X509Certificate): Boolean {
      var hostnamex: java.lang.String = this.asciiToLowercase(hostname);
      var `$this$any$iv`: java.lang.Iterable = this.getSubjectAltNames(certificate, 2);
      var var10000: Boolean;
      if (`$this$any$iv` is java.util.Collection && (`$this$any$iv` as java.util.Collection).isEmpty()) {
         var10000 = false;
      } else {
         var var6: java.util.Iterator = `$this$any$iv`.iterator();

         while (true) {
            if (!var6.hasNext()) {
               var10000 = false;
               break;
            }

            if (INSTANCE.verifyHostname(hostnamex, var6.next() as java.lang.String)) {
               var10000 = true;
               break;
            }
         }
      }

      return var10000;
   }

   private fun String.asciiToLowercase(): String {
      var var3: java.lang.String;
      if (this.isAscii(`$this$asciiToLowercase`)) {
         var var10000: Locale = Locale.US;
         var3 = `$this$asciiToLowercase`.toLowerCase(var10000);
      } else {
         var3 = `$this$asciiToLowercase`;
      }

      return var3;
   }

   private fun String.isAscii(): Boolean {
      return `$this$isAscii`.length() == (int)Utf8.size$default(`$this$isAscii`, 0, 0, 3, null);
   }

   private fun verifyHostname(hostname: String?, pattern: String?): Boolean {
      var hostnamex: java.lang.String = hostname;
      var patternx: java.lang.String = pattern;
      if (hostname != null
         && hostname.length() != 0
         && !StringsKt.startsWith$default(hostname, ".", false, 2, null)
         && !StringsKt.endsWith$default(hostname, "..", false, 2, null)) {
         if (pattern != null
            && pattern.length() != 0
            && !StringsKt.startsWith$default(pattern, ".", false, 2, null)
            && !StringsKt.endsWith$default(pattern, "..", false, 2, null)) {
            if (!StringsKt.endsWith$default(hostname, ".", false, 2, null)) {
               hostnamex = hostname + '.';
            }

            if (!StringsKt.endsWith$default(pattern, ".", false, 2, null)) {
               patternx = pattern + '.';
            }

            patternx = this.asciiToLowercase(patternx);
            if (!StringsKt.contains$default(patternx, "*", false, 2, null)) {
               return hostnamex == patternx;
            } else if (!StringsKt.startsWith$default(patternx, "*.", false, 2, null) || StringsKt.indexOf$default(patternx, '*', 1, false, 4, null) != -1) {
               return false;
            } else if (hostnamex.length() < patternx.length()) {
               return false;
            } else if ("*." == patternx) {
               return false;
            } else {
               var var10000: java.lang.String = patternx.substring(1);
               if (!StringsKt.endsWith$default(hostnamex, var10000, false, 2, null)) {
                  return false;
               } else {
                  var suffixStartIndexInHostname: Int = hostnamex.length() - var10000.length();
                  return suffixStartIndexInHostname <= 0 || StringsKt.lastIndexOf$default(hostnamex, '.', suffixStartIndexInHostname - 1, false, 4, null) == -1;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public fun allSubjectAltNames(certificate: X509Certificate): List<String> {
      return CollectionsKt.plus(this.getSubjectAltNames(certificate, 7), this.getSubjectAltNames(certificate, 2));
   }

   private fun getSubjectAltNames(certificate: X509Certificate, type: Int): List<String> {
      try {
         if (certificate.getSubjectAlternativeNames() == null) {
            return CollectionsKt.emptyList();
         } else {
            var result: java.util.List = new ArrayList();

            var _: java.util.Collection;
            for (java.util.List subjectAltName : _) {
               if (subjectAltName != null && subjectAltName.size() >= 2 && subjectAltName.get(0) == type) {
                  var var9: Any = subjectAltName.get(1);
                  if (var9 != null) {
                     result.add(var9 as java.lang.String);
                  }
               }
            }

            return result;
         }
      } catch (CertificateParsingException var8) {
         return CollectionsKt.emptyList();
      }
   }
}
