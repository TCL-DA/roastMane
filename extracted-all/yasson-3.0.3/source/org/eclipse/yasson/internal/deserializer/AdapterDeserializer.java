package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class AdapterDeserializer implements ModelDeserializer<Object> {
   private final JsonbAdapter<Object, Object> adapter;
   private final AdapterBinding adapterBinding;
   private final ModelDeserializer<Object> delegate;

   AdapterDeserializer(AdapterBinding adapterBinding, ModelDeserializer<Object> delegate) {
      this.adapterBinding = adapterBinding;
      this.adapter = (JsonbAdapter<Object, Object>)adapterBinding.getAdapter();
      this.delegate = delegate;
   }

   @Override
   public Object deserialize(Object value, DeserializationContextImpl context) {
      try {
         return this.delegate.deserialize(this.adapter.adaptFromJson(value), context);
      } catch (Exception var4) {
         throw new JsonbException(
            Messages.getMessage(
               MessageKeys.ADAPTER_EXCEPTION,
               this.adapterBinding.getBindingType(),
               this.adapterBinding.getToType(),
               this.adapterBinding.getAdapter().getClass()
            ),
            var4
         );
      }
   }
}
