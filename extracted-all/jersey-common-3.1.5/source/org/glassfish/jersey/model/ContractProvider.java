package org.glassfish.jersey.model;

import jakarta.inject.Singleton;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public final class ContractProvider implements Scoped, NameBound {
   public static final int NO_PRIORITY = -1;
   private final Class<?> implementationClass;
   private final Map<Class<?>, Integer> contracts;
   private final int defaultPriority;
   private final Set<Class<? extends Annotation>> nameBindings;
   private final Class<? extends Annotation> scope;

   public static ContractProvider.Builder builder(Class<?> implementationClass) {
      return new ContractProvider.Builder(implementationClass);
   }

   public static ContractProvider.Builder builder(ContractProvider original) {
      return new ContractProvider.Builder(original);
   }

   private ContractProvider(
      Class<?> implementationClass,
      Class<? extends Annotation> scope,
      Map<Class<?>, Integer> contracts,
      int defaultPriority,
      Set<Class<? extends Annotation>> nameBindings
   ) {
      this.implementationClass = implementationClass;
      this.scope = scope;
      this.contracts = contracts;
      this.defaultPriority = defaultPriority;
      this.nameBindings = nameBindings;
   }

   @Override
   public Class<? extends Annotation> getScope() {
      return this.scope;
   }

   public Class<?> getImplementationClass() {
      return this.implementationClass;
   }

   public Set<Class<?>> getContracts() {
      return this.contracts.keySet();
   }

   public Map<Class<?>, Integer> getContractMap() {
      return this.contracts;
   }

   @Override
   public boolean isNameBound() {
      return !this.nameBindings.isEmpty();
   }

   public int getPriority(Class<?> contract) {
      return this.contracts.containsKey(contract) ? this.contracts.get(contract) : this.defaultPriority;
   }

   public Set<Class<? extends Annotation>> getNameBindings() {
      return this.nameBindings;
   }

   public static final class Builder {
      private static final ContractProvider EMPTY_MODEL = new ContractProvider(null, Singleton.class, Collections.emptyMap(), -1, Collections.emptySet());
      private Class<?> implementationClass = null;
      private Class<? extends Annotation> scope = null;
      private Map<Class<?>, Integer> contracts = new HashMap<>();
      private int defaultPriority = -1;
      private Set<Class<? extends Annotation>> nameBindings = Collections.newSetFromMap(new IdentityHashMap<>());

      private Builder(Class<?> implementationClass) {
         this.implementationClass = implementationClass;
      }

      private Builder(ContractProvider original) {
         this.implementationClass = original.implementationClass;
         this.scope = original.scope;
         this.contracts.putAll(original.contracts);
         this.defaultPriority = original.defaultPriority;
         this.nameBindings.addAll(original.nameBindings);
      }

      public ContractProvider.Builder scope(Class<? extends Annotation> scope) {
         this.scope = scope;
         return this;
      }

      public ContractProvider.Builder addContract(Class<?> contract) {
         return this.addContract(contract, this.defaultPriority);
      }

      public ContractProvider.Builder addContract(Class<?> contract, int priority) {
         this.contracts.put(contract, priority);
         return this;
      }

      public ContractProvider.Builder addContracts(Map<Class<?>, Integer> contracts) {
         this.contracts.putAll(contracts);
         return this;
      }

      public ContractProvider.Builder addContracts(Collection<Class<?>> contracts) {
         for (Class<?> contract : contracts) {
            this.addContract(contract, this.defaultPriority);
         }

         return this;
      }

      public ContractProvider.Builder defaultPriority(int defaultPriority) {
         this.defaultPriority = defaultPriority;
         return this;
      }

      public ContractProvider.Builder addNameBinding(Class<? extends Annotation> binding) {
         this.nameBindings.add(binding);
         return this;
      }

      public Class<? extends Annotation> getScope() {
         return this.scope;
      }

      public Map<Class<?>, Integer> getContracts() {
         return this.contracts;
      }

      public int getDefaultPriority() {
         return this.defaultPriority;
      }

      public Set<Class<? extends Annotation>> getNameBindings() {
         return this.nameBindings;
      }

      public ContractProvider build() {
         if (this.scope == null) {
            this.scope = Singleton.class;
         }

         Map<Class<?>, Integer> _contracts = this.contracts.isEmpty()
            ? Collections.emptyMap()
            : this.contracts.entrySet().stream().collect(Collectors.toMap(Entry::getKey, classIntegerEntry -> {
               Integer priority = classIntegerEntry.getValue();
               return priority != -1 ? priority : this.defaultPriority;
            }));
         Set<Class<? extends Annotation>> bindings = this.nameBindings.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(this.nameBindings);
         return this.implementationClass == null && this.scope == Singleton.class && _contracts.isEmpty() && this.defaultPriority == -1 && bindings.isEmpty()
            ? EMPTY_MODEL
            : new ContractProvider(this.implementationClass, this.scope, _contracts, this.defaultPriority, bindings);
      }
   }
}
