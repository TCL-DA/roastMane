package org.glassfish.jersey.client.oauth2;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.ReaderInterceptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.glassfish.jersey.client.oauth2.internal.LocalizationMessages;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.MessageBodyWorkers;

class AuthCodeGrantImpl implements OAuth2CodeGrantFlow {
   private final String accessTokenUri;
   private final String authorizationUri;
   private final String refreshTokenUri;
   private final ClientIdentifier clientIdentifier;
   private final Client client;
   private final Map<String, String> authorizationProperties;
   private final Map<String, String> accessTokenProperties;
   private final Map<String, String> refreshTokenProperties;
   private volatile TokenResult tokenResult;

   private AuthCodeGrantImpl(
      String authorizationUri,
      String accessTokenUri,
      String redirectUri,
      String refreshTokenUri,
      ClientIdentifier clientIdentifier,
      String scope,
      Client client,
      Map<String, String> authorizationProperties,
      Map<String, String> accessTokenProperties,
      Map<String, String> refreshTokenProperties
   ) {
      this.accessTokenUri = accessTokenUri;
      this.authorizationUri = authorizationUri;
      this.authorizationProperties = authorizationProperties;
      this.accessTokenProperties = accessTokenProperties;
      this.refreshTokenProperties = refreshTokenProperties;
      if (refreshTokenUri != null) {
         this.refreshTokenUri = refreshTokenUri;
      } else {
         this.refreshTokenUri = accessTokenUri;
      }

      this.clientIdentifier = clientIdentifier;
      this.client = this.configureClient(client);
      this.initDefaultProperties(redirectUri, scope);
   }

   private Client configureClient(Client client) {
      if (client == null) {
         client = ClientBuilder.newClient();
      }

      Configuration config = client.getConfiguration();
      if (!config.isRegistered(AuthCodeGrantImpl.DefaultTokenMessageBodyReader.class)) {
         client.register(AuthCodeGrantImpl.DefaultTokenMessageBodyReader.class);
      }

      return client;
   }

   private void setDefaultProperty(String key, String value, Map<String, String>... properties) {
      if (value != null) {
         for (Map<String, String> props : properties) {
            if (props.get(key) == null) {
               props.put(key, value);
            }
         }
      }
   }

   private void initDefaultProperties(String redirectUri, String scope) {
      this.setDefaultProperty("response_type", "code", this.authorizationProperties);
      this.setDefaultProperty(
         "client_id", this.clientIdentifier.getClientId(), this.authorizationProperties, this.accessTokenProperties, this.refreshTokenProperties
      );
      this.setDefaultProperty(
         "redirect_uri", redirectUri == null ? "urn:ietf:wg:oauth:2.0:oob" : redirectUri, this.authorizationProperties, this.accessTokenProperties
      );
      this.setDefaultProperty("state", UUID.randomUUID().toString(), this.authorizationProperties);
      this.setDefaultProperty("scope", scope, this.authorizationProperties);
      this.setDefaultProperty("client_secret", this.clientIdentifier.getClientSecret(), this.accessTokenProperties, this.refreshTokenProperties);
      this.setDefaultProperty("grant_type", OAuth2Parameters.GrantType.AUTHORIZATION_CODE.name().toLowerCase(Locale.ROOT), this.accessTokenProperties);
      this.setDefaultProperty("grant_type", OAuth2Parameters.GrantType.REFRESH_TOKEN.name().toLowerCase(Locale.ROOT), this.refreshTokenProperties);
   }

   @Override
   public String start() {
      UriBuilder uriBuilder = UriBuilder.fromUri(this.authorizationUri);

      for (Entry<String, String> entry : this.authorizationProperties.entrySet()) {
         uriBuilder.queryParam(entry.getKey(), new Object[]{entry.getValue()});
      }

      return uriBuilder.build(new Object[0]).toString();
   }

   @Override
   public TokenResult finish(String authorizationCode, String state) {
      if (!this.authorizationProperties.get("state").equals(state)) {
         throw new IllegalArgumentException(LocalizationMessages.ERROR_FLOW_WRONG_STATE());
      } else {
         this.accessTokenProperties.put("code", authorizationCode);
         Form form = new Form();

         for (Entry<String, String> entry : this.accessTokenProperties.entrySet()) {
            form.param(entry.getKey(), entry.getValue());
         }

         Response response = this.client
            .target(this.accessTokenUri)
            .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
         if (response.getStatus() != 200) {
            throw new ProcessingException(LocalizationMessages.ERROR_FLOW_REQUEST_ACCESS_TOKEN(response.getStatus()));
         } else {
            this.tokenResult = (TokenResult)response.readEntity(TokenResult.class);
            return this.tokenResult;
         }
      }
   }

   @Override
   public TokenResult refreshAccessToken(String refreshToken) {
      this.refreshTokenProperties.put("refresh_token", refreshToken);
      Form form = new Form();

      for (Entry<String, String> entry : this.refreshTokenProperties.entrySet()) {
         form.param(entry.getKey(), entry.getValue());
      }

      Response response = this.client
         .target(this.refreshTokenUri)
         .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
         .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
      if (response.getStatus() != 200) {
         throw new ProcessingException(LocalizationMessages.ERROR_FLOW_REQUEST_REFRESH_TOKEN(response.getStatus()));
      } else {
         this.tokenResult = (TokenResult)response.readEntity(TokenResult.class);
         return this.tokenResult;
      }
   }

