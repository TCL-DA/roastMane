package org.glassfish.jersey.client.oauth2;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Feature;

public final class OAuth2ClientSupport {
   public static final String OAUTH2_PROPERTY_ACCESS_TOKEN = "jersey.config.client.oauth2.access.token";

   public static Feature feature(String accessToken) {
      return new OAuth2ClientFeature(accessToken);
   }

   public static OAuth2CodeGrantFlow.Builder authorizationCodeGrantFlowBuilder(
      ClientIdentifier clientIdentifier, String authorizationUri, String accessTokenUri
   ) {
      return new AuthCodeGrantImpl.Builder(clientIdentifier, authorizationUri, accessTokenUri);
   }

   public static OAuth2FlowGoogleBuilder googleFlowBuilder(ClientIdentifier clientIdentifier, String redirectURI, String scope) {
      return (OAuth2FlowGoogleBuilder)((OAuth2FlowGoogleBuilder)((OAuth2FlowGoogleBuilder)((OAuth2FlowGoogleBuilder)((OAuth2FlowGoogleBuilder)new OAuth2FlowGoogleBuilder()
                     .authorizationUri("https://accounts.google.com/o/oauth2/auth"))
                  .accessTokenUri("https://accounts.google.com/o/oauth2/token"))
               .redirectUri(redirectURI == null ? "urn:ietf:wg:oauth:2.0:oob" : redirectURI))
            .clientIdentifier(clientIdentifier))
         .scope(scope);
   }

   public static OAuth2CodeGrantFlow.Builder facebookFlowBuilder(ClientIdentifier clientIdentifier, String redirectURI) {
      return OAuth2FlowFacebookBuilder.getFacebookAuthorizationBuilder(clientIdentifier, redirectURI, ClientBuilder.newClient());
   }

   private OAuth2ClientSupport() {
   }
}
