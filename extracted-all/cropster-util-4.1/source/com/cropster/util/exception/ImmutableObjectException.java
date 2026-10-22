package com.cropster.util.exception;

public class ImmutableObjectException extends MessageIdentifyingException {
   private static final long serialVersionUID = 1L;
   public static final String CODE = "error.object.immutable";
   private final Class<?> validatedType;

   public ImmutableObjectException(Class<?> validatedType) {
      this.validatedType = validatedType;
   }

   @Override
   public String getCode() {
      return "error.object.immutable";
   }

   @Override
   public String getMessage() {
      return "This object is immutable and cannot be changed";
   }

   public Class<?> getValidatedType() {
      return this.validatedType;
   }
}
