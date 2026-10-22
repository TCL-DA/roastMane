package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;

public class HeaderValueException extends ProcessingException {
   private static final long serialVersionUID = 981810773601231157L;
   private final HeaderValueException.Context context;

   public HeaderValueException(String message, Throwable cause, HeaderValueException.Context context) {
      super(message, cause);
      this.context = context;
   }

   public HeaderValueException(String message, HeaderValueException.Context context) {
      super(message);
      this.context = context;
   }

   public HeaderValueException.Context getContext() {
      return this.context;
   }

   public static enum Context {
      INBOUND,
      OUTBOUND;
   }
}
