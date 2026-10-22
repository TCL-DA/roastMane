package com.fasterxml.jackson.databind.jsonFormatVisitors;

import com.fasterxml.jackson.core.JsonParser.NumberType;

public interface JsonNumberFormatVisitor extends JsonValueFormatVisitor {
   void numberType(NumberType var1);

   public static class Base extends JsonValueFormatVisitor.Base implements JsonNumberFormatVisitor {
      @Override
      public void numberType(NumberType type) {
      }
   }
}
