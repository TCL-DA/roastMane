package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.Configuration;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ContextInjectionResolver;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.AsyncContext;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ServerBootstrapBag;
import org.glassfish.jersey.server.Uri;
import org.glassfish.jersey.server.internal.process.RequestProcessingContextReference;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public class ValueParamProviderConfigurator implements BootstrapConfigurator {
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      Provider<AsyncContext> asyncContextProvider = () -> {
         RequestProcessingContextReference reference = (RequestProcessingContextReference)injectionManager.getInstance(RequestProcessingContextReference.class);
         return reference.get().asyncContext();
      };
      LazyValue<ContextInjectionResolver> lazyContextResolver = Values.lazy(
         () -> (ContextInjectionResolver)injectionManager.getInstance(ContextInjectionResolver.class)
      );
      Supplier<Configuration> configuration = serverBag::getConfiguration;
      Provider<MultivaluedParameterExtractorProvider> paramExtractor = serverBag::getMultivaluedParameterExtractorProvider;
      Collection<ValueParamProvider> suppliers = new ArrayList<>();
      AsyncResponseValueParamProvider asyncProvider = new AsyncResponseValueParamProvider(asyncContextProvider);
      suppliers.add(asyncProvider);
      CookieParamValueParamProvider cookieProvider = new CookieParamValueParamProvider(paramExtractor);
      suppliers.add(cookieProvider);
      EntityParamValueParamProvider entityProvider = new EntityParamValueParamProvider(paramExtractor);
      suppliers.add(entityProvider);
      FormParamValueParamProvider formProvider = new FormParamValueParamProvider(paramExtractor, injectionManager);
      suppliers.add(formProvider);
      HeaderParamValueParamProvider headerProvider = new HeaderParamValueParamProvider(paramExtractor);
      suppliers.add(headerProvider);
      MatrixParamValueParamProvider matrixProvider = new MatrixParamValueParamProvider(paramExtractor);
      suppliers.add(matrixProvider);
      PathParamValueParamProvider pathProvider = new PathParamValueParamProvider(paramExtractor);
      suppliers.add(pathProvider);
      QueryParamValueParamProvider queryProvider = new QueryParamValueParamProvider(paramExtractor);
      suppliers.add(queryProvider);
      BeanParamValueParamProvider beanProvider = new BeanParamValueParamProvider(paramExtractor, injectionManager);
      suppliers.add(beanProvider);
      WebTargetValueParamProvider webTargetProvider = new WebTargetValueParamProvider(
         configuration, clientConfigClass -> (Configuration)Injections.getOrCreate(injectionManager, clientConfigClass)
      );
      suppliers.add(webTargetProvider);
      DelegatedInjectionValueParamProvider contextProvider = new DelegatedInjectionValueParamProvider(
         lazyContextResolver, injectionManager::createForeignDescriptor
      );
      suppliers.add(contextProvider);
      serverBag.setValueParamProviders(Collections.unmodifiableCollection(suppliers));
      injectionManager.register(Bindings.service(asyncProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(cookieProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(formProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(headerProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(matrixProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(pathProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(queryProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(webTargetProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(beanProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(entityProvider).to(ValueParamProvider.class));
      injectionManager.register(Bindings.service(contextProvider).to(ValueParamProvider.class));
      Provider<ContainerRequest> request = () -> {
         RequestProcessingContextReference reference = (RequestProcessingContextReference)injectionManager.getInstance(RequestProcessingContextReference.class);
         return reference.get().request();
      };
      this.registerResolver(injectionManager, asyncProvider, Suspended.class, request);
      this.registerResolver(injectionManager, cookieProvider, CookieParam.class, request);
      this.registerResolver(injectionManager, formProvider, FormParam.class, request);
      this.registerResolver(injectionManager, headerProvider, HeaderParam.class, request);
      this.registerResolver(injectionManager, matrixProvider, MatrixParam.class, request);
      this.registerResolver(injectionManager, pathProvider, PathParam.class, request);
      this.registerResolver(injectionManager, queryProvider, QueryParam.class, request);
      this.registerResolver(injectionManager, webTargetProvider, Uri.class, request);
      this.registerResolver(injectionManager, beanProvider, BeanParam.class, request);
   }

   private void registerResolver(InjectionManager im, ValueParamProvider vfp, Class<? extends Annotation> annotation, Provider<ContainerRequest> request) {
      im.register(Bindings.injectionResolver(new ParamInjectionResolver<>(vfp, annotation, request)));
   }

   public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      List<ValueParamProvider> addedInstances = injectionManager.getAllInstances(ValueParamProvider.class);
      if (!addedInstances.isEmpty()) {
         ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
         addedInstances.addAll(serverBag.getValueParamProviders());
         serverBag.setValueParamProviders(Collections.unmodifiableCollection(addedInstances));
      }
   }
}
