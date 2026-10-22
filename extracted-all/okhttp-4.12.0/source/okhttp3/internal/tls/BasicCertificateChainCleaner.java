package okhttp3.internal.tls

import java.security.GeneralSecurityException
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.ArrayDeque
import java.util.ArrayList
import java.util.Deque
import javax.net.ssl.SSLPeerUnverifiedException

class BasicCertificateChainCleaner(trustRootIndex: TrustRootIndex) : CertificateChainCleaner {
   @JvmStatic
   public BasicCertificateChainCleaner.Companion Companion = new BasicCertificateChainCleaner.Companion(null);
   @JvmStatic
   private int MAX_SIGNERS = 9;

   private final val trustRootIndex: TrustRootIndex

   init {
      this.trustRootIndex = trustRootIndex;
   }

   @Throws(javax/net/ssl/SSLPeerUnverifiedException::class)
   public override fun clean(chain: List<Certificate>, hostname: String): List<Certificate> {
      var queue: Deque = new ArrayDeque(chain);
      var result: java.util.List = new ArrayList();
      var var10001: Any = queue.removeFirst();
      result.add(var10001);
      var foundTrustedCertificate: Boolean = false;

      for (int c = 0; c < 9; c++) {
         var var10000: Any = result.get(result.size() - 1);
         var toVerify: X509Certificate = var10000 as X509Certificate;
         var trustedCert: X509Certificate = this.trustRootIndex.findByIssuerAndSignature(var10000 as X509Certificate);
         if (trustedCert == null) {
            var10000 = queue.iterator();
            var i: java.util.Iterator = (java.util.Iterator)var10000;

            do {
               if (!i.hasNext()) {
                  if (foundTrustedCertificate) {
                     return result;
                  }

                  throw new SSLPeerUnverifiedException("Failed to find a trusted cert that signed " + toVerify);
               }

               var10000 = i.next();
            } while (!this.verifySignature(toVerify, (X509Certificate)var10000));

            i.remove();
            result.add(var10000 as X509Certificate);
         } else {
            if (result.size() > 1 || !(toVerify == trustedCert)) {
               result.add(trustedCert);
            }

            if (this.verifySignature(trustedCert, trustedCert)) {
               return result;
            }

            foundTrustedCertificate = true;
         }
      }

      throw new SSLPeerUnverifiedException("Certificate chain too long: " + result);
   }

   private fun verifySignature(toVerify: X509Certificate, signingCert: X509Certificate): Boolean {
      if (!(toVerify.getIssuerDN() == signingCert.getSubjectDN())) {
         return false;
      } else {
         var var3: Boolean;
         try {
            toVerify.verify(signingCert.getPublicKey());
            var3 = true;
         } catch (GeneralSecurityException var5) {
            var3 = false;
         }

         return var3;
      }
   }

   public override fun hashCode(): Int {
      return this.trustRootIndex.hashCode();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this || other is BasicCertificateChainCleaner && (other as BasicCertificateChainCleaner).trustRootIndex == this.trustRootIndex;
   }

   companion object Companion private constructor() {
      private const val MAX_SIGNERS: Int

   }
}
