package com.nimbusds.jose.crypto;

import com.nimbusds.jose.CriticalHeaderParamsAware;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.CriticalHeaderParamsDeferral;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.RSA1_5;
import com.nimbusds.jose.crypto.impl.RSACryptoProvider;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.crypto.impl.RSA_OAEP;
import com.nimbusds.jose.crypto.impl.RSA_OAEP_SHA2;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.PrivateKey;
import java.util.Set;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSADecrypter extends RSACryptoProvider implements JWEDecrypter, CriticalHeaderParamsAware {
   private final CriticalHeaderParamsDeferral critPolicy = new CriticalHeaderParamsDeferral();
   private final PrivateKey privateKey;
   private Exception cekDecryptionException;

   public RSADecrypter(PrivateKey privateKey) {
      this(privateKey, null, false);
   }

   public RSADecrypter(RSAKey rsaJWK) throws JOSEException {
      this(RSAKeyUtils.toRSAPrivateKey(rsaJWK));
   }

   public RSADecrypter(PrivateKey privateKey, Set<String> defCritHeaders) {
      this(privateKey, defCritHeaders, false);
   }

   public RSADecrypter(PrivateKey privateKey, Set<String> defCritHeaders, boolean allowWeakKey) {
      super(null);
      if (!privateKey.getAlgorithm().equalsIgnoreCase("RSA")) {
         throw new IllegalArgumentException("The private key algorithm must be RSA");
      } else {
         if (!allowWeakKey) {
            int keyBitLength = RSAKeyUtils.keyBitLength(privateKey);
            if (keyBitLength > 0 && keyBitLength < 2048) {
               throw new IllegalArgumentException("The RSA key size must be at least 2048 bits");
            }
         }

         this.privateKey = privateKey;
         this.critPolicy.setDeferredCriticalHeaderParams(defCritHeaders);
      }
   }

   public PrivateKey getPrivateKey() {
      return this.privateKey;
   }

   @Override
   public Set<String> getProcessedCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Override
   public Set<String> getDeferredCriticalHeaderParams() {
      return this.critPolicy.getProcessedCriticalHeaderParams();
   }

   @Deprecated
   public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag) throws JOSEException {
      return this.decrypt(header, encryptedKey, iv, cipherText, authTag, AAD.compute(header));
   }

   @Override
   public byte[] decrypt(JWEHeader header, Base64URL encryptedKey, Base64URL iv, Base64URL cipherText, Base64URL authTag, byte[] aad) throws JOSEException {
      if (encryptedKey == null) {
         throw new JOSEException("Missing JWE encrypted key");
      } else if (iv == null) {
         throw new JOSEException("Missing JWE initialization vector (IV)");
      } else if (authTag == null) {
         throw new JOSEException("Missing JWE authentication tag");
      } else {
         this.critPolicy.ensureHeaderPasses(header);
         JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
         SecretKey cek;
         if (alg.equals(JWEAlgorithm.RSA1_5)) {
            int keyLength = header.getEncryptionMethod().cekBitLength();
            SecretKey randomCEK = ContentCryptoProvider.generateCEK(header.getEncryptionMethod(), this.getJCAContext().getSecureRandom());

            try {
               cek = RSA1_5.decryptCEK(this.privateKey, encryptedKey.decode(), keyLength, this.getJCAContext().getKeyEncryptionProvider());
               if (cek == null) {
                  cek = randomCEK;
               }
            } catch (Exception var12) {
               this.cekDecryptionException = var12;
               cek = randomCEK;
            }

            this.cekDecryptionException = null;
         } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
            cek = RSA_OAEP.decryptCEK(this.privateKey, encryptedKey.decode(), this.getJCAContext().getKeyEncryptionProvider());
         } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
            cek = RSA_OAEP_SHA2.decryptCEK(this.privateKey, encryptedKey.decode(), 256, this.getJCAContext().getKeyEncryptionProvider());
         } else if (alg.equals(JWEAlgorithm.RSA_OAEP_384)) {
            cek = RSA_OAEP_SHA2.decryptCEK(this.privateKey, encryptedKey.decode(), 384, this.getJCAContext().getKeyEncryptionProvider());
         } else {
            if (!alg.equals(JWEAlgorithm.RSA_OAEP_512)) {
               throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
            }

            cek = RSA_OAEP_SHA2.decryptCEK(this.privateKey, encryptedKey.decode(), 512, this.getJCAContext().getKeyEncryptionProvider());
         }

         return ContentCryptoProvider.decrypt(header, aad, encryptedKey, iv, cipherText, authTag, cek, this.getJCAContext());
      }
   }

   public Exception getCEKDecryptionException() {
      return this.cekDecryptionException;
   }
}
