package com.cropster.util.exception;

import com.cropster.util.exception.errordetail.InvalidPropertyErrorDetails;

public class InvalidPropertyException extends MessageIdentifyingException {
   private static final long serialVersionUID = 1L;
   private final InvalidPropertyErrorDetails invalidProperty;

   public InvalidPropertyException(Class<?> validatedType, String property) {
      this.invalidProperty = new InvalidPropertyErrorDetails(null, validatedType, property);
   }

   public InvalidPropertyException(Class<?> validatedType, String property, Object value) {
      this.invalidProperty = new InvalidPropertyErrorDetails(null, validatedType, property, value, null);
   }

   public InvalidPropertyException(Object id, Class<?> validatedType, String property) {
      this.invalidProperty = new InvalidPropertyErrorDetails(id, validatedType, property);
   }

   public InvalidPropertyException(Object id, Class<?> validatedType, String property, Object value, Object[] legalValues) {
      this.invalidProperty = new InvalidPropertyErrorDetails(id, validatedType, property, value, legalValues);
   }

   public String getProperty() {
      return this.invalidProperty.getProperty();
   }

   public Object getValue() {
      return this.invalidProperty.getValue();
   }

   @Override
   public String getCode() {
      return this.invalidProperty.getCode();
   }

   @Override
   public String getMessage() {
      return this.invalidProperty.toString();
   }

   public Class<?> getValidatedType() {
      return this.invalidProperty.getValidatedType();
   }
}
