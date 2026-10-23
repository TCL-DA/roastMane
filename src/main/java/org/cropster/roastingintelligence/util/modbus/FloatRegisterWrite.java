package org.cropster.roastingintelligence.util.modbus;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.WriteMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.net.SerialConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;

public class FloatRegisterWrite {
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private final WriteMultipleRegistersRequest request;

   public FloatRegisterWrite(int startRegister, float value) {
      int bitsAsInt = Float.floatToIntBits(value);
      int hiWord = bitsAsInt >> 16;
      int loWord = bitsAsInt & 65535;
      SimpleRegister[] registers = new SimpleRegister[]{new SimpleRegister(hiWord), new SimpleRegister(loWord)};
      this.request = new WriteMultipleRegistersRequest(startRegister, registers);
      this.request.setUnitID(1);
      this.request.setHeadless();
   }

   public void execute(ModbusSerialTransaction transaction) throws ModbusException {
      transaction.setRequest(this.request);
      transaction.execute();
   }

   public void execute(SerialConnection connection) throws ModbusException {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
      transaction.setRetries(1);
      this.execute(transaction);
   }

   public WriteMultipleRegistersRequest getRequest() {
      return this.request;
   }
}
