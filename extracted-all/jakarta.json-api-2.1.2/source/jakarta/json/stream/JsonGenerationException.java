package jakarta.json.stream;

import jakarta.json.JsonException;

public class JsonGenerationException extends JsonException {
   private static final long serialVersionUID = -207609100806940680L;

   public JsonGenerationException(String message) {
      super(message);
   }

   public JsonGenerationException(String message, Throwable cause) {
      super(message, cause);
   }
}
