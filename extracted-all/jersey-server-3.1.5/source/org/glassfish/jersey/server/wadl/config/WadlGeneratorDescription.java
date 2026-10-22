package org.glassfish.jersey.server.wadl.config;

import java.util.Properties;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public class WadlGeneratorDescription {
   private Class<? extends WadlGenerator> generatorClass;
   private Properties properties;
   private Class<?> configuratorClass;

   public WadlGeneratorDescription() {
   }

   public WadlGeneratorDescription(Class<? extends WadlGenerator> generatorClass, Properties properties) {
      this.generatorClass = generatorClass;
      this.properties = properties;
   }

   public Class<? extends WadlGenerator> getGeneratorClass() {
      return this.generatorClass;
   }

   public void setGeneratorClass(Class<? extends WadlGenerator> generatorClass) {
      this.generatorClass = generatorClass;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties = properties;
   }

   public Class<?> getConfiguratorClass() {
      return this.configuratorClass;
   }

   void setConfiguratorClass(Class<?> configuratorClass) {
      this.configuratorClass = configuratorClass;
   }
}
