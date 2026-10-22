package org.glassfish.jersey.client.authentication;

import jakarta.annotation.Priority;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.internal.LocalizationMessages;

@Priority(1000)
class HttpAuthenticationFilter implements ClientRequestFilter, ClientResponseFilter {
   private static final String REQUEST_PROPERTY_FILTER_REUSED = "org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused";
   private static final String REQUEST_PROPERTY_OPERATION = "org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation";
   static final Charset CHARACTER_SET = Charset.forName("iso-8859-1");
   private final HttpAuthenticationFeature.Mode mode;
   private final Map<String, HttpAuthenticationFilter.Type> uriCache;
   private final DigestAuthenticator digestAuth;
   private final BasicAuthenticator basicAuth;
   private static final int MAXIMUM_DIGEST_CACHE_SIZE = 10000;

   HttpAuthenticationFilter(
      HttpAuthenticationFeature.Mode mode,
      HttpAuthenticationFilter.Credentials basicCredentials,
      HttpAuthenticationFilter.Credentials digestCredentials,
      Configuration configuration
   ) {
      int limit = this.getMaximumCacheLimit(configuration);
      final int uriCacheLimit = limit * 2;
      this.uriCache = Collections.synchronizedMap(new LinkedHashMap<String, HttpAuthenticationFilter.Type>(uriCacheLimit) {
         private static final long serialVersionUID = 1946245645415625L;

         @Override
         protected boolean removeEldestEntry(Entry<String, HttpAuthenticationFilter.Type> eldest) {
            return this.size() > uriCacheLimit;
         }
      });
      this.mode = mode;
      switch (mode) {
         case BASIC_PREEMPTIVE:
         case BASIC_NON_PREEMPTIVE:
            this.basicAuth = new BasicAuthenticator(basicCredentials);
            this.digestAuth = null;
            break;
         case DIGEST:
            this.basicAuth = null;
            this.digestAuth = new DigestAuthenticator(digestCredentials, limit);
            break;
         case UNIVERSAL:
            this.basicAuth = new BasicAuthenticator(basicCredentials);
            this.digestAuth = new DigestAuthenticator(digestCredentials, limit);
            break;
         default:
            throw new IllegalStateException("Not implemented.");
      }
   }

   private int getMaximumCacheLimit(Configuration configuration) {
      int limit = ClientProperties.getValue(configuration.getProperties(), "jersey.config.client.digestAuthUriCacheSizeLimit", 10000);
      if (limit < 1) {
         limit = 10000;
      }

      return limit;
   }

