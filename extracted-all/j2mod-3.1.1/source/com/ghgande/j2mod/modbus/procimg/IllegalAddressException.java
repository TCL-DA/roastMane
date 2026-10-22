package com.ghgande.j2mod.modbus.procimg;

public class IllegalAddressException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public IllegalAddressException() {
   }

   public IllegalAddressException(String message) {
      super(message);
   }
}
