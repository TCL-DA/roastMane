package org.eclipse.yasson.internal.jsonstructure;

import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Deque;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

public class JsonGeneratorToStructureAdapter implements JsonGenerator {
   private final Deque<JsonStructureBuilder> builders = new ArrayDeque<>();
   private JsonStructure root;
   private final JsonProvider provider;

   public JsonGeneratorToStructureAdapter(JsonProvider provider) {
      this.provider = provider;
   }

   public JsonGenerator writeStartObject() {
      this.builders.push(new JsonObjectBuilder(this.provider));
      return this;
   }

   public JsonGenerator writeStartObject(String name) {
      this.getJsonObjectBuilder(name).writeKey(name);
      this.builders.push(new JsonObjectBuilder(this.provider));
      return this;
   }

   public JsonGenerator writeKey(String name) {
      this.getJsonObjectBuilder(name).writeKey(name);
      return this;
   }

   public JsonGenerator writeStartArray() {
      this.builders.push(new JsonArrayBuilder(this.provider));
      return this;
   }

   public JsonGenerator writeStartArray(String name) {
      this.getJsonObjectBuilder(name).writeKey(name);
      this.builders.push(new JsonArrayBuilder(this.provider));
      return this;
   }

   public JsonGenerator write(String name, JsonValue value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, String value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, BigInteger value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, BigDecimal value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, int value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, long value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, double value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   public JsonGenerator write(String name, boolean value) {
      this.getJsonObjectBuilder(name).write(name, value);
      return this;
   }

   private JsonObjectBuilder getJsonObjectBuilder(String keyName) {
      JsonStructureBuilder current = this.builders.peek();
      if (!(current instanceof JsonObjectBuilder)) {
         throw new JsonbException(
            Messages.getMessage(
               MessageKeys.INTERNAL_ERROR,
               "Can't write key ["
                  + keyName
                  + "] into "
                  + current.getClass()
                  + "because "
                  + current.getClass()
                  + " is not an instance of "
                  + JsonObjectBuilder.class
            )
         );
      } else {
         return (JsonObjectBuilder)current;
      }
   }

   public JsonGenerator writeNull(String name) {
      this.getJsonObjectBuilder(name).writeNull(name);
      return this;
   }

   public JsonGenerator writeEnd() {
      JsonStructureBuilder builder = this.builders.pop();
      JsonStructure structure = builder.build();
      if (this.builders.isEmpty()) {
         this.root = structure;
      } else {
         this.builders.peek().put(structure);
      }

      return this;
   }

   public JsonGenerator write(JsonValue value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(String value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(BigDecimal value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(BigInteger value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(int value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(long value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(double value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator write(boolean value) {
      this.builders.peek().write(value);
      return this;
   }

   public JsonGenerator writeNull() {
      this.builders.peek().writeNull();
      return this;
   }

   public void close() {
   }

   public void flush() {
   }

   public JsonStructure getRootStructure() {
      return this.root;
   }
}
