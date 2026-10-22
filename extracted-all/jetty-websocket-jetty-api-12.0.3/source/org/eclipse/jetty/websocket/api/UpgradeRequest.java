package org.eclipse.jetty.websocket.api;

import java.net.HttpCookie;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface UpgradeRequest {
   List<HttpCookie> getCookies();

   List<ExtensionConfig> getExtensions();

   String getHeader(String var1);

   int getHeaderInt(String var1);

   Map<String, List<String>> getHeaders();

   List<String> getHeaders(String var1);

   String getHost();

   String getHttpVersion();

   String getMethod();

   String getOrigin();

   Map<String, List<String>> getParameterMap();

   String getProtocolVersion();

   String getQueryString();

   URI getRequestURI();

   List<String> getSubProtocols();

   Principal getUserPrincipal();

   boolean hasSubProtocol(String var1);

   boolean isSecure();
}
