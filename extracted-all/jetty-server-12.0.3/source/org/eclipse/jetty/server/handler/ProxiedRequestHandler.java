package org.eclipse.jetty.server.handler;

import java.net.SocketAddress;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.server.ConnectionMetaData;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HostPort;

public class ProxiedRequestHandler extends Handler.Wrapper {
   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      final ConnectionMetaData proxiedFor = new ConnectionMetaData.Wrapper(request.getConnectionMetaData()) {
         @Override
         public boolean isSecure() {
            return super.isSecure();
         }

         @Override
         public SocketAddress getRemoteSocketAddress() {
            return super.getRemoteSocketAddress();
         }

         @Override
         public SocketAddress getLocalSocketAddress() {
            return super.getLocalSocketAddress();
         }

         @Override
         public HostPort getServerAuthority() {
            return super.getServerAuthority();
         }
      };
      return super.handle(new Request.Wrapper(request) {
         @Override
         public HttpURI getHttpURI() {
            return super.getHttpURI();
         }

         @Override
         public ConnectionMetaData getConnectionMetaData() {
            return proxiedFor;
         }
      }, response, callback);
   }
}
