package com.nimbusds.jose;

import com.nimbusds.jose.util.ArrayUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class JWSAlgorithm extends Algorithm {
   private static final long serialVersionUID = 1L;
   public static final JWSAlgorithm HS256 = new JWSAlgorithm("HS256", Requirement.REQUIRED);
   public static final JWSAlgorithm HS384 = new JWSAlgorithm("HS384", Requirement.OPTIONAL);
   public static final JWSAlgorithm HS512 = new JWSAlgorithm("HS512", Requirement.OPTIONAL);
   public static final JWSAlgorithm RS256 = new JWSAlgorithm("RS256", Requirement.RECOMMENDED);
   public static final JWSAlgorithm RS384 = new JWSAlgorithm("RS384", Requirement.OPTIONAL);
   public static final JWSAlgorithm RS512 = new JWSAlgorithm("RS512", Requirement.OPTIONAL);
   public static final JWSAlgorithm ES256 = new JWSAlgorithm("ES256", Requirement.RECOMMENDED);
   public static final JWSAlgorithm ES256K = new JWSAlgorithm("ES256K", Requirement.OPTIONAL);
   public static final JWSAlgorithm ES384 = new JWSAlgorithm("ES384", Requirement.OPTIONAL);
   public static final JWSAlgorithm ES512 = new JWSAlgorithm("ES512", Requirement.OPTIONAL);
   public static final JWSAlgorithm PS256 = new JWSAlgorithm("PS256", Requirement.OPTIONAL);
   public static final JWSAlgorithm PS384 = new JWSAlgorithm("PS384", Requirement.OPTIONAL);
   public static final JWSAlgorithm PS512 = new JWSAlgorithm("PS512", Requirement.OPTIONAL);
   public static final JWSAlgorithm EdDSA = new JWSAlgorithm("EdDSA", Requirement.OPTIONAL);

   public JWSAlgorithm(String name, Requirement req) {
      super(name, req);
   }

   public JWSAlgorithm(String name) {
      super(name, null);
   }

   public static JWSAlgorithm parse(String s) {
      if (s.equals(HS256.getName())) {
         return HS256;
      } else if (s.equals(HS384.getName())) {
         return HS384;
      } else if (s.equals(HS512.getName())) {
         return HS512;
      } else if (s.equals(RS256.getName())) {
         return RS256;
      } else if (s.equals(RS384.getName())) {
         return RS384;
      } else if (s.equals(RS512.getName())) {
         return RS512;
      } else if (s.equals(ES256.getName())) {
         return ES256;
      } else if (s.equals(ES256K.getName())) {
         return ES256K;
      } else if (s.equals(ES384.getName())) {
         return ES384;
      } else if (s.equals(ES512.getName())) {
         return ES512;
      } else if (s.equals(PS256.getName())) {
         return PS256;
      } else if (s.equals(PS384.getName())) {
         return PS384;
      } else if (s.equals(PS512.getName())) {
         return PS512;
      } else {
         return s.equals(EdDSA.getName()) ? EdDSA : new JWSAlgorithm(s);
      }
   }

   public static final class Family extends AlgorithmFamily<JWSAlgorithm> {
      private static final long serialVersionUID = 1L;
      public static final JWSAlgorithm.Family HMAC_SHA = new JWSAlgorithm.Family(JWSAlgorithm.HS256, JWSAlgorithm.HS384, JWSAlgorithm.HS512);
      public static final JWSAlgorithm.Family RSA = new JWSAlgorithm.Family(
         JWSAlgorithm.RS256, JWSAlgorithm.RS384, JWSAlgorithm.RS512, JWSAlgorithm.PS256, JWSAlgorithm.PS384, JWSAlgorithm.PS512
      );
      public static final JWSAlgorithm.Family EC = new JWSAlgorithm.Family(JWSAlgorithm.ES256, JWSAlgorithm.ES256K, JWSAlgorithm.ES384, JWSAlgorithm.ES512);
      public static final JWSAlgorithm.Family ED = new JWSAlgorithm.Family(JWSAlgorithm.EdDSA);
      public static final JWSAlgorithm.Family SIGNATURE = new JWSAlgorithm.Family(
         ArrayUtils.concat(RSA.toArray(new JWSAlgorithm[0]), EC.toArray(new JWSAlgorithm[0]), ED.toArray(new JWSAlgorithm[0]))
      );

      public Family(JWSAlgorithm... algs) {
         super(algs);
      }
   }
}
