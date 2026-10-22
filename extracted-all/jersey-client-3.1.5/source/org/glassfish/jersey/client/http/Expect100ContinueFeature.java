package org.glassfish.jersey.client.http;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import org.glassfish.jersey.client.ClientProperties;

public class Expect100ContinueFeature implements Feature {
   private long thresholdSize;

   public Expect100ContinueFeature() {
      this(ClientProperties.DEFAULT_EXPECT_100_CONTINUE_THRESHOLD_SIZE);
   }

   private Expect100ContinueFeature(long thresholdSize) {
      this.thresholdSize = thresholdSize;
   }

   public static Expect100ContinueFeature withCustomThreshold(long thresholdSize) {
      return new Expect100ContinueFeature(thresholdSize);
   }

   public static Expect100ContinueFeature basic() {
      return new Expect100ContinueFeature();
   }

   public boolean configure(FeatureContext configurableContext) {
      if (configurableContext.getConfiguration().getProperty("jersey.config.client.request.expect.100.continue.processing") == null) {
         configurableContext.property("jersey.config.client.request.expect.100.continue.processing", Boolean.TRUE);
         if (configurableContext.getConfiguration().getProperty("jersey.config.client.request.expect.100.continue.threshold.size") == null) {
            configurableContext.property("jersey.config.client.request.expect.100.continue.threshold.size", this.thresholdSize);
         }

         return true;
      } else {
         return false;
      }
   }
}
