package com.cropster.util.exception;

import com.cropster.util.exception.errordetail.InvalidRelationErrorDetails;

public class InvalidRelationshipException extends MessageIdentifyingException {
   private static final long serialVersionUID = 1L;
   private final Object id;
   private final InvalidRelationErrorDetails invalidRelationship;

   public InvalidRelationshipException(Class<?> validatedType, String relation) {
      this(null, validatedType, relation);
   }

   public InvalidRelationshipException(Object id, Class<?> validatedType, String relation) {
      this.id = id;
      this.invalidRelationship = new InvalidRelationErrorDetails(id, validatedType, relation);
   }

   public InvalidRelationshipException(Object id, Class<?> validatedType, String relation, Object value, Object[] legalValues) {
      this.id = id;
      this.invalidRelationship = new InvalidRelationErrorDetails(id, validatedType, relation, value, legalValues);
   }

   @Override
   public Object getId() {
      return this.id;
   }

   public String getRelationship() {
      return this.invalidRelationship.getRelation();
   }

   public Object getValue() {
      return this.invalidRelationship.getValue();
   }

   @Override
   public String getCode() {
      return this.invalidRelationship.getCode();
   }

   @Override
   public String getMessage() {
      return this.invalidRelationship.toString();
   }

   public Class<?> getValidatedType() {
      return this.invalidRelationship.getValidatedType();
   }
}
