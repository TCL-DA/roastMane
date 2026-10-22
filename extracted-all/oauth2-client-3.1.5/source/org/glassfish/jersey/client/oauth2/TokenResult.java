package org.glassfish.jersey.client.oauth2;

import java.util.Collection;
import java.util.Map;

public class TokenResult {
   private final Map<String, Object> properties;

   public TokenResult(Map<String, Object> properties) {
      this.properties = properties;
   }

   public String getAccessToken() {
      return this.getProperty("access_token");
   }

   public Long getExpiresIn() {
      String expiration = this.getProperty("expires_in");
      return expiration == null ? null : Long.valueOf(expiration);
   }

   public String getRefreshToken() {
      return this.getProperty("refresh_token");
   }

   public String getTokenType() {
      return this.getProperty("token_type");
   }

   public Map<String, Object> getAllProperties() {
      return this.properties;
   }

   private String getProperty(String name) {
      Object property = this.properties.get(name);
      if (property != null) {
         if (!(property instanceof Collection)) {
            return property.toString();
         }

         for (Object value : (Collection)property) {
            if (value != null) {
               return value.toString();
            }
         }
      }

      return null;
   }
}
