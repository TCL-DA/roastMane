package org.glassfish.jersey.internal.inject;

import jakarta.inject.Named;
import jakarta.ws.rs.core.GenericType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class Binding<T, D extends Binding> {
   private final Set<Type> contracts = new HashSet<>();
   private final Set<Annotation> qualifiers = new HashSet<>();
   private final Set<AliasBinding> aliases = new HashSet<>();
   private Class<? extends Annotation> scope = null;
   private String name = null;
   private Class<T> implementationType = null;
   private String analyzer = null;
   private Boolean proxiable = null;
   private Boolean proxyForSameScope = null;
   private Integer ranked = null;

   public Boolean isProxiable() {
      return this.proxiable;
   }

   public Boolean isProxiedForSameScope() {
      return this.proxyForSameScope;
   }

   public Integer getRank() {
      return this.ranked;
   }

   public Set<Type> getContracts() {
      return this.contracts;
   }

   public Set<Annotation> getQualifiers() {
      return this.qualifiers;
   }

   public Class<? extends Annotation> getScope() {
      return this.scope;
   }

   public String getName() {
      return this.name;
   }

   public Class<T> getImplementationType() {
      return this.implementationType;
   }

   public String getAnalyzer() {
      return this.analyzer;
   }

   public Set<AliasBinding> getAliases() {
      return this.aliases;
   }

   public D analyzeWith(String analyzer) {
      this.analyzer = analyzer;
      return (D)this;
   }

   public D to(Collection<Class<? super T>> contracts) {
      if (contracts != null) {
         this.contracts.addAll(contracts);
      }

      return (D)this;
   }

   public D to(Class<? super T> contract) {
      this.contracts.add(contract);
      return (D)this;
   }

   public D to(GenericType<?> contract) {
      this.contracts.add(contract.getType());
      return (D)this;
   }

   public D to(Type contract) {
      this.contracts.add(contract);
      return (D)this;
   }

   public D qualifiedBy(Annotation annotation) {
      if (Named.class.equals(annotation.annotationType())) {
         this.name = ((Named)annotation).value();
      }

      this.qualifiers.add(annotation);
      return (D)this;
   }

   public D in(Class<? extends Annotation> scopeAnnotation) {
      this.scope = scopeAnnotation;
      return (D)this;
   }

   public D named(String name) {
      this.name = name;
      return (D)this;
   }

   public AliasBinding addAlias(Class<?> contract) {
      AliasBinding alias = new AliasBinding(contract);
      this.aliases.add(alias);
      return alias;
   }

   public D proxy(boolean proxiable) {
      this.proxiable = proxiable;
      return (D)this;
   }

   public D proxyForSameScope(boolean proxyForSameScope) {
      this.proxyForSameScope = proxyForSameScope;
      return (D)this;
   }

   public void ranked(int rank) {
      this.ranked = rank;
   }

   D asType(Class type) {
      this.implementationType = type;
      return (D)this;
   }
}
