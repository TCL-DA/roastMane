package io.netty.handler.ssl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
   static final String KEY_TYPE_RSA = "RSA";
   static final String KEY_TYPE_DH_RSA = "DH_RSA";
   static final String KEY_TYPE_EC = "EC";
   static final String KEY_TYPE_EC_EC = "EC_EC";
   static final String KEY_TYPE_EC_RSA = "EC_RSA";
   private static final Map<String, String> KEY_TYPES = new HashMap<>();
   private final OpenSslKeyMaterialProvider provider;

   OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider provider) {
      this.provider = provider;
   }

   void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine engine) throws SSLException {
      String[] authMethods = engine.authMethods();
      if (authMethods.length == 0) {
         throw new SSLHandshakeException("Unable to find key material");
      } else {
         Set<String> typeSet = new HashSet<>(KEY_TYPES.size());

         for (String authMethod : authMethods) {
            String type = KEY_TYPES.get(authMethod);
            if (type != null && typeSet.add(type)) {
               String alias = this.chooseServerAlias(engine, type);
               if (alias != null) {
                  this.setKeyMaterial(engine, alias);
                  return;
               }
            }
         }

         throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString((Object[])authMethods));
      }
   }

   void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) throws SSLException {
      String alias = this.chooseClientAlias(engine, keyTypes, issuer);
      if (alias != null) {
         this.setKeyMaterial(engine, alias);
      }
   }

   private void setKeyMaterial(ReferenceCountedOpenSslEngine engine, String alias) throws SSLException {
      OpenSslKeyMaterial keyMaterial = null;

      try {
         keyMaterial = this.provider.chooseKeyMaterial(engine.alloc, alias);
         if (keyMaterial != null) {
            engine.setKeyMaterial(keyMaterial);
            return;
         }
      } catch (SSLException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new SSLException(var10);
      } finally {
         if (keyMaterial != null) {
            keyMaterial.release();
         }
      }
   }

   private String chooseClientAlias(ReferenceCountedOpenSslEngine engine, String[] keyTypes, X500Principal[] issuer) {
      X509KeyManager manager = this.provider.keyManager();
      return manager instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)manager).chooseEngineClientAlias(keyTypes, issuer, engine)
         : manager.chooseClientAlias(keyTypes, issuer, null);
   }

   private String chooseServerAlias(ReferenceCountedOpenSslEngine engine, String type) {
      X509KeyManager manager = this.provider.keyManager();
      return manager instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)manager).chooseEngineServerAlias(type, null, engine)
         : manager.chooseServerAlias(type, null, null);
   }

   static {
      KEY_TYPES.put("RSA", "RSA");
      KEY_TYPES.put("DHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_ECDSA", "EC");
      KEY_TYPES.put("ECDH_RSA", "EC_RSA");
      KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
      KEY_TYPES.put("DH_RSA", "DH_RSA");
   }
}
