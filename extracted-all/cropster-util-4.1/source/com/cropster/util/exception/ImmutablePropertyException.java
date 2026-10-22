package com.cropster.util.exception;

import com.cropster.util.exception.errordetail.ImmutablePropertyErrorDetails;

public class ImmutablePropertyException extends MessageIdentifyingException {
   private static final long serialVersionUID = 1L;
   private final ImmutablePropertyErrorDetails immutablePropertyErrorDetails;

   public ImmutablePropertyException(Class<?> validatedType, String property) {
      this(null, validatedType, property);
   }

   public ImmutablePropertyException(String id, Class<?> validatedType, String property) {
      this.immutablePropertyErrorDetails = new ImmutablePropertyErrorDetails(id, validatedType, property);
   }

   public String getProperty() {
      return this.immutablePropertyErrorDetails.getProperty();
   }

   public Class<?> getValidatedType() {
      return this.immutablePropertyErrorDetails.getValidatedType();
   }

   @Override
   public String getCode() {
      return this.immutablePropertyErrorDetails.getCode();
   }

   @Override
   public String getMessage() {
      return this.immutablePropertyErrorDetails.toString();
   }
}
