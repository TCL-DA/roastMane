package org.glassfish.jersey.client.oauth2;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

class OAuth2ClientFeature implements Feature {
   private final OAuth2ClientFilter filter;

   public OAuth2ClientFeature(String accessToken) {
      this.filter = new OAuth2ClientFilter(accessToken);
   }

   public OAuth2ClientFeature() {
      this.filter = new OAuth2ClientFilter();
   }

   OAuth2ClientFeature(OAuth2ClientFilter filter) {
      this.filter = filter;
   }

   public boolean configure(FeatureContext context) {
      context.register(this.filter);
      return true;
   }
}
