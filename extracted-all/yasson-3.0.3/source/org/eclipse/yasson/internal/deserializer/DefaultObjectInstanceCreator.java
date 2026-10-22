package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import java.lang.reflect.Constructor;
import org.eclipse.yasson.internal.ClassMultiReleaseExtension;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.ReflectionUtils;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class DefaultObjectInstanceCreator implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> delegate;
   private final Constructor<?> defaultConstructor;
   private final JsonbException exception;

   DefaultObjectInstanceCreator(ModelDeserializer<JsonParser> delegate, Class<?> clazz, Constructor<?> defaultConstructor) {
      this.delegate = delegate;
      this.defaultConstructor = defaultConstructor;
      if (clazz.isInterface()) {
         this.exception = new JsonbException(Messages.getMessage(MessageKeys.INFER_TYPE_FOR_UNMARSHALL, clazz.getName()));
      } else if (defaultConstructor == null) {
         this.exception = ClassMultiReleaseExtension.exceptionToThrow(clazz)
            .orElse(new JsonbException(Messages.getMessage(MessageKeys.NO_DEFAULT_CONSTRUCTOR, clazz)));
      } else {
         this.exception = null;
      }
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      if (this.exception != null) {
         throw this.exception;
      } else {
         Object instance = ReflectionUtils.createNoArgConstructorInstance(this.defaultConstructor);
         context.setInstance(instance);
         return this.delegate.deserialize(value, context);
      }
   }
}
