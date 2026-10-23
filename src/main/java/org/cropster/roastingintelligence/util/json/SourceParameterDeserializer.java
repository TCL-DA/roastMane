package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceParameterDeserializer extends StdDeserializer<Map<String, Set<SourceParameter>>> {
   private static final long serialVersionUID = 4748802054729803626L;
   private final Logger logger = LoggerFactory.getLogger(SourceParameterDeserializer.class);

   public SourceParameterDeserializer() {
      super(Set.class);
   }

   public Map<String, Set<SourceParameter>> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Set<MeasurementSourceFactory> factories = RoastingIntelligenceApp.retrieveService(MeasurementSourceFactory.class);
      Map<String, Set<SourceParameter>> sourceConfiguration = new LinkedHashMap<>();

      while (jp.nextToken() != JsonToken.END_OBJECT) {
         String factoryName = jp.getCurrentName();
         Optional<MeasurementSourceFactory> factory = factories.stream().filter(f -> f.getClass().getSimpleName().equals(factoryName)).findFirst();
         Set<SourceParameter> parameters = new LinkedHashSet<>();
         if (factory.isPresent()) {
            sourceConfiguration.put(factoryName, parameters);
         } else {
            this.logger.warn("Ignoring unkown source factory {}", factoryName);
         }

         if (jp.nextToken() == JsonToken.START_OBJECT) {
            while (jp.nextToken() != JsonToken.END_OBJECT) {
               if (jp.getCurrentToken() == JsonToken.FIELD_NAME) {
                  String name = jp.getText();
                  String value = jp.nextTextValue();
                  SourceParameter param = factory.map(MeasurementSourceFactory::getParameters)
                     .orElse(Collections.emptySet())
                     .stream()
                     .filter(p -> p.getName().equals(name))
                     .findFirst()
                     .orElse(null);
                  if (param != null) {
                     param.setValue(value);
                     parameters.add(param);
                  }
               }
            }
         }
      }

      return sourceConfiguration;
   }
}
