package org.glassfish.jersey.server;

import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.SeBootstrap.Configuration.SSLClientAuthentication;
import jakarta.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import org.glassfish.jersey.internal.config.SystemPropertiesConfigurationModel;
import org.glassfish.jersey.internal.util.PropertiesClass;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.spi.WebServer;

public final class JerseySeBootstrapConfiguration implements Configuration {
   private static final Logger LOGGER = Logger.getLogger(JerseySeBootstrapConfiguration.class.getName());
   protected static final Random RANDOM = new Random();
   private final Configuration configuration;

   private JerseySeBootstrapConfiguration(Configuration configuration) {
      this.configuration = configuration;
   }

   public Object property(String name) {
      return this.configuration.property(name);
   }

   public URI uri(boolean resolveDefaultPort) {
      String protocol = this.configuration.protocol();
      String host = this.configuration.host();
      int port = resolveDefaultPort ? this.resolvePort() : this.configuration.port();
      String rootPath = this.configuration.rootPath();
      return UriBuilder.newInstance().scheme(protocol.toLowerCase()).host(host).port(port).path(rootPath).build(new Object[0]);
   }

   private int resolvePort() {
      int configPort = this.configuration.port();
      int basePort = this.allowPrivilegedPorts() ? 0 : 8000;
      int port;
      switch (configPort) {
         case -1:
            port = basePort + (this.isHttps() ? 443 : 80);
            break;
         case 0:
            port = this._resolvePort(basePort == 0);
            break;
         default:
            port = configPort;
      }

      return port;
   }

