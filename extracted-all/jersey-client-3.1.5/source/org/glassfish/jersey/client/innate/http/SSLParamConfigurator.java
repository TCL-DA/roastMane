package org.glassfish.jersey.client.innate.http;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.UriBuilder;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import org.glassfish.jersey.client.ClientRequest;

public final class SSLParamConfigurator {
   private final URI uri;
   private final Optional<SniConfigurator> sniConfigurator;

   private SSLParamConfigurator(SSLParamConfigurator.Builder builder) {
      String sniHostName = builder.sniHostNameHeader == null ? builder.sniHostNameProperty : builder.sniHostNameHeader;
      this.uri = builder.uri;
      this.sniConfigurator = SniConfigurator.createWhenHostHeader(this.uri, sniHostName, builder.setAlways);
   }

   public static SSLParamConfigurator.Builder builder() {
      return new SSLParamConfigurator.Builder();
   }

   public String getSNIHostName() {
      return this.sniConfigurator.isPresent() ? this.sniConfigurator.get().getHostName() : this.uri.getHost();
   }

   public URI toIPRequestUri() {
      String host = this.uri.getHost();

      try {
         InetAddress ip = InetAddress.getByName(host);
         return UriBuilder.fromUri(this.uri).host(ip.getHostAddress()).build(new Object[0]);
      } catch (UnknownHostException var3) {
         return this.uri;
      }
   }

   public boolean isSNIRequired() {
      return this.sniConfigurator.isPresent();
   }

   public URI getSNIUri() {
      return this.sniConfigurator.isPresent() ? UriBuilder.fromUri(this.uri).host(this.getSNIHostName()).build(new Object[0]) : this.uri;
   }

   public void setSNIServerName(SSLEngine sslEngine) {
      this.sniConfigurator.ifPresent(sni -> sni.setServerNames(sslEngine));
   }

   public void setSNIServerName(SSLSocket sslSocket) {
      this.sniConfigurator.ifPresent(sni -> sni.setServerNames(sslSocket));
   }

   public void setSNIServerName(SSLParameters parameters) {
      this.sniConfigurator.ifPresent(sni -> sni.updateSSLParameters(parameters));
   }

   public void setEndpointIdentificationAlgorithm(SSLEngine sslEngine) {
      SSLParameters sslParameters = sslEngine.getSSLParameters();
      sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
      sslEngine.setSSLParameters(sslParameters);
   }

   public static final class Builder {
      private URI uri = null;
      private String sniHostNameHeader = null;
      private String sniHostNameProperty = null;
      private boolean setAlways = false;

      public SSLParamConfigurator.Builder request(ClientRequest clientRequest) {
         this.sniHostNameHeader = getSniHostNameHeader(clientRequest.getHeaders());
         this.sniHostNameProperty = clientRequest.resolveProperty("jersey.config.client.sniHostName", String.class);
         this.uri = clientRequest.getUri();
         return this;
      }

      public SSLParamConfigurator.Builder configuration(Configuration configuration) {
         this.sniHostNameProperty = (String)configuration.getProperty("jersey.config.client.sniHostName");
         return this;
      }

      public SSLParamConfigurator.Builder uri(URI uri) {
         this.uri = uri;
         return this;
      }

      public SSLParamConfigurator.Builder headers(Map<String, List<Object>> httpHeaders) {
         this.sniHostNameHeader = getSniHostNameHeader(httpHeaders);
         return this;
      }

      public SSLParamConfigurator.Builder setSNIAlways(boolean setAlways) {
         this.setAlways = setAlways;
         return this;
      }

      public SSLParamConfigurator build() {
         return new SSLParamConfigurator(this);
      }

      private static String getSniHostNameHeader(Map<String, List<Object>> httpHeaders) {
         List<Object> hostHeaders = httpHeaders.get("Host");
         if (hostHeaders != null && hostHeaders.get(0) != null) {
            String hostHeader = hostHeaders.get(0).toString();
            String trimmedHeader;
            if (hostHeader != null) {
               int index = hostHeader.indexOf(58);
               String trimmedHeader0 = index != -1 ? hostHeader.substring(0, index).trim() : hostHeader.trim();
               trimmedHeader = trimmedHeader0.isEmpty() ? hostHeader : trimmedHeader0;
            } else {
               trimmedHeader = null;
            }

            return trimmedHeader;
         } else {
            return null;
         }
      }
   }
}
