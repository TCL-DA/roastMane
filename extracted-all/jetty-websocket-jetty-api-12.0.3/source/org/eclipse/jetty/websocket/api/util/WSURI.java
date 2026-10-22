package org.eclipse.jetty.websocket.api.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public final class WSURI {
   public static URI toHttp(URI inputUri) throws URISyntaxException {
      Objects.requireNonNull(inputUri, "Input URI must not be null");
      String wsScheme = inputUri.getScheme();
      if ("http".equalsIgnoreCase(wsScheme) || "https".equalsIgnoreCase(wsScheme)) {
         return inputUri;
      } else if ("ws".equalsIgnoreCase(wsScheme)) {
         return new URI("http" + inputUri.toString().substring(wsScheme.length()));
      } else if ("wss".equalsIgnoreCase(wsScheme)) {
         return new URI("https" + inputUri.toString().substring(wsScheme.length()));
      } else {
         throw new URISyntaxException(inputUri.toString(), "Unrecognized WebSocket scheme");
      }
   }

   public static URI toWebsocket(CharSequence inputUrl) throws URISyntaxException {
      return toWebsocket(new URI(inputUrl.toString()));
   }

   public static URI toWebsocket(CharSequence inputUrl, String query) throws URISyntaxException {
      return query == null ? toWebsocket(new URI(inputUrl.toString())) : toWebsocket(new URI(inputUrl.toString() + "?" + query));
   }

   public static URI toWebsocket(URI inputUri) throws URISyntaxException {
      Objects.requireNonNull(inputUri, "Input URI must not be null");
      String httpScheme = inputUri.getScheme();
      if (httpScheme == null) {
         throw new URISyntaxException(inputUri.toString(), "Undefined HTTP scheme");
      } else if (!"ws".equalsIgnoreCase(httpScheme) && !"wss".equalsIgnoreCase(httpScheme)) {
         String afterScheme = inputUri.toString().substring(httpScheme.length());
         if ("http".equalsIgnoreCase(httpScheme)) {
            return new URI("ws" + afterScheme);
         } else if ("https".equalsIgnoreCase(httpScheme)) {
            return new URI("wss" + afterScheme);
         } else {
            throw new URISyntaxException(inputUri.toString(), "Unrecognized HTTP scheme");
         }
      } else {
         return inputUri;
      }
   }
}
