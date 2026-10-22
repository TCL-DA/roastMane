package com.cropster.util.exception.errordetail;

public class ImmutablePropertyErrorDetails implements ErrorDetails {
   public static final String CODE = "error.property.immutable";
   private final Class<?> validatedType;
   private final String property;
   private final Object id;

   public ImmutablePropertyErrorDetails(Object id, Class<?> validatedType, String property) {
      this.property = property;
      this.validatedType = validatedType;
      this.id = id;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.property.immutable";
   }

   @Override
   public Class<?> getValidatedType() {
      return this.validatedType;
   }

   public String getProperty() {
      return this.property;
   }

   @Override
   public String toString() {
      return String.format("Property '%s' of object '%s' is immutable.", this.property, this.validatedType.getSimpleName());
   }
}
