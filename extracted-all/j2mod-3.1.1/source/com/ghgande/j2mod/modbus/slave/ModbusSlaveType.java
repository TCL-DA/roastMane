package com.ghgande.j2mod.modbus.slave;

import com.ghgande.j2mod.modbus.util.ModbusUtil;

public enum ModbusSlaveType {
   TCP,
   UDP,
   SERIAL;

   public boolean is(ModbusSlaveType... types) {
      if (!ModbusUtil.isBlank(types)) {
         for (ModbusSlaveType type : types) {
            if (this.equals(type)) {
               return true;
            }
         }
      }

      return false;
   }

   public String getKey(int port) {
      return this.toString() + port;
   }
}
