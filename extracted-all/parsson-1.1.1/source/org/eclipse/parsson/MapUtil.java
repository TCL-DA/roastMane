package org.eclipse.parsson;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import org.eclipse.parsson.api.BufferPool;

public final class MapUtil {
   private MapUtil() {
   }

   static JsonValue handle(Object value, BufferPool bufferPool) {
      if (value == null) {
         return JsonValue.NULL;
      } else if (value instanceof JsonValue) {
         return (JsonValue)value;
      } else if (value instanceof JsonArrayBuilder) {
         return ((JsonArrayBuilder)value).build();
      } else if (value instanceof JsonObjectBuilder) {
         return ((JsonObjectBuilder)value).build();
      } else if (value instanceof BigDecimal) {
         return JsonNumberImpl.getJsonNumber((BigDecimal)value);
      } else if (value instanceof BigInteger) {
         return JsonNumberImpl.getJsonNumber((BigInteger)value);
      } else if (value instanceof Boolean) {
         Boolean b = (Boolean)value;
         return b ? JsonValue.TRUE : JsonValue.FALSE;
      } else if (value instanceof Double) {
         return JsonNumberImpl.getJsonNumber((Double)value);
      } else if (value instanceof Integer) {
         return JsonNumberImpl.getJsonNumber((Integer)value);
      } else if (value instanceof Long) {
         return JsonNumberImpl.getJsonNumber((Long)value);
      } else if (value instanceof String) {
         return new JsonStringImpl((String)value);
      } else if (value instanceof Collection) {
         Collection<?> collection = (Collection<?>)value;
         JsonArrayBuilder jsonArrayBuilder = new JsonArrayBuilderImpl(collection, bufferPool);
         return jsonArrayBuilder.build();
      } else if (value instanceof Map) {
         JsonObjectBuilder object = new JsonObjectBuilderImpl((Map<String, ?>)value, bufferPool);
         return object.build();
      } else {
         throw new IllegalArgumentException(String.format("Type %s is not supported.", value.getClass()));
      }
   }
}
