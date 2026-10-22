package com.cropster.util.exception.errordetail;

public class InvalidPropertyCustomMessage extends InvalidPropertyErrorDetails {
   private final String message;

   public InvalidPropertyCustomMessage(Class<?> validatedType, String property, String message) {
      this(null, validatedType, property, message);
   }

   public InvalidPropertyCustomMessage(Object id, Class<?> validatedType, String property, String message) {
      super(id, validatedType, property);
      this.message = message;
   }

   @Override
   public String toString() {
      return super.toString() + " " + this.message;
   }
}
