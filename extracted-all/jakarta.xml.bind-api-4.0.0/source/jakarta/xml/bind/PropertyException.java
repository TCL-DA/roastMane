package jakarta.xml.bind;

public class PropertyException extends JAXBException {
   private static final long serialVersionUID = 3159963351607157477L;

   public PropertyException(String message) {
      super(message);
   }

   public PropertyException(String message, String errorCode) {
      super(message, errorCode);
   }

   public PropertyException(Throwable exception) {
      super(exception);
   }

   public PropertyException(String message, Throwable exception) {
      super(message, exception);
   }

   public PropertyException(String message, String errorCode, Throwable exception) {
      super(message, errorCode, exception);
   }

   public PropertyException(String name, Object value) {
      super(Messages.format("PropertyException.NameValue", name, value.toString()));
   }
}
