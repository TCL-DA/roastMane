package org.eclipse.jetty.io;

public interface QuietException {
   static boolean isQuiet(Throwable throwable) {
      while (throwable != null) {
         if (throwable instanceof QuietException) {
            return true;
         }

         throwable = throwable.getCause();
      }

      return false;
   }

   public static class Exception extends java.lang.Exception implements QuietException {
      public Exception() {
      }

      public Exception(String message) {
         super(message);
      }

      public Exception(String message, Throwable cause) {
         super(message, cause);
      }

      public Exception(Throwable cause) {
         super(cause);
      }
   }

   public static class RuntimeException extends java.lang.RuntimeException implements QuietException {
      public RuntimeException() {
      }

      public RuntimeException(String message) {
         super(message);
      }

      public RuntimeException(String message, Throwable cause) {
         super(message, cause);
      }

      public RuntimeException(Throwable cause) {
         super(cause);
      }
   }
}
