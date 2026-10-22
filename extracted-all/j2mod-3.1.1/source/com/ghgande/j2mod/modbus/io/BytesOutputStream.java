package com.ghgande.j2mod.modbus.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class BytesOutputStream extends FastByteArrayOutputStream implements DataOutput {
   private final DataOutputStream dataOutputStream;

   public BytesOutputStream(int size) {
      super(size);
      this.dataOutputStream = new DataOutputStream(this);
   }

   public BytesOutputStream(byte[] buffer) {
      this.buf = buffer;
      this.count = 0;
      this.dataOutputStream = new DataOutputStream(this);
   }

   public synchronized byte[] getBuffer() {
      byte[] dest = new byte[this.buf.length];
      System.arraycopy(this.buf, 0, dest, 0, dest.length);
      return dest;
   }

   @Override
   public void reset() {
      this.count = 0;
   }

   @Override
   public void writeBoolean(boolean v) throws IOException {
      this.dataOutputStream.writeBoolean(v);
   }

   @Override
   public void writeByte(int v) throws IOException {
      this.dataOutputStream.writeByte(v);
   }

   @Override
   public void writeShort(int v) throws IOException {
      this.dataOutputStream.writeShort(v);
   }

   @Override
   public void writeChar(int v) throws IOException {
      this.dataOutputStream.writeChar(v);
   }

   @Override
   public void writeInt(int v) throws IOException {
      this.dataOutputStream.writeInt(v);
   }

   @Override
   public void writeLong(long v) throws IOException {
      this.dataOutputStream.writeLong(v);
   }

   @Override
   public void writeFloat(float v) throws IOException {
      this.dataOutputStream.writeFloat(v);
   }

   @Override
   public void writeDouble(double v) throws IOException {
      this.dataOutputStream.writeDouble(v);
   }

   @Override
   public void writeBytes(String s) throws IOException {
      int len = s.length();

      for (int i = 0; i < len; i++) {
         this.write((byte)s.charAt(i));
      }
   }

   @Override
   public void writeChars(String s) throws IOException {
      this.dataOutputStream.writeChars(s);
   }

   @Override
   public void writeUTF(String str) throws IOException {
      this.dataOutputStream.writeUTF(str);
   }
}
