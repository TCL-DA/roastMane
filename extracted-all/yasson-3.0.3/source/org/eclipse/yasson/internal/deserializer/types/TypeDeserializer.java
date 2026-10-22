package org.eclipse.yasson.internal.deserializer.types;

import jakarta.json.stream.JsonParser;
import java.lang.reflect.Type;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;

public abstract class TypeDeserializer implements ModelDeserializer<String> {
   private final ModelDeserializer<Object> delegate;
   private final Class<?> clazz;

   TypeDeserializer(TypeDeserializerBuilder builder) {
      this.delegate = builder.getDelegate();
      this.clazz = builder.getClazz();
   }

   public final Object deserialize(String value, DeserializationContextImpl context) {
      return this.delegate.deserialize(this.deserializeStringValue(value, context, this.clazz), context);
   }

   public final Object deserialize(boolean value, DeserializationContextImpl context) {
      return this.delegate.deserialize(this.deserializeBooleanValue(value, context, this.clazz), context);
   }

   public final Object deserialize(JsonParser value, DeserializationContextImpl context) {
      return this.delegate.deserialize(this.deserializeNumberValue(value, context, this.clazz), context);
   }

   abstract Object deserializeStringValue(String var1, DeserializationContextImpl var2, Type var3);

   Object deserializeBooleanValue(boolean value, DeserializationContextImpl context, Type rType) {
      return this.deserializeStringValue(String.valueOf(value), context, rType);
   }

   Object deserializeNumberValue(JsonParser value, DeserializationContextImpl context, Type rType) {
      return this.deserializeStringValue(value.getString(), context, rType);
   }

   Class<?> getType() {
      return this.clazz;
   }
}
