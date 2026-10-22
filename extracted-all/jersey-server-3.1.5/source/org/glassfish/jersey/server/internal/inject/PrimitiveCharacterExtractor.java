package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class PrimitiveCharacterExtractor implements MultivaluedParameterExtractor<Object> {
   final String parameter;
   final String defaultStringValue;
   final Object defaultPrimitiveTypeValue;

   public PrimitiveCharacterExtractor(String parameter, String defaultStringValue, Object defaultPrimitiveTypeValue) {
      this.parameter = parameter;
      this.defaultStringValue = defaultStringValue;
      this.defaultPrimitiveTypeValue = defaultPrimitiveTypeValue;
   }

   @Override
   public String getName() {
      return this.parameter;
   }

   @Override
   public String getDefaultValueString() {
      return this.defaultStringValue;
   }

   @Override
   public Object extract(MultivaluedMap<String, String> parameters) {
      String v = (String)parameters.getFirst(this.parameter);
      if (v != null && !v.trim().isEmpty()) {
         if (v.length() == 1) {
            return v.charAt(0);
         } else {
            throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(v));
         }
      } else if (this.defaultStringValue != null && !this.defaultStringValue.trim().isEmpty()) {
         if (this.defaultStringValue.length() == 1) {
            return this.defaultStringValue.charAt(0);
         } else {
            throw new ExtractorException(LocalizationMessages.ERROR_PARAMETER_INVALID_CHAR_VALUE(this.defaultStringValue));
         }
      } else {
         return this.defaultPrimitiveTypeValue;
      }
   }
}
