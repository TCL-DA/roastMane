package com.ghgande.j2mod.modbus;

public class ModbusIOException extends ModbusException {
   private static final long serialVersionUID = 1L;
   private boolean eof = false;

   public ModbusIOException() {
   }

   public ModbusIOException(String message) {
      super(message);
   }

   public ModbusIOException(String message, Object... values) {
      super(message, values);
   }

   public ModbusIOException(boolean b) {
      this.eof = b;
   }

   public ModbusIOException(String message, boolean b) {
      super(message);
      this.eof = b;
   }

   public ModbusIOException(String message, Throwable cause) {
      super(message, cause);
   }

   public boolean isEOF() {
      return this.eof;
   }

   public void setEOF(boolean b) {
      this.eof = b;
   }
}
