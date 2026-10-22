package org.glassfish.jersey.client.oauth2;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

@Priority(1000)
class OAuth2ClientFilter implements ClientRequestFilter {
   private final String accessToken;

   public OAuth2ClientFilter(String accessToken) {
      this.accessToken = accessToken;
   }

   public OAuth2ClientFilter() {
      this.accessToken = null;
   }

   public void filter(ClientRequestContext request) throws IOException {
      String token = this.accessToken;
      String propertyToken = (String)request.getProperty("jersey.config.client.oauth2.access.token");
      if (propertyToken != null) {
         token = propertyToken;
      }

      request.removeProperty("jersey.config.client.oauth2.access.token");
      if (token != null) {
         String authentication = "Bearer " + token;
         if (!request.getHeaders().containsKey("Authorization")) {
            request.getHeaders().add("Authorization", authentication);
         }
      }
   }
}
