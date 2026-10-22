package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.stream.JsonGenerator;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class AdapterSerializer extends AbstractSerializer {
   private final JsonbAdapter<Object, Object> adapter;
   private final AdapterBinding adapterBinding;

   AdapterSerializer(AdapterBinding adapterBinding, ModelSerializer delegate) {
      super(delegate);
      this.adapter = (JsonbAdapter<Object, Object>)adapterBinding.getAdapter();
      this.adapterBinding = adapterBinding;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      try {
         this.delegate.serialize(this.adapter.adaptToJson(value), generator, context);
      } catch (Exception var5) {
         throw new JsonbException(
            Messages.getMessage(MessageKeys.ADAPTER_EXCEPTION, this.adapterBinding.getBindingType(), this.adapterBinding.getToType(), this.adapter.getClass()),
            var5
         );
      }
   }
}
