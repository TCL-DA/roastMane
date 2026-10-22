package org.apache.logging.log4j.util;

public class InternalException extends RuntimeException {
   private static final long serialVersionUID = 6366395965071580537L;

   public InternalException(final String message) {
      super(message);
   }

   public InternalException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public InternalException(final Throwable cause) {
      super(cause);
   }
}
