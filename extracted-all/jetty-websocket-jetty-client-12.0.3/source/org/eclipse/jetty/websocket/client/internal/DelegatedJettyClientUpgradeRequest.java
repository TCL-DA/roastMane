package org.eclipse.jetty.websocket.client.internal;

import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.UrlEncoded;
import org.eclipse.jetty.websocket.api.ExtensionConfig;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.core.client.CoreClientUpgradeRequest;

public class DelegatedJettyClientUpgradeRequest implements UpgradeRequest {
   private final CoreClientUpgradeRequest delegate;

   public DelegatedJettyClientUpgradeRequest(CoreClientUpgradeRequest delegate) {
      this.delegate = delegate;
   }

   public List<HttpCookie> getCookies() {
      return this.delegate.getCookies().stream().<HttpCookie>map(org.eclipse.jetty.http.HttpCookie::asJavaNetHttpCookie).toList();
   }

   public String getHeader(String name) {
      return this.delegate.getHeaders().get(name);
   }

   public int getHeaderInt(String name) {
      HttpField field = this.delegate.getHeaders().getField(name);
      return field == null ? -1 : field.getIntValue();
   }

   public List<String> getHeaders(String name) {
      return this.delegate.getHeaders().getValuesList(name);
   }

   public Map<String, List<String>> getHeaders() {
      return null;
   }

   public String getHost() {
      return this.delegate.getURI().getHost();
   }

   public String getHttpVersion() {
      return this.delegate.getVersion().toString();
   }

   public void configure(EndPoint endpoint) {
   }

   public String getMethod() {
      return this.delegate.getMethod();
   }

   public String getOrigin() {
      return this.delegate.getHeaders().get(HttpHeader.ORIGIN);
   }

   public Map<String, List<String>> getParameterMap() {
      if (this.getQueryString() == null) {
         return Collections.emptyMap();
      } else {
         MultiMap<String> params = new MultiMap();
         UrlEncoded.decodeTo(this.getQueryString(), params, StandardCharsets.UTF_8);
         return params;
      }
   }

   public String getProtocolVersion() {
      return this.delegate.getHeaders().get(HttpHeader.SEC_WEBSOCKET_VERSION);
   }

   public String getQueryString() {
      return this.delegate.getURI().getRawQuery();
   }

   public URI getRequestURI() {
      return this.delegate.getURI();
   }

   public List<String> getSubProtocols() {
      return this.delegate.getHeaders().getValuesList(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);
   }

   public boolean hasSubProtocol(String test) {
      HttpField field = this.delegate.getHeaders().getField(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);
      return field == null ? false : field.contains(test);
   }

   public boolean isSecure() {
      return HttpScheme.isSecure(this.delegate.getURI().getScheme());
   }

   public Principal getUserPrincipal() {
      return null;
   }

   public List<ExtensionConfig> getExtensions() {
      List<String> rawExtensions = this.delegate.getHeaders().getValuesList(HttpHeader.SEC_WEBSOCKET_EXTENSIONS);
      return rawExtensions != null && !rawExtensions.isEmpty()
         ? rawExtensions.stream().<ExtensionConfig>map(ExtensionConfig::parse).collect(Collectors.toList())
         : Collections.emptyList();
   }
}
