package org.eclipse.yasson.internal.serializer;

import jakarta.json.JsonValue;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonGenerator;
import java.math.BigDecimal;
import java.math.BigInteger;

class YassonGenerator implements JsonGenerator {
   private final JsonGenerator delegate;
   private int level;

   YassonGenerator(JsonGenerator delegate) {
      this.delegate = delegate;
   }

   public JsonGenerator writeStartObject() {
      this.writeValidate("writeStartObject()");
      this.level++;
      this.delegate.writeStartObject();
      return this;
   }

   public JsonGenerator writeStartObject(String name) {
      this.writeValidate("writeStartObject(String name)");
      this.level++;
      this.delegate.writeStartObject(name);
      return this;
   }

   public JsonGenerator writeKey(String name) {
      this.writeValidate("writeKey(String name)");
      this.delegate.writeKey(name);
      return this;
   }

   public JsonGenerator writeStartArray() {
      this.writeValidate("writeStartArray()");
      this.level++;
      this.delegate.writeStartArray();
      return this;
   }

   public JsonGenerator writeStartArray(String name) {
      this.writeValidate("writeStartArray(String name)");
      this.level++;
      this.delegate.writeStartArray(name);
      return this;
   }

   public JsonGenerator write(String name, JsonValue value) {
      this.writeValidate("write(String name, JsonValue value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, String value) {
      this.writeValidate("write(String name, String value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, BigInteger value) {
      this.writeValidate("write(String name, BigInteger value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, BigDecimal value) {
      this.writeValidate("write(String name, BigDecimal value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, int value) {
      this.writeValidate("write(String name, int value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, long value) {
      this.writeValidate("write(String name, long value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, double value) {
      this.writeValidate("write(String name, double value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator write(String name, boolean value) {
      this.writeValidate("write(String name, boolean value)");
      this.delegate.write(name, value);
      return this;
   }

   public JsonGenerator writeNull(String name) {
      this.writeValidate("writeNull(String name)");
      this.delegate.writeNull(name);
      return this;
   }

   public JsonGenerator writeEnd() {
      this.level--;
      if (this.level < 0) {
         throw new JsonbException("writeEnd() cannot be called outside of the scope of user generator.");
      } else {
         if (this.level == 0) {
            this.level--;
         }

         this.delegate.writeEnd();
         return this;
      }
   }

   public JsonGenerator write(JsonValue value) {
      this.writeValidate("write(JsonValue value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(String value) {
      this.writeValidate("write(String value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(BigDecimal value) {
      this.writeValidate("write(BigDecimal value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(BigInteger value) {
      this.writeValidate("write(BigInteger value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(int value) {
      this.writeValidate("write(int value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(long value) {
      this.writeValidate("write(long value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(double value) {
      this.writeValidate("write(double value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator write(boolean value) {
      this.writeValidate("write(boolean value)");
      this.delegate.write(value);
      return this;
   }

   public JsonGenerator writeNull() {
      this.writeValidate("writeNull()");
      this.delegate.writeNull();
      return this;
   }

   public void close() {
      throw new JsonbException("Unsupported operation in user defined serializer.");
   }

   public void flush() {
      throw new JsonbException("Unsupported operation in user defined serializer.");
   }

   private void writeValidate(String method) {
      if (this.level < 0) {
         throw new JsonbException(method + " cannot be called outside of the scope of user generator.");
      }
   }
}
