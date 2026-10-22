package org.eclipse.yasson.internal.serializer;

import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.SerializationContextImpl;

class NullVisibilitySwitcher implements ModelSerializer {
   private final boolean nullsEnabled;
   private final ModelSerializer delegate;

   NullVisibilitySwitcher(boolean nullsEnabled, ModelSerializer delegate) {
      this.nullsEnabled = nullsEnabled;
      this.delegate = delegate;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      boolean previous = context.isContainerWithNulls();
      context.setContainerWithNulls(this.nullsEnabled);
      this.delegate.serialize(value, generator, context);
      context.setContainerWithNulls(previous);
   }
}
