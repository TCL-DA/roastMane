package com.cropster.util.exception.errordetail;

public class InvalidRelationCustomMessage extends InvalidRelationErrorDetails {
   private final String message;

   public InvalidRelationCustomMessage(Object id, Class<?> validatedType, String relation, String message) {
      super(id, validatedType, relation);
      this.message = message;
   }

   @Override
   public String toString() {
      return super.toString() + " " + this.message;
   }
}
