package org.cropster.roastingintelligence.util.modbus;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadInputRegistersResponse;
import com.ghgande.j2mod.modbus.net.SerialConnection;

public class FloatRegisterRead {
   private static final int MODBUS_SERIAL_RETRIES = 1;
   private final ReadInputRegistersRequest request;

   public FloatRegisterRead(int startRegister) {
      this.request = new ReadInputRegistersRequest(startRegister, 2);
      this.request.setUnitID(1);
      this.request.setHeadless();
   }

   public float execute(ModbusSerialTransaction transaction) throws ModbusException {
      transaction.setRequest(this.request);
      transaction.execute();
      ReadInputRegistersResponse response = (ReadInputRegistersResponse)transaction.getResponse();
      int higher = response.getRegisterValue(0);
      int lower = response.getRegisterValue(1);
      int bits = higher << 16 | lower;
      return Float.intBitsToFloat(bits);
   }

   public double execute(SerialConnection connection) throws ModbusException {
      ModbusSerialTransaction transaction = new ModbusSerialTransaction(connection);
      transaction.setRetries(1);
      return (double)this.execute(transaction);
   }

   public ReadInputRegistersRequest getRequest() {
      return this.request;
   }
}
