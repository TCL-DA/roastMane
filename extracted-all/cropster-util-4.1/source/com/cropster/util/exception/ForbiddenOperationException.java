package com.cropster.util.exception;

import com.cropster.util.exception.errordetail.ForbiddenOperationErrorDetails;

public class ForbiddenOperationException extends MessageIdentifyingException {
   private static final long serialVersionUID = 1L;
   private final ForbiddenOperationErrorDetails errorDetails;

   public ForbiddenOperationException(String id, Class<?> objectType, String message) {
      this.errorDetails = new ForbiddenOperationErrorDetails(id, objectType, message);
   }

   public ForbiddenOperationException(Class<?> objectType, String message) {
      this(null, objectType, message);
   }

   public ForbiddenOperationException(String message) {
      this(null, null, message);
   }

   public ForbiddenOperationException() {
      this(null, null, "The attempted operation was rejected");
   }

   @Override
   public String getCode() {
      return this.errorDetails.getCode();
   }

   @Override
   public String getMessage() {
      return this.errorDetails.toString();
   }

   public Class<?> getValidatedType() {
      return this.errorDetails.getValidatedType();
   }
}
