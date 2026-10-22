package com.ghgande.j2mod.modbus.msg;

import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.SimpleInputRegister;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ReadFIFOQueueResponse extends ModbusResponse {
   private int count;
   private InputRegister[] registers;

   public ReadFIFOQueueResponse() {
      this.setFunctionCode(24);
      this.count = 0;
      this.registers = new InputRegister[0];
      this.setDataLength(7);
   }

   public synchronized int getWordCount() {
      return this.count;
   }

   public synchronized void setWordCount(int ref) {
      if (ref >= 0 && ref <= 31) {
         this.count = ref;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public synchronized int[] getRegisters() {
      int[] values = new int[this.count];

      for (int i = 0; i < this.count; i++) {
         values[i] = this.getRegister(i);
      }

      return values;
   }

   public synchronized void setRegisters(InputRegister[] regs) {
      if (regs == null) {
         this.registers = null;
         this.count = 0;
      } else {
         this.registers = Arrays.copyOf(regs, regs.length);
         if (regs.length > 31) {
            throw new IllegalArgumentException();
         } else {
            this.count = regs.length;
         }
      }
   }

   public int getRegister(int index) {
      return this.registers[index].getValue();
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      din.readShort();
      this.count = din.readUnsignedShort();
      this.registers = new InputRegister[this.count];

      for (int i = 0; i < this.count; i++) {
         this.registers[i] = new SimpleInputRegister(din.readShort());
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[this.count * 2 + 4];
      int len = this.count * 2 + 2;
      result[0] = (byte)(len >> 8);
      result[1] = (byte)(len & 0xFF);
      result[2] = (byte)(this.count >> 8);
      result[3] = (byte)(this.count & 0xFF);

      for (int i = 0; i < this.count; i++) {
         byte[] value = this.registers[i].toBytes();
         result[i * 2 + 4] = value[0];
         result[i * 2 + 5] = value[1];
      }

      return result;
   }
}
