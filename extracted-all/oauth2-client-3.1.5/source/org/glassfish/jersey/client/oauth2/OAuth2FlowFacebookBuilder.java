package org.glassfish.jersey.client.oauth2;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.glassfish.jersey.message.internal.ReaderWriter;

class OAuth2FlowFacebookBuilder {
   public static OAuth2CodeGrantFlow.Builder getFacebookAuthorizationBuilder(ClientIdentifier clientIdentifier, String redirectUri, Client client) {
      AuthCodeGrantImpl.Builder builder = new AuthCodeGrantImpl.Builder();
      builder.accessTokenUri("https://graph.facebook.com/oauth/access_token");
      builder.authorizationUri("https://www.facebook.com/dialog/oauth");
      builder.redirectUri(redirectUri);
      builder.clientIdentifier(clientIdentifier);
      client.register(OAuth2FlowFacebookBuilder.FacebookTokenMessageBodyReader.class);
      builder.client(client);
      return builder;
   }

   @Consumes({"text/plain"})
   static class FacebookTokenMessageBodyReader implements MessageBodyReader<TokenResult> {
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
         Map<String, Object> map = new HashMap<>();
         String str = ReaderWriter.readFromAsString(entityStream, mediaType);
         String[] splitArray = str.split("&");

         for (String s : splitArray) {
            String[] keyValue = s.split("=");
            map.put(keyValue[0], keyValue[1]);
         }

         return new TokenResult(map);
      }
   }
}
