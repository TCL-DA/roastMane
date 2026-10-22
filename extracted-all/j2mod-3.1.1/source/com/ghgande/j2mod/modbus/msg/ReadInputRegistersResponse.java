package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ReadInputRegistersResponse extends ModbusResponse {
   private int byteCount;
   private InputRegister[] registers;

   public ReadInputRegistersResponse() {
      this.setFunctionCode(4);
   }

   public ReadInputRegistersResponse(InputRegister[] registers) {
      this.setFunctionCode(4);
      this.setDataLength(registers == null ? 0 : registers.length * 2 + 1);
      this.registers = registers == null ? null : Arrays.copyOf(registers, registers.length);
      this.byteCount = registers == null ? 0 : registers.length * 2;
   }

   public int getByteCount() {
      return this.byteCount;
   }

   public int getWordCount() {
      return this.byteCount / 2;
   }

   public void setWordCount(int count) {
      this.byteCount = count * 2;
   }

   public InputRegister getRegister(int index) throws IndexOutOfBoundsException {
      if (index < 0) {
         throw new IndexOutOfBoundsException(index + " < 0");
      } else if (index >= this.getWordCount()) {
         throw new IndexOutOfBoundsException(index + " >= " + this.getWordCount());
      } else {
         return this.registers[index];
      }
   }

   public int getRegisterValue(int index) throws IndexOutOfBoundsException {
      return this.getRegister(index).toUnsignedShort();
   }

   public synchronized InputRegister[] getRegisters() {
      InputRegister[] dest = new InputRegister[this.registers.length];
      System.arraycopy(this.registers, 0, dest, 0, dest.length);
      return dest;
   }

   public synchronized void setRegisters(InputRegister[] registers) {
      this.setDataLength(registers == null ? 0 : registers.length * 2 + 1);
      this.registers = registers == null ? null : Arrays.copyOf(registers, registers.length);
      this.byteCount = registers == null ? 0 : registers.length * 2;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.writeByte(this.byteCount);

      for (int k = 0; k < this.getWordCount(); k++) {
         dout.write(this.registers[k].toBytes());
      }
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.byteCount = din.readUnsignedByte();
      InputRegister[] inputRegisters = new InputRegister[this.getWordCount()];

      for (int k = 0; k < this.getWordCount(); k++) {
         inputRegisters[k] = new SimpleInputRegister(din.readByte(), din.readByte());
      }

      this.registers = inputRegisters;
      this.setDataLength(this.byteCount);
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[this.registers.length * 2 + 1];
      result[0] = (byte)(this.registers.length * 2);

      for (int i = 0; i < this.registers.length; i++) {
         byte[] value = this.registers[i].toBytes();
         result[1 + i * 2] = value[0];
         result[2 + i * 2] = value[1];
      }

      return result;
   }
}
