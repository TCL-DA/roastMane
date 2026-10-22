package org.eclipse.yasson.internal.deserializer.types;

class ShortDeserializer extends AbstractNumberDeserializer<Short> {
   ShortDeserializer(TypeDeserializerBuilder builder) {
      super(builder, true);
   }

   Short parseNumberValue(String value) {
      return Short.parseShort(value);
   }
}
