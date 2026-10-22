package com.cropster.util.exception.errordetail;

public class ForbiddenOperationErrorDetails implements ErrorDetails {
   public static final String CODE = "error.operation.forbidden";
   private final Class<?> objectType;
   private final String message;
   private final Object id;

   public ForbiddenOperationErrorDetails(Object id, Class<?> objectType, String message) {
      this.objectType = objectType;
      this.message = message;
      this.id = id;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.operation.forbidden";
   }

   @Override
   public String toString() {
      return this.message;
   }

   @Override
   public Class<?> getValidatedType() {
      return this.objectType;
   }
}
