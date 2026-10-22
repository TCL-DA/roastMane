package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.RuntimeResource;

final class PushMatchedRuntimeResourceRouter implements Router {
   private final RuntimeResource resource;

   PushMatchedRuntimeResourceRouter(RuntimeResource resource) {
      this.resource = resource;
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext context) {
      context.routingContext().pushMatchedRuntimeResource(this.resource);
      return Router.Continuation.of(context);
   }
}
