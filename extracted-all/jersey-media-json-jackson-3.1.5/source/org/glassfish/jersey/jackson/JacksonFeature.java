package org.glassfish.jersey.jackson;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.jackson.internal.DefaultJacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.FilteringJacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.JacksonFilteringFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.JsonMappingExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.JsonParseExceptionMapper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.message.MessageProperties;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;

public class JacksonFeature implements Feature {
   private final boolean registerExceptionMappers;
   private int maxStringLength = 20000000;
   private static final String JSON_FEATURE = JacksonFeature.class.getSimpleName();

   public JacksonFeature() {
      this(true);
   }

   private JacksonFeature(boolean registerExceptionMappers) {
      this.registerExceptionMappers = registerExceptionMappers;
   }

   public static JacksonFeature withExceptionMappers() {
      return new JacksonFeature();
   }

   public static JacksonFeature withoutExceptionMappers() {
      return new JacksonFeature(false);
   }

   public JacksonFeature maxStringLength(int maxStringLength) {
      this.maxStringLength = maxStringLength;
      return this;
   }

   public boolean configure(FeatureContext context) {
      Configuration config = context.getConfiguration();
      String jsonFeature = (String)CommonProperties.getValue(
         config.getProperties(), config.getRuntimeType(), "jersey.config.jsonFeature", JSON_FEATURE, String.class
      );
      if (!JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
         return false;
      } else {
         context.property(PropertiesHelper.getPropertyNameForRuntime("jersey.config.jsonFeature", config.getRuntimeType()), JSON_FEATURE);
         if (!config.isRegistered(JacksonJaxbJsonProvider.class)) {
            if (this.registerExceptionMappers) {
               context.register(JsonParseExceptionMapper.class);
               context.register(JsonMappingExceptionMapper.class);
            }

            if (EntityFilteringFeature.enabled(config)) {
               context.register(JacksonFilteringFeature.class);
               context.register(FilteringJacksonJaxbJsonProvider.class, new Class[]{MessageBodyReader.class, MessageBodyWriter.class});
            } else {
               context.register(DefaultJacksonJaxbJsonProvider.class, new Class[]{MessageBodyReader.class, MessageBodyWriter.class});
            }
         }

         if (config.getProperty(MessageProperties.JSON_MAX_STRING_LENGTH) == null) {
            context.property(MessageProperties.JSON_MAX_STRING_LENGTH, this.maxStringLength);
         }

         return true;
      }
   }
}
