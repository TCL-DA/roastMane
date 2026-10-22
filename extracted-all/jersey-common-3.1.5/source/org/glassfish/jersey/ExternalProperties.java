package org.glassfish.jersey;

import org.glassfish.jersey.internal.util.PropertiesClass;

@PropertiesClass
public final class ExternalProperties {
   public static final String HTTP_PROXY_HOST = "http.proxyHost";
   public static final String HTTP_PROXY_PORT = "http.proxyPort";
   public static final String HTTP_NON_PROXY_HOSTS = "http.nonProxyHosts";
   public static final String HTTP_PROXY_USER = "http.proxyUser";
   public static final String HTTP_PROXY_PASSWORD = "http.proxyPassword";

   private ExternalProperties() {
   }
}
