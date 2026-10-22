package org.eclipse.jetty.http;

public class BadMessageException extends HttpException.RuntimeException {
   public BadMessageException() {
      this(null, null);
   }

   public BadMessageException(String reason) {
      this(reason, null);
   }

   public BadMessageException(String reason, Throwable cause) {
      this(400, reason, cause);
   }

   public BadMessageException(int code) {
      this(code, null, null);
   }

   public BadMessageException(int code, String reason) {
      this(code, reason, null);
   }

   public BadMessageException(int code, String reason, Throwable cause) {
      super(code, reason, cause);

      assert code >= 400 && code < 500;
   }
}
