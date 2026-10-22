package org.glassfish.jersey.client.oauth2;

public final class OAuth2Parameters {
   public static final String CLIENT_ID = "client_id";
   public static final String CLIENT_SECRET = "client_secret";
   public static final String RESPONSE_TYPE = "response_type";
   public static final String REDIRECT_URI = "redirect_uri";
   public static final String SCOPE = "scope";
   public static final String STATE = "state";
   public static final String REFRESH_TOKEN = "refresh_token";
   public static final String CODE = "code";
   public static final String REDIRECT_URI_UNDEFINED = "urn:ietf:wg:oauth:2.0:oob";

   private OAuth2Parameters() {
   }

   public static enum GrantType {
      AUTHORIZATION_CODE,
      REFRESH_TOKEN,
      PASSWORD,
      CLIENT_CREDENTIALS;

      public static final String key = "grant_type";
   }
}
