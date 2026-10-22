package com.cropster.util.exception.errordetail;

public class MissingPropertyErrorDetails implements ErrorDetails {
   public static final String CODE = "error.property.missing";
   private final Object id;
   private final String property;
   private final Class<?> validatedType;

   public MissingPropertyErrorDetails(Object id, Class<?> validatedType, String property) {
      this.id = id;
      this.property = property;
      this.validatedType = validatedType;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.property.missing";
   }

   public String getProperty() {
      return this.property;
   }

   @Override
   public String toString() {
      return String.format("Property '%s' is missing.", this.property);
   }

   @Override
   public Class<?> getValidatedType() {
      return this.validatedType;
   }
}
