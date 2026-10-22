package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.process.internal.AbstractChainableStage;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.Stage.Continuation;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.monitoring.RequestEvent;

final class RoutingStage extends AbstractChainableStage<RequestProcessingContext> {
   private final Router routingRoot;

   RoutingStage(Router routingRoot) {
      this.routingRoot = routingRoot;
   }

   public Continuation<RequestProcessingContext> apply(RequestProcessingContext context) {
      ContainerRequest request = context.request();
      context.triggerEvent(RequestEvent.Type.MATCHING_START);
      TracingLogger tracingLogger = TracingLogger.getInstance(request);
      long timestamp = tracingLogger.timestamp(ServerTraceEvent.MATCH_SUMMARY);

      Continuation var8;
      try {
         RoutingStage.RoutingResult result = this._apply(context, this.routingRoot);
         Stage<RequestProcessingContext> nextStage = null;
         if (result.endpoint != null) {
            context.routingContext().setEndpoint(result.endpoint);
            nextStage = this.getDefaultNext();
         }

         var8 = Continuation.of(result.context, nextStage);
      } finally {
         tracingLogger.logDuration(ServerTraceEvent.MATCH_SUMMARY, timestamp, new Object[0]);
      }

      return var8;
   }

   private RoutingStage.RoutingResult _apply(RequestProcessingContext request, Router router) {
      Router.Continuation continuation = router.apply(request);

      for (Router child : continuation.next()) {
         RoutingStage.RoutingResult result = this._apply(continuation.requestContext(), child);
         if (result.endpoint != null) {
            return result;
         }
      }

      Endpoint endpoint = Routers.extractEndpoint(router);
      return endpoint != null
         ? RoutingStage.RoutingResult.from(continuation.requestContext(), endpoint)
         : RoutingStage.RoutingResult.from(continuation.requestContext());
   }

   private static final class RoutingResult {
      private final RequestProcessingContext context;
      private final Endpoint endpoint;

      private static RoutingStage.RoutingResult from(RequestProcessingContext requestProcessingContext, Endpoint endpoint) {
         return new RoutingStage.RoutingResult(requestProcessingContext, endpoint);
      }

      private static RoutingStage.RoutingResult from(RequestProcessingContext requestProcessingContext) {
         return new RoutingStage.RoutingResult(requestProcessingContext, null);
      }

      private RoutingResult(RequestProcessingContext context, Endpoint endpoint) {
         this.context = context;
         this.endpoint = endpoint;
      }
   }
}
