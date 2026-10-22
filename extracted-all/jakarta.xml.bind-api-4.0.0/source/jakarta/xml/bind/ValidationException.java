package jakarta.xml.bind;

public class ValidationException extends JAXBException {
   private static final long serialVersionUID = 2206436657505193108L;

   public ValidationException(String message) {
      this(message, null, null);
   }

   public ValidationException(String message, String errorCode) {
      this(message, errorCode, null);
   }

   public ValidationException(Throwable exception) {
      this(null, null, exception);
   }

   public ValidationException(String message, Throwable exception) {
      this(message, null, exception);
   }

   public ValidationException(String message, String errorCode, Throwable exception) {
      super(message, errorCode, exception);
   }
}
