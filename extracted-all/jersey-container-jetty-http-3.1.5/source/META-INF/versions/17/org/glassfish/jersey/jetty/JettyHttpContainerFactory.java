package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import java.net.URI;
import java.util.concurrent.ThreadFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.internal.guava.ThreadFactoryBuilder;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
import org.glassfish.jersey.process.JerseyProcessingUncaughtExceptionHandler;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public final class JettyHttpContainerFactory {
   private JettyHttpContainerFactory() {
   }

   public static Server createServer(URI uri) throws ProcessingException {
      return createServer(uri, null, null, true);
   }

   public static Server createServer(URI uri, boolean start) throws ProcessingException {
      return createServer(uri, null, null, start);
   }

   public static Server createServer(URI uri, ResourceConfig config) throws ProcessingException {
      JettyHttpContainer container = (JettyHttpContainer)ContainerFactory.createContainer(JettyHttpContainer.class, config);
      return createServer(uri, null, container, true);
   }

   public static Server createServer(URI uri, ResourceConfig configuration, boolean start) throws ProcessingException {
      return createServer(uri, null, (JettyHttpContainer)ContainerFactory.createContainer(JettyHttpContainer.class, configuration), start);
   }

   public static Server createServer(URI uri, ResourceConfig config, boolean start, Object parentContext) {
      return createServer(uri, null, new JettyHttpContainer(config, parentContext), start);
   }

   public static Server createServer(URI uri, ResourceConfig config, Object parentContext) {
      return createServer(uri, null, new JettyHttpContainer(config, parentContext), true);
   }

   public static Server createServer(URI uri, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory, ResourceConfig config) throws ProcessingException {
      JettyHttpContainer container = (JettyHttpContainer)ContainerFactory.createContainer(JettyHttpContainer.class, config);
      return createServer(uri, sslContextFactory, container, true);
   }

   public static Server createServer(URI uri, org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory, JettyHttpContainer handler, boolean start) {
      if (uri == null) {
         throw new IllegalArgumentException(LocalizationMessages.URI_CANNOT_BE_NULL());
      } else {
         String scheme = uri.getScheme();
         int defaultPort = 80;
         if (sslContextFactory == null) {
            if (!"http".equalsIgnoreCase(scheme)) {
               throw new IllegalArgumentException(LocalizationMessages.WRONG_SCHEME_WHEN_USING_HTTP());
            }
         } else {
            if (!"https".equalsIgnoreCase(scheme)) {
               throw new IllegalArgumentException(LocalizationMessages.WRONG_SCHEME_WHEN_USING_HTTPS());
            }

            defaultPort = 443;
         }

         int port = uri.getPort() == -1 ? defaultPort : uri.getPort();
         Server server = new Server(new JettyHttpContainerFactory.JettyConnectorThreadPool());
         HttpConfiguration config = new HttpConfiguration();
         if (sslContextFactory != null) {
            config.setSecureScheme("https");
            config.setSecurePort(port);
            config.addCustomizer(new SecureRequestCustomizer());
            ServerConnector https = new ServerConnector(
               server, new ConnectionFactory[]{new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(config)}
            );
            https.setPort(port);
            server.setConnectors(new Connector[]{https});
         } else {
            ServerConnector http = new ServerConnector(server, new ConnectionFactory[]{new HttpConnectionFactory(config)});
            http.setPort(port);
            server.setConnectors(new Connector[]{http});
         }

         if (handler != null) {
            server.setHandler(handler);
         }

         if (start) {
            try {
               server.start();
            } catch (Exception var10) {
               throw new ProcessingException(LocalizationMessages.ERROR_WHEN_CREATING_SERVER(), var10);
            }
         }

         return server;
      }
   }

   private static final class JettyConnectorThreadPool extends QueuedThreadPool {
      private final ThreadFactory threadFactory = new ThreadFactoryBuilder()
         .setNameFormat("jetty-http-server-%d")
         .setUncaughtExceptionHandler(new JerseyProcessingUncaughtExceptionHandler())
         .build();

      private JettyConnectorThreadPool() {
      }

      public Thread newThread(Runnable runnable) {
         return this.threadFactory.newThread(runnable);
      }
   }
}
