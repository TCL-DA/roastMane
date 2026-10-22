package org.eclipse.yasson.internal.deserializer;

import org.eclipse.yasson.internal.DeserializationContextImpl;

class DeferredDeserializer implements ModelDeserializer<Object> {
   private final ModelDeserializer<Object> delegate;

   DeferredDeserializer(ModelDeserializer<Object> delegate) {
      this.delegate = delegate;
   }

   @Override
   public Object deserialize(Object value, DeserializationContextImpl context) {
      context.getDeferredDeserializers().add(() -> this.delegate.deserialize(value, context));
      return value;
   }
}
