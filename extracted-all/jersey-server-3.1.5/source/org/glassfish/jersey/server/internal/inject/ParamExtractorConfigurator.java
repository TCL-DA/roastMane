package org.glassfish.jersey.server.internal.inject;

import jakarta.ws.rs.ext.ParamConverterProvider;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.ParamConverterFactory;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.server.ServerBootstrapBag;

public class ParamExtractorConfigurator implements BootstrapConfigurator {
   public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
      ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
      LazyValue<ParamConverterFactory> lazyParamConverterFactory = Values.lazy(
         () -> new ParamConverterFactory(
               Providers.getProviders(injectionManager, ParamConverterProvider.class),
               Providers.getCustomProviders(injectionManager, ParamConverterProvider.class)
            )
      );
      MultivaluedParameterExtractorFactory multiExtractor = new MultivaluedParameterExtractorFactory(lazyParamConverterFactory);
      serverBag.setMultivaluedParameterExtractorProvider(multiExtractor);
      injectionManager.register(Bindings.service(multiExtractor).to(MultivaluedParameterExtractorProvider.class));
   }
}
