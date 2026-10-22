package com.cropster.util.exception.errordetail;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class InvalidRelationErrorDetails implements ErrorDetails {
   public static final String CODE = "error.relationship.invalid";
   private final Object id;
   private final String relation;
   private final Object uninitialized = new Object();
   private final Class<?> validatedType;
   private Object value = this.uninitialized;
   private Object[] legalValues = null;

   public InvalidRelationErrorDetails(Object id, Class<?> validatedType, String relation) {
      this.id = id;
      this.validatedType = validatedType;
      this.relation = relation;
   }

   public InvalidRelationErrorDetails(Object id, Class<?> validatedType, String relation, Object value, Object[] legalValues) {
      this(id, validatedType, relation);
      this.value = value;
      this.legalValues = legalValues;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.relationship.invalid";
   }

   public String getRelation() {
      return this.relation;
   }

   public Object getValue() {
      return this.value;
   }

   @Override
   public Class<?> getValidatedType() {
      return this.validatedType;
   }

   @Override
   public String toString() {
      if (this.uninitialized.equals(this.value)) {
         return String.format("Relation '%s' is invalid.", this.relation);
      } else {
         String nullsafeValue = this.value == null ? "null" : this.value.toString();
         if (ArrayUtils.isEmpty(this.legalValues)) {
            return String.format("Value '%s' for relation '%s' is invalid.", nullsafeValue, this.relation);
         } else {
            String template = "Value '%s' for relation '%s' is invalid. Valid options are: %s.";
            String options = StringUtils.join(this.legalValues, ", ");
            return String.format(template, nullsafeValue, this.relation, options);
         }
      }
   }
}
