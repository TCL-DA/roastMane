package jakarta.xml.bind;

public class DataBindingException extends RuntimeException {
   private static final long serialVersionUID = 4743686626270704879L;

   public DataBindingException(String message, Throwable cause) {
      super(message, cause);
   }

   public DataBindingException(Throwable cause) {
      super(cause);
   }
}
