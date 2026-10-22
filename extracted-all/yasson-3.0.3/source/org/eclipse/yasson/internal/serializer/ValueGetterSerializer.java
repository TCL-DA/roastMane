package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import java.lang.invoke.MethodHandle;
import org.eclipse.yasson.internal.SerializationContextImpl;

class ValueGetterSerializer implements ModelSerializer {
   private final MethodHandle valueGetter;
   private final ModelSerializer delegate;

   ValueGetterSerializer(MethodHandle valueGetter, ModelSerializer delegate) {
      this.valueGetter = valueGetter;
      this.delegate = delegate;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      Object object;
      try {
         object = (Object)this.valueGetter.invoke((Object)value);
      } catch (Throwable var6) {
         throw new JsonbException("Error getting value on: " + value.getClass().getName(), var6);
      }

      this.delegate.serialize(object, generator, context);
   }
}
