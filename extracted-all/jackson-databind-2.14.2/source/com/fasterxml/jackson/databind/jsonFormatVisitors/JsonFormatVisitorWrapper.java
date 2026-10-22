package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

public interface JsonFormatVisitorWrapper extends JsonFormatVisitorWithSerializerProvider {
   JsonObjectFormatVisitor expectObjectFormat(JavaType var1) throws JsonMappingException;

   JsonArrayFormatVisitor expectArrayFormat(JavaType var1) throws JsonMappingException;

   JsonStringFormatVisitor expectStringFormat(JavaType var1) throws JsonMappingException;

   JsonNumberFormatVisitor expectNumberFormat(JavaType var1) throws JsonMappingException;

   JsonIntegerFormatVisitor expectIntegerFormat(JavaType var1) throws JsonMappingException;

   JsonBooleanFormatVisitor expectBooleanFormat(JavaType var1) throws JsonMappingException;

   JsonNullFormatVisitor expectNullFormat(JavaType var1) throws JsonMappingException;

   JsonAnyFormatVisitor expectAnyFormat(JavaType var1) throws JsonMappingException;

   JsonMapFormatVisitor expectMapFormat(JavaType var1) throws JsonMappingException;

   public static class Base implements JsonFormatVisitorWrapper {
      protected SerializerProvider _provider;

      public Base() {
      }

      public Base(SerializerProvider p) {
         this._provider = p;
      }

      @Override
      public SerializerProvider getProvider() {
         return this._provider;
      }

      @Override
      public void setProvider(SerializerProvider p) {
         this._provider = p;
      }

      @Override
      public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
         return null;
      }

      @Override
      public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
         return null;
      }
   }
}
