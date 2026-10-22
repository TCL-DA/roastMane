package org.cropster.roastingintelligence.util.json;

import com.cropster.util.model.Unit;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import org.cropster.roastingintelligence.model.Curve;

public class CurveDeserializer extends StdDeserializer<Map<String, Curve>> {
   private static final long serialVersionUID = 4746567512001773153L;

   public CurveDeserializer() {
      super(Map.class);
   }

   public Map<String, Curve> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      MapType mapType = TypeFactory.defaultInstance().constructMapType(TreeMap.class, Long.class, Double.class);
      JsonDeserializer<Object> mapDeserializer = ctxt.findContextualValueDeserializer(mapType, null);
      if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
         return null;
      } else {
         Map<String, Curve> curves = new LinkedHashMap<>();

         while (jp.nextToken() == JsonToken.START_OBJECT) {
            String name = null;
            Unit unit = null;
            Map<Long, Double> values = null;

            while (jp.nextValue() != JsonToken.END_OBJECT) {
               if ("name".equalsIgnoreCase(jp.getCurrentName())) {
                  name = jp.getText();
               } else if ("unit".equalsIgnoreCase(jp.getCurrentName())) {
                  unit = Unit.valueOf(jp.getText());
               } else if ("values".equalsIgnoreCase(jp.getCurrentName())) {
                  values = (Map<Long, Double>)mapDeserializer.deserialize(jp, ctxt);
               }
            }

            Curve curve = new Curve(unit);
            curve.putAll(values);
            curves.put(name, curve);
         }

         return curves;
      }
   }
}
