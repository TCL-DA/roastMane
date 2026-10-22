package org.eclipse.jetty.io;

import java.io.IOException;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public interface ClientConnectionFactory {
   String CLIENT_CONTEXT_KEY = "org.eclipse.jetty.client";

   Connection newConnection(EndPoint var1, Map<String, Object> var2) throws IOException;

   default Connection customize(Connection connection, Map<String, Object> context) {
      ContainerLifeCycle client = (ContainerLifeCycle)context.get("org.eclipse.jetty.client");
      if (client != null) {
         client.getBeans(EventListener.class).forEach(connection::addEventListener);
      }

      return connection;
   }

   public interface Decorator {
      ClientConnectionFactory apply(ClientConnectionFactory var1);
   }

   public abstract static class Info extends ContainerLifeCycle {
      private final ClientConnectionFactory factory;

      public Info(ClientConnectionFactory factory) {
         this.factory = factory;
         this.addBean(factory);
      }

      public abstract List<String> getProtocols(boolean var1);

      public ClientConnectionFactory getClientConnectionFactory() {
         return this.factory;
      }

      public boolean matches(List<String> candidates, boolean secure) {
         return this.getProtocols(secure).stream().anyMatch(p -> candidates.stream().anyMatch(c -> c.equalsIgnoreCase(p)));
      }

      public void upgrade(EndPoint endPoint, Map<String, Object> context) {
         throw new UnsupportedOperationException(this + " does not support upgrade to another protocol");
      }
   }
}
