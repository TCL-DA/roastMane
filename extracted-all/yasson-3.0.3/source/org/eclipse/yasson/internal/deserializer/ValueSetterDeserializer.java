package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import java.lang.invoke.MethodHandle;
import java.util.Objects;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class ValueSetterDeserializer implements ModelDeserializer<Object> {
   private final MethodHandle valueSetter;

   ValueSetterDeserializer(MethodHandle valueSetter) {
      this.valueSetter = Objects.requireNonNull(valueSetter);
   }

   @Override
   public Object deserialize(Object value, DeserializationContextImpl context) {
      Object object = context.getInstance();

      try {
         this.valueSetter.invoke((Object)object, (Object)value);
         return value;
      } catch (Throwable var5) {
         throw new JsonbException("Error setting value on: " + object, var5);
      }
   }
}
