package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

class ForceRefreshJWKSetCacheEvaluator extends JWKSetCacheRefreshEvaluator {
   @Override
   public boolean requiresRefresh(JWKSet jwkSet) {
      return true;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof ForceRefreshJWKSetCacheEvaluator;
   }

   @Override
   public int hashCode() {
      return 0;
   }
}
