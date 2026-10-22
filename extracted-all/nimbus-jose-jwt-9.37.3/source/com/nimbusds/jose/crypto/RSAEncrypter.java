package com.nimbusds.jose.crypto;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWECryptoParts;
import com.nimbusds.jose.JWEEncrypter;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.impl.AAD;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.ContentCryptoProvider;
import com.nimbusds.jose.crypto.impl.JWEHeaderValidation;
import com.nimbusds.jose.crypto.impl.RSA1_5;
import com.nimbusds.jose.crypto.impl.RSACryptoProvider;
import com.nimbusds.jose.crypto.impl.RSA_OAEP;
import com.nimbusds.jose.crypto.impl.RSA_OAEP_SHA2;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.util.Base64URL;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.SecretKey;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSAEncrypter extends RSACryptoProvider implements JWEEncrypter {
   private final RSAPublicKey publicKey;

   public RSAEncrypter(RSAPublicKey publicKey) {
      this(publicKey, null);
   }

   public RSAEncrypter(RSAKey rsaJWK) throws JOSEException {
      this(rsaJWK.toRSAPublicKey());
   }

   public RSAEncrypter(RSAPublicKey publicKey, SecretKey contentEncryptionKey) {
      super(contentEncryptionKey);
      if (publicKey == null) {
         throw new IllegalArgumentException("The public RSA key must not be null");
      } else {
         this.publicKey = publicKey;
      }
   }

   public RSAPublicKey getPublicKey() {
      return this.publicKey;
   }

   @Deprecated
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText) throws JOSEException {
      return this.encrypt(header, clearText, AAD.compute(header));
   }

   @Override
   public JWECryptoParts encrypt(JWEHeader header, byte[] clearText, byte[] aad) throws JOSEException {
      JWEAlgorithm alg = JWEHeaderValidation.getAlgorithmAndEnsureNotNull(header);
      EncryptionMethod enc = header.getEncryptionMethod();
      SecretKey cek = this.getCEK(enc);
      Base64URL encryptedKey;
      if (alg.equals(JWEAlgorithm.RSA1_5)) {
         encryptedKey = Base64URL.encode(RSA1_5.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP)) {
         encryptedKey = Base64URL.encode(RSA_OAEP.encryptCEK(this.publicKey, cek, this.getJCAContext().getKeyEncryptionProvider()));
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP_256)) {
         encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 256, this.getJCAContext().getKeyEncryptionProvider()));
      } else if (alg.equals(JWEAlgorithm.RSA_OAEP_384)) {
         encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 384, this.getJCAContext().getKeyEncryptionProvider()));
      } else {
         if (!alg.equals(JWEAlgorithm.RSA_OAEP_512)) {
            throw new JOSEException(AlgorithmSupportMessage.unsupportedJWEAlgorithm(alg, SUPPORTED_ALGORITHMS));
         }

         encryptedKey = Base64URL.encode(RSA_OAEP_SHA2.encryptCEK(this.publicKey, cek, 512, this.getJCAContext().getKeyEncryptionProvider()));
      }

      return ContentCryptoProvider.encrypt(header, clearText, aad, cek, encryptedKey, this.getJCAContext());
   }
}
