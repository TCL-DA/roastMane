package com.cropster.util.exception;

import com.cropster.util.exception.errordetail.ErrorDetails;
import com.cropster.util.exception.errordetail.ImmutablePropertyErrorDetails;
import com.cropster.util.exception.errordetail.InvalidPropertyCustomMessage;
import com.cropster.util.exception.errordetail.InvalidPropertyErrorDetails;
import com.cropster.util.exception.errordetail.InvalidRelationCustomMessage;
import com.cropster.util.exception.errordetail.InvalidRelationErrorDetails;
import com.cropster.util.exception.errordetail.MissingPropertyErrorDetails;
import com.cropster.util.exception.errordetail.MissingRelationErrorDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;

public final class DataValidationException extends IllegalArgumentException {
   private static final long serialVersionUID = 1L;
   private final DataValidationException.DataValidator dataValidator;

   private DataValidationException(DataValidationException.DataValidator dataValidator) {
      super(dataValidator.getSummary());
      this.dataValidator = dataValidator;
   }

   public Collection<MissingRelationErrorDetails> getMissingRelations() {
      return this.dataValidator.missingRelations;
   }

   public Collection<InvalidPropertyErrorDetails> getInvalidProperties() {
      return this.dataValidator.invalidProperties;
   }

   public Collection<ImmutablePropertyErrorDetails> getImmutableProperties() {
      return this.dataValidator.immutableProperties;
   }

   public Collection<InvalidRelationErrorDetails> getInvalidRelations() {
      return this.dataValidator.invalidRelations;
   }

   public Collection<MissingPropertyErrorDetails> getMissingProperties() {
      return this.dataValidator.missingProperties;
   }

   public Collection<ErrorDetails> getAllErrors() {
      ArrayList<ErrorDetails> all = new ArrayList<>();
      all.addAll(this.getMissingProperties());
      all.addAll(this.getInvalidProperties());
      all.addAll(this.getImmutableProperties());
      all.addAll(this.getMissingRelations());
      all.addAll(this.getInvalidRelations());
      return all;
   }

   public Class<?> getValidatedType() {
      return this.dataValidator.getValidatedType();
   }

   public static final class DataValidator {
      private Object id;
      private final Class<?> validatedType;
      private final HashSet<MissingRelationErrorDetails> missingRelations = new HashSet<>();
      private final HashSet<InvalidRelationErrorDetails> invalidRelations = new HashSet<>();
      private final HashSet<MissingPropertyErrorDetails> missingProperties = new HashSet<>();
      private final HashSet<InvalidPropertyErrorDetails> invalidProperties = new HashSet<>();
      private final HashSet<ImmutablePropertyErrorDetails> immutableProperties = new HashSet<>();

      private DataValidator(Class<?> validatedType) {
         this.validatedType = validatedType;
      }

      public static DataValidationException.DataValidator forType(Class<?> validatedType) {
         return new DataValidationException.DataValidator(validatedType);
      }

      private String uncap(String s) {
         return StringUtils.uncapitalize(s);
      }

      public DataValidationException.DataValidator id(Object id) {
         this.id = id;
         return this;
      }

      public DataValidationException.DataValidator addMissingRelation(String relation) {
         this.missingRelations.add(new MissingRelationErrorDetails(this.id, this.validatedType, this.uncap(relation)));
         return this;
      }

      public DataValidationException.DataValidator addInvalidRelation(String relation) {
         this.invalidRelations.add(new InvalidRelationErrorDetails(this.id, this.validatedType, this.uncap(relation)));
         return this;
      }

      public DataValidationException.DataValidator addInvalidRelation(Class<?> relationshipClass) {
         return this.addInvalidRelation(relationshipClass.getSimpleName());
      }

      public DataValidationException.DataValidator addInvalidRelation(String relation, Object value) {
         this.invalidRelations.add(new InvalidRelationErrorDetails(this.id, this.validatedType, this.uncap(relation), value, null));
         return this;
      }

      public DataValidationException.DataValidator addInvalidRelation(Class<?> relationClass, Object value) {
         return this.addInvalidRelation(relationClass.getSimpleName(), value);
      }

      public DataValidationException.DataValidator addInvalidRelation(String relation, Object value, Object[] legalValues) {
         this.invalidRelations.add(new InvalidRelationErrorDetails(this.id, this.validatedType, relation, value, legalValues));
         return this;
      }

      public DataValidationException.DataValidator addInvalidRelation(Class<?> relationClass, Object value, Object[] legalValues) {
         return this.addInvalidRelation(relationClass.getSimpleName(), value, legalValues);
      }

      public DataValidationException.DataValidator addMissingRelation(Class<?> clazz) {
         return this.addMissingRelation(clazz.getSimpleName());
      }

