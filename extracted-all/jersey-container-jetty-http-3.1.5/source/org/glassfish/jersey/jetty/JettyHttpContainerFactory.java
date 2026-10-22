package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import java.net.URI;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
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
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }
}
