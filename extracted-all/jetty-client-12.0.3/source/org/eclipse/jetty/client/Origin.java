package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Objects;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.URIUtil;

public class Origin {
   private final String scheme;
   private final Origin.Address address;
   private final Object tag;
   private final Origin.Protocol protocol;

   public Origin(String scheme, String host, int port) {
      this(scheme, host, port, null);
   }

   public Origin(String scheme, String host, int port, Object tag) {
      this(scheme, new Origin.Address(host, port), tag);
   }

   public Origin(String scheme, String host, int port, Object tag, Origin.Protocol protocol) {
      this(scheme, new Origin.Address(host, port), tag, protocol);
   }

   public Origin(String scheme, Origin.Address address) {
      this(scheme, address, null);
   }

   public Origin(String scheme, Origin.Address address, Object tag) {
      this(scheme, address, tag, null);
   }

   public Origin(String scheme, Origin.Address address, Object tag, Origin.Protocol protocol) {
      this.scheme = Objects.requireNonNull(scheme);
      this.address = address;
      this.tag = tag;
      this.protocol = protocol;
   }

   public String getScheme() {
      return this.scheme;
   }

   public Origin.Address getAddress() {
      return this.address;
   }

   public Object getTag() {
      return this.tag;
   }

   public Origin.Protocol getProtocol() {
      return this.protocol;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         Origin that = (Origin)obj;
         return this.scheme.equals(that.scheme)
            && this.address.equals(that.address)
            && Objects.equals(this.tag, that.tag)
            && Objects.equals(this.protocol, that.protocol);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.scheme, this.address, this.tag, this.protocol);
   }

   public String asString() {
      StringBuilder result = new StringBuilder();
      URIUtil.appendSchemeHostPort(result, this.scheme, this.address.host, this.address.port);
      return result.toString();
   }

   @Override
   public String toString() {
      return String.format("%s@%x[%s,tag=%s,protocol=%s]", this.getClass().getSimpleName(), this.hashCode(), this.asString(), this.getTag(), this.getProtocol());
   }

   public static class Address {
      private final String host;
      private final int port;
      private final SocketAddress address;

      public Address(String host, int port) {
         this.host = HostPort.normalizeHost(Objects.requireNonNull(host));
         this.port = port;
         this.address = InetSocketAddress.createUnresolved(this.getHost(), this.getPort());
      }

      public String getHost() {
         return this.host;
      }

      public int getPort() {
         return this.port;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            Origin.Address that = (Origin.Address)obj;
            return this.host.equals(that.host) && this.port == that.port;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.host, this.port);
      }

      public String asString() {
         return String.format("%s:%d", this.host, this.port);
      }

      public SocketAddress getSocketAddress() {
         return this.address;
      }

      @Override
      public String toString() {
         return this.asString();
      }
   }

   public static class Protocol {
      private final List<String> protocols;
      private final boolean negotiate;

      public Protocol(List<String> protocols, boolean negotiate) {
         this.protocols = protocols;
         this.negotiate = negotiate;
      }

      public List<String> getProtocols() {
         return this.protocols;
      }

      public boolean isNegotiate() {
         return this.negotiate;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            Origin.Protocol that = (Origin.Protocol)obj;
            return this.protocols.equals(that.protocols) && this.negotiate == that.negotiate;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.protocols, this.negotiate);
      }

      public String asString() {
         return String.format("proto=%s,nego=%b", this.protocols, this.negotiate);
      }

      @Override
      public String toString() {
         return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.asString());
      }
   }
}
