package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import java.util.Collection;
import org.eclipse.yasson.internal.SerializationContextImpl;

class CollectionSerializer implements ModelSerializer {
   private final ModelSerializer delegate;

   CollectionSerializer(ModelSerializer delegate) {
      this.delegate = delegate;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      Collection<Object> collection = (Collection<Object>)value;
      generator.writeStartArray();
      collection.forEach(object -> this.delegate.serialize(object, generator, context));
      generator.writeEnd();
   }
}
