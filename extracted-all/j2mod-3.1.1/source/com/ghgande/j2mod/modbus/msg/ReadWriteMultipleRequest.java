package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.io.NonWordDataHandler;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ReadWriteMultipleRequest extends ModbusRequest {
   private NonWordDataHandler nonWordDataHandler;
   private int readReference;
   private int readCount;
   private int writeReference;
   private int writeCount;
   private Register[] registers;

   public ReadWriteMultipleRequest(int unit, int readRef, int readCount, int writeRef, int writeCount) {
      this.setUnitID(unit);
      this.setFunctionCode(23);
      this.setDataLength(9 + writeCount * 2);
      this.readReference = readRef;
      this.readCount = readCount;
      this.writeReference = writeRef;
      this.writeCount = writeCount;
      this.registers = new Register[writeCount];

      for (int i = 0; i < writeCount; i++) {
         this.registers[i] = new SimpleRegister(0);
      }
   }

   public ReadWriteMultipleRequest(int unit) {
      this.setUnitID(unit);
      this.setFunctionCode(23);
      this.setDataLength(9);
   }

   public ReadWriteMultipleRequest() {
      this.setFunctionCode(23);
      this.setDataLength(9);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new ReadWriteMultipleResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      ProcessImage procimg = listener.getProcessImage(this.getUnitID());

      InputRegister[] readRegs;
      try {
         Register[] writeRegs = procimg.getRegisterRange(this.getWriteReference(), this.getWriteWordCount());

         for (int i = 0; i < writeRegs.length; i++) {
            writeRegs[i].setValue(this.getRegister(i).getValue());
         }

         readRegs = procimg.getRegisterRange(this.getReadReference(), this.getReadWordCount());
         InputRegister[] dummy = new InputRegister[readRegs.length];

         for (int i = 0; i < readRegs.length; i++) {
            dummy[i] = new SimpleInputRegister(readRegs[i].getValue());
         }

         readRegs = dummy;
      } catch (IllegalAddressException var8) {
         return this.createExceptionResponse(2);
      }

      ReadWriteMultipleResponse response = (ReadWriteMultipleResponse)this.getResponse();
      response.setRegisters(readRegs);
      return response;
   }

   public int getReadReference() {
      return this.readReference;
   }

   public void setReadReference(int ref) {
      this.readReference = ref;
   }

   public int getWriteReference() {
      return this.writeReference;
   }

   public void setWriteReference(int ref) {
      this.writeReference = ref;
   }

   public synchronized Register[] getRegisters() {
      Register[] dest = new Register[this.registers.length];
      System.arraycopy(this.registers, 0, dest, 0, dest.length);
      return dest;
   }

   public synchronized void setRegisters(Register[] registers) {
      this.writeCount = registers != null ? registers.length : 0;
      this.registers = registers != null ? Arrays.copyOf(registers, registers.length) : null;
   }

   public Register getRegister(int index) throws IndexOutOfBoundsException {
      if (index < 0) {
         throw new IndexOutOfBoundsException(index + " < 0");
      } else if (index >= this.getWriteWordCount()) {
         throw new IndexOutOfBoundsException(index + " > " + this.getWriteWordCount());
      } else {
         return this.registers[index];
      }
   }

   public int getReadRegisterValue(int index) throws IndexOutOfBoundsException {
      return this.getRegister(index).toUnsignedShort();
   }

   public int getByteCount() {
      return this.getWriteWordCount() * 2;
   }

   public int getWriteWordCount() {
      return this.writeCount;
   }

   public void setWriteWordCount(int count) {
      this.writeCount = count;
   }

   public int getReadWordCount() {
      return this.readCount;
   }

   public void setReadWordCount(int count) {
      this.readCount = count;
   }

   public NonWordDataHandler getNonWordDataHandler() {
      return this.nonWordDataHandler;
   }

   public void setNonWordDataHandler(NonWordDataHandler dhandler) {
      this.nonWordDataHandler = dhandler;
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput input) throws IOException {
      this.readReference = input.readUnsignedShort();
      this.readCount = input.readUnsignedShort();
      this.writeReference = input.readUnsignedShort();
      this.writeCount = input.readUnsignedShort();
      int byteCount = input.readUnsignedByte();
      if (this.nonWordDataHandler == null) {
         byte[] buffer = new byte[byteCount];
         input.readFully(buffer, 0, byteCount);
         int offset = 0;
         this.registers = new Register[this.writeCount];

         for (int register = 0; register < this.writeCount; register++) {
            this.registers[register] = new SimpleRegister(buffer[offset], buffer[offset + 1]);
            offset += 2;
         }
      } else {
         this.nonWordDataHandler.readData(input, this.writeReference, this.writeCount);
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] results = new byte[9 + 2 * this.getWriteWordCount()];
      results[0] = (byte)(this.readReference >> 8);
      results[1] = (byte)(this.readReference & 0xFF);
      results[2] = (byte)(this.readCount >> 8);
      results[3] = (byte)(this.readCount & 0xFF);
      results[4] = (byte)(this.writeReference >> 8);
      results[5] = (byte)(this.writeReference & 0xFF);
      results[6] = (byte)(this.writeCount >> 8);
      results[7] = (byte)(this.writeCount & 0xFF);
      results[8] = (byte)(this.writeCount * 2);
      int offset = 9;

      for (int i = 0; i < this.writeCount; i++) {
         Register reg = this.getRegister(i);
         byte[] bytes = reg.toBytes();
         results[offset++] = bytes[0];
         results[offset++] = bytes[1];
      }

      return results;
   }
}
