package org.eclipse.jetty.websocket.client.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.websocket.api.ExtensionConfig;
import org.eclipse.jetty.websocket.api.UpgradeResponse;

public class DelegatedJettyClientUpgradeResponse implements UpgradeResponse {
   private final Response delegate;

   public DelegatedJettyClientUpgradeResponse(Response response) {
      this.delegate = response;
   }

   public String getAcceptedSubProtocol() {
      return this.delegate.getHeaders().get(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);
   }

   public String getHeader(String name) {
      return this.delegate.getHeaders().get(name);
   }

   public Set<String> getHeaderNames() {
      return this.delegate.getHeaders().getFieldNamesCollection();
   }

   public List<String> getHeaders(String name) {
      return this.delegate.getHeaders().getValuesList(name);
   }

   public Map<String, List<String>> getHeaders() {
      Map<String, List<String>> headers = this.getHeaderNames()
         .stream()
         .collect(Collectors.toMap(name -> (String)name, name -> new ArrayList<>(this.getHeaders(name))));
      return Collections.unmodifiableMap(headers);
   }

   public int getStatusCode() {
      return this.delegate.getStatus();
   }

   public List<ExtensionConfig> getExtensions() {
      List<String> rawExtensions = this.delegate.getHeaders().getValuesList(HttpHeader.SEC_WEBSOCKET_EXTENSIONS);
      return rawExtensions != null && !rawExtensions.isEmpty()
         ? rawExtensions.stream().<ExtensionConfig>map(ExtensionConfig::parse).collect(Collectors.toList())
         : Collections.emptyList();
   }
}
