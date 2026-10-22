package org.eclipse.jetty.server;

import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields.Mutable;

public class HostHeaderCustomizer implements HttpConfiguration.Customizer {
   private final String serverName;
   private final int serverPort;

   public HostHeaderCustomizer() {
      this(null, 0);
   }

   public HostHeaderCustomizer(String serverName) {
      this(serverName, 0);
   }

   public HostHeaderCustomizer(String serverName, int serverPort) {
      this.serverName = serverName;
      this.serverPort = serverPort;
   }

   @Override
   public Request customize(Request request, Mutable responseHeaders) {
      if (request.getConnectionMetaData().getHttpVersion() != HttpVersion.HTTP_1_1 && !request.getHeaders().contains(HttpHeader.HOST)) {
         String host = this.serverName == null ? Request.getServerName(request) : this.serverName;
         int port = HttpScheme.normalizePort(request.getHttpURI().getScheme(), this.serverPort == 0 ? Request.getServerPort(request) : this.serverPort);
         final HttpURI uri = (HttpURI)(this.serverName == null && this.serverPort <= 0
            ? request.getHttpURI()
            : HttpURI.build(request.getHttpURI()).authority(host, port).asImmutable());
         HttpFields original = request.getHeaders();
         Mutable builder = HttpFields.build(original.size() + 1);
         builder.add(new HostPortHttpField(host, port));
         builder.add(request.getHeaders());
         final HttpFields headers = builder.asImmutable();
         return new Request.Wrapper(request) {
            @Override
            public HttpURI getHttpURI() {
               return uri;
            }

            @Override
            public HttpFields getHeaders() {
               return headers;
            }
         };
      } else {
         return request;
      }
   }
}
