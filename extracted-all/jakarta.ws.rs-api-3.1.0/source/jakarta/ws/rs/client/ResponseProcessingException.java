package jakarta.ws.rs.client;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;

public class ResponseProcessingException extends ProcessingException {
   private static final long serialVersionUID = -4923161617935731839L;
   private final Response response;

   public ResponseProcessingException(Response response, Throwable cause) {
      super(cause);
      this.response = response;
   }

   public ResponseProcessingException(Response response, String message, Throwable cause) {
      super(message, cause);
      this.response = response;
   }

   public ResponseProcessingException(Response response, String message) {
      super(message);
      this.response = response;
   }

   public Response getResponse() {
      return this.response;
   }
}
