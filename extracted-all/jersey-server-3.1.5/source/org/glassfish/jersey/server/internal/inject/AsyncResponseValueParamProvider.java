package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.ws.rs.container.AsyncResponse;
import java.util.function.Function;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.AsyncContext;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

final class AsyncResponseValueParamProvider implements ValueParamProvider {
   private final Provider<AsyncContext> asyncContextProvider;

   public AsyncResponseValueParamProvider(Provider<AsyncContext> asyncContextProvider) {
      this.asyncContextProvider = asyncContextProvider;
   }

   @Override
   public Function<ContainerRequest, AsyncResponse> getValueProvider(Parameter parameter) {
      if (parameter.getSource() != Source.SUSPENDED) {
         return null;
      } else {
         return !AsyncResponse.class.isAssignableFrom(parameter.getRawType()) ? null : containerRequest -> (AsyncResponse)this.asyncContextProvider.get();
      }
   }

   @Override
   public ValueParamProvider.PriorityType getPriority() {
      return ValueParamProvider.Priority.NORMAL;
   }
}
