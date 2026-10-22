package org.eclipse.yasson.internal.deserializer.types;

class ByteDeserializer extends AbstractNumberDeserializer<Byte> {
   ByteDeserializer(TypeDeserializerBuilder builder) {
      super(builder, true);
   }

   Byte parseNumberValue(String value) {
      return Byte.parseByte(value);
   }
}
