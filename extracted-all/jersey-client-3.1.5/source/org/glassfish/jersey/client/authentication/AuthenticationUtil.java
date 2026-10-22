package org.glassfish.jersey.client.authentication;

import jakarta.ws.rs.client.ClientRequestContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

class AuthenticationUtil {
   static void discardInputAndClose(InputStream is) {
      byte[] buf = new byte[4096];

      try {
         while (is.read(buf) > 0) {
         }
      } catch (IOException var11) {
      } finally {
         try {
            is.close();
         } catch (IOException var10) {
         }
      }
   }

   static URI getCacheKey(ClientRequestContext request) {
      URI requestUri = request.getUri();
      if (requestUri.getRawQuery() != null) {
         try {
            return new URI(requestUri.getScheme(), requestUri.getAuthority(), requestUri.getPath(), null, requestUri.getFragment());
         } catch (URISyntaxException var3) {
         }
      }

      return requestUri;
   }
}
