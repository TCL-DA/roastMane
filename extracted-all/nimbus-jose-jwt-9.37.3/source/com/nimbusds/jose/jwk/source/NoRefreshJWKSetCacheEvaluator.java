package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;

class NoRefreshJWKSetCacheEvaluator extends JWKSetCacheRefreshEvaluator {
   @Override
   public boolean requiresRefresh(JWKSet jwkSet) {
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof NoRefreshJWKSetCacheEvaluator;
   }

   @Override
   public int hashCode() {
      return 0;
   }
}
