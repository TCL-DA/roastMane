package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NetworkSourceFactory implements MeasurementSourceFactory {
   private static final Logger LOGGER = LoggerFactory.getLogger(NetworkSourceFactory.class);
   private static final String PARAM_HOST = "host";
   private String _host;
   protected InetAddress _inetAddress;

   public static void logAvailableInterfaces() throws SocketException {
      for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
         String ips = "";

         for (InterfaceAddress address : iface.getInterfaceAddresses()) {
            ips = ips + (ips.length() > 0 ? ", " : "");
            ips = ips + address.getAddress().getHostAddress() + "/" + address.getNetworkPrefixLength();
         }

         LOGGER.info("Available interface {}: {}", iface.getDisplayName(), ips);
      }
   }

   protected abstract String getDefaultIpAddress();

   @Override
   public int getChannelCount() {
      return 0;
   }

   @Override
   public String[] getTypes() {
      return new String[]{"Default"};
   }

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>();
      parameters.add(new SourceParameter("host", String.class, this._host != null ? this._host : this.getDefaultIpAddress()));
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      LOGGER.info("Parameters: {}", StringUtils.strip(parameters.toString(), "{}"));
      logAvailableInterfaces();

      for (SourceParameter parameter : parameters) {
         if ("host".equals(parameter.getName())) {
            String host = parameter.getValue();
            if (host == null || host.trim().length() == 0) {
               throw new IllegalArgumentException("Hostname/IP address required!");
            }

            this._inetAddress = InetAddress.getByName(host);
            this._host = host;
         }
      }
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }
}
