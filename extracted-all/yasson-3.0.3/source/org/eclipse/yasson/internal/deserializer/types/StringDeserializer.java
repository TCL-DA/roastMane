package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class StringDeserializer extends TypeDeserializer {
   StringDeserializer(TypeDeserializerBuilder builder) {
      super(builder);
   }

   @Override
   public Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
      JsonbConfigProperties config = context.getJsonbContext().getConfigProperties();
      return this.checkIJson(value, config);
   }

   private String checkIJson(String value, JsonbConfigProperties config) {
      if (config.isStrictIJson()) {
         String newString = new String(value.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
         if (!newString.equals(value)) {
            throw new JsonbException(Messages.getMessage(MessageKeys.UNPAIRED_SURROGATE));
         }
      }

      return value;
   }
}
