package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class ECDSAProvider extends BaseJWSProvider {
   public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;

   protected ECDSAProvider(JWSAlgorithm alg) throws JOSEException {
      super(Collections.singleton(alg));
      if (!SUPPORTED_ALGORITHMS.contains(alg)) {
         throw new JOSEException("Unsupported EC DSA algorithm: " + alg);
      }
   }

   public JWSAlgorithm supportedECDSAAlgorithm() {
      return this.supportedJWSAlgorithms().iterator().next();
   }

   static {
      Set<JWSAlgorithm> algs = new LinkedHashSet<>();
      algs.add(JWSAlgorithm.ES256);
      algs.add(JWSAlgorithm.ES256K);
      algs.add(JWSAlgorithm.ES384);
      algs.add(JWSAlgorithm.ES512);
      SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
   }
}
