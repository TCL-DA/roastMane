package org.eclipse.jetty.http;

import org.eclipse.jetty.util.HostPort;

public class HostPortHttpField extends HttpField {
   final HostPort _hostPort;

   public HostPortHttpField(String authority) {
      this(HttpHeader.HOST, HttpHeader.HOST.asString(), authority);
   }

   protected HostPortHttpField(HttpHeader header, String name, String authority) {
      super(header, name, authority);

      try {
         this._hostPort = new HostPort(authority);
      } catch (Exception var5) {
         throw new BadMessageException("Bad HostPort", var5);
      }
   }

   public HostPortHttpField(String host, int port) {
      this(new HostPort(host, port));
   }

   public HostPortHttpField(HostPort hostport) {
      super(HttpHeader.HOST, HttpHeader.HOST.asString(), hostport.toString());
      this._hostPort = hostport;
   }

   public HostPortHttpField(HttpHeader header, String headerString, HostPort hostport) {
      super(header, headerString, hostport.toString());
      this._hostPort = hostport;
   }

   public String getHost() {
      return this._hostPort.getHost();
   }

   public int getPort() {
      return this._hostPort.getPort();
   }

   public int getPort(int defaultPort) {
      return this._hostPort.getPort(defaultPort);
   }

   public HostPort getHostPort() {
      return this._hostPort;
   }
}
