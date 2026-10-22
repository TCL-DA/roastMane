package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ReadWriteMultipleResponse extends ModbusResponse {
   private int byteCount;
   private InputRegister[] registers;

   public ReadWriteMultipleResponse(InputRegister[] registers) {
      this.setFunctionCode(23);
      this.setDataLength(registers.length * 2 + 1);
      this.registers = Arrays.copyOf(registers, registers.length);
      this.byteCount = registers.length * 2;
   }

   public ReadWriteMultipleResponse(int count) {
      this.setFunctionCode(23);
      this.setDataLength(count * 2 + 1);
      this.registers = new InputRegister[count];
      this.byteCount = count * 2;
   }

   public ReadWriteMultipleResponse() {
      this.setFunctionCode(23);
   }

   public int getByteCount() {
      return this.byteCount;
   }

   public int getWordCount() {
      return this.byteCount / 2;
   }

   public InputRegister getRegister(int index) {
      if (this.registers == null) {
         throw new IndexOutOfBoundsException("No registers defined!");
      } else if (index < 0) {
         throw new IndexOutOfBoundsException("Negative index: " + index);
      } else if (index >= this.getWordCount()) {
         throw new IndexOutOfBoundsException(index + " > " + this.getWordCount());
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

   public void setRegisters(InputRegister[] registers) {
      this.byteCount = registers.length * 2;
      this.setDataLength(this.byteCount + 1);
      this.registers = Arrays.copyOf(registers, registers.length);
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
      this.registers = new Register[this.getWordCount()];

      for (int k = 0; k < this.getWordCount(); k++) {
         this.registers[k] = new SimpleRegister(din.readByte(), din.readByte());
      }

      this.setDataLength(this.byteCount + 1);
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[this.getWordCount() * 2 + 1];
      int offset = 0;
      result[offset++] = (byte)this.byteCount;

      for (InputRegister register : this.registers) {
         byte[] data = register.toBytes();
         result[offset++] = data[0];
         result[offset++] = data[1];
      }

      return result;
   }
}
