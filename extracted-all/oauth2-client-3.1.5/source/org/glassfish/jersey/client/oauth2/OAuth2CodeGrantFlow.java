package org.glassfish.jersey.client.oauth2;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.Feature;
import java.util.Map;

public interface OAuth2CodeGrantFlow {
   String start();

   TokenResult finish(String var1, String var2);

   TokenResult refreshAccessToken(String var1);

   Client getAuthorizedClient();

   Feature getOAuth2Feature();

   public interface Builder<T extends OAuth2CodeGrantFlow.Builder> {
      T accessTokenUri(String var1);

      T authorizationUri(String var1);

      T redirectUri(String var1);

      T clientIdentifier(ClientIdentifier var1);

      T scope(String var1);

      T client(Client var1);

      T refreshTokenUri(String var1);

      T property(OAuth2CodeGrantFlow.Phase var1, String var2, String var3);

      OAuth2CodeGrantFlow build();
   }

   public static enum Phase {
      AUTHORIZATION {
         @Override
         public void property(
            String key, String value, Map<String, String> authorizationProps, Map<String, String> accessTokenProps, Map<String, String> refreshTokenProps
         ) {
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, authorizationProps);
         }
      },
      ACCESS_TOKEN_REQUEST {
         @Override
         public void property(
            String key, String value, Map<String, String> authorizationProps, Map<String, String> accessTokenProps, Map<String, String> refreshTokenProps
         ) {
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, accessTokenProps);
         }
      },
      REFRESH_ACCESS_TOKEN {
         @Override
         public void property(
            String key, String value, Map<String, String> authorizationProps, Map<String, String> accessTokenProps, Map<String, String> refreshTokenProps
         ) {
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, refreshTokenProps);
         }
      },
      ALL {
         @Override
         public void property(
            String key, String value, Map<String, String> authorizationProps, Map<String, String> accessTokenProps, Map<String, String> refreshTokenProps
         ) {
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, authorizationProps);
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, accessTokenProps);
            OAuth2CodeGrantFlow.Phase.nonNullProperty(key, value, refreshTokenProps);
         }
      };

      private Phase() {
      }

      public abstract void property(String var1, String var2, Map<String, String> var3, Map<String, String> var4, Map<String, String> var5);

      private static void nonNullProperty(String key, String value, Map<String, String> props) {
         if (value == null) {
            props.remove(key);
         } else {
            props.put(key, value);
         }
      }
   }
}
