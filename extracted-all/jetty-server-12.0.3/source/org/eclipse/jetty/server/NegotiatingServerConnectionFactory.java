package org.eclipse.jetty.server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.net.ssl.SSLEngine;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ssl.SslConnection.SslEndPoint;

public abstract class NegotiatingServerConnectionFactory extends AbstractConnectionFactory {
   private final List<String> negotiatedProtocols = new ArrayList<>();
   private String defaultProtocol;

   public NegotiatingServerConnectionFactory(String protocol, String... negotiatedProtocols) {
      super(protocol);
      if (negotiatedProtocols != null) {
         for (String p : negotiatedProtocols) {
            p = p.trim();
            if (!p.isEmpty()) {
               this.negotiatedProtocols.add(p);
            }
         }
      }
   }

   public String getDefaultProtocol() {
      return this.defaultProtocol;
   }

   public void setDefaultProtocol(String defaultProtocol) {
      String dft = defaultProtocol == null ? "" : defaultProtocol.trim();
      this.defaultProtocol = dft.isEmpty() ? null : dft;
   }

   public List<String> getNegotiatedProtocols() {
      return this.negotiatedProtocols;
   }

   @Override
   public Connection newConnection(Connector connector, EndPoint endPoint) {
      List<String> negotiated = this.negotiatedProtocols;
      if (negotiated.isEmpty()) {
         negotiated = connector.getProtocols().stream().filter(p -> {
            ConnectionFactory f = connector.getConnectionFactory(p);
            return !(f instanceof SslConnectionFactory) && !(f instanceof NegotiatingServerConnectionFactory);
         }).collect(Collectors.toList());
      }

      String dft = this.defaultProtocol;
      if (dft == null && !negotiated.isEmpty()) {
         dft = negotiated.stream().filter(HttpVersion.HTTP_1_1::is).findFirst().orElse(negotiated.get(0));
      }

      SSLEngine engine = null;
      EndPoint ep = endPoint;

      while (engine == null && ep != null) {
         if (ep instanceof SslEndPoint ssl) {
            engine = ssl.getSslConnection().getSSLEngine();
         } else {
            ep = null;
         }
      }

      return this.configure(this.newServerConnection(connector, endPoint, engine, negotiated, dft), connector, endPoint);
   }

   protected abstract AbstractConnection newServerConnection(Connector var1, EndPoint var2, SSLEngine var3, List<String> var4, String var5);

   @Override
   public String toString() {
      return String.format(
         "%s@%x{%s,%s,%s}", this.getClass().getSimpleName(), this.hashCode(), this.getProtocols(), this.getDefaultProtocol(), this.getNegotiatedProtocols()
      );
   }
}
