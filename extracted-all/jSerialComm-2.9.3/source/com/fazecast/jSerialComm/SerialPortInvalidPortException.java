package com.fazecast.jSerialComm;

public final class SerialPortInvalidPortException extends RuntimeException {
   private static final long serialVersionUID = 3420177672598538224L;

   public SerialPortInvalidPortException(String message, Throwable cause) {
      super(message, cause);
   }
}
