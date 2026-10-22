package org.glassfish.jersey.client.innate.http;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import org.glassfish.jersey.client.internal.LocalizationMessages;

final class SniConfigurator {
   private static final Logger LOGGER = Logger.getLogger(SniConfigurator.class.getName());
   private final String hostName;

   private SniConfigurator(String hostName) {
      this.hostName = hostName;
   }

   String getHostName() {
      return this.hostName;
   }

   static Optional<SniConfigurator> createWhenHostHeader(URI hostUri, String sniHostName, boolean whenDiffer) {
      if (sniHostName == null) {
         return Optional.empty();
      } else {
         if (hostUri != null) {
            String hostUriString = hostUri.getHost();
            if (!whenDiffer && hostUriString.equals(sniHostName)) {
               return Optional.empty();
            }
         }

         return Optional.of(new SniConfigurator(sniHostName));
      }
   }

   void setServerNames(SSLEngine sslEngine) {
      SSLParameters sslParameters = sslEngine.getSSLParameters();
      this.updateSSLParameters(sslParameters);
      sslEngine.setSSLParameters(sslParameters);
      LOGGER.fine(LocalizationMessages.SNI_ON_SSLENGINE());
   }

   void setServerNames(SSLSocket sslSocket) {
      SSLParameters sslParameters = sslSocket.getSSLParameters();
      this.updateSSLParameters(sslParameters);
      sslSocket.setSSLParameters(sslParameters);
      LOGGER.fine(LocalizationMessages.SNI_ON_SSLSOCKET());
   }

   SSLParameters updateSSLParameters(SSLParameters sslParameters) {
      SNIHostName serverName = new SNIHostName(this.hostName);
      List<SNIServerName> serverNames = new LinkedList<>();
      serverNames.add(serverName);
      sslParameters.setServerNames(serverNames);
      LOGGER.finer(LocalizationMessages.SNI_UPDATE_SSLPARAMS(this.hostName));
      return sslParameters;
   }
}
