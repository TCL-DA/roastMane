package jakarta.json.bind;

public class JsonbException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public JsonbException(String message) {
      super(message);
   }

   public JsonbException(String message, Throwable cause) {
      super(message, cause);
   }
}
