package jakarta.xml.bind;

public class MarshalException extends JAXBException {
   private static final long serialVersionUID = 1570397297836071517L;

   public MarshalException(String message) {
      this(message, null, null);
   }

   public MarshalException(String message, String errorCode) {
      this(message, errorCode, null);
   }

   public MarshalException(Throwable exception) {
      this(null, null, exception);
   }

   public MarshalException(String message, Throwable exception) {
      this(message, null, exception);
   }

   public MarshalException(String message, String errorCode, Throwable exception) {
      super(message, errorCode, exception);
   }
}
