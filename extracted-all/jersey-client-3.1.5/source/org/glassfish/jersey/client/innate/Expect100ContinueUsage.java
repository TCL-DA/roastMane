package org.glassfish.jersey.client.innate;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.ClientRequest;
import org.glassfish.jersey.client.RequestEntityProcessing;

public final class Expect100ContinueUsage {
   private Expect100ContinueUsage() {
   }

   public static boolean isAllowed(ClientRequest request, String requestMethod) {
      long requestLength = request.getLengthLong();
      RequestEntityProcessing entityProcessing = request.resolveProperty("jersey.config.client.request.entity.processing", RequestEntityProcessing.class);
      Boolean expectContinueActivated = request.resolveProperty("jersey.config.client.request.expect.100.continue.processing", Boolean.class);
      Long expectContinueSizeThreshold = request.resolveProperty(
         "jersey.config.client.request.expect.100.continue.threshold.size", ClientProperties.DEFAULT_EXPECT_100_CONTINUE_THRESHOLD_SIZE
      );
      boolean allowStreaming = requestLength > expectContinueSizeThreshold || entityProcessing == RequestEntityProcessing.CHUNKED;
      return Boolean.TRUE.equals(expectContinueActivated) && ("POST".equals(requestMethod) || "PUT".equals(requestMethod)) && allowStreaming;
   }
}
