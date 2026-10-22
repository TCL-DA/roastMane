package org.eclipse.jetty.websocket.client;

import java.net.HttpCookie;
import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.websocket.api.ExtensionConfig;
import org.eclipse.jetty.websocket.api.UpgradeRequest;

public final class ClientUpgradeRequest implements UpgradeRequest {
   private final List<String> subProtocols = new ArrayList<>(1);
   private final List<ExtensionConfig> extensions = new ArrayList<>(1);
   private final List<HttpCookie> cookies = new ArrayList<>(1);
   private final Map<String, List<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final URI requestURI;
   private final String host;
   private long timeout;
   public static final String ABNF_REQUIRED_QUOTING = "\"'\\\n\r\t\f\b%+ ;=";

   public ClientUpgradeRequest() {
      this.requestURI = null;
      this.host = null;
   }

   @Deprecated
   public ClientUpgradeRequest(URI uri) {
      this.requestURI = uri;
      String scheme = uri.getScheme();
      if (!HttpScheme.WS.is(scheme) && !HttpScheme.WSS.is(scheme)) {
         throw new IllegalArgumentException("URI scheme must be 'ws' or 'wss'");
      } else {
         this.host = this.requestURI.getHost();
      }
   }

   public List<HttpCookie> getCookies() {
      return this.cookies;
   }

   public List<ExtensionConfig> getExtensions() {
      return this.extensions;
   }

   public String getHeader(String name) {
      List<String> values = this.headers.get(name);
      return joinValues(values);
   }

   public int getHeaderInt(String name) {
      List<String> values = this.headers.get(name);
      if (values == null) {
         return -1;
      } else {
         int size = values.size();
         if (size <= 0) {
            return -1;
         } else if (size == 1) {
            return Integer.parseInt(values.get(0));
         } else {
            throw new NumberFormatException("Cannot convert multi-value header into int");
         }
      }
   }

   public Map<String, List<String>> getHeaders() {
      return this.headers;
   }

   public List<String> getHeaders(String name) {
      return this.headers.get(name);
   }

   public String getHost() {
      return this.host;
   }

   public String getHttpVersion() {
      throw new UnsupportedOperationException("HttpVersion not available on ClientUpgradeRequest");
   }

   public String getMethod() {
      throw new UnsupportedOperationException("Method not available on ClientUpgradeRequest");
   }

   public String getOrigin() {
      return this.getHeader(HttpHeader.ORIGIN.name());
   }

   public Map<String, List<String>> getParameterMap() {
      return Collections.emptyMap();
   }

   public String getProtocolVersion() {
      String version = this.getHeader("Sec-WebSocket-Version");
      return Objects.requireNonNullElse(version, "13");
   }

   public String getQueryString() {
      return this.requestURI == null ? null : this.requestURI.getQuery();
   }

   public URI getRequestURI() {
      return this.requestURI;
   }

   public List<String> getSubProtocols() {
      return this.subProtocols;
   }

   public Principal getUserPrincipal() {
      throw new UnsupportedOperationException("User Principal not available on Client request");
   }

   public boolean hasSubProtocol(String test) {
      for (String protocol : this.subProtocols) {
         if (protocol.equalsIgnoreCase(test)) {
            return true;
         }
      }

      return false;
   }

   public boolean isSecure() {
      throw new UnsupportedOperationException("Request.isSecure not available on Client request");
   }

   public void addExtensions(ExtensionConfig... configs) {
      Collections.addAll(this.extensions, configs);
   }

   public void addExtensions(String... configs) {
      for (String config : configs) {
         this.extensions.add(ExtensionConfig.parse(config));
      }
   }

   public void setCookies(List<HttpCookie> cookies) {
      this.cookies.clear();
      if (cookies != null && !cookies.isEmpty()) {
         this.cookies.addAll(cookies);
      }
   }

   public void setExtensions(List<ExtensionConfig> configs) {
      this.extensions.clear();
      if (configs != null) {
         this.extensions.addAll(configs);
      }
   }

   public void setHeader(String name, List<String> values) {
      this.headers.put(name, values);
   }

   public void setHeader(String name, String value) {
      List<String> values = new ArrayList<>();
      values.add(value);
      this.setHeader(name, values);
   }

   public void setHeaders(Map<String, List<String>> headers) {
      this.headers.clear();

      for (Entry<String, List<String>> entry : headers.entrySet()) {
         String name = entry.getKey();
         List<String> values = entry.getValue();
         this.setHeader(name, values);
      }
   }

   public void setSubProtocols(List<String> protocols) {
      this.subProtocols.clear();
      if (protocols != null) {
         this.subProtocols.addAll(protocols);
      }
   }

   public void setSubProtocols(String... protocols) {
      this.subProtocols.clear();
      Collections.addAll(this.subProtocols, protocols);
   }

   public void setTimeout(long timeout, TimeUnit unit) {
      this.timeout = unit.toMillis(timeout);
   }

   public long getTimeout() {
      return this.timeout;
   }

   public static String joinValues(List<String> values) {
      if (values == null) {
         return null;
      } else {
         int size = values.size();
         if (size <= 0) {
            return null;
         } else if (size == 1) {
            return values.get(0);
         } else {
            boolean needsDelim = false;
            StringBuilder ret = new StringBuilder();

            for (String value : values) {
               if (needsDelim) {
                  ret.append(", ");
               }

               quoteIfNeeded(ret, value, "\"'\\\n\r\t\f\b%+ ;=");
               needsDelim = true;
            }

            return ret.toString();
         }
      }
   }

   private static void quoteIfNeeded(StringBuilder buf, String str, String delim) {
      if (str != null) {
         int len = str.length();
         if (len != 0) {
            for (int i = 0; i < len; i++) {
               int ch = str.codePointAt(i);
               if (delim.indexOf(ch) >= 0) {
                  buf.append('"');
                  buf.append(str);
                  buf.append('"');
                  return;
               }
            }

            buf.append(str);
         }
      }
   }
}
