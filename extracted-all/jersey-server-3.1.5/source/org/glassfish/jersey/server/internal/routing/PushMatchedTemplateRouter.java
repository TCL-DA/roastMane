package org.glassfish.jersey.server.internal.routing;

import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.uri.UriTemplate;

final class PushMatchedTemplateRouter implements Router {
   private final UriTemplate resourceTemplate;
   private final UriTemplate methodTemplate;

   PushMatchedTemplateRouter(UriTemplate resourceTemplate, UriTemplate methodTemplate) {
      this.resourceTemplate = resourceTemplate;
      this.methodTemplate = methodTemplate;
   }

   PushMatchedTemplateRouter(UriTemplate resourceTemplate) {
      this.resourceTemplate = resourceTemplate;
      this.methodTemplate = null;
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext context) {
      context.routingContext().pushTemplates(this.resourceTemplate, this.methodTemplate);
      return Router.Continuation.of(context);
   }
}
