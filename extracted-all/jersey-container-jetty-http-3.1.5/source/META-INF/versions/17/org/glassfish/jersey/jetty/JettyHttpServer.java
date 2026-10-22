package org.glassfish.jersey.jetty;

import jakarta.ws.rs.SeBootstrap.Configuration.SSLClientAuthentication;
import jakarta.ws.rs.core.Application;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.JerseySeBootstrapConfiguration;
import org.glassfish.jersey.server.spi.WebServer;

final class JettyHttpServer implements WebServer {
   private final JettyHttpContainer container;
   private final Server httpServer;

   JettyHttpServer(Application application, JerseySeBootstrapConfiguration configuration) {
      this((JettyHttpContainer)ContainerFactory.createContainer(JettyHttpContainer.class, application), configuration);
   }

   JettyHttpServer(Class<? extends Application> applicationClass, JerseySeBootstrapConfiguration configuration) {
      this(new JettyHttpContainer(applicationClass), configuration);
   }

   JettyHttpServer(JettyHttpContainer container, JerseySeBootstrapConfiguration configuration) {
      SSLClientAuthentication sslClientAuthentication = configuration.sslClientAuthentication();
      org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory;
      if (configuration.isHttps()) {
         sslContextFactory = new org.eclipse.jetty.util.ssl.SslContextFactory.Server();
         sslContextFactory.setSslContext(configuration.sslContext());
         sslContextFactory.setWantClientAuth(sslClientAuthentication == SSLClientAuthentication.OPTIONAL);
         sslContextFactory.setNeedClientAuth(sslClientAuthentication == SSLClientAuthentication.MANDATORY);
      } else {
         sslContextFactory = null;
      }

      this.container = container;
      this.httpServer = JettyHttpContainerFactory.createServer(configuration.uri(true), sslContextFactory, this.container, configuration.autoStart());
   }

   public final JettyHttpContainer container() {
      return this.container;
   }

   public final int port() {
      ServerConnector serverConnector = (ServerConnector)this.httpServer.getConnectors()[0];
      int configuredPort = serverConnector.getPort();
      int localPort = serverConnector.getLocalPort();
      return localPort < 0 ? configuredPort : localPort;
   }

   public final CompletableFuture<Void> start() {
      return CompletableFuture.runAsync(() -> {
         try {
            this.httpServer.start();
         } catch (Exception var2) {
            throw new CompletionException(var2);
         }
      });
   }

   public final CompletableFuture<Void> stop() {
      return CompletableFuture.runAsync(() -> {
         try {
            this.httpServer.stop();
         } catch (Exception var2) {
            throw new CompletionException(var2);
         }
      });
   }

   public final <T> T unwrap(Class<T> nativeClass) {
      return nativeClass.cast(this.httpServer);
   }
}
