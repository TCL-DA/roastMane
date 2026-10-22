package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Provider;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

@ConstrainedTo(RuntimeType.SERVER)
public final class FormDataParamInjectionFeature implements Feature {
   public boolean configure(FeatureContext context) {
      context.register(new AbstractBinder() {
         protected void configure() {
            Provider<MultivaluedParameterExtractorProvider> extractorProvider = this.createManagedInstanceProvider(MultivaluedParameterExtractorProvider.class);
            Provider<ContainerRequest> requestProvider = this.createManagedInstanceProvider(ContainerRequest.class);
            FormDataParamValueParamProvider valueSupplier = new FormDataParamValueParamProvider(extractorProvider);
            this.bind(Bindings.service(valueSupplier).to(ValueParamProvider.class));
            this.bind(Bindings.injectionResolver(new ParamInjectionResolver(valueSupplier, FormDataParam.class, requestProvider)));
         }
      });
      return true;
   }
}
