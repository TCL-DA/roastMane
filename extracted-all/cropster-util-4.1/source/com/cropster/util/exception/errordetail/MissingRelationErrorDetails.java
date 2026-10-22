package com.cropster.util.exception.errordetail;

public class MissingRelationErrorDetails implements ErrorDetails {
   public static final String CODE = "error.relation.missing";
   private final Object id;
   private final String relation;
   private final Class<?> validatedType;

   public MissingRelationErrorDetails(Object id, Class<?> validatedType, String relation) {
      this.id = id;
      this.relation = relation;
      this.validatedType = validatedType;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.relation.missing";
   }

   public String getRelation() {
      return this.relation;
   }

   @Override
   public String toString() {
      return String.format("Relationship '%s' is missing.", this.relation);
   }

   @Override
   public Class<?> getValidatedType() {
      return this.validatedType;
   }
}
