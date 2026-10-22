package org.eclipse.jetty.alpn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.NegotiatingClientConnectionFactory;
import org.eclipse.jetty.io.ssl.ALPNProcessor.Client;
import org.eclipse.jetty.util.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ALPNClientConnectionFactory extends NegotiatingClientConnectionFactory {
   private static final Logger LOG = LoggerFactory.getLogger(ALPNClientConnectionFactory.class);
   private final List<Client> processors = new ArrayList<>();
   private final Executor executor;
   private final List<String> protocols;

   public ALPNClientConnectionFactory(Executor executor, ClientConnectionFactory connectionFactory, List<String> protocols) {
      super(connectionFactory);
      if (protocols.isEmpty()) {
         throw new IllegalArgumentException("ALPN protocol list cannot be empty");
      } else {
         this.executor = executor;
         this.protocols = protocols;
         IllegalStateException failure = new IllegalStateException("No Client ALPNProcessors!");
         TypeUtil.serviceProviderStream(ServiceLoader.load(Client.class)).forEach(provider -> {
            Client processor;
            try {
               processor = (Client)provider.get();
            } catch (Throwable var5) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Unable to load client processor", var5);
               }

               failure.addSuppressed(var5);
               return;
            }

            try {
               processor.init();
               this.processors.add(processor);
            } catch (Throwable var6) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Could not initialize {}", processor, var6);
               }

               failure.addSuppressed(var6);
            }
         });
         if (LOG.isDebugEnabled()) {
            LOG.debug("protocols: {}", protocols);
            LOG.debug("processors: {}", this.processors);
         }

         if (this.processors.isEmpty()) {
            throw failure;
         }
      }
   }

   public Connection newConnection(EndPoint endPoint, Map<String, Object> context) {
      SSLEngine engine = (SSLEngine)context.get("org.eclipse.jetty.client.ssl.engine");

      for (Client processor : this.processors) {
         if (processor.appliesTo(engine)) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("{} for {} on {}", new Object[]{processor, engine, endPoint});
            }

            ALPNClientConnection connection = new ALPNClientConnection(
               endPoint, this.executor, this.getClientConnectionFactory(), engine, context, this.protocols
            );
            processor.configure(engine, connection);
            return this.customize(connection, context);
         }
      }

      throw new IllegalStateException("No ALPNProcessor for " + engine);
   }
}
