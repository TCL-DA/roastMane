package com.ghgande.j2mod.modbus;

public class ModbusException extends Exception {
   private static final long serialVersionUID = 1L;

   public ModbusException() {
   }

   public ModbusException(String message) {
      super(message);
   }

   public ModbusException(String message, Object... values) {
      super(String.format(message, values));
   }

   public ModbusException(String message, Throwable cause) {
      super(message, cause);
   }
}
