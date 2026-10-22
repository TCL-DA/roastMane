package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class JsonObjectBuilder extends JsonStructureBuilder {
   private final jakarta.json.JsonObjectBuilder builder;
   private String nextKey;

   JsonObjectBuilder(JsonProvider provider) {
      this.builder = provider.createObjectBuilder();
   }

   @Override
   JsonStructure build() {
      return this.builder.build();
   }

   @Override
   void put(JsonStructure structure) {
      this.builder.add(this.getNextKey(), structure);
   }

   void put(String name, JsonStructure structure) {
      this.builder.add(name, structure);
   }

   @Override
   void write(JsonValue value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(String value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(BigDecimal value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(BigInteger value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(int value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(long value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(double value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void write(boolean value) {
      this.builder.add(this.getNextKey(), value);
   }

   @Override
   void writeNull() {
      this.builder.addNull(this.getNextKey());
   }

   void write(String name, JsonValue value) {
      this.builder.add(name, value);
   }

   void write(String name, String value) {
      this.builder.add(name, value);
   }

   void write(String name, BigDecimal value) {
      this.builder.add(name, value);
   }

   void write(String name, BigInteger value) {
      this.builder.add(name, value);
   }

   void write(String name, int value) {
      this.builder.add(name, value);
   }

   void write(String name, long value) {
      this.builder.add(name, value);
   }

   void write(String name, double value) {
      this.builder.add(name, value);
   }

   void write(String name, boolean value) {
      this.builder.add(name, value);
   }

   void writeNull(String name) {
      this.builder.addNull(name);
   }

   void writeKey(String key) {
      this.nextKey = key;
   }

   private String getNextKey() {
      if (this.nextKey == null) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, "Can't write a value without key name"));
      } else {
         String key = this.nextKey;
         this.nextKey = null;
         return key;
      }
   }
}
