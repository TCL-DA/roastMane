package com.cropster.roastingintelligence.exception;

public class BetaAccessDeniedException extends Exception {
   private static final long serialVersionUID = 1L;

   public BetaAccessDeniedException(String message) {
      super(message);
   }
}
