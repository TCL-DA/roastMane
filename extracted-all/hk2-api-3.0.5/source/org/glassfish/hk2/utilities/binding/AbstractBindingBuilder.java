package org.glassfish.hk2.utilities.binding;

import jakarta.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.TypeLiteral;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.FactoryDescriptorsImpl;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.jvnet.hk2.component.MultiMap;

abstract class AbstractBindingBuilder<T> implements ServiceBindingBuilder<T>, NamedBindingBuilder<T>, ScopedBindingBuilder<T>, ScopedNamedBindingBuilder<T> {
   Set<Type> contracts = new HashSet<>();
   HK2Loader loader = null;
   final MultiMap<String, String> metadata = new MultiMap();
   Set<Annotation> qualifiers = new HashSet<>();
   Annotation scopeAnnotation = null;
   Class<? extends Annotation> scope = null;
   Integer ranked = null;
   String name = null;
   Boolean proxiable = null;
   Boolean proxyForSameScope = null;
   Type implementationType = null;
   String analyzer = null;

   public AbstractBindingBuilder<T> proxy(boolean proxiable) {
      this.proxiable = proxiable;
      return this;
   }

   public AbstractBindingBuilder<T> proxyForSameScope(boolean proxyForSameScope) {
      this.proxyForSameScope = proxyForSameScope;
      return this;
   }

   public AbstractBindingBuilder<T> analyzeWith(String analyzer) {
      this.analyzer = analyzer;
      return this;
   }

   public AbstractBindingBuilder<T> to(Class<? super T> contract) {
      this.contracts.add(contract);
      return this;
   }

   public AbstractBindingBuilder<T> to(TypeLiteral<?> contract) {
      this.contracts.add(contract.getType());
      return this;
   }

   public AbstractBindingBuilder<T> to(Type contract) {
      this.contracts.add(contract);
      return this;
   }

   public AbstractBindingBuilder<T> loadedBy(HK2Loader loader) {
      this.loader = loader;
      return this;
   }

   public AbstractBindingBuilder<T> withMetadata(String key, String value) {
      this.metadata.add(key, value);
      return this;
   }

   public AbstractBindingBuilder<T> withMetadata(String key, List<String> values) {
      for (String value : values) {
         this.metadata.add(key, value);
      }

      return this;
   }

   public AbstractBindingBuilder<T> qualifiedBy(Annotation annotation) {
      if (Named.class.equals(annotation.annotationType())) {
         this.name = ((Named)annotation).value();
      }

      this.qualifiers.add(annotation);
      return this;
   }

   public AbstractBindingBuilder<T> in(Annotation scopeAnnotation) {
      this.scopeAnnotation = scopeAnnotation;
      return this;
   }

   public AbstractBindingBuilder<T> in(Class<? extends Annotation> scopeAnnotation) {
      this.scope = scopeAnnotation;
      return this;
   }

   public AbstractBindingBuilder<T> named(String name) {
      this.name = name;
      return this;
   }

   @Override
   public void ranked(int rank) {
      this.ranked = rank;
   }

   public AbstractBindingBuilder<T> asType(Type t) {
      this.implementationType = t;
      return this;
   }

   abstract void complete(DynamicConfiguration var1, HK2Loader var2);

   static <T> AbstractBindingBuilder<T> create(Class<T> serviceType, boolean bindAsContract) {
      return new AbstractBindingBuilder.ClassBasedBindingBuilder<>(serviceType, bindAsContract ? serviceType : null);
   }

   static <T> AbstractBindingBuilder<T> create(Type serviceType, boolean bindAsContract) {
      return new AbstractBindingBuilder.ClassBasedBindingBuilder<T>(ReflectionHelper.getRawClass(serviceType), bindAsContract ? serviceType : null)
         .asType(serviceType);
   }

