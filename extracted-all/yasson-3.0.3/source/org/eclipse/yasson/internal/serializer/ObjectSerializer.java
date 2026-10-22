package org.eclipse.yasson.internal.serializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import java.util.LinkedHashMap;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class ObjectSerializer implements ModelSerializer {
   private final LinkedHashMap<String, ModelSerializer> propertySerializers;

   ObjectSerializer(LinkedHashMap<String, ModelSerializer> propertySerializers) {
      this.propertySerializers = propertySerializers;
   }

   @Override
   public void serialize(Object value, JsonGenerator generator, SerializationContextImpl context) {
      generator.writeStartObject();
      this.propertySerializers.forEach((key, serializer) -> {
         try {
            context.setKey(key);
            serializer.serialize(value, generator, context);
         } catch (Exception var6) {
            throw new JsonbException(Messages.getMessage(MessageKeys.SERIALIZE_PROPERTY_ERROR, key, value.getClass().getCanonicalName()), var6);
         }
      });
      generator.writeEnd();
   }
}
