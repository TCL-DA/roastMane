package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class InsufficientDataException extends MathIllegalArgumentException {
   private static final long serialVersionUID = -2629324471511903359L;

   public InsufficientDataException() {
      this(LocalizedFormats.INSUFFICIENT_DATA);
   }

   public InsufficientDataException(Localizable pattern, Object... arguments) {
      super(pattern, arguments);
   }
}
