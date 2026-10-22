package org.glassfish.jersey.server;

import jakarta.ws.rs.NameBinding;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.ProcessingProviders;

class ProcessingProvidersConfigurator implements BootstrapConfigurator {
   private static final Logger LOGGER = Logger.getLogger(ProcessingProvidersConfigurator.class.getName());

   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
   }

   public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      ComponentBag componentBag = serverBag.getRuntimeConfig().getComponentBag();
      Collection<Class<? extends Annotation>> applicationNameBindings = ReflectionHelper.getAnnotationTypes(
         ResourceConfig.unwrapApplication(serverBag.getRuntimeConfig()).getClass(), NameBinding.class
      );
      MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundRespFiltersInverse = new MultivaluedHashMap();
      MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundReqFiltersInverse = new MultivaluedHashMap();
      MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse = new MultivaluedHashMap();
      MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse = new MultivaluedHashMap();
      Iterable<RankedProvider<ContainerResponseFilter>> responseFilters = Providers.getAllRankedProviders(injectionManager, ContainerResponseFilter.class);
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters = filterNameBound(
         responseFilters, null, componentBag, applicationNameBindings, nameBoundRespFiltersInverse
      );
      Iterable<RankedProvider<ContainerRequestFilter>> requestFilters = Providers.getAllRankedProviders(injectionManager, ContainerRequestFilter.class);
      List<RankedProvider<ContainerRequestFilter>> preMatchFilters = new ArrayList<>();
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundReqFilters = filterNameBound(
         requestFilters, preMatchFilters, componentBag, applicationNameBindings, nameBoundReqFiltersInverse
      );
      Iterable<RankedProvider<ReaderInterceptor>> readerInterceptors = Providers.getAllRankedProviders(injectionManager, ReaderInterceptor.class);
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors = filterNameBound(
         readerInterceptors, null, componentBag, applicationNameBindings, nameBoundReaderInterceptorsInverse
      );
      Iterable<RankedProvider<WriterInterceptor>> writerInterceptors = Providers.getAllRankedProviders(injectionManager, WriterInterceptor.class);
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors = filterNameBound(
         writerInterceptors, null, componentBag, applicationNameBindings, nameBoundWriterInterceptorsInverse
      );
      Iterable<DynamicFeature> dynamicFeatures = Providers.getAllProviders(injectionManager, DynamicFeature.class);
      ProcessingProviders processingProviders = new ProcessingProviders(
         nameBoundReqFilters,
         nameBoundReqFiltersInverse,
         nameBoundResponseFilters,
         nameBoundRespFiltersInverse,
         nameBoundReaderInterceptors,
         nameBoundReaderInterceptorsInverse,
         nameBoundWriterInterceptors,
         nameBoundWriterInterceptorsInverse,
         requestFilters,
         preMatchFilters,
         responseFilters,
         readerInterceptors,
         writerInterceptors,
         dynamicFeatures
      );
      serverBag.setProcessingProviders(processingProviders);
   }

   private static <T> MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>> filterNameBound(
      Iterable<RankedProvider<T>> all,
      Collection<RankedProvider<ContainerRequestFilter>> preMatchingFilters,
      ComponentBag componentBag,
      Collection<Class<? extends Annotation>> applicationNameBindings,
      MultivaluedMap<RankedProvider<T>, Class<? extends Annotation>> inverseNameBoundMap
   ) {
      MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>> result = new MultivaluedHashMap();
      Iterator<RankedProvider<T>> it = all.iterator();

      while (it.hasNext()) {
         RankedProvider<T> provider = it.next();
         Class<?> providerClass = provider.getProvider().getClass();
         Set<Type> contractTypes = provider.getContractTypes();
         if (contractTypes != null && !contractTypes.contains(providerClass)) {
            providerClass = ReflectionHelper.theMostSpecificTypeOf(contractTypes);
         }

         ContractProvider model = componentBag.getModel(providerClass);
         if (model == null) {
            model = ComponentBag.modelFor(providerClass);
         }

         boolean preMatching = providerClass.getAnnotation(PreMatching.class) != null;
         if (preMatching && preMatchingFilters != null) {
            it.remove();
            preMatchingFilters.add(new RankedProvider((ContainerRequestFilter)provider.getProvider(), model.getPriority(ContainerRequestFilter.class)));
         }

         boolean nameBound = model.isNameBound();
         if (nameBound && !applicationNameBindings.isEmpty() && applicationNameBindings.containsAll(model.getNameBindings())) {
            nameBound = false;
         }

         if (nameBound) {
            if (!preMatching) {
               it.remove();

               for (Class<? extends Annotation> binding : model.getNameBindings()) {
                  result.add(binding, provider);
                  inverseNameBoundMap.add(provider, binding);
               }
            } else {
               LOGGER.warning(LocalizationMessages.PREMATCHING_ALSO_NAME_BOUND(providerClass));
            }
         }
      }

      return result;
   }
}
