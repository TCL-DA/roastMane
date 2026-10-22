package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class MACProvider extends BaseJWSProvider {
   public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
   private final byte[] secret;
   private final SecretKey secretKey;

   protected static String getJCAAlgorithmName(JWSAlgorithm alg) throws JOSEException {
      if (alg.equals(JWSAlgorithm.HS256)) {
         return "HMACSHA256";
      } else if (alg.equals(JWSAlgorithm.HS384)) {
         return "HMACSHA384";
      } else if (alg.equals(JWSAlgorithm.HS512)) {
         return "HMACSHA512";
      } else {
         throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, SUPPORTED_ALGORITHMS));
      }
   }

   protected MACProvider(byte[] secret, Set<JWSAlgorithm> supportedAlgs) throws KeyLengthException {
      super(supportedAlgs);
      if (secret.length < 32) {
         throw new KeyLengthException("The secret length must be at least 256 bits");
      } else {
         this.secret = secret;
         this.secretKey = null;
      }
   }

   protected MACProvider(SecretKey secretKey, Set<JWSAlgorithm> supportedAlgs) throws KeyLengthException {
      super(supportedAlgs);
      if (secretKey.getEncoded() != null && secretKey.getEncoded().length < 32) {
         throw new KeyLengthException("The secret length must be at least 256 bits");
      } else {
         this.secretKey = secretKey;
         this.secret = null;
      }
   }

   public SecretKey getSecretKey() {
      if (this.secretKey != null) {
         return this.secretKey;
      } else if (this.secret != null) {
         return new SecretKeySpec(this.secret, "MAC");
      } else {
         throw new IllegalStateException("Unexpected state");
      }
   }

   public byte[] getSecret() {
      if (this.secretKey != null) {
         return this.secretKey.getEncoded();
      } else if (this.secret != null) {
         return this.secret;
      } else {
         throw new IllegalStateException("Unexpected state");
      }
   }

   public String getSecretString() {
      byte[] secret = this.getSecret();
      return secret == null ? null : new String(secret, StandardCharset.UTF_8);
   }

   static {
      Set<JWSAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWSAlgorithm.HS256);
      algs.add(JWSAlgorithm.HS384);
      algs.add(JWSAlgorithm.HS512);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
