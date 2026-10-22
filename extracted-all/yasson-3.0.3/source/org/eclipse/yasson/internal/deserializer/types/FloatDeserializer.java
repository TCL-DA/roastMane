package org.eclipse.yasson.internal.deserializer.types;

class FloatDeserializer extends AbstractNumberDeserializer<Float> {
   FloatDeserializer(TypeDeserializerBuilder builder) {
      super(builder, false);
   }

   Float parseNumberValue(String value) {
      return Float.parseFloat(value);
   }
}
