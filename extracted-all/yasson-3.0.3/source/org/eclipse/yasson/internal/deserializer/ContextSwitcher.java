package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class ContextSwitcher implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<Object> delegate;
   private final ModelDeserializer<JsonParser> modelDeserializer;

   ContextSwitcher(ModelDeserializer<Object> delegate, ModelDeserializer<JsonParser> modelDeserializer) {
      this.delegate = delegate;
      this.modelDeserializer = modelDeserializer;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      DeserializationContextImpl ctx = new DeserializationContextImpl(context);
      Object returnedValue = this.delegate.deserialize(this.modelDeserializer.deserialize(value, ctx), context);
      context.setLastValueEvent(ctx.getLastValueEvent());
      return returnedValue;
   }
}
