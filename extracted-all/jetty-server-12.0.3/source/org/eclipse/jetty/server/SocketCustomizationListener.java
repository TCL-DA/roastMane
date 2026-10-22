package org.eclipse.jetty.server;

import java.net.Socket;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.io.Connection.Listener;
import org.eclipse.jetty.io.ssl.SslConnection.SslEndPoint;

public class SocketCustomizationListener implements Listener {
   private final boolean _ssl;

   public SocketCustomizationListener() {
      this(true);
   }

   public SocketCustomizationListener(boolean ssl) {
      this._ssl = ssl;
   }

   public void onOpened(Connection connection) {
      EndPoint endPoint = connection.getEndPoint();
      boolean ssl = false;
      if (this._ssl && endPoint instanceof SslEndPoint sslEndPoint) {
         endPoint = sslEndPoint.getSslConnection().getEndPoint();
         ssl = true;
      }

      if (endPoint instanceof SocketChannelEndPoint socketEndPoint) {
         Socket socket = socketEndPoint.getChannel().socket();
         this.customize(socket, connection.getClass(), ssl);
      }
   }

   protected void customize(Socket socket, Class<? extends Connection> connection, boolean ssl) {
   }

   public void onClosed(Connection connection) {
   }
}
