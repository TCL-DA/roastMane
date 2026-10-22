package org.eclipse.jetty.http;

import org.eclipse.jetty.io.QuietException;
import org.eclipse.jetty.util.ExceptionUtil;

public interface HttpException extends QuietException {
   int getCode();

   String getReason();

   static void throwAsUnchecked(HttpException httpException) {
      ExceptionUtil.ifExceptionThrowUnchecked((Throwable)httpException);
   }

   public static class IllegalArgumentException extends java.lang.IllegalArgumentException implements HttpException {
      private final int _code;
      private final String _reason;

      public IllegalArgumentException(int code) {
         this(code, null, null);
      }

      public IllegalArgumentException(int code, String reason) {
         this(code, reason, null);
      }

      public IllegalArgumentException(int code, String reason, Throwable cause) {
         super(code + ": " + reason, cause);
         this._code = code;
         this._reason = reason;
      }

      @Override
      public int getCode() {
         return this._code;
      }

      @Override
      public String getReason() {
         return this._reason;
      }
   }

   public static class RuntimeException extends java.lang.RuntimeException implements HttpException {
      private final int _code;
      private final String _reason;

      public RuntimeException(int code) {
         this(code, null, null);
      }

      public RuntimeException(int code, String reason) {
         this(code, reason, null);
      }

      public RuntimeException(int code, Throwable cause) {
         this(code, null, cause);
      }

      public RuntimeException(int code, String reason, Throwable cause) {
         super(code + ": " + reason, cause);
         this._code = code;
         this._reason = reason;
      }

      @Override
      public int getCode() {
         return this._code;
      }

      @Override
      public String getReason() {
         return this._reason;
      }
   }
}
