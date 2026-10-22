package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.cropster.roastingintelligence.model.Measure;

public class MapAsNameValuesDeserializer extends StdDeserializer<Map<String, Object>> {
   private static final long serialVersionUID = 2357782234266221778L;

   public MapAsNameValuesDeserializer() {
      super(Map.class);
   }

   public Map<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt, Map<String, Object> intoValue) throws IOException, JsonProcessingException {
      MapType mapType = TypeFactory.defaultInstance().constructMapType(TreeMap.class, Long.class, Double.class);
      JsonDeserializer<Object> mapDeserializer = ctxt.findContextualValueDeserializer(mapType, null);
      JavaType measureType = TypeFactory.defaultInstance().constructType(Measure.class);
      JsonDeserializer<Object> measureDeserializer = ctxt.findContextualValueDeserializer(measureType, null);
      if (jp.getCurrentToken() == JsonToken.START_ARRAY) {
         while (jp.nextToken() == JsonToken.START_OBJECT) {
            String name = null;
            Object object = null;

            while (jp.nextValue() != JsonToken.END_OBJECT) {
               if ("name".equalsIgnoreCase(jp.getCurrentName())) {
                  name = jp.getText();
               } else if ("value".equalsIgnoreCase(jp.getCurrentName())) {
                  object = measureDeserializer.deserialize(jp, ctxt);
               } else if ("values".equalsIgnoreCase(jp.getCurrentName())) {
                  object = mapDeserializer.deserialize(jp, ctxt);
               }
            }

            intoValue.put(name, object);
         }
      }

      return intoValue;
   }

   public Map<String, Object> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      return this.deserialize(jp, ctxt, new LinkedHashMap<>());
   }
}
