package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityProcessor {
   EntityProcessor.Result process(EntityProcessorContext var1);

   public static enum Result {
      APPLY,
      SKIP,
      ROLLBACK;
   }
}
