package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonArray;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import java.math.BigDecimal;
import java.math.BigInteger;

class JsonArrayBuilder extends JsonStructureBuilder {
   private final jakarta.json.JsonArrayBuilder arrayBuilder;

   JsonArrayBuilder(JsonProvider provider) {
      this.arrayBuilder = provider.createArrayBuilder();
   }

   JsonArray build() {
      return this.arrayBuilder.build();
   }

   @Override
   void write(JsonValue value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(String value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(BigDecimal value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(BigInteger value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(int value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(long value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(double value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void write(boolean value) {
      this.arrayBuilder.add(value);
   }

   @Override
   void writeNull() {
      this.arrayBuilder.addNull();
   }

   @Override
   void put(JsonStructure structure) {
      this.arrayBuilder.add(structure);
   }
}
