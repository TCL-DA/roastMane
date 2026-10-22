package org.glassfish.jersey.server.wadl.config;

import jakarta.ws.rs.ProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.wadl.WadlGenerator;

public abstract class WadlGeneratorConfig {
   public abstract List configure();

   public WadlGenerator createWadlGenerator(InjectionManager injectionManager) {
      List<WadlGeneratorDescription> wadlGeneratorDescriptions;
      try {
         wadlGeneratorDescriptions = this.configure();
      } catch (Exception var7) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_CONFIGURE(), var7);
      }

      for (WadlGeneratorDescription desc : wadlGeneratorDescriptions) {
         desc.setConfiguratorClass(this.getClass());
      }

      try {
         return WadlGeneratorLoader.loadWadlGeneratorDescriptions(injectionManager, wadlGeneratorDescriptions);
      } catch (Exception var6) {
         throw new ProcessingException(LocalizationMessages.ERROR_WADL_GENERATOR_LOAD(), var6);
      }
   }

   public static WadlGeneratorConfig.WadlGeneratorConfigDescriptionBuilder generator(Class<? extends WadlGenerator> generatorClass) {
      return new WadlGeneratorConfig.WadlGeneratorConfigDescriptionBuilder().generator(generatorClass);
   }

   public static class WadlGeneratorConfigDescriptionBuilder {
      private List<WadlGeneratorDescription> _descriptions = new ArrayList<>();
      private WadlGeneratorDescription _description;

      public WadlGeneratorConfig.WadlGeneratorConfigDescriptionBuilder generator(Class<? extends WadlGenerator> generatorClass) {
         if (this._description != null) {
            this._descriptions.add(this._description);
         }

         this._description = new WadlGeneratorDescription();
         this._description.setGeneratorClass(generatorClass);
         return this;
      }

      public WadlGeneratorConfig.WadlGeneratorConfigDescriptionBuilder prop(String propName, Object propValue) {
         if (this._description.getProperties() == null) {
            this._description.setProperties(new Properties());
         }

         this._description.getProperties().put(propName, propValue);
         return this;
      }

      public List<WadlGeneratorDescription> descriptions() {
         if (this._description != null) {
            this._descriptions.add(this._description);
         }

         return this._descriptions;
      }

      public WadlGeneratorConfig build() {
         if (this._description != null) {
            this._descriptions.add(this._description);
         }

         return new WadlGeneratorConfig.WadlGeneratorConfigImpl(this._descriptions);
      }
   }

   static class WadlGeneratorConfigImpl extends WadlGeneratorConfig {
      public List<WadlGeneratorDescription> _descriptions;

      public WadlGeneratorConfigImpl(List<WadlGeneratorDescription> descriptions) {
         this._descriptions = descriptions;
      }

      @Override
      public List<WadlGeneratorDescription> configure() {
         return this._descriptions;
      }
   }
}