   static <T> AbstractBindingBuilder<T> create(TypeLiteral<T> serviceType, boolean bindAsContract) {
      Type type = serviceType.getType();
      return new AbstractBindingBuilder.ClassBasedBindingBuilder<>(serviceType.getRawType(), bindAsContract ? serviceType.getType() : null).asType(type);
   }

   static <T> AbstractBindingBuilder<T> create(T service) {
      return new AbstractBindingBuilder.InstanceBasedBindingBuilder<>(service);
   }

   static <T> AbstractBindingBuilder<T> createFactoryBinder(Factory<T> factory) {
      return new AbstractBindingBuilder.FactoryInstanceBasedBindingBuilder<>(factory);
   }

   static <T> AbstractBindingBuilder<T> createFactoryBinder(Class<? extends Factory<T>> factoryType, Class<? extends Annotation> factoryScope) {
      return new AbstractBindingBuilder.FactoryTypeBasedBindingBuilder<>(factoryType, factoryScope);
   }

   private static class ClassBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
      private final Class<T> service;

      public ClassBasedBindingBuilder(Class<T> service, Type serviceContractType) {
         this.service = service;
         if (serviceContractType != null) {
            super.contracts.add(serviceContractType);
         }
      }

      @Override
      void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
         if (this.loader == null) {
            this.loader = defaultLoader;
         }

         ActiveDescriptorBuilder builder = BuilderHelper.activeLink(this.service).named(this.name).andLoadWith(this.loader).analyzeWith(this.analyzer);
         if (this.scopeAnnotation != null) {
            builder.in(this.scopeAnnotation);
         }

         if (this.scope != null) {
            builder.in(this.scope);
         }

         if (this.ranked != null) {
            builder.ofRank(this.ranked);
         }

         for (String key : this.metadata.keySet()) {
            for (String value : this.metadata.get(key)) {
               builder.has(key, value);
            }
         }

         for (Annotation annotation : this.qualifiers) {
            builder.qualifiedBy(annotation);
         }

         for (Type contract : this.contracts) {
            builder.to(contract);
         }

         if (this.proxiable != null) {
            builder.proxy(this.proxiable);
         }

         if (this.proxyForSameScope != null) {
            builder.proxyForSameScope(this.proxyForSameScope);
         }

         if (this.implementationType != null) {
            builder.asType(this.implementationType);
         }

