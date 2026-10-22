package com.cropster.util.exception.errordetail;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class InvalidPropertyErrorDetails implements ErrorDetails {
   public static final String CODE = "error.property.invalid";
   private final Object id;
   private final String property;
   private final Object uninitialized = new Object();
   private final Class<?> validatedType;
   private Object value = this.uninitialized;
   private Object[] legalValues = null;

   public InvalidPropertyErrorDetails(Object id, Class<?> validatedType, String property) {
      this.id = id;
      this.property = property;
      this.validatedType = validatedType;
   }

   public InvalidPropertyErrorDetails(Object id, Class<?> validatedType, String property, Object value, Object[] legalValues) {
      this(id, validatedType, property);
      this.value = value;
      this.legalValues = legalValues;
   }

   @Override
   public Object getId() {
      return this.id;
   }

   @Override
   public String getCode() {
      return "error.property.invalid";
   }

   public String getProperty() {
      return this.property;
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
         return String.format("Property '%s' is invalid.", this.property);
      } else {
         String nullsafeValue = this.value == null ? "null" : this.value.toString();
         if (ArrayUtils.isEmpty(this.legalValues)) {
            return String.format("Value '%s' for property '%s' is invalid.", nullsafeValue, this.property);
         } else {
            String template = "Value '%s' for property '%s' is invalid. Valid options are: %s.";
            String options = StringUtils.join(this.legalValues, ", ");
            return String.format(template, nullsafeValue, this.property, options);
         }
      }
   }
}
