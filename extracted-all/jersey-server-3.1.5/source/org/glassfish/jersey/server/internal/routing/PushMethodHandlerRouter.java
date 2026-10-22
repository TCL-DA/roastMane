package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.MethodHandler;

final class PushMethodHandlerRouter implements Router {
   private final MethodHandler methodHandler;
   private final Router next;

   PushMethodHandlerRouter(MethodHandler methodHandler, Router next) {
      this.methodHandler = methodHandler;
      this.next = next;
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext context) {
      RoutingContext routingContext = context.routingContext();
      Object storedResource = routingContext.peekMatchedResource();
      if (storedResource == null || !storedResource.getClass().equals(this.methodHandler.getHandlerClass())) {
         Object handlerInstance = this.methodHandler.getInstance(context.injectionManager());
         routingContext.pushMatchedResource(handlerInstance);
      }

      return Router.Continuation.of(context, this.next);
   }
}
