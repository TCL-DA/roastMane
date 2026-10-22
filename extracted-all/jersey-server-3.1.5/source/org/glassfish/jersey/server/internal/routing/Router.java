package org.glassfish.jersey.server.internal.routing;

import java.util.Collections;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;

interface Router {
   Router.Continuation apply(RequestProcessingContext var1);

   public static final class Continuation {
      private final RequestProcessingContext requestProcessingContext;
      private final Iterable<Router> next;

      static Router.Continuation of(RequestProcessingContext result) {
         return new Router.Continuation(result, null);
      }

      static Router.Continuation of(RequestProcessingContext result, Iterable<Router> next) {
         return new Router.Continuation(result, next);
      }

      static Router.Continuation of(RequestProcessingContext request, Router next) {
         return new Router.Continuation(request, Collections.singletonList(next));
      }

      private Continuation(RequestProcessingContext request, Iterable<Router> next) {
         this.requestProcessingContext = request;
         this.next = (Iterable<Router>)(next == null ? Collections.emptyList() : next);
      }

      RequestProcessingContext requestContext() {
         return this.requestProcessingContext;
      }

      Iterable<Router> next() {
         return this.next;
      }
   }
}
