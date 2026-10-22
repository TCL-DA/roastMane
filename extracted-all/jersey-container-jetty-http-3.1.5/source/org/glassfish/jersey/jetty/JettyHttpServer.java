package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Application;
import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
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
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public final JettyHttpContainer container() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public final int port() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public final CompletableFuture<Void> start() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public final CompletableFuture<Void> stop() {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }

   public final <T> T unwrap(Class<T> nativeClass) {
      throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
   }
}
