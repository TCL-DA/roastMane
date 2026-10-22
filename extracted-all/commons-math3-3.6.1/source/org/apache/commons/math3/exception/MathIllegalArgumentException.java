package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;
import org.apache.commons.math3.exception.util.Localizable;

public class MathIllegalArgumentException extends IllegalArgumentException implements ExceptionContextProvider {
   private static final long serialVersionUID = -6024911025449780478L;
   private final ExceptionContext context = new ExceptionContext(this);

   public MathIllegalArgumentException(Localizable pattern, Object... args) {
      this.context.addMessage(pattern, args);
   }

   @Override
   public ExceptionContext getContext() {
      return this.context;
   }

   @Override
   public String getMessage() {
      return this.context.getMessage();
   }

   @Override
   public String getLocalizedMessage() {
      return this.context.getLocalizedMessage();
   }
}
