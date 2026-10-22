package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class CyclicReferenceDeserializer implements ModelDeserializer<JsonParser> {
   private final Type type;
   private ModelDeserializer<JsonParser> delegate;

   CyclicReferenceDeserializer(Type type) {
      this.type = type;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      if (this.delegate == null) {
         this.delegate = context.getJsonbContext().getChainModelCreator().deserializerChain(this.type);
      }

      return this.delegate.deserialize(value, context);
   }
}
