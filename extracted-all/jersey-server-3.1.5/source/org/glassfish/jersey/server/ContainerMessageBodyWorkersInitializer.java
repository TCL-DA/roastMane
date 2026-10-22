package org.glassfish.jersey.server;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.function.Function;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

public class ContainerMessageBodyWorkersInitializer implements Function<RequestProcessingContext, RequestProcessingContext> {
   private final Provider<MessageBodyWorkers> workersFactory;

   @Inject
   public ContainerMessageBodyWorkersInitializer(Provider<MessageBodyWorkers> workersFactory) {
      this.workersFactory = workersFactory;
   }

   public RequestProcessingContext apply(RequestProcessingContext requestContext) {
      requestContext.request().setWorkers((MessageBodyWorkers)this.workersFactory.get());
      return requestContext;
   }
}
