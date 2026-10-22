package org.glassfish.jersey.client;

import jakarta.ws.rs.client.CompletionStageRxInvoker;

public class JerseyCompletionStageRxInvoker extends JerseyInvocation.AsyncInvoker implements CompletionStageRxInvoker {
   JerseyCompletionStageRxInvoker(JerseyInvocation.Builder builder) {
      super(builder);
   }
}
