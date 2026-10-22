package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.ResourceMethod;

final class PushMatchedMethodRouter implements Router {
   private final ResourceMethod resourceMethod;

   PushMatchedMethodRouter(ResourceMethod resourceMethod) {
      this.resourceMethod = resourceMethod;
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext processingContext) {
      RoutingContext rc = processingContext.routingContext();
      switch (this.resourceMethod.getType()) {
         case RESOURCE_METHOD:
         case SUB_RESOURCE_METHOD:
            rc.setMatchedResourceMethod(this.resourceMethod);
            break;
         case SUB_RESOURCE_LOCATOR:
            rc.pushMatchedLocator(this.resourceMethod);
      }

      return Router.Continuation.of(processingContext);
   }
}
