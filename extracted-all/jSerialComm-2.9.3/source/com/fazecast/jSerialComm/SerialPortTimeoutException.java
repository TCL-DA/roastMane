package com.fazecast.jSerialComm;

import java.io.InterruptedIOException;

public final class SerialPortTimeoutException extends InterruptedIOException {
   private static final long serialVersionUID = 3209035213903386044L;

   public SerialPortTimeoutException(String message) {
      super(message);
      this.bytesTransferred = 0;
   }
}
