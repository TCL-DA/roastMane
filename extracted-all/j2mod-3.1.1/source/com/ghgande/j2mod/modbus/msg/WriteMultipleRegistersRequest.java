package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.io.NonWordDataHandler;
import com.ghgande.j2mod.modbus.net.AbstractModbusListener;
import com.ghgande.j2mod.modbus.procimg.IllegalAddressException;
import com.ghgande.j2mod.modbus.procimg.ProcessImage;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class WriteMultipleRegistersRequest extends ModbusRequest {
   private int reference;
   private Register[] registers;
   private NonWordDataHandler nonWordDataHandler = null;

   public WriteMultipleRegistersRequest(int first, Register[] registers) {
      this.setFunctionCode(16);
      this.setReference(first);
      this.setRegisters(registers);
      this.setDataLength(5 + registers.length * 2);
   }

   public WriteMultipleRegistersRequest() {
      this.setFunctionCode(16);
      this.setDataLength(5);
   }

   @Override
   public ModbusResponse getResponse() {
      return this.updateResponseWithHeader(new WriteMultipleRegistersResponse());
   }

   @Override
   public ModbusResponse createResponse(AbstractModbusListener listener) {
      WriteMultipleRegistersResponse response;
      if (this.nonWordDataHandler == null) {
         ProcessImage procimg = listener.getProcessImage(this.getUnitID());

         try {
            Register[] regs = procimg.getRegisterRange(this.getReference(), this.getWordCount());

            for (int i = 0; i < regs.length; i++) {
               regs[i].setValue(this.getRegister(i).getValue());
            }
         } catch (IllegalAddressException var6) {
            return this.createExceptionResponse(2);
         }

         response = (WriteMultipleRegistersResponse)this.getResponse();
         response.setReference(this.getReference());
         response.setWordCount(this.getWordCount());
      } else {
         int result = this.nonWordDataHandler.commitUpdate();
         if (result > 0) {
            return this.createExceptionResponse(result);
         }

         response = (WriteMultipleRegistersResponse)this.getResponse();
         response.setReference(this.getReference());
         response.setWordCount(this.nonWordDataHandler.getWordCount());
      }

      return response;
   }

   public int getReference() {
      return this.reference;
   }

   public void setReference(int ref) {
      this.reference = ref;
   }

   public synchronized Register[] getRegisters() {
      Register[] dest = new Register[this.registers.length];
      System.arraycopy(this.registers, 0, dest, 0, dest.length);
      return dest;
   }

   public synchronized void setRegisters(Register[] registers) {
      if (registers == null) {
         this.registers = null;
         this.setDataLength(5);
      } else {
         this.registers = Arrays.copyOf(registers, registers.length);
         this.setDataLength(5 + registers.length * 2);
      }
   }

   public Register getRegister(int index) throws IndexOutOfBoundsException {
      if (index < 0) {
         throw new IndexOutOfBoundsException(index + " < 0");
      } else if (index >= this.getWordCount()) {
         throw new IndexOutOfBoundsException(index + " > " + this.getWordCount());
      } else {
         return this.registers[index];
      }
   }

   public int getRegisterValue(int index) throws IndexOutOfBoundsException {
      return this.getRegister(index).toUnsignedShort();
   }

   public int getByteCount() {
      return this.getWordCount() * 2;
   }

   public int getWordCount() {
      return this.registers == null ? 0 : this.registers.length;
   }

   public NonWordDataHandler getNonWordDataHandler() {
      return this.nonWordDataHandler;
   }

   public void setNonWordDataHandler(NonWordDataHandler dhandler) {
      this.nonWordDataHandler = dhandler;
   }

   @Override
   public void writeData(DataOutput output) throws IOException {
      output.write(this.getMessage());
   }

   @Override
   public void readData(DataInput input) throws IOException {
      this.reference = input.readUnsignedShort();
      int registerCount = input.readUnsignedShort();
      int byteCount = input.readUnsignedByte();
      if (this.nonWordDataHandler == null) {
         byte[] buffer = new byte[byteCount];
         input.readFully(buffer, 0, byteCount);
         int offset = 0;
         this.registers = new Register[registerCount];

         for (int register = 0; register < registerCount; register++) {
            this.registers[register] = new SimpleRegister(buffer[offset], buffer[offset + 1]);
            offset += 2;
         }
      } else {
         this.nonWordDataHandler.readData(input, this.reference, registerCount);
      }
   }

   @Override
   public byte[] getMessage() {
      int len = 5;
      if (this.registers != null) {
         len += this.registers.length * 2;
      }

      byte[] result = new byte[len];
      int registerCount = this.registers != null ? this.registers.length : 0;
      result[0] = (byte)(this.reference >> 8 & 0xFF);
      result[1] = (byte)(this.reference & 0xFF);
      result[2] = (byte)(registerCount >> 8 & 0xFF);
      result[3] = (byte)(registerCount & 0xFF);
      result[4] = (byte)(registerCount * 2);
      int offset = 5;
      if (this.nonWordDataHandler == null) {
         for (int i = 0; i < registerCount; i++) {
            byte[] bytes = this.registers[i].toBytes();
            result[offset++] = bytes[0];
            result[offset++] = bytes[1];
         }
      } else {
         this.nonWordDataHandler.prepareData(this.reference, registerCount);
         byte[] bytes = this.nonWordDataHandler.getData();
         if (bytes != null) {
            int nonWordBytes = bytes.length;
            if (nonWordBytes > registerCount * 2) {
               nonWordBytes = registerCount * 2;
            }

            System.arraycopy(bytes, 0, result, offset, nonWordBytes);
         }
      }

      return result;
   }
}
