package jakarta.json.stream;

import jakarta.json.JsonException;

public class JsonParsingException extends JsonException {
   private static final long serialVersionUID = 9073566598484238797L;
   private final transient JsonLocation location;

   public JsonParsingException(String message, JsonLocation location) {
      super(message);
      this.location = location;
   }

   public JsonParsingException(String message, Throwable cause, JsonLocation location) {
      super(message, cause);
      this.location = location;
   }

   public JsonLocation getLocation() {
      return this.location;
   }
}
