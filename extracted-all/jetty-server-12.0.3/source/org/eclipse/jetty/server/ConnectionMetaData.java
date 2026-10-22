package org.eclipse.jetty.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.HostPort;

public interface ConnectionMetaData extends Attributes {
   String getId();

   HttpConfiguration getHttpConfiguration();

   HttpVersion getHttpVersion();

   String getProtocol();

   Connection getConnection();

   Connector getConnector();

   boolean isPersistent();

   boolean isSecure();

   default boolean isPushSupported() {
      return false;
   }

   SocketAddress getRemoteSocketAddress();

   SocketAddress getLocalSocketAddress();

   default HostPort getServerAuthority() {
      HttpConfiguration httpConfiguration = this.getHttpConfiguration();
      HostPort authority = httpConfiguration.getServerAuthority();
      if (authority != null) {
         return authority;
      } else {
         SocketAddress localSocketAddress = this.getLocalSocketAddress();
         if (localSocketAddress instanceof InetSocketAddress inetSocketAddress) {
            return new HostPort(inetSocketAddress.getHostString(), inetSocketAddress.getPort());
         } else {
            return localSocketAddress != null ? new HostPort(localSocketAddress.toString()) : null;
         }
      }
   }

   public static class Wrapper extends org.eclipse.jetty.util.Attributes.Wrapper implements ConnectionMetaData {
      public Wrapper(ConnectionMetaData wrapped) {
         super(wrapped);
      }

      public ConnectionMetaData getWrapped() {
         return (ConnectionMetaData)super.getWrapped();
      }

      @Override
      public String getId() {
         return this.getWrapped().getId();
      }

      @Override
      public HttpConfiguration getHttpConfiguration() {
         return this.getWrapped().getHttpConfiguration();
      }

      @Override
      public HttpVersion getHttpVersion() {
         return this.getWrapped().getHttpVersion();
      }

      @Override
      public String getProtocol() {
         return this.getWrapped().getProtocol();
      }

      @Override
      public Connection getConnection() {
         return this.getWrapped().getConnection();
      }

      @Override
      public Connector getConnector() {
         return this.getWrapped().getConnector();
      }

      @Override
      public boolean isPersistent() {
         return this.getWrapped().isPersistent();
      }

      @Override
      public boolean isSecure() {
         return this.getWrapped().isSecure();
      }

      @Override
      public boolean isPushSupported() {
         return this.getWrapped().isPushSupported();
      }

      @Override
      public SocketAddress getRemoteSocketAddress() {
         return this.getWrapped().getRemoteSocketAddress();
      }

      @Override
      public SocketAddress getLocalSocketAddress() {
         return this.getWrapped().getLocalSocketAddress();
      }

      @Override
      public HostPort getServerAuthority() {
         return this.getWrapped().getServerAuthority();
      }
   }
}
