package org.jvnet.mimepull;

public class MIMEParsingException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public MIMEParsingException() {
   }

   public MIMEParsingException(String message) {
      super(message);
   }

   public MIMEParsingException(String message, Throwable cause) {
      super(message, cause);
   }

   public MIMEParsingException(Throwable cause) {
      super(cause);
   }
}
