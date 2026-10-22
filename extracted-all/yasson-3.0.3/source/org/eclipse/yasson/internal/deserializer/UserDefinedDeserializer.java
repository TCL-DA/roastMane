package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.model.customization.Customization;

class UserDefinedDeserializer implements ModelDeserializer<JsonParser> {
   private final JsonbDeserializer<?> userDefinedDeserializer;
   private final ModelDeserializer<Object> delegate;
   private final Type rType;
   private final Customization customization;

   UserDefinedDeserializer(JsonbDeserializer<?> userDefinedDeserializer, ModelDeserializer<Object> delegate, Type rType, Customization customization) {
      this.userDefinedDeserializer = userDefinedDeserializer;
      this.delegate = delegate;
      this.rType = rType;
      this.customization = customization;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      DeserializationContextImpl newContext = new DeserializationContextImpl(context);
      newContext.setCustomization(this.customization);
      YassonParser yassonParser = new YassonParser(value, context.getLastValueEvent(), newContext);
      Object object = this.userDefinedDeserializer.deserialize(yassonParser, newContext, this.rType);
      yassonParser.skipRemaining();
      context.setLastValueEvent(newContext.getLastValueEvent());
      return this.delegate.deserialize(object, context);
   }
}
