package com.ghgande.j2mod.modbus.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class BytesInputStream extends FastByteArrayInputStream implements DataInput {
   DataInputStream dataInputStream = new DataInputStream(this);

   public BytesInputStream(int size) {
      super(new byte[size]);
   }

   public BytesInputStream(byte[] data) {
      super(data);
   }

   public void reset(byte[] data) {
      this.pos = 0;
      this.mark = 0;
      this.buf = data;
      this.count = data.length;
   }

   public void reset(byte[] data, int length) {
      this.pos = 0;
      this.mark = 0;
      this.count = length;
      this.buf = data;
   }

   public void reset(int length) {
      this.pos = 0;
      this.count = length;
   }

   public int skip(int n) {
      this.mark(this.pos);
      this.pos += n;
      return n;
   }

   public synchronized byte[] getBuffer() {
      byte[] dest = new byte[this.buf.length];
      System.arraycopy(this.buf, 0, dest, 0, dest.length);
      return dest;
   }

   @Override
   public int getBufferLength() {
      return this.buf.length;
   }

   @Override
   public void readFully(byte[] b) throws IOException {
      this.dataInputStream.readFully(b);
   }

   @Override
   public void readFully(byte[] b, int off, int len) throws IOException {
      this.dataInputStream.readFully(b, off, len);
   }

   @Override
   public int skipBytes(int n) throws IOException {
      return this.dataInputStream.skipBytes(n);
   }

   @Override
   public boolean readBoolean() throws IOException {
      return this.dataInputStream.readBoolean();
   }

   @Override
   public byte readByte() throws IOException {
      return this.dataInputStream.readByte();
   }

   @Override
   public int readUnsignedByte() throws IOException {
      return this.dataInputStream.readUnsignedByte();
   }

   @Override
   public short readShort() throws IOException {
      return this.dataInputStream.readShort();
   }

   @Override
   public int readUnsignedShort() throws IOException {
      return this.dataInputStream.readUnsignedShort();
   }

   @Override
   public char readChar() throws IOException {
      return this.dataInputStream.readChar();
   }

   @Override
   public int readInt() throws IOException {
      return this.dataInputStream.readInt();
   }

   @Override
   public long readLong() throws IOException {
      return this.dataInputStream.readLong();
   }

   @Override
   public float readFloat() throws IOException {
      return this.dataInputStream.readFloat();
   }

   @Override
   public double readDouble() throws IOException {
      return this.dataInputStream.readDouble();
   }

   @Override
   public String readLine() throws IOException {
      throw new IOException("Not supported");
   }

   @Override
   public String readUTF() throws IOException {
      return this.dataInputStream.readUTF();
   }
}
