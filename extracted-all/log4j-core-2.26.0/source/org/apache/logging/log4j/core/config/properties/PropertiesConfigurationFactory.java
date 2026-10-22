package org.apache.logging.log4j.core.config.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "PropertiesConfigurationFactory",
   category = "ConfigurationFactory"
)
@Order(8)
public class PropertiesConfigurationFactory extends ConfigurationFactory {
   @Override
   protected String[] getSupportedTypes() {
      return new String[]{".properties"};
   }

   public PropertiesConfiguration getConfiguration(final LoggerContext loggerContext, final ConfigurationSource source) {
      Properties properties = new Properties();

      try {
         InputStream configStream = source.getInputStream();

         try {
            properties.load(configStream);
         } catch (Throwable var8) {
            if (configStream != null) {
               try {
                  configStream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (configStream != null) {
            configStream.close();
         }
      } catch (IOException var9) {
         throw new ConfigurationException("Unable to load " + source.toString(), var9);
      }

      return new PropertiesConfigurationBuilder().setConfigurationSource(source).setRootProperties(properties).setLoggerContext(loggerContext).build();
   }
}