   @Override
   public Client getAuthorizedClient() {
      return (Client)ClientBuilder.newClient().register(this.getOAuth2Feature());
   }

   @Override
   public Feature getOAuth2Feature() {
      if (this.tokenResult == null) {
         throw new IllegalStateException(LocalizationMessages.ERROR_FLOW_NOT_FINISHED());
      } else {
         return new OAuth2ClientFeature(this.tokenResult.getAccessToken());
      }
   }

   static class Builder implements OAuth2CodeGrantFlow.Builder {
      private String accessTokenUri;
      private String refreshTokenUri;
      private String authorizationUri;
      private String callbackUri;
      private ClientIdentifier clientIdentifier;
      private Client client;
      private String scope;
      private Map<String, String> authorizationProperties = new HashMap<>();
      private Map<String, String> accessTokenProperties = new HashMap<>();
      private Map<String, String> refreshTokenProperties = new HashMap<>();

      public Builder() {
      }

      public Builder(ClientIdentifier clientIdentifier, String authorizationUri, String accessTokenUri) {
         this();
         this.accessTokenUri = accessTokenUri;
         this.authorizationUri = authorizationUri;
         this.clientIdentifier = clientIdentifier;
      }

      public Builder(ClientIdentifier clientIdentifier, String authorizationUri, String accessTokenUri, String callbackUri) {
         this();
         this.accessTokenUri = accessTokenUri;
         this.authorizationUri = authorizationUri;
         this.callbackUri = callbackUri;
         this.clientIdentifier = clientIdentifier;
      }

      public AuthCodeGrantImpl.Builder accessTokenUri(String accessTokenUri) {
         this.accessTokenUri = accessTokenUri;
         return this;
      }

      public AuthCodeGrantImpl.Builder authorizationUri(String authorizationUri) {
         this.authorizationUri = authorizationUri;
         return this;
      }

      public AuthCodeGrantImpl.Builder redirectUri(String redirectUri) {
         this.callbackUri = redirectUri;
         return this;
      }

      public AuthCodeGrantImpl.Builder clientIdentifier(ClientIdentifier clientIdentifier) {
         this.clientIdentifier = clientIdentifier;
         return this;
      }

      public AuthCodeGrantImpl.Builder scope(String scope) {
         this.scope = scope;
         return this;
      }

      public AuthCodeGrantImpl.Builder client(Client client) {
         this.client = client;
         return this;
      }

      public AuthCodeGrantImpl.Builder refreshTokenUri(String refreshTokenUri) {
         this.refreshTokenUri = refreshTokenUri;
         return this;
      }

      public AuthCodeGrantImpl.Builder property(OAuth2CodeGrantFlow.Phase phase, String key, String value) {
         phase.property(key, value, this.authorizationProperties, this.accessTokenProperties, this.refreshTokenProperties);
         return this;
      }

      String getAccessTokenUri() {
         return this.accessTokenUri;
      }

      String getRefreshTokenUri() {
         return this.refreshTokenUri;
      }

      String getAuthorizationUri() {
         return this.authorizationUri;
      }

      String getScope() {
         return this.scope;
      }

      String getCallbackUri() {
         return this.callbackUri;
      }

      ClientIdentifier getClientIdentifier() {
         return this.clientIdentifier;
      }

      Client getClient() {
         return this.client;
      }

      Map<String, String> getAuthorizationProperties() {
         return this.authorizationProperties;
      }

      Map<String, String> getAccessTokenProperties() {
         return this.accessTokenProperties;
      }

      Map<String, String> getRefreshTokenProperties() {
         return this.refreshTokenProperties;
      }

      public AuthCodeGrantImpl build() {
         return new AuthCodeGrantImpl(
            this.authorizationUri,
            this.accessTokenUri,
            this.callbackUri,
            this.refreshTokenUri,
            this.clientIdentifier,
            this.scope,
            this.client,
            this.authorizationProperties,
            this.accessTokenProperties,
            this.refreshTokenProperties
         );
      }
   }

   static class DefaultTokenMessageBodyReader implements MessageBodyReader<TokenResult> {
      private final Provider<MessageBodyWorkers> workers;
      private final Provider<PropertiesDelegate> propertiesDelegateProvider;
      private static Iterable<ReaderInterceptor> EMPTY_INTERCEPTORS = new ArrayList<>();

      @Inject
      public DefaultTokenMessageBodyReader(@Context Provider<MessageBodyWorkers> workers, @Context Provider<PropertiesDelegate> propertiesDelegateProvider) {
         this.propertiesDelegateProvider = propertiesDelegateProvider;
         this.workers = workers;
      }

      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
         return type.equals(TokenResult.class);
      }

      public TokenResult readFrom(
         Class<TokenResult> type,
         Type genericType,
         Annotation[] annotations,
         MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders,
         InputStream entityStream
      ) throws IOException, WebApplicationException {
         GenericType<Map<String, Object>> mapType = new GenericType<Map<String, Object>>() {
         };
         Map<String, Object> map = (Map<String, Object>)((MessageBodyWorkers)this.workers.get())
            .readFrom(
               mapType.getRawType(),
               mapType.getType(),
               annotations,
               mediaType,
               httpHeaders,
               (PropertiesDelegate)this.propertiesDelegateProvider.get(),
               entityStream,
               EMPTY_INTERCEPTORS,
               false
            );
         return new TokenResult(map);
      }
   }
}
