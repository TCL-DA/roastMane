package org.eclipse.yasson.internal.components;

import jakarta.json.bind.serializer.JsonbSerializer;
import java.lang.reflect.Type;

public class SerializerBinding<T> extends AbstractComponentBinding {
   private final JsonbSerializer<T> jsonbSerializer;

   public SerializerBinding(Type bindingType, JsonbSerializer<T> jsonbSerializer) {
      super(bindingType);
      this.jsonbSerializer = jsonbSerializer;
   }

   public JsonbSerializer<T> getJsonbSerializer() {
      return this.jsonbSerializer;
   }

   @Override
   public Class<?> getComponentClass() {
      return this.jsonbSerializer.getClass();
   }
}
