package org.glassfish.jersey.server.internal.process;

import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.internal.RequestScoped;

@RequestScoped
public class RequestProcessingContextReference implements Ref<RequestProcessingContext> {
   private RequestProcessingContext processingContext;

   public void set(RequestProcessingContext processingContext) {
      this.processingContext = processingContext;
   }

   public RequestProcessingContext get() {
      return this.processingContext;
   }
}
