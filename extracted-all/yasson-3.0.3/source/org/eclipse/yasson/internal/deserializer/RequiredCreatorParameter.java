package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class RequiredCreatorParameter implements ModelDeserializer<Object> {
   private final String parameterName;

   RequiredCreatorParameter(String parameterName) {
      this.parameterName = parameterName;
   }

   @Override
   public Object deserialize(Object value, DeserializationContextImpl context) {
      throw new JsonbException(Messages.getMessage(MessageKeys.JSONB_CREATOR_MISSING_PROPERTY, this.parameterName));
   }
}
