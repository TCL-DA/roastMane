package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.ExceptionContextProvider;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MathUnsupportedOperationException extends UnsupportedOperationException implements ExceptionContextProvider {
   private static final long serialVersionUID = -6024911025449780478L;
   private final ExceptionContext context = new ExceptionContext(this);

   public MathUnsupportedOperationException() {
      this(LocalizedFormats.UNSUPPORTED_OPERATION);
   }

   public MathUnsupportedOperationException(Localizable pattern, Object... args) {
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
