package com.lmax.disruptor.dsl;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.ExceptionHandlers;

public class ExceptionHandlerWrapper<T> implements ExceptionHandler<T> {
   private ExceptionHandler<? super T> delegate;

   public void switchTo(ExceptionHandler<? super T> exceptionHandler) {
      this.delegate = exceptionHandler;
   }

   @Override
   public void handleEventException(Throwable ex, long sequence, T event) {
      this.getExceptionHandler().handleEventException(ex, sequence, event);
   }

   @Override
   public void handleOnStartException(Throwable ex) {
      this.getExceptionHandler().handleOnStartException(ex);
   }

   @Override
   public void handleOnShutdownException(Throwable ex) {
      this.getExceptionHandler().handleOnShutdownException(ex);
   }

   private ExceptionHandler<? super T> getExceptionHandler() {
      ExceptionHandler<? super T> handler = this.delegate;
      return handler == null ? ExceptionHandlers.defaultHandler() : handler;
   }
}