   public void filter(ClientRequestContext request) throws IOException {
      if (!"true".equals(request.getProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused"))) {
         if (!request.getHeaders().containsKey("Authorization")) {
            HttpAuthenticationFilter.Type operation = null;
            if (this.mode == HttpAuthenticationFeature.Mode.BASIC_PREEMPTIVE) {
               this.basicAuth.filterRequest(request);
               operation = HttpAuthenticationFilter.Type.BASIC;
            } else if (this.mode != HttpAuthenticationFeature.Mode.BASIC_NON_PREEMPTIVE) {
               if (this.mode == HttpAuthenticationFeature.Mode.DIGEST) {
                  if (this.digestAuth.filterRequest(request)) {
                     operation = HttpAuthenticationFilter.Type.DIGEST;
                  }
               } else if (this.mode == HttpAuthenticationFeature.Mode.UNIVERSAL) {
                  HttpAuthenticationFilter.Type lastSuccessfulMethod = this.uriCache.get(this.getCacheKey(request));
                  if (lastSuccessfulMethod != null) {
                     request.setProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation", lastSuccessfulMethod);
                     if (lastSuccessfulMethod == HttpAuthenticationFilter.Type.BASIC) {
                        this.basicAuth.filterRequest(request);
                        operation = HttpAuthenticationFilter.Type.BASIC;
                     } else if (lastSuccessfulMethod == HttpAuthenticationFilter.Type.DIGEST && this.digestAuth.filterRequest(request)) {
                        operation = HttpAuthenticationFilter.Type.DIGEST;
                     }
                  }
               }
            }

            if (operation != null) {
               request.setProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation", operation);
            }
         }
      }
   }

   public void filter(ClientRequestContext request, ClientResponseContext response) throws IOException {
      if (!"true".equals(request.getProperty("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused"))) {
         HttpAuthenticationFilter.Type result = null;
         boolean authenticate;
         if (response.getStatus() == Status.UNAUTHORIZED.getStatusCode()) {
            List<String> authStrings = (List<String>)response.getHeaders().get("WWW-Authenticate");
            if (authStrings != null) {
               for (String authString : authStrings) {
                  String upperCaseAuth = authString.trim().toUpperCase(Locale.ROOT);
                  if (result == null && upperCaseAuth.startsWith("BASIC")) {
                     result = HttpAuthenticationFilter.Type.BASIC;
                  } else if (upperCaseAuth.startsWith("DIGEST")) {
                     result = HttpAuthenticationFilter.Type.DIGEST;
                  }
               }

               if (result == null) {
                  return;
               }
            }

            authenticate = true;
         } else {
            authenticate = false;
         }

         if (this.mode != HttpAuthenticationFeature.Mode.BASIC_PREEMPTIVE) {
            if (this.mode == HttpAuthenticationFeature.Mode.BASIC_NON_PREEMPTIVE) {
               if (authenticate && result == HttpAuthenticationFilter.Type.BASIC) {
                  this.basicAuth.filterResponseAndAuthenticate(request, response);
               }
            } else if (this.mode == HttpAuthenticationFeature.Mode.DIGEST) {
               if (authenticate && result == HttpAuthenticationFilter.Type.DIGEST) {
                  this.digestAuth.filterResponse(request, response);
               }
            } else if (this.mode == HttpAuthenticationFeature.Mode.UNIVERSAL) {
               HttpAuthenticationFilter.Type operation = (HttpAuthenticationFilter.Type)request.getProperty(
                  "org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.operation"
               );
               if (operation != null) {
                  this.updateCache(request, !authenticate, operation);
               }

               if (authenticate) {
                  boolean success = false;
                  if (result == HttpAuthenticationFilter.Type.BASIC) {
                     success = this.basicAuth.filterResponseAndAuthenticate(request, response);
                  } else if (result == HttpAuthenticationFilter.Type.DIGEST) {
                     success = this.digestAuth.filterResponse(request, response);
                  }

                  this.updateCache(request, success, result);
               }
            }
         }
      }
   }

   private String getCacheKey(ClientRequestContext request) {
      return AuthenticationUtil.getCacheKey(request).toString() + ":" + request.getMethod();
   }

   private void updateCache(ClientRequestContext request, boolean success, HttpAuthenticationFilter.Type operation) {
      String cacheKey = this.getCacheKey(request);
      if (success) {
         this.uriCache.put(cacheKey, operation);
      } else {
         this.uriCache.remove(cacheKey);
      }
   }

   static boolean repeatRequest(ClientRequestContext request, ClientResponseContext response, String newAuthorizationHeader) {
      if (response.hasEntity()) {
         AuthenticationUtil.discardInputAndClose(response.getEntityStream());
         response.setEntityStream(null);
      }

      Client client = request.getClient();
      String method = request.getMethod();
      MediaType mediaType = request.getMediaType();
      URI lUri = request.getUri();
      WebTarget resourceTarget = client.target(lUri);
      Builder builder = resourceTarget.request(new MediaType[]{mediaType});
      MultivaluedMap<String, Object> newHeaders = new MultivaluedHashMap();

      for (Entry<String, List<Object>> entry : request.getHeaders().entrySet()) {
         if (!"Authorization".equals(entry.getKey())) {
            newHeaders.put(entry.getKey(), entry.getValue());
         }
      }

      newHeaders.add("Authorization", newAuthorizationHeader);
      builder.headers(newHeaders);
      builder.property("org.glassfish.jersey.client.authentication.HttpAuthenticationFilter.reused", "true");

      for (String propertyName : request.getPropertyNames()) {
         Object propertyValue = request.getProperty(propertyName);
         builder.property(propertyName, propertyValue);
      }

      Invocation invocation;
      if (request.getEntity() == null) {
         invocation = builder.build(method);
      } else {
         invocation = builder.build(method, Entity.entity(request.getEntity(), request.getMediaType()));
      }

      Response nextResponse = invocation.invoke();
      if (nextResponse.hasEntity()) {
         response.setEntityStream((InputStream)nextResponse.readEntity(InputStream.class));
      }

      MultivaluedMap<String, String> headers = response.getHeaders();
      headers.clear();
      headers.putAll(nextResponse.getStringHeaders());
      response.setStatus(nextResponse.getStatus());
      return response.getStatus() != Status.UNAUTHORIZED.getStatusCode();
   }

   private static HttpAuthenticationFilter.Credentials extractCredentials(ClientRequestContext request, HttpAuthenticationFilter.Type type) {
      String usernameKey = null;
      String passwordKey = null;
      if (type == null) {
         usernameKey = "jersey.config.client.http.auth.username";
         passwordKey = "jersey.config.client.http.auth.password";
      } else if (type == HttpAuthenticationFilter.Type.BASIC) {
         usernameKey = "jersey.config.client.http.auth.basic.username";
         passwordKey = "jersey.config.client.http.auth.basic.password";
      } else if (type == HttpAuthenticationFilter.Type.DIGEST) {
         usernameKey = "jersey.config.client.http.auth.digest.username";
         passwordKey = "jersey.config.client.http.auth.digest.password";
      }

      String userName = (String)request.getProperty(usernameKey);
      if (userName != null && !userName.equals("")) {
         Object password = request.getProperty(passwordKey);
         byte[] pwdBytes;
         if (password instanceof byte[]) {
            pwdBytes = (byte[])password;
         } else {
            if (!(password instanceof String)) {
               throw new RequestAuthenticationException(LocalizationMessages.AUTHENTICATION_CREDENTIALS_REQUEST_PASSWORD_UNSUPPORTED());
            }

            pwdBytes = ((String)password).getBytes(CHARACTER_SET);
         }

         return new HttpAuthenticationFilter.Credentials(userName, pwdBytes);
      } else {
         return null;
      }
   }

   static HttpAuthenticationFilter.Credentials getCredentials(
      ClientRequestContext request, HttpAuthenticationFilter.Credentials defaultCredentials, HttpAuthenticationFilter.Type type
   ) {
      HttpAuthenticationFilter.Credentials commonCredentials = extractCredentials(request, type);
      if (commonCredentials != null) {
         return commonCredentials;
      } else {
         HttpAuthenticationFilter.Credentials specificCredentials = extractCredentials(request, null);
         return specificCredentials != null ? specificCredentials : defaultCredentials;
      }
   }

   static class Credentials {
      private final String username;
      private final byte[] password;

      Credentials(String username, byte[] password) {
         this.username = username;
         this.password = password;
      }

      Credentials(String username, String password) {
         this.username = username;
         this.password = password == null ? null : password.getBytes(HttpAuthenticationFilter.CHARACTER_SET);
      }

      String getUsername() {
         return this.username;
      }

      byte[] getPassword() {
         return this.password;
      }
   }

   static enum Type {
      BASIC,
      DIGEST;
   }
}