   private int _resolvePort(boolean allowPrivilegedPort) {
      int basePort = allowPrivilegedPort ? 0 : 1024;
      int lower = basePort;
      int range = 65535;
      int initialOffset = RANDOM.nextInt(65535 - basePort);
      int offset = initialOffset;

      while (true) {
         int port = lower + offset;

         try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return port;
         } catch (IOException var10) {
            offset = (offset + 1) % 65535;
            if (offset == initialOffset) {
               throw new IllegalArgumentException(LocalizationMessages.COULD_NOT_BIND_TO_ANY_PORT());
            }
         }
      }
   }

   public SSLContext sslContext() {
      SSLContext sslContext = this.configuration.sslContext();
      return this.isHttps() ? sslContext : null;
   }

   public boolean isHttps() {
      return "HTTPS".equalsIgnoreCase(this.configuration.protocol());
   }

   public boolean autoStart() {
      return (Boolean)Optional.ofNullable((Boolean)this.configuration.property("jersey.config.server.bootstrap.webserver.autostart")).orElse(Boolean.TRUE);
   }

   public boolean allowPrivilegedPorts() {
      return (Boolean)Optional.ofNullable((Boolean)this.configuration.property("jersey.config.server.bootstrap.webserver.allow.privileged.ports"))
         .orElse(Boolean.FALSE);
   }

   public static JerseySeBootstrapConfiguration from(Configuration configuration) {
      return JerseySeBootstrapConfiguration.class.isInstance(configuration)
         ? (JerseySeBootstrapConfiguration)configuration
         : new JerseySeBootstrapConfiguration(configuration);
   }

   public static JerseySeBootstrapConfiguration.Builder builder() {
      return new JerseySeBootstrapConfiguration.Builder();
   }

   public static final class Builder implements jakarta.ws.rs.SeBootstrap.Configuration.Builder {
      private static final Map<String, Class<?>> PROPERTY_TYPES = new HashMap<>();
      private final Map<String, Object> properties = new HashMap<>();

      private Builder() {
         this.properties.put("jakarta.ws.rs.SeBootstrap.Protocol", "HTTP");
         this.properties.put("jakarta.ws.rs.SeBootstrap.Host", "localhost");
         this.properties.put("jakarta.ws.rs.SeBootstrap.Port", -1);
         this.properties.put("jakarta.ws.rs.SeBootstrap.RootPath", "/");
         this.properties.put("jersey.config.server.bootstrap.webserver.class", WebServer.class);

         try {
            this.properties.put("jakarta.ws.rs.SeBootstrap.SSLContext", SSLContext.getDefault());
         } catch (NoSuchAlgorithmException var2) {
            throw new RuntimeException(var2);
         }

         this.properties.put("jakarta.ws.rs.SeBootstrap.SSLClientAuthentication", SSLClientAuthentication.NONE);
         this.properties.put("jersey.config.server.bootstrap.webserver.autostart", Boolean.TRUE);
         this.properties.put("jersey.config.server.bootstrap.webserver.allow.privileged.ports", Boolean.FALSE);
         SystemPropertiesConfigurationModel propertiesConfigurationModel = new SystemPropertiesConfigurationModel(
            Collections.singletonList(JerseySeBootstrapConfiguration.Properties.class.getName())
         );
         this.from(
            (name, aClass) -> !String.class.equals(aClass) && !Integer.class.equals(aClass) && !Boolean.class.equals(aClass)
                  ? Optional.empty()
                  : propertiesConfigurationModel.getOptionalProperty(name, aClass)
         );
      }

      public JerseySeBootstrapConfiguration build() {
         return JerseySeBootstrapConfiguration.from(this.properties::get);
      }

      public JerseySeBootstrapConfiguration.Builder property(String name, Object value) {
         this.properties.put(name, value);
         return this;
      }

      public JerseySeBootstrapConfiguration.Builder webServerClass(Class<? extends WebServer> webServerClass) {
         return this.property("jersey.config.server.bootstrap.webserver.class", webServerClass);
      }

      public JerseySeBootstrapConfiguration.Builder autoStart(Boolean autostart) {
         return this.property("jersey.config.server.bootstrap.webserver.autostart", autostart);
      }

      public <T> JerseySeBootstrapConfiguration.Builder from(BiFunction<String, Class<T>, Optional<T>> configProvider) {
         PROPERTY_TYPES.forEach(
            (propertyName, propertyType) -> configProvider.apply(propertyName, (Class<T>)propertyType)
                  .ifPresent(propertyValue -> this.properties.put(propertyName, propertyValue))
         );
         return this;
      }

      public JerseySeBootstrapConfiguration.Builder from(Object externalConfig) {
         if (Configuration.class.isInstance(externalConfig)) {
            Configuration other = (Configuration)externalConfig;
            this.from((name, clazz) -> {
               Object property = other.property(name);
               if (property != null) {
                  if (clazz.equals(property.getClass())) {
                     return Optional.of(property);
                  }

                  JerseySeBootstrapConfiguration.LOGGER.warning(LocalizationMessages.IGNORE_SEBOOTSTRAP_CONFIGURATION_PROPERTY(name, clazz));
               }

               return Optional.empty();
            });
         }

         return this;
      }

      static {
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.Protocol", String.class);
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.Host", String.class);
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.Port", Integer.class);
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.RootPath", String.class);
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.SSLContext", SSLContext.class);
         PROPERTY_TYPES.put("jakarta.ws.rs.SeBootstrap.SSLClientAuthentication", SSLClientAuthentication.class);
         PROPERTY_TYPES.put("jersey.config.server.bootstrap.webserver.allow.privileged.ports", Boolean.class);
         PROPERTY_TYPES.put("jersey.config.server.bootstrap.webserver.autostart", Boolean.class);
         PROPERTY_TYPES.put("jersey.config.server.bootstrap.webserver.class", Class.class);
      }
   }

   @PropertiesClass
   private static class Properties {
      public static final String SE_BOOTSTRAP_CONFIGURATION_PROTOCOL = "jakarta.ws.rs.SeBootstrap.Protocol";
      public static final String SE_BOOTSTRAP_CONFIGURATION_HOST = "jakarta.ws.rs.SeBootstrap.Host";
      public static final String SE_BOOTSTRAP_CONFIGURATION_PORT = "jakarta.ws.rs.SeBootstrap.Port";
      public static final String SE_BOOTSTRAP_CONFIGURATION_ROOT_PATH = "jakarta.ws.rs.SeBootstrap.RootPath";
      public static final String WEBSERVER_ALLOW_PRIVILEGED_PORTS = "jersey.config.server.bootstrap.webserver.allow.privileged.ports";
      public static final String WEBSERVER_AUTO_START = "jersey.config.server.bootstrap.webserver.autostart";
   }
}
