package com.cropster.roastingintelligence.remote.impl;

import com.cropster.roastingintelligence.exception.LoginTimeoutException;
import com.cropster.roastingintelligence.exception.TokenRefreshException;
import com.cropster.roastingintelligence.remote.Authenticator;
import com.cropster.roastingintelligence.util.json.ObjectMapperProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.Priority;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.client.oauth2.ClientIdentifier;
import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow;
import org.glassfish.jersey.client.oauth2.TokenResult;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow.Builder;
import org.glassfish.jersey.client.oauth2.OAuth2CodeGrantFlow.Phase;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.message.GZipEncoder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuth2Connection implements Authenticator {
   private static final String CLIENT_ID = "1b425832-2251-45c3-9f14-7b1d96617089";
   private static final String CLIENT_SECRET = "";
   private static final String CODE_VERIFIER_CHARS = "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWXYZ1234567890-._~";
   private static final URI OAUTH2_CALLBACK_BASE_URI;
   private final Logger logger = LoggerFactory.getLogger(OAuth2Connection.class);
   private final ClientIdentifier clientIdentifier = new ClientIdentifier("1b425832-2251-45c3-9f14-7b1d96617089", "");
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
   private final ClientConfig clientConfig;
   private final ResourceConfig serverConfig;
   private URI serverUri;
   private Server server;
   private URI oauth2ServerUri;
   private URI authorizationEndpoint;
   private URI tokenEndpoint;
   private URI jwksUri;
   private JWKSource<SecurityContext> jwkSource;
   private CountDownLatch latch = new CountDownLatch(1);
   private String accessToken;
   private String refreshToken;
   private boolean invalidateOnClose;
   private OAuth2TokenHandler tokenHandler;

   public OAuth2Connection(String refreshToken) {
      this.serverConfig = new ResourceConfig(new Class[]{OAuth2CallbackResource.class});
      this.serverConfig.register(new OAuth2Connection.SingeltonBinder<>(this));
      this.serverConfig.property("jersey.config.server.wadl.disableWadl", true);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      this.clientConfig = new ClientConfig();
      this.clientConfig.property("jersey.config.client.followRedirects", true);
      this.clientConfig.property("jersey.config.client.connectTimeout", 10000);
      this.clientConfig.property("jersey.config.client.readTimeout", 20000);
      this.clientConfig.register(JacksonFeature.class);
      this.clientConfig.register(new ObjectMapperProvider(objectMapper));
      this.clientConfig.register(EncodingFilter.class);
      this.clientConfig.register(GZipEncoder.class);
      this.refreshToken = refreshToken;
   }

   public synchronized URI init(URI serverUri, OAuth2TokenHandler tokenHandler) {
      Objects.requireNonNull(serverUri, "Parameter 'serverUri' must not be null");
      Objects.requireNonNull(tokenHandler, "Parameter 'tokenHandler' must not be null");
      this.serverUri = serverUri;
      this.tokenHandler = tokenHandler;
      return this.init(serverUri);
   }

   private synchronized URI init(URI serverUri) {
      if (this.oauth2ServerUri != null && this.jwkSource != null) {
         return this.oauth2ServerUri;
      } else {
         this.logger.info("Retrieving OpenID configuration from {}", serverUri);
         Client tempClient = ClientBuilder.newClient(this.clientConfig);

         try {
            jakarta.ws.rs.client.Invocation.Builder request = tempClient.target(serverUri).path(".well-known").path("openid-configuration").request();
            OAuth2Connection.OpenIdConfiguration configuration = (OAuth2Connection.OpenIdConfiguration)request.get(OAuth2Connection.OpenIdConfiguration.class);
            this.oauth2ServerUri = configuration.issuer();
            this.logger.info("Using OAuth2 server {}", this.oauth2ServerUri);
            this.authorizationEndpoint = configuration.authorizationEndpoint();
            this.tokenEndpoint = configuration.tokenEndpoint();
            this.jwksUri = configuration.jwksUri();
         } catch (Throwable var7) {
            if (tempClient != null) {
               try {
                  tempClient.close();
               } catch (Throwable var5) {
                  var7.addSuppressed(var5);
               }
            }

            throw var7;
         }

         if (tempClient != null) {
            tempClient.close();
         }

         try {
            this.jwkSource = JWKSourceBuilder.create(this.jwksUri.toURL()).retrying(true).cache(1800000L, 20000L).build();
         } catch (MalformedURLException var6) {
            throw new Error(var6);
         }

         return this.oauth2ServerUri;
      }
   }

   @Override
   public void invalidate() {
      this.invalidateOnClose = true;
   }

   private Server startServer() throws Exception {
      synchronized (this.serverConfig) {
         this.server = JettyHttpContainerFactory.createServer(OAUTH2_CALLBACK_BASE_URI, this.serverConfig);
         this.server.start();
         return this.server;
      }
   }

   private void stopServer() {
      synchronized (this.serverConfig) {
         if (this.server != null) {
            try {
               this.server.stop();
               this.server = null;
            } catch (Exception var4) {
               this.logger.error("Error stopping OAuth2 callback server", var4);
            }
         }
      }
   }

   public URI getUri() {
      return this.oauth2ServerUri;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public String getRefreshToken() {
      return this.refreshToken;
   }

   private Date getExpirationDate() {
      if (this.accessToken == null) {
         return null;
      } else {
         try {
            SignedJWT signedJWT = SignedJWT.parse(this.accessToken);
            return signedJWT.getJWTClaimsSet().getExpirationTime();
         } catch (ParseException var2) {
            throw new IllegalStateException(var2);
         }
      }
   }

   public boolean isExpired() {
      Date expirationDate = this.getExpirationDate();
      return expirationDate == null ? true : expirationDate.before(new Date(System.currentTimeMillis() + 30000L));
   }

   private OAuth2CodeGrantFlow createFlow() {
      String codeVerifier = RandomStringUtils.random(64, "abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMOPQRSTUVWXYZ1234567890-._~");
      MessageDigest sha256 = null;

      try {
         sha256 = MessageDigest.getInstance("SHA-256");
      } catch (NoSuchAlgorithmException var6) {
         throw new IllegalStateException("Error getting hash algorithm", var6);
      }

      sha256.update(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      String codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(sha256.digest());
      Builder<?> builder = OAuth2ClientSupport.authorizationCodeGrantFlowBuilder(
            this.clientIdentifier, this.authorizationEndpoint.toString(), this.tokenEndpoint.toString()
         )
         .redirectUri(OAUTH2_CALLBACK_BASE_URI.resolve("/roastingintelligence/callback").toString())
         .property(Phase.AUTHORIZATION, "code_challenge", codeChallenge)
         .property(Phase.AUTHORIZATION, "code_challenge_method", "S256")
         .property(Phase.ACCESS_TOKEN_REQUEST, "code_verifier", codeVerifier);
      return builder.scope("offline_access").build();
   }

   public synchronized OAuth2CodeGrantFlow initFlow() {
      this.latch.countDown();
      this.latch = new CountDownLatch(1);
      OAuth2CodeGrantFlow flow = this.createFlow();

      try {
         this.server = this.startServer();
      } catch (Exception var3) {
         throw new RuntimeException("Error starting server", var3);
      }

      OAuthState.get().setFlow(flow);
      return flow;
   }

   public void startFlow() {
      this.init(this.serverUri);
      OAuth2CodeGrantFlow flow = this.initFlow();
      String authorizationUri = flow.start();
      RoastingIntelligenceApp.showDocument(authorizationUri);

      try {
         if (!this.latch.await(5L, TimeUnit.MINUTES)) {
            throw new LoginTimeoutException();
         }
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         throw new RuntimeException("Interrupted waiting for login", var4);
      }
   }

   public synchronized void cancel() {
      this.stopServer();
      OAuthState.get().setFlow(null);
   }

   synchronized void finishFlow(String code, String state) {
      OAuth2CodeGrantFlow flow = OAuthState.get().getFlow();
      if (flow == null) {
         throw new IllegalStateException("No OAuth flow in progress");
      } else {
         TokenResult result = flow.finish(code, state);
         flow = OAuthState.get().getFlow();
         if (flow == null) {
            throw new IllegalStateException("OAuth flow cancelled");
         } else {
            this.accessToken = result.getAccessToken();
            this.refreshToken = result.getRefreshToken();
            this.tokenHandler.refreshTokenChanged(this.refreshToken);

            try {
               SignedJWT signedJWT = SignedJWT.parse(this.accessToken);
               JWKSelector selector = new JWKSelector(
                  new com.nimbusds.jose.jwk.JWKMatcher.Builder().keyType(KeyType.RSA).keyUse(KeyUse.SIGNATURE).keyID(signedJWT.getHeader().getKeyID()).build()
               );
               List<JWK> publicKeys = this.jwkSource.get(selector, null);
               if (publicKeys.size() != 1) {
                  throw new IllegalStateException("Unexpected number of public keys: " + publicKeys.size());
               }

               JWSObject jwsObject = JWSObject.parse(result.getAccessToken());
               JWSVerifier verifier = new RSASSAVerifier(publicKeys.get(0).toRSAKey());
               this.logger.info("Token verified: {}", jwsObject.verify(verifier));
               this.logger.info("Token verified - expires: {}", signedJWT.getJWTClaimsSet().getExpirationTime());
            } catch (JOSEException | ParseException var10) {
               throw new IllegalStateException("Error verifying JWT", var10);
            }

            this.executor.schedule(this::stopServer, 10L, TimeUnit.SECONDS);
            this.latch.countDown();
         }
      }
   }

   public synchronized String refreshFlow() {
      this.init(this.serverUri);
      OAuth2CodeGrantFlow flow = OAuthState.get().getFlow();
      if (flow == null) {
         flow = this.createFlow();
         OAuthState.get().setFlow(flow);
      }

      try {
         TokenResult result = flow.refreshAccessToken(this.refreshToken);
         this.accessToken = result.getAccessToken();
         this.refreshToken = result.getRefreshToken();
         this.logger.info("Token refreshed - expires: {}", this.getExpirationDate());
         this.tokenHandler.refreshTokenChanged(this.refreshToken);
         return this.accessToken;
      } catch (ProcessingException var3) {
         throw new TokenRefreshException("Error refreshing token", var3);
      }
   }

   @Override
   public void open(Client client) {
      client.register(new OAuth2Connection.OAuth2ClientFilter());
      if (this.refreshToken != null) {
         try {
            this.refreshFlow();
         } catch (Exception var3) {
            this.close();
            throw var3;
         }
      } else {
         this.startFlow();
      }
   }

   private void revokeToken(String token) {
      Client tempClient = ClientBuilder.newClient(this.clientConfig);

      try {
         Form form = new Form().param("client_id", "1b425832-2251-45c3-9f14-7b1d96617089").param("client_secret", "").param("token", token);
         Response response = tempClient.target(this.oauth2ServerUri)
            .path("oauth2")
            .path("revoke")
            .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})
            .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
         this.logger.info("Token revoked: {} - {}", response.getStatus(), response.getStatusInfo());
      } catch (Throwable var6) {
         if (tempClient != null) {
            try {
               tempClient.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (tempClient != null) {
         tempClient.close();
      }
   }

   @Override
   public void close() {
      if (this.invalidateOnClose) {
         this.revokeToken(this.refreshToken);
      }

      this.accessToken = null;
      this.refreshToken = null;
      OAuthState.get().setFlow(null);
   }

   static {
      try {
         OAUTH2_CALLBACK_BASE_URI = new URI("http://127.0.0.1:31815");
      } catch (URISyntaxException var1) {
         throw new Error("Error initializing URIs", var1);
      }
   }

   @Priority(1000)
   private final class OAuth2ClientFilter implements ClientRequestFilter {
      public void filter(ClientRequestContext request) throws IOException {
         if (!request.getHeaders().containsKey("Authorization")) {
            if (OAuth2Connection.this.isExpired()) {
               OAuth2Connection.this.refreshFlow();
            }

            String authentication = "Bearer " + OAuth2Connection.this.getAccessToken();
            request.getHeaders().add("Authorization", authentication);
         }
      }
   }

   private static record OpenIdConfiguration(
      @JsonProperty("issuer") URI issuer,
      @JsonProperty("authorization_endpoint") URI authorizationEndpoint,
      @JsonProperty("token_endpoint") URI tokenEndpoint,
      @JsonProperty("jwks_uri") URI jwksUri
   ) {
   }

   private final class SingeltonBinder<T> extends AbstractBinder {
      private T singleton;

      private SingeltonBinder(T singleton) {
         this.singleton = singleton;
      }

      protected void configure() {
         this.bind(this.singleton);
      }
   }
}
