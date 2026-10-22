package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.process.internal.Stage.Continuation;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

final class MatchedEndpointExtractorStage implements Stage<RequestProcessingContext> {
   public Continuation<RequestProcessingContext> apply(RequestProcessingContext processingContext) {
      Endpoint endpoint = processingContext.routingContext().getEndpoint();
      return endpoint != null ? Continuation.of(processingContext, Stages.asStage(endpoint)) : Continuation.of(processingContext);
   }
}
