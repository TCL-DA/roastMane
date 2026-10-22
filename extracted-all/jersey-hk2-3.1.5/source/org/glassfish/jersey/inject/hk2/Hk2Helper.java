package org.glassfish.jersey.inject.hk2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.AbstractActiveDescriptor;
import org.glassfish.hk2.utilities.ActiveDescriptorBuilder;
import org.glassfish.hk2.utilities.AliasDescriptor;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.hk2.utilities.binding.ServiceBindingBuilder;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.jersey.internal.inject.AliasBinding;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.DisposableSupplier;
import org.glassfish.jersey.internal.inject.InjectionResolverBinding;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.internal.inject.PerThread;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;

class Hk2Helper {
   static void bind(AbstractHk2InjectionManager injectionManager, Binder jerseyBinder) {
      bind(injectionManager.getServiceLocator(), Bindings.getBindings(injectionManager, jerseyBinder));
   }

   static void bind(ServiceLocator locator, Binding binding) {
      bindBinding(locator, binding);
   }

   static void bind(ServiceLocator locator, Iterable<Binding> descriptors) {
      DynamicConfiguration dc = getDynamicConfiguration(locator);

      for (Binding binding : descriptors) {
         bindBinding(locator, dc, binding);
      }

      dc.commit();
   }

   private static DynamicConfiguration getDynamicConfiguration(ServiceLocator locator) {
      DynamicConfigurationService dcs = (DynamicConfigurationService)locator.getService(DynamicConfigurationService.class, new Annotation[0]);
      return dcs.createDynamicConfiguration();
   }

   private static void bindBinding(ServiceLocator locator, Binding<?, ?> binding) {
      DynamicConfiguration dc = getDynamicConfiguration(locator);
      bindBinding(locator, dc, binding);
      dc.commit();
   }

   private static void bindBinding(ServiceLocator locator, DynamicConfiguration dc, Binding<?, ?> binding) {
      if (ClassBinding.class.isAssignableFrom(binding.getClass())) {
         ActiveDescriptor<?> activeDescriptor = translateToActiveDescriptor((ClassBinding<?>)binding);
         bindBinding(locator, dc, activeDescriptor, binding.getAliases());
      } else if (InstanceBinding.class.isAssignableFrom(binding.getClass())) {
         ActiveDescriptor<?> activeDescriptor = translateToActiveDescriptor((InstanceBinding<?>)binding);
         bindBinding(locator, dc, activeDescriptor, binding.getAliases());
      } else if (InjectionResolverBinding.class.isAssignableFrom(binding.getClass())) {
         InjectionResolverBinding resolverDescriptor = (InjectionResolverBinding)binding;
         bindBinding(locator, dc, wrapInjectionResolver(resolverDescriptor), binding.getAliases());
         bindBinding(locator, dc, translateToActiveDescriptor(resolverDescriptor), binding.getAliases());
      } else if (SupplierClassBinding.class.isAssignableFrom(binding.getClass())) {
         bindSupplierClassBinding(locator, (SupplierClassBinding<?>)binding);
      } else {
         if (!SupplierInstanceBinding.class.isAssignableFrom(binding.getClass())) {
            throw new RuntimeException(org.glassfish.jersey.internal.LocalizationMessages.UNKNOWN_DESCRIPTOR_TYPE(binding.getClass().getSimpleName()));
         }

         bindSupplierInstanceBinding(locator, (SupplierInstanceBinding<?>)binding);
      }
   }

   private static ActiveDescriptor<?> wrapInjectionResolver(InjectionResolverBinding resolverDescriptor) {
      InjectionResolverWrapper<?> wrappedResolver = new InjectionResolverWrapper(resolverDescriptor.getResolver());
      return translateToActiveDescriptor(
         Bindings.service(wrappedResolver), new ParameterizedTypeImpl(InjectionResolver.class, new Type[]{resolverDescriptor.getResolver().getAnnotation()})
      );
   }

   private static void bindSupplierInstanceBinding(ServiceLocator locator, SupplierInstanceBinding<?> binding) {
      Consumer<AbstractBinder> bindConsumer = binder -> {
         Supplier<?> supplier = binding.getSupplier();
         boolean disposable = DisposableSupplier.class.isAssignableFrom(supplier.getClass());
         AbstractActiveDescriptor<? extends Supplier<?>> supplierBuilder = BuilderHelper.createConstantDescriptor(supplier);
         binding.getContracts().forEach(contract -> {
            supplierBuilder.addContractType(new ParameterizedTypeImpl(Supplier.class, new Type[]{contract}));
            if (disposable) {
               supplierBuilder.addContractType(new ParameterizedTypeImpl(DisposableSupplier.class, new Type[]{contract}));
            }
         });
         supplierBuilder.setName(binding.getName());
         binding.getQualifiers().forEach(supplierBuilder::addQualifierAnnotation);
         binder.bind(supplierBuilder);
         ServiceBindingBuilder<?> builder = binder.bindFactory(new InstanceSupplierFactoryBridge<>(supplier, disposable));
         setupSupplierFactoryBridge(binding, builder);
      };
      ServiceLocatorUtilities.bind(locator, new org.glassfish.hk2.utilities.Binder[]{createBinder(bindConsumer)});
   }

