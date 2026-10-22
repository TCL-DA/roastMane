package org.eclipse.yasson.internal.components;

import jakarta.json.bind.serializer.JsonbDeserializer;
import java.lang.reflect.Type;

public class DeserializerBinding<T> extends AbstractComponentBinding {
   private final JsonbDeserializer<T> jsonbDeserializer;

   public DeserializerBinding(Type bindingType, JsonbDeserializer<T> jsonbDeserializer) {
      super(bindingType);
      this.jsonbDeserializer = jsonbDeserializer;
   }

   public JsonbDeserializer<T> getJsonbDeserializer() {
      return this.jsonbDeserializer;
   }

   @Override
   public Class<?> getComponentClass() {
      return this.jsonbDeserializer.getClass();
   }
}
