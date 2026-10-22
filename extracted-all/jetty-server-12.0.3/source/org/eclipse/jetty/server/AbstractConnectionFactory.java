package org.eclipse.jetty.server;

import java.util.Iterator;
import java.util.List;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

@ManagedObject
public abstract class AbstractConnectionFactory extends ContainerLifeCycle implements ConnectionFactory {
   private final String _protocol;
   private final List<String> _protocols;
   private int _inputBufferSize = 8192;

   protected AbstractConnectionFactory(String protocol) {
      this._protocol = protocol;
      this._protocols = List.of(protocol);
   }

   protected AbstractConnectionFactory(String... protocols) {
      this._protocol = protocols[0];
      this._protocols = List.of(protocols);
   }

   @ManagedAttribute(
      value = "The protocol name",
      readonly = true
   )
   @Override
   public String getProtocol() {
      return this._protocol;
   }

   @Override
   public List<String> getProtocols() {
      return this._protocols;
   }

   @ManagedAttribute("The buffer size used to read from the network")
   public int getInputBufferSize() {
      return this._inputBufferSize;
   }

   public void setInputBufferSize(int size) {
      this._inputBufferSize = size;
   }

   protected String findNextProtocol(Connector connector) {
      return findNextProtocol(connector, this.getProtocol());
   }

   protected static String findNextProtocol(Connector connector, String currentProtocol) {
      String nextProtocol = null;
      Iterator<String> it = connector.getProtocols().iterator();

      while (it.hasNext()) {
         String protocol = it.next();
         if (currentProtocol.equalsIgnoreCase(protocol)) {
            nextProtocol = it.hasNext() ? it.next() : null;
            break;
         }
      }

      return nextProtocol;
   }

   protected AbstractConnection configure(AbstractConnection connection, Connector connector, EndPoint endPoint) {
      connection.setInputBufferSize(this.getInputBufferSize());
      connector.getEventListeners().forEach(connection::addEventListener);
      this.getEventListeners().forEach(connection::addEventListener);
      return connection;
   }

   @Override
   public String toString() {
      return String.format("%s@%x%s", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols());
   }

   public static ConnectionFactory[] getFactories(org.eclipse.jetty.util.ssl.SslContextFactory.Server sslContextFactory, ConnectionFactory... factories) {
      factories = (ConnectionFactory[])ArrayUtil.removeNulls(factories);
      if (sslContextFactory == null) {
         return factories;
      } else {
         for (ConnectionFactory factory : factories) {
            if (factory instanceof HttpConfiguration.ConnectionFactory) {
               HttpConfiguration config = ((HttpConfiguration.ConnectionFactory)factory).getHttpConfiguration();
               if (config.getCustomizer(SecureRequestCustomizer.class) == null) {
                  config.addCustomizer(new SecureRequestCustomizer());
               }
            }
         }

         return (ConnectionFactory[])ArrayUtil.prependToArray(
            new SslConnectionFactory(sslContextFactory, factories[0].getProtocol()), factories, ConnectionFactory.class
         );
      }
   }
}
