package org.cropster.roastingintelligence.measurement.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPhidgetNetworkingSourceFactory extends Phidget22SourceFactory {
   private static final String PARAM_HOST = "host";
   private static final String PARAM_PORT = "port";
   private final Logger logger = LoggerFactory.getLogger(AbstractPhidgetNetworkingSourceFactory.class);
   protected String _host;
   protected InetAddress _ipAddress;
   protected Integer _port;

   @Override
   public Set<SourceParameter> getParameters() {
      Set<SourceParameter> parameters = new LinkedHashSet<>(super.getParameters());
      parameters.add(new SourceParameter("host", String.class, this._host != null ? this._host : null));
      parameters.add(new SourceParameter("port", Integer.class, this._port != null ? String.valueOf(this._port) : null));
      return Collections.unmodifiableSet(parameters);
   }

   @Override
   public void setup(Set<SourceParameter> parameters) throws Exception {
      super.setup(parameters);
      this.logger.info("Parameters: {}", StringUtils.strip(parameters.toString(), "{}"));

      for (SourceParameter parameter : parameters) {
         if ("host".equals(parameter.getName())) {
            String host = parameter.getValue();
            if (StringUtils.isNotBlank(host)) {
               try {
                  this._ipAddress = InetAddress.getByName(host);
               } catch (UnknownHostException var6) {
                  this.logger.info("Unknown host: {}", host);
               }
            }

            this._host = host;
         } else if ("port".equals(parameter.getName())) {
            this._port = StringUtils.isNotBlank(parameter.getValue()) ? Integer.valueOf(parameter.getValue()) : null;
         }
      }
   }
}
