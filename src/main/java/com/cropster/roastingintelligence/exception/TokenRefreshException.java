package com.cropster.roastingintelligence.exception;

public class TokenRefreshException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public TokenRefreshException(String message, Exception cause) {
      super(message, cause);
   }
}
