package com.lmax.disruptor;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class FatalExceptionHandler implements ExceptionHandler<Object> {
   private static final Logger LOGGER = Logger.getLogger(FatalExceptionHandler.class.getName());
   private final Logger logger;

   public FatalExceptionHandler() {
      this.logger = LOGGER;
   }

   public FatalExceptionHandler(Logger logger) {
      this.logger = logger;
   }

   @Override
   public void handleEventException(Throwable ex, long sequence, Object event) {
      this.logger.log(Level.SEVERE, "Exception processing: " + sequence + " " + event, ex);
      throw new RuntimeException(ex);
   }

   @Override
   public void handleOnStartException(Throwable ex) {
      this.logger.log(Level.SEVERE, "Exception during onStart()", ex);
   }

   @Override
   public void handleOnShutdownException(Throwable ex) {
      this.logger.log(Level.SEVERE, "Exception during onShutdown()", ex);
   }
}
