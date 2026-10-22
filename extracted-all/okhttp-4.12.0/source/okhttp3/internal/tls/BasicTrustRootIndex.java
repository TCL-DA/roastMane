package okhttp3.internal.tls

import java.security.cert.X509Certificate
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import javax.security.auth.x500.X500Principal
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nBasicTrustRootIndex.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BasicTrustRootIndex.kt\nokhttp3/internal/tls/BasicTrustRootIndex\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,56:1\n372#2,7:57\n288#3,2:64\n*S KotlinDebug\n*F\n+ 1 BasicTrustRootIndex.kt\nokhttp3/internal/tls/BasicTrustRootIndex\n*L\n28#1:57,7\n37#1:64,2\n*E\n"])
class BasicTrustRootIndex(vararg caCerts: X509Certificate) : TrustRootIndex {
   private final val subjectToCaCerts: Map<X500Principal, Set<X509Certificate>>

   init {
      var map: java.util.Map = new LinkedHashMap();

      for (X509Certificate caCert : caCerts) {
         var var10000: X500Principal = caCert.getSubjectX500Principal();
         var `value$iv`: Any = map.get(var10000);
         if (`value$iv` == null) {
            var var11: Any = new LinkedHashSet();
            map.put(var10000, var11);
            var10000 = (X500Principal)var11;
         } else {
            var10000 = (X500Principal)`value$iv`;
         }

         (var10000 as java.util.Set).add(caCert);
      }

      this.subjectToCaCerts = map;
   }

   public override fun findByIssuerAndSignature(cert: X509Certificate): X509Certificate? {
      var var10000: java.util.Set = this.subjectToCaCerts.get(cert.getIssuerX500Principal());
      if (var10000 == null) {
         return null;
      } else {
         var var6: java.util.Iterator = var10000.iterator();

         while (true) {
            if (var6.hasNext()) {
               var `element$iv`: Any = var6.next();
               var it: X509Certificate = `element$iv` as X509Certificate;

               try {
                  cert.verify(it.getPublicKey());
                  var13 = true;
               } catch (Exception var11) {
                  var13 = false;
               }

               if (!var13) {
                  continue;
               }

               var10000 = (java.util.Set)`element$iv`;
               break;
            }

            var10000 = null;
            break;
         }

         return var10000 as X509Certificate;
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      return other === this || other is BasicTrustRootIndex && (other as BasicTrustRootIndex).subjectToCaCerts == this.subjectToCaCerts;
   }

   public override fun hashCode(): Int {
      return this.subjectToCaCerts.hashCode();
   }
}
