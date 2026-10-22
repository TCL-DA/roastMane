package jakarta.json;

public class JsonException extends RuntimeException {
   private static final long serialVersionUID = 359810709545392112L;

   public JsonException(String message) {
      super(message);
   }

   public JsonException(String message, Throwable cause) {
      super(message, cause);
   }
}
