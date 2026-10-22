package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.ext.Providers;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.jackson.LocalizationMessages;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JsonEndpointConfig;
import org.glassfish.jersey.message.MessageProperties;

@Singleton
public class DefaultJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {
   private Configuration commonConfig;
   private static final Logger LOGGER = Logger.getLogger(DefaultJacksonJaxbJsonProvider.class.getName());
   private static final String[] EXCLUDE_MODULE_NAMES = new String[]{"JaxbAnnotationModule", "JakartaXmlBindAnnotationModule"};

   @Inject
   public DefaultJacksonJaxbJsonProvider(@Context Providers providers, @Context Configuration config) {
      super(new JacksonMapperConfigurator(null, DEFAULT_ANNOTATIONS));
      this.commonConfig = config;
      this._providers = providers;
   }

   public DefaultJacksonJaxbJsonProvider() {
      super(new JacksonMapperConfigurator(null, DEFAULT_ANNOTATIONS));
   }

   public DefaultJacksonJaxbJsonProvider(Providers providers, Configuration config, Annotations... annotationsToUse) {
      super(new JacksonMapperConfigurator(null, annotationsToUse));
      this.commonConfig = config;
      this._providers = providers;
   }

   @Override
   protected JsonEndpointConfig _configForReading(ObjectReader reader, Annotation[] annotations) {
      try {
         this.updateFactoryConstraints(reader.getFactory());
      } catch (Throwable var4) {
         LOGGER.warning(LocalizationMessages.ERROR_JACKSON_STREAMREADCONSTRAINTS(var4.getMessage()));
      }

      return super._configForReading(reader, annotations);
   }

   @PostConstruct
   private void findAndRegisterModules() {
      ObjectMapper defaultMapper = this._mapperConfig.getDefaultMapper();
      ObjectMapper mapper = this._mapperConfig.getConfiguredMapper();
      List<Module> modules = this.filterModules();
      defaultMapper.registerModules(modules);
      if (mapper != null) {
         mapper.registerModules(modules);
      }
   }

   private List<Module> filterModules() {
      String disabledModules = (String)CommonProperties.getValue(
         this.commonConfig.getProperties(), this.commonConfig.getRuntimeType(), "jersey.config.json.jackson.disabled.modules", String.class
      );
      String enabledModules = (String)CommonProperties.getValue(
         this.commonConfig.getProperties(), this.commonConfig.getRuntimeType(), "jersey.config.json.jackson.enabled.modules", String.class
      );
      List<Module> modules = ObjectMapper.findModules();

      for (String exludeModuleName : EXCLUDE_MODULE_NAMES) {
         modules.removeIf(mod -> mod.getModuleName().contains(exludeModuleName));
      }

      if (enabledModules != null && !enabledModules.isEmpty()) {
         List<String> enabledModulesList = Arrays.asList(enabledModules.split(","));
         modules.removeIf(mod -> !enabledModulesList.contains(mod.getModuleName()));
      } else if (disabledModules != null && !disabledModules.isEmpty()) {
         List<String> disabledModulesList = Arrays.asList(disabledModules.split(","));
         modules.removeIf(mod -> disabledModulesList.contains(mod.getModuleName()));
      }

      return modules;
   }

   private void updateFactoryConstraints(JsonFactory jsonFactory) {
      Object maxStringLengthObject = this.commonConfig.getProperty(MessageProperties.JSON_MAX_STRING_LENGTH);
      Integer maxStringLength = (Integer)PropertiesHelper.convertValue(maxStringLengthObject, Integer.class);
      if (maxStringLength != 20000000) {
         StreamReadConstraints constraints = jsonFactory.streamReadConstraints();
         jsonFactory.setStreamReadConstraints(
            StreamReadConstraints.builder()
               .maxStringLength(maxStringLength)
               .maxNestingDepth(constraints.getMaxNestingDepth())
               .maxNumberLength(constraints.getMaxNumberLength())
               .build()
         );
      }
   }
}
