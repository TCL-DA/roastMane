package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

final class MatchResultInitializerRouter implements Router {
   private final Router rootRouter;

   MatchResultInitializerRouter(Router rootRouter) {
      this.rootRouter = rootRouter;
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext processingContext) {
      RoutingContext rc = processingContext.routingContext();
      rc.pushMatchResult(new SingleMatchResult("/" + processingContext.request().getPath(false)));
      return Router.Continuation.of(processingContext, this.rootRouter);
   }
}
