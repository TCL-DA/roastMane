package org.eclipse.yasson.internal.deserializer.types;

class DoubleDeserializer extends AbstractNumberDeserializer<Double> {
   DoubleDeserializer(TypeDeserializerBuilder builder) {
      super(builder, false);
   }

   Double parseNumberValue(String value) {
      return Double.parseDouble(value);
   }
}
