package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.internal.util.JdkVersion;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
import org.glassfish.jersey.server.JerseySeBootstrapConfiguration;
import org.glassfish.jersey.server.spi.WebServer;
import org.glassfish.jersey.server.spi.WebServerProvider;

public final class JettyHttpServerProvider implements WebServerProvider {
   public <T extends WebServer> T createServer(Class<T> type, Application application, Configuration configuration) {
      if (JdkVersion.getJdkVersion().getMajor() < 17) {
         throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
      } else {
         return WebServerProvider.isSupportedWebServer(JettyHttpServer.class, type, configuration)
            ? type.cast(new JettyHttpServer(application, JerseySeBootstrapConfiguration.from(configuration)))
            : null;
      }
   }

   public <T extends WebServer> T createServer(Class<T> type, Class<? extends Application> applicationClass, Configuration configuration) {
      if (JdkVersion.getJdkVersion().getMajor() < 17) {
         throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
      } else {
         return WebServerProvider.isSupportedWebServer(JettyHttpServer.class, type, configuration)
            ? type.cast(new JettyHttpServer(applicationClass, JerseySeBootstrapConfiguration.from(configuration)))
            : null;
      }
   }
}