         configuration.bind(builder.build(), false);
      }
   }

   private static class FactoryInstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
      private final Factory<T> factory;

      public FactoryInstanceBasedBindingBuilder(Factory<T> factory) {
         this.factory = factory;
      }

      @Override
      void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
         if (this.loader == null) {
            this.loader = defaultLoader;
         }

         AbstractActiveDescriptor<?> factoryContractDescriptor = BuilderHelper.createConstantDescriptor(this.factory);
         factoryContractDescriptor.addContractType(this.factory.getClass());
         factoryContractDescriptor.setLoader(this.loader);
         ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(this.factory.getClass())
            .named(this.name)
            .andLoadWith(this.loader)
            .analyzeWith(this.analyzer);
         if (this.scope != null) {
            descriptorBuilder.in(this.scope);
         }

         if (this.ranked != null) {
            descriptorBuilder.ofRank(this.ranked);
         }

         for (Annotation qualifier : this.qualifiers) {
            factoryContractDescriptor.addQualifierAnnotation(qualifier);
            descriptorBuilder.qualifiedBy(qualifier);
         }

         for (Type contract : this.contracts) {
            factoryContractDescriptor.addContractType(new ParameterizedTypeImpl(Factory.class, new Type[]{contract}));
            descriptorBuilder.to(contract);
         }

         for (String key : this.metadata.keySet()) {
            for (String value : this.metadata.get(key)) {
               factoryContractDescriptor.addMetadata(key, value);
               descriptorBuilder.has(key, value);
            }
         }

         if (this.proxiable != null) {
            descriptorBuilder.proxy(this.proxiable);
         }

         if (this.proxyForSameScope != null) {
            descriptorBuilder.proxyForSameScope(this.proxyForSameScope);
         }

         configuration.bind(new FactoryDescriptorsImpl(factoryContractDescriptor, descriptorBuilder.buildProvideMethod()));
      }
   }

   private static class FactoryTypeBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
      private final Class<? extends Factory<T>> factoryClass;
      private final Class<? extends Annotation> factoryScope;

      public FactoryTypeBasedBindingBuilder(Class<? extends Factory<T>> factoryClass, Class<? extends Annotation> factoryScope) {
         this.factoryClass = factoryClass;
         this.factoryScope = factoryScope;
      }

      @Override
      void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
         if (this.loader == null) {
            this.loader = defaultLoader;
         }

         ActiveDescriptorBuilder factoryDescriptorBuilder = BuilderHelper.activeLink(this.factoryClass)
            .named(this.name)
            .andLoadWith(this.loader)
            .analyzeWith(this.analyzer);
         if (this.factoryScope != null) {
            factoryDescriptorBuilder.in(this.factoryScope);
         }

         ActiveDescriptorBuilder descriptorBuilder = BuilderHelper.activeLink(this.factoryClass)
            .named(this.name)
            .andLoadWith(this.loader)
            .analyzeWith(this.analyzer);
         if (this.scope != null) {
            descriptorBuilder.in(this.scope);
         }

         if (this.ranked != null) {
            descriptorBuilder.ofRank(this.ranked);
         }

         for (Annotation qualifier : this.qualifiers) {
            factoryDescriptorBuilder.qualifiedBy(qualifier);
            descriptorBuilder.qualifiedBy(qualifier);
         }

         for (Type contract : this.contracts) {
            factoryDescriptorBuilder.to(new ParameterizedTypeImpl(Factory.class, new Type[]{contract}));
            descriptorBuilder.to(contract);
         }

         for (String key : this.metadata.keySet()) {
            for (String value : this.metadata.get(key)) {
               factoryDescriptorBuilder.has(key, value);
               descriptorBuilder.has(key, value);
            }
         }

         if (this.proxiable != null) {
            descriptorBuilder.proxy(this.proxiable);
         }

         if (this.proxyForSameScope != null) {
            descriptorBuilder.proxyForSameScope(this.proxyForSameScope);
         }

         configuration.bind(new FactoryDescriptorsImpl(factoryDescriptorBuilder.build(), descriptorBuilder.buildProvideMethod()));
      }
   }

   private static class InstanceBasedBindingBuilder<T> extends AbstractBindingBuilder<T> {
      private final T service;

      public InstanceBasedBindingBuilder(T service) {
         if (service == null) {
            throw new IllegalArgumentException();
         } else {
            this.service = service;
         }
      }

      @Override
      void complete(DynamicConfiguration configuration, HK2Loader defaultLoader) {
         if (this.loader == null) {
            this.loader = defaultLoader;
         }

         AbstractActiveDescriptor<?> descriptor = BuilderHelper.createConstantDescriptor(this.service);
         descriptor.setName(this.name);
         descriptor.setLoader(this.loader);
         descriptor.setClassAnalysisName(this.analyzer);
         if (this.scope != null) {
            descriptor.setScope(this.scope.getName());
         }

         if (this.ranked != null) {
            descriptor.setRanking(this.ranked);
         }

         for (String key : this.metadata.keySet()) {
            for (String value : this.metadata.get(key)) {
               descriptor.addMetadata(key, value);
            }
         }

         for (Annotation annotation : this.qualifiers) {
            descriptor.addQualifierAnnotation(annotation);
         }

         for (Type contract : this.contracts) {
            descriptor.addContractType(contract);
         }

         if (this.proxiable != null) {
            descriptor.setProxiable(this.proxiable);
         }

         if (this.proxyForSameScope != null) {
            descriptor.setProxyForSameScope(this.proxyForSameScope);
         }

         configuration.bind(descriptor, false);
      }
   }
}
