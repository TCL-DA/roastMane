package org.eclipse.jetty.websocket.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.util.Fields.Field;

public class Negotiated {
   private final URI requestURI;
   private final Map<String, List<String>> parameterMap;
   private final String subProtocol;
   private final boolean secure;
   private final ExtensionStack extensions;
   private final String protocolVersion;

   public Negotiated(URI requestURI, String subProtocol, boolean secure, ExtensionStack extensions, String protocolVersion) {
      this.requestURI = toWebsocket(requestURI);
      this.subProtocol = subProtocol;
      this.secure = secure;
      this.extensions = extensions;
      this.protocolVersion = protocolVersion;
      String rawQuery = requestURI.getRawQuery();
      Map<String, List<String>> map;
      if (StringUtil.isBlank(rawQuery)) {
         map = Collections.emptyMap();
      } else {
         map = new HashMap<>();
         Fields fields = new Fields();
         UrlEncoded.decodeUtf8To(rawQuery, fields);

         for (Field field : fields) {
            map.put(field.getName(), Collections.unmodifiableList(field.getValues()));
         }
      }

      this.parameterMap = Collections.unmodifiableMap(map);
   }

   public URI getRequestURI() {
      return this.requestURI;
   }

   public Map<String, List<String>> getParameterMap() {
      return this.parameterMap;
   }

   public String getSubProtocol() {
      return this.subProtocol;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public ExtensionStack getExtensions() {
      return this.extensions;
   }

   public String getProtocolVersion() {
      return this.protocolVersion;
   }

   @Override
   public String toString() {
      return String.format(
         "[%s,%s,%b.%s]",
         this.requestURI,
         this.subProtocol,
         this.secure,
         this.extensions.getNegotiatedExtensions().stream().map(ExtensionConfig::getName).collect(Collectors.toList())
      );
   }

   public static Negotiated from(ExtensionStack extensions) {
      try {
         return new Negotiated(new URI("/"), "", false, extensions, WebSocketConstants.SPEC_VERSION_STRING);
      } catch (URISyntaxException var2) {
         throw new RuntimeException(var2);
      }
   }

   public static URI toWebsocket(URI uri) {
      try {
         Objects.requireNonNull(uri, "Input URI must not be null");
         String httpScheme = uri.getScheme();
         if (httpScheme == null) {
            return uri;
         } else if (!HttpScheme.WS.is(httpScheme) && !HttpScheme.WSS.is(httpScheme)) {
            String afterScheme = uri.toString().substring(httpScheme.length());
            if (HttpScheme.HTTP.is(httpScheme)) {
               return new URI("ws" + afterScheme);
            } else if (HttpScheme.HTTPS.is(httpScheme)) {
               return new URI("wss" + afterScheme);
            } else {
               throw new URISyntaxException(uri.toString(), "Unrecognized HTTP scheme");
            }
         } else {
            return uri;
         }
      } catch (URISyntaxException var3) {
         throw new IllegalArgumentException(var3);
      }
   }
}