   private static void bindSupplierClassBinding(ServiceLocator locator, SupplierClassBinding<?> binding) {
      Consumer<AbstractBinder> bindConsumer = binder -> {
         boolean disposable = DisposableSupplier.class.isAssignableFrom(binding.getSupplierClass());
         ServiceBindingBuilder<?> supplierBuilder = binder.bind(binding.getSupplierClass());
         binding.getContracts().forEach(contractx -> {
            supplierBuilder.to(new ParameterizedTypeImpl(Supplier.class, new Type[]{contractx}));
            if (disposable) {
               supplierBuilder.to(new ParameterizedTypeImpl(DisposableSupplier.class, new Type[]{contractx}));
            }
         });
         binding.getQualifiers().forEach(supplierBuilder::qualifiedBy);
         supplierBuilder.named(binding.getName());
         supplierBuilder.in(transformScope(binding.getSupplierScope()));
         binder.bind(supplierBuilder);
         Type contract = null;
         if (binding.getContracts().iterator().hasNext()) {
            contract = (Type)binding.getContracts().iterator().next();
         }

         ServiceBindingBuilder<?> builder = binder.bindFactory(new SupplierFactoryBridge(locator, contract, binding.getName(), disposable));
         setupSupplierFactoryBridge(binding, builder);
         if (binding.getImplementationType() != null) {
            builder.asType(binding.getImplementationType());
         }
      };
      ServiceLocatorUtilities.bind(locator, new org.glassfish.hk2.utilities.Binder[]{createBinder(bindConsumer)});
   }

   private static void setupSupplierFactoryBridge(Binding<?, ?> binding, ServiceBindingBuilder<?> builder) {
      builder.named(binding.getName());
      binding.getContracts().forEach(builder::to);
      binding.getQualifiers().forEach(builder::qualifiedBy);
      builder.in(transformScope(binding.getScope()));
      if (binding.getRank() != null) {
         builder.ranked(binding.getRank());
      }

      if (binding.isProxiable() != null) {
         builder.proxy(binding.isProxiable());
      }

      if (binding.isProxiedForSameScope() != null) {
         builder.proxyForSameScope(binding.isProxiedForSameScope());
      }
   }

   static ActiveDescriptor<?> translateToActiveDescriptor(ClassBinding<?> desc) {
      ActiveDescriptorBuilder binding = BuilderHelper.activeLink(desc.getService()).named(desc.getName()).analyzeWith(desc.getAnalyzer());
      if (desc.getScope() != null) {
         binding.in(transformScope(desc.getScope()));
      }

      if (desc.getRank() != null) {
         binding.ofRank(desc.getRank());
      }

      for (Annotation annotation : desc.getQualifiers()) {
         binding.qualifiedBy(annotation);
      }

      for (Type contract : desc.getContracts()) {
         binding.to(contract);
      }

      if (desc.isProxiable() != null) {
         binding.proxy(desc.isProxiable());
      }

      if (desc.isProxiedForSameScope() != null) {
         binding.proxyForSameScope(desc.isProxiedForSameScope());
      }

      if (desc.getImplementationType() != null) {
         binding.asType(desc.getImplementationType());
      }

      return binding.build();
   }

   private static void bindBinding(ServiceLocator locator, DynamicConfiguration dc, ActiveDescriptor<?> activeDescriptor, Set<AliasBinding> aliases) {
      ActiveDescriptor<Object> boundDescriptor = dc.bind(activeDescriptor);

      for (AliasBinding alias : aliases) {
         dc.bind(createAlias(locator, boundDescriptor, alias));
      }
   }

   static ActiveDescriptor<?> translateToActiveDescriptor(InstanceBinding<?> desc, Type... contracts) {
      AbstractActiveDescriptor<?> binding;
      if (contracts.length == 0) {
         binding = BuilderHelper.createConstantDescriptor(desc.getService());
      } else {
         binding = BuilderHelper.createConstantDescriptor(desc.getService(), null, contracts);
      }

      binding.setName(desc.getName());
      binding.setClassAnalysisName(desc.getAnalyzer());
      if (desc.getScope() != null) {
         binding.setScope(desc.getScope().getName());
      }

      if (desc.getRank() != null) {
         binding.setRanking(desc.getRank());
      }

      for (Annotation annotation : desc.getQualifiers()) {
         binding.addQualifierAnnotation(annotation);
      }

      for (Type contract : desc.getContracts()) {
         binding.addContractType(contract);
      }

      if (desc.isProxiable() != null) {
         binding.setProxiable(desc.isProxiable());
      }

      if (desc.isProxiedForSameScope() != null) {
         binding.setProxyForSameScope(desc.isProxiedForSameScope());
      }

      return binding;
   }

   private static ActiveDescriptor<?> translateToActiveDescriptor(InjectionResolverBinding<?> desc) {
      ParameterizedTypeImpl parameterizedType = new ParameterizedTypeImpl(
         org.glassfish.jersey.internal.inject.InjectionResolver.class, new Type[]{desc.getResolver().getAnnotation()}
      );
      return BuilderHelper.createConstantDescriptor(desc.getResolver(), null, new Type[]{parameterizedType});
   }

   private static AliasDescriptor<?> createAlias(ServiceLocator locator, ActiveDescriptor<?> descriptor, AliasBinding alias) {
      AliasDescriptor<?> hk2Alias = new AliasDescriptor(locator, descriptor, alias.getContract().getName(), null);
      alias.getQualifiers().forEach(hk2Alias::addQualifierAnnotation);
      alias.getScope().ifPresent(hk2Alias::setScope);
      alias.getRank().ifPresent(hk2Alias::setRanking);
      return hk2Alias;
   }

   private static org.glassfish.hk2.utilities.Binder createBinder(final Consumer<AbstractBinder> bindConsumer) {
      return new AbstractBinder() {
         protected void configure() {
            bindConsumer.accept(this);
         }
      };
   }

   private static Class<? extends Annotation> transformScope(Class<? extends Annotation> scope) {
      if (scope == PerLookup.class) {
         return org.glassfish.hk2.api.PerLookup.class;
      } else {
         return scope == PerThread.class ? org.glassfish.hk2.api.PerThread.class : scope;
      }
   }
}