      public DataValidationException.DataValidator addInvalidRelationCustomMessage(String relation, String message) {
         this.invalidRelations.add(new InvalidRelationCustomMessage(this.id, this.validatedType, this.uncap(relation), message));
         return this;
      }

      public DataValidationException.DataValidator addInvalidRelationCustomMessage(Class<?> clazz, String message) {
         this.invalidRelations.add(new InvalidRelationCustomMessage(this.id, this.validatedType, this.uncap(clazz.getSimpleName()), message));
         return this;
      }

      public DataValidationException.DataValidator addMissingProperty(String property) {
         this.missingProperties.add(new MissingPropertyErrorDetails(this.id, this.validatedType, property));
         return this;
      }

      public DataValidationException.DataValidator addInvalidProperty(String property, Object value) {
         this.invalidProperties.add(new InvalidPropertyErrorDetails(this.id, this.validatedType, property, value, null));
         return this;
      }

      public DataValidationException.DataValidator addInvalidProperty(String property) {
         this.invalidProperties.add(new InvalidPropertyErrorDetails(this.id, this.validatedType, property));
         return this;
      }

      public DataValidationException.DataValidator addInvalidProperty(String property, Object value, Object[] legalValues) {
         this.invalidProperties.add(new InvalidPropertyErrorDetails(this.id, this.validatedType, property, value, legalValues));
         return this;
      }

      public DataValidationException.DataValidator addInvalidPropertyCustomMessage(String property, String message) {
         this.invalidProperties.add(new InvalidPropertyCustomMessage(this.id, this.validatedType, property, message));
         return this;
      }

      public DataValidationException.DataValidator addImmutableProperty(String property) {
         this.immutableProperties.add(new ImmutablePropertyErrorDetails(this.id, this.validatedType, property));
         return this;
      }

      public Class<?> getValidatedType() {
         return this.validatedType;
      }

      private void appendMissingPropertyInfo(StringBuilder sb) {
         if (!this.missingProperties.isEmpty()) {
            sb.append("Missing properties: ");
         }

         Iterator<MissingPropertyErrorDetails> iter = this.missingProperties.iterator();

         while (iter.hasNext()) {
            MissingPropertyErrorDetails mp = iter.next();
            sb.append(mp.getProperty());
            if (iter.hasNext()) {
               sb.append(", ");
            }
         }

         sb.append("\n");
      }

      private void appendInvalidPropertyInfo(StringBuilder sb) {
         if (!this.invalidProperties.isEmpty()) {
            sb.append("Invalid properties:");
            sb.append("\n");
         }

         for (InvalidPropertyErrorDetails ip : this.invalidProperties) {
            sb.append(ip.toString());
            sb.append("\n");
         }
      }

      private void appendMissingRelationInfo(StringBuilder sb) {
         if (!this.missingRelations.isEmpty()) {
            sb.append("Missing relations: ");
         }

         Iterator<MissingRelationErrorDetails> iter = this.missingRelations.iterator();

         while (iter.hasNext()) {
            MissingRelationErrorDetails mr = iter.next();
            sb.append(mr.getRelation());
            if (iter.hasNext()) {
               sb.append(", ");
            }
         }

         sb.append("\n");
      }

      private void appendInvalidRelationInfo(StringBuilder sb) {
         if (!this.invalidRelations.isEmpty()) {
            sb.append("Invalid relations: ");
            sb.append("\n");
         }

         Iterator<InvalidRelationErrorDetails> iter = this.invalidRelations.iterator();

         while (iter.hasNext()) {
            InvalidRelationErrorDetails ir = iter.next();
            sb.append(ir.toString());
            if (iter.hasNext()) {
               sb.append("\n");
            }
         }
      }

      private void appendImmutablePropertyInfo(StringBuilder sb) {
         if (!this.immutableProperties.isEmpty()) {
            sb.append("Immutable properties:");
            sb.append("\n");
         }

         for (ImmutablePropertyErrorDetails ip : this.immutableProperties) {
            sb.append(ip.toString());
            sb.append("\n");
         }
      }

      public String getSummary() {
         String nl = "\n";
         StringBuilder sb = new StringBuilder();
         sb.append(String.format("Object of type '%s' violates data constraints.", this.validatedType.getSimpleName()));
         sb.append("\n");
         this.appendMissingPropertyInfo(sb);
         this.appendInvalidPropertyInfo(sb);
         this.appendImmutablePropertyInfo(sb);
         this.appendMissingRelationInfo(sb);
         this.appendInvalidRelationInfo(sb);
         return sb.toString();
      }

      public void check() {
         if (!this.missingRelations.isEmpty()
            || !this.missingProperties.isEmpty()
            || !this.immutableProperties.isEmpty()
            || !this.invalidProperties.isEmpty()
            || !this.invalidRelations.isEmpty()) {
            throw new DataValidationException(this);
         }
      }
   }
}
