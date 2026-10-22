package org.glassfish.jersey.server;

import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.server.spi.WebServer;
import org.glassfish.jersey.server.spi.WebServerProvider;

public final class WebServerFactory {
   private WebServerFactory() {
   }

   public static <T extends WebServer> T createServer(Class<T> type, Application application, Configuration configuration) {
      for (WebServerProvider webServerProvider : ServiceFinder.find(WebServerProvider.class)) {
         T server = webServerProvider.createServer(type, application, configuration);
         if (server != null) {
            return server;
         }
      }

      throw new IllegalArgumentException("No server provider supports the type " + type);
   }

   public static <T extends WebServer> T createServer(Class<T> type, Class<? extends Application> application, Configuration configuration) {
      for (WebServerProvider webServerProvider : ServiceFinder.find(WebServerProvider.class)) {
         T server = webServerProvider.createServer(type, application, configuration);
         if (server != null) {
            return server;
         }
      }

      throw new IllegalArgumentException("No server provider supports the type " + type);
   }
}
