package com.fazecast.jSerialComm;

import java.io.IOException;

public final class SerialPortIOException extends IOException {
   private static final long serialVersionUID = 3353684802475494674L;

   public SerialPortIOException(String message) {
      super(message);
   }
}
