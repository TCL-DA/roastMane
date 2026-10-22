package com.ghgande.j2mod.modbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ReadCommEventLogResponse extends ModbusResponse {
   private int byteCount;
   private int status;
   private int eventCount;
   private int messageCount;
   private byte[] events;

   public ReadCommEventLogResponse() {
      this.setFunctionCode(12);
      this.setDataLength(7);
   }

   public int getStatus() {
      return this.status;
   }

   public void setStatus(int status) {
      this.status = status;
   }

   public int getEventCount() {
      return this.eventCount;
   }

   public void setEventCount(int count) {
      this.eventCount = count;
   }

   public int getMessageCount() {
      return this.messageCount;
   }

   public void setMessageCount(int count) {
      this.messageCount = count;
   }

   public int getEvent(int index) {
      if (this.events != null && index >= 0 && index < this.events.length) {
         return this.events[index] & 0xFF;
      } else {
         throw new IndexOutOfBoundsException("index = " + index + ", limit = " + (this.events == null ? "null" : this.events.length));
      }
   }

   public byte[] getEvents() {
      if (this.events == null) {
         return null;
      } else {
         byte[] result = new byte[this.events.length];
         System.arraycopy(this.events, 0, result, 0, this.events.length);
         return result;
      }
   }

   public void setEvents(byte[] events) {
      if (events.length > 64) {
         throw new IllegalArgumentException("events list too big (> 64 bytes)");
      } else {
         events = new byte[events.length];
         if (events.length > 0) {
            System.arraycopy(events, 0, events, 0, events.length);
         }
      }
   }

   public void setEvents(int count) {
      if (count >= 0 && count <= 64) {
         this.events = new byte[count];
      } else {
         throw new IllegalArgumentException("invalid event list size (0 <= count <= 64)");
      }
   }

   public void setEvent(int index, int event) {
      if (this.events != null && index >= 0 && index < this.events.length) {
         this.events[index] = (byte)event;
      } else {
         throw new IndexOutOfBoundsException("index = " + index + ", limit = " + (this.events == null ? "null" : this.events.length));
      }
   }

   @Override
   public void writeData(DataOutput dout) throws IOException {
      dout.write(this.getMessage());
   }

   @Override
   public void readData(DataInput din) throws IOException {
      this.byteCount = din.readByte();
      this.status = din.readUnsignedShort();
      this.eventCount = din.readUnsignedShort();
      this.messageCount = din.readUnsignedShort();
      this.events = new byte[this.byteCount - 6];
      if (this.events.length > 0) {
         din.readFully(this.events, 0, this.events.length);
      }
   }

   @Override
   public byte[] getMessage() {
      byte[] result = new byte[this.events.length + 7];
      this.byteCount = this.events.length + 6;
      result[0] = (byte)this.byteCount;
      result[1] = (byte)(this.status >> 8);
      result[2] = (byte)(this.status & 0xFF);
      result[3] = (byte)(this.eventCount >> 8);
      result[4] = (byte)(this.eventCount & 0xFF);
      result[5] = (byte)(this.messageCount >> 8);
      result[6] = (byte)(this.messageCount & 0xFF);
      System.arraycopy(this.events, 0, result, 7, this.events.length);
      return result;
   }
}
