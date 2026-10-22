package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.SerializationContextImpl;

class CyclicReferenceSerializer implements ModelSerializer {
   private final Type type;
   private ModelSerializer delegate;

   CyclicReferenceSerializer(Type type) {
      this.type = type;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      if (this.delegate == null) {
         this.delegate = context.getJsonbContext().getSerializationModelCreator().serializerChain(this.type, true, true);
      }

      this.delegate.serialize(value, generator, context);
   }
}
