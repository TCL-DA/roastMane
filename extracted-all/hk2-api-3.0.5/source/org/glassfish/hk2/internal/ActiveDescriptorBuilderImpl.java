package org.glassfish.hk2.internal;

import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;

public class ActiveDescriptorBuilderImpl implements ActiveDescriptorBuilder {
   private String name;
   private final HashSet<Type> contracts = new HashSet<>();
   private Annotation scopeAnnotation = null;
   private Class<? extends Annotation> scope = PerLookup.class;
   private final HashSet<Annotation> qualifiers = new HashSet<>();
   private final HashMap<String, List<String>> metadatas = new HashMap<>();
   private final Class<?> implementation;
   private HK2Loader loader = null;
   private int rank = 0;
   private Boolean proxy = null;
   private Boolean proxyForSameScope = null;
   private DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
   private String classAnalysisName = null;
   private Type implementationType;

   public ActiveDescriptorBuilderImpl(Class<?> implementation) {
      this.implementation = implementation;
   }

   @Override
   public ActiveDescriptorBuilder named(String name) throws IllegalArgumentException {
      this.name = name;
      return this;
   }

   @Override
   public ActiveDescriptorBuilder to(Type contract) throws IllegalArgumentException {
      if (contract != null) {
         this.contracts.add(contract);
      }

      return this;
   }

   @Override
   public ActiveDescriptorBuilder in(Annotation scopeAnnotation) throws IllegalArgumentException {
      if (scopeAnnotation == null) {
         throw new IllegalArgumentException();
      } else {
         this.scopeAnnotation = scopeAnnotation;
         this.scope = scopeAnnotation.annotationType();
         return this;
      }
   }

   @Override
   public ActiveDescriptorBuilder in(Class<? extends Annotation> scope) throws IllegalArgumentException {
      this.scope = scope;
      if (scope == null) {
         this.scopeAnnotation = null;
      } else if (this.scopeAnnotation != null && !scope.equals(this.scopeAnnotation.annotationType())) {
         throw new IllegalArgumentException(
            "Scope set to different class (" + scope.getName() + ") from the scope annotation (" + this.scopeAnnotation.annotationType().getName()
         );
      }

      return this;
   }

   @Override
   public ActiveDescriptorBuilder qualifiedBy(Annotation annotation) throws IllegalArgumentException {
      if (annotation != null) {
         if (Named.class.equals(annotation.annotationType())) {
            this.name = ((Named)annotation).value();
         }

         this.qualifiers.add(annotation);
      }

      return this;
   }

   @Override
   public ActiveDescriptorBuilder has(String key, String value) throws IllegalArgumentException {
      return this.has(key, Collections.singletonList(value));
   }

   @Override
   public ActiveDescriptorBuilder has(String key, List<String> values) throws IllegalArgumentException {
      if (key != null && values != null && values.size() > 0) {
         this.metadatas.put(key, values);
         return this;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public ActiveDescriptorBuilder ofRank(int rank) {
      this.rank = rank;
      return this;
   }

   @Override
   public ActiveDescriptorBuilder proxy() {
      return this.proxy(true);
   }

   @Override
   public ActiveDescriptorBuilder proxy(boolean forceProxy) {
      if (forceProxy) {
         this.proxy = Boolean.TRUE;
      } else {
         this.proxy = Boolean.FALSE;
      }

      return this;
   }

   @Override
   public ActiveDescriptorBuilder proxyForSameScope() {
      return this.proxy(true);
   }

   @Override
   public ActiveDescriptorBuilder proxyForSameScope(boolean forceProxyForSameScope) {
      if (forceProxyForSameScope) {
         this.proxyForSameScope = Boolean.TRUE;
      } else {
         this.proxyForSameScope = Boolean.FALSE;
      }

      return this;
   }

   @Override
   public ActiveDescriptorBuilder andLoadWith(HK2Loader loader) throws IllegalArgumentException {
      this.loader = loader;
      return this;
   }

   @Override
   public ActiveDescriptorBuilder analyzeWith(String serviceName) {
      this.classAnalysisName = serviceName;
      return this;
   }

   @Override
   public ActiveDescriptorBuilder localOnly() {
      this.visibility = DescriptorVisibility.LOCAL;
      return this;
   }

   @Override
   public ActiveDescriptorBuilder visibility(DescriptorVisibility visibility) {
      if (visibility == null) {
         throw new IllegalArgumentException();
      } else {
         this.visibility = visibility;
         return this;
      }
   }

   @Override
   public ActiveDescriptorBuilder asType(Type t) {
      if (t == null) {
         throw new IllegalArgumentException();
      } else {
         this.implementationType = t;
         return this;
      }
   }

   @Override
   public <T> AbstractActiveDescriptor<T> build() throws IllegalArgumentException {
      return new ActiveDescriptorBuilderImpl.BuiltActiveDescriptor<>(
         this.implementation,
         this.contracts,
         this.scopeAnnotation,
         this.scope,
         this.name,
         this.qualifiers,
         DescriptorType.CLASS,
         this.visibility,
         this.rank,
         this.proxy,
         this.proxyForSameScope,
         this.classAnalysisName,
         this.metadatas,
         this.loader,
         this.implementationType
      );
   }

   @Deprecated
   @Override
   public <T> AbstractActiveDescriptor<T> buildFactory() throws IllegalArgumentException {
      return this.buildProvideMethod();
   }

   @Override
   public <T> AbstractActiveDescriptor<T> buildProvideMethod() throws IllegalArgumentException {
      return new ActiveDescriptorBuilderImpl.BuiltActiveDescriptor<>(
         this.implementation,
         this.contracts,
         this.scopeAnnotation,
         this.scope,
         this.name,
         this.qualifiers,
         DescriptorType.PROVIDE_METHOD,
         this.visibility,
         this.rank,
         this.proxy,
         this.proxyForSameScope,
         this.classAnalysisName,
         this.metadatas,
         this.loader,
         this.implementationType
      );
   }

   private static class BuiltActiveDescriptor<T> extends AbstractActiveDescriptor<T> {
      private static final long serialVersionUID = 2434137639270026082L;
      private Class<?> implementationClass;
      private Type implementationType;

      public BuiltActiveDescriptor() {
      }

      private BuiltActiveDescriptor(
         Class<?> implementationClass,
         Set<Type> advertisedContracts,
         Annotation scopeAnnotation,
         Class<? extends Annotation> scope,
         String name,
         Set<Annotation> qualifiers,
         DescriptorType descriptorType,
         DescriptorVisibility descriptorVisibility,
         int ranking,
         Boolean proxy,
         Boolean proxyForSameScope,
         String classAnalysisName,
         Map<String, List<String>> metadata,
         HK2Loader loader,
         Type implementationType
      ) {
         super(
            advertisedContracts, scope, name, qualifiers, descriptorType, descriptorVisibility, ranking, proxy, proxyForSameScope, classAnalysisName, metadata
         );
         super.setReified(false);
         super.setLoader(loader);
         super.setScopeAsAnnotation(scopeAnnotation);
         this.implementationClass = implementationClass;
         super.setImplementation(implementationClass.getName());
         if (implementationType == null) {
            implementationType = implementationClass;
         }

         this.implementationType = implementationType;
      }

      @Override
      public Class<?> getImplementationClass() {
         return this.implementationClass;
      }

      @Override
      public Type getImplementationType() {
         return this.implementationType;
      }

      @Override
      public T create(ServiceHandle<?> root) {
         throw new AssertionError("Should not be called directly");
      }

      @Override
      public void setImplementationType(Type t) {
         this.implementationType = t;
      }
   }
}
