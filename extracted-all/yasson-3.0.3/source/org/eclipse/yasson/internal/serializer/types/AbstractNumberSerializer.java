package org.eclipse.yasson.internal.serializer.types;

import jakarta.json.stream.JsonGenerator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.model.customization.Customization;
import org.eclipse.yasson.internal.serializer.ModelSerializer;

abstract class AbstractNumberSerializer<T> extends TypeSerializer<T> {
   private final ModelSerializer actualSerializer;

   AbstractNumberSerializer(TypeSerializerBuilder builder) {
      super(builder);
      this.actualSerializer = this.actualSerializer(builder.getCustomization(), builder.getJsonbContext());
   }

   private ModelSerializer actualSerializer(Customization customization, JsonbContext jsonbContext) {
      JsonbNumberFormatter formatter = customization.getSerializeNumberFormatter();
      if (formatter == null) {
         return (value, generator, context) -> this.writeValue((T)value, generator);
      } else {
         NumberFormat format = NumberFormat.getInstance(jsonbContext.getConfigProperties().getLocale(formatter.getLocale()));
         ((DecimalFormat)format).applyPattern(formatter.getFormat());
         return (value, generator, context) -> generator.write(format.format(value));
      }
   }

   @Override
   void serializeValue(T value, JsonGenerator generator, SerializationContextImpl context) {
      this.actualSerializer.serialize(value, generator, context);
   }

   abstract void writeValue(T var1, JsonGenerator var2);
}
