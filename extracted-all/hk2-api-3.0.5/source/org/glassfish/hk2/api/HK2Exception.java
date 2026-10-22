package org.glassfish.hk2.api;

public class HK2Exception extends Exception {
   private static final long serialVersionUID = -6933923442167686426L;

   public HK2Exception() {
   }

   public HK2Exception(String message) {
      super(message);
   }

   public HK2Exception(Throwable cause) {
      super(cause);
   }

   public HK2Exception(String message, Throwable cause) {
      super(message, cause);
   }
}
