package org.eclipse.yasson.internal;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParserFactory;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory;
import org.eclipse.yasson.internal.deserializer.DeserializationModelCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.internal.serializer.SerializationModelCreator;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

public class JsonbContext {
   private static final Logger LOGGER = Logger.getLogger(JsonbContext.class.getName());
   private final JsonbConfig jsonbConfig;
   private final MappingContext mappingContext;
   private final DeserializationModelCreator deserializationModelCreator;
   private final SerializationModelCreator serializationModelCreator;
   private final JsonbComponentInstanceCreator componentInstanceCreator;
   private final JsonProvider jsonProvider;
   private final JsonParserFactory jsonParserFactory;
   private final ComponentMatcher componentMatcher;
   private final AnnotationIntrospector annotationIntrospector;
   private final JsonbConfigProperties configProperties;

   public JsonbContext(JsonbConfig jsonbConfig, JsonProvider jsonProvider) {
      Objects.requireNonNull(jsonbConfig);
      this.jsonbConfig = jsonbConfig;
      this.mappingContext = new MappingContext(this);
      this.componentInstanceCreator = this.initComponentInstanceCreator();
      this.componentMatcher = new ComponentMatcher(this);
      this.annotationIntrospector = new AnnotationIntrospector(this);
      this.jsonProvider = jsonProvider;
      this.jsonParserFactory = this.initJsonParserFactory();
      this.configProperties = new JsonbConfigProperties(jsonbConfig);
      this.deserializationModelCreator = new DeserializationModelCreator(this);
      this.serializationModelCreator = new SerializationModelCreator(this);
   }

   public JsonbConfig getConfig() {
      return this.jsonbConfig;
   }

   public MappingContext getMappingContext() {
      return this.mappingContext;
   }

   public DeserializationModelCreator getChainModelCreator() {
      return this.deserializationModelCreator;
   }

   public SerializationModelCreator getSerializationModelCreator() {
      return this.serializationModelCreator;
   }

   public JsonProvider getJsonProvider() {
      return this.jsonProvider;
   }

   public JsonbComponentInstanceCreator getComponentInstanceCreator() {
      return this.componentInstanceCreator;
   }

   public ComponentMatcher getComponentMatcher() {
      return this.componentMatcher;
   }

   public AnnotationIntrospector getAnnotationIntrospector() {
      return this.annotationIntrospector;
   }

   public JsonbConfigProperties getConfigProperties() {
      return this.configProperties;
   }

   public JsonParserFactory getJsonParserFactory() {
      return this.jsonParserFactory;
   }

   private JsonParserFactory initJsonParserFactory() {
      return this.jsonProvider.createParserFactory(this.createJsonpProperties(this.jsonbConfig));
   }

   protected Map<String, ?> createJsonpProperties(JsonbConfig jsonbConfig) {
      Optional<Object> property = jsonbConfig.getProperty("jsonb.formatting");
      Map<String, Object> factoryProperties = new HashMap<>();
      if (property.isPresent()) {
         Object value = property.get();
         if (!(value instanceof Boolean)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CONFIG_FORMATTING_ILLEGAL_VALUE));
         } else {
            if ((Boolean)value) {
               factoryProperties.put("jakarta.json.stream.JsonGenerator.prettyPrinting", Boolean.TRUE);
            }

            return factoryProperties;
         }
      } else {
         return factoryProperties;
      }
   }

   private JsonbComponentInstanceCreator initComponentInstanceCreator() {
      ServiceLoader<JsonbComponentInstanceCreator> loader = AccessController.doPrivileged(
         (PrivilegedAction<ServiceLoader<JsonbComponentInstanceCreator>>)(() -> ServiceLoader.load(JsonbComponentInstanceCreator.class))
      );
      List<JsonbComponentInstanceCreator> creators = new ArrayList<>();

      for (JsonbComponentInstanceCreator creator : loader) {
         creators.add(creator);
      }

      if (creators.isEmpty()) {
         return JsonbComponentInstanceCreatorFactory.getComponentInstanceCreator();
      } else {
         creators.sort(Comparator.comparingInt(JsonbComponentInstanceCreator::getPriority).reversed());
         JsonbComponentInstanceCreator creator = creators.get(0);
         LOGGER.finest("Component instance creator:" + creator.getClass());
         return creator;
      }
   }
}
