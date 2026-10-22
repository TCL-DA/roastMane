package org.glassfish.jersey.internal.inject;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

public class AliasBinding {
   private final Class<?> contract;
   private final Set<Annotation> qualifiers = new LinkedHashSet<>();
   private Optional<String> scope = Optional.empty();
   private OptionalInt rank = OptionalInt.empty();

   AliasBinding(Class<?> contract) {
      this.contract = contract;
   }

   public Class<?> getContract() {
      return this.contract;
   }

   public Optional<String> getScope() {
      return this.scope;
   }

   public AliasBinding in(String scope) {
      this.scope = Optional.of(scope);
      return this;
   }

   public OptionalInt getRank() {
      return this.rank;
   }

   public AliasBinding ranked(int rank) {
      this.rank = OptionalInt.of(rank);
      return this;
   }

   public Set<Annotation> getQualifiers() {
      return this.qualifiers;
   }

   public AliasBinding qualifiedBy(Annotation annotation) {
      if (annotation != null) {
         this.qualifiers.add(annotation);
      }

      return this;
   }
}
