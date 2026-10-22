package org.eclipse.jetty.websocket.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface UpgradeResponse {
   String getAcceptedSubProtocol();

   List<ExtensionConfig> getExtensions();

   String getHeader(String var1);

   Set<String> getHeaderNames();

   Map<String, List<String>> getHeaders();

   List<String> getHeaders(String var1);

   int getStatusCode();
}
