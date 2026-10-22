package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import java.nio.ByteOrder;
import java.util.Arrays;

public final class SockAddr {
   private final int sinFamily;
   private final int sinPort;
   private final byte[] sinAddr;
   private final long sinZero;

   public SockAddr(int sinFamily, int sinPort, byte[] sinAddr, long sinZero) {
      this.sinFamily = sinFamily;
      this.sinPort = sinPort;
      this.sinAddr = sinAddr;
      this.sinZero = sinZero;
   }

   public int getSinFamily() {
      return this.sinFamily;
   }

   public int getSinPort() {
      return this.sinPort;
   }

   public byte[] getSinAddr() {
      return this.sinAddr;
   }

   public long getSinZero() {
      return this.sinZero;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SockAddr sockAddr = (SockAddr)o;
         return this.sinFamily == sockAddr.sinFamily
            && this.sinPort == sockAddr.sinPort
            && this.sinZero == sockAddr.sinZero
            && Arrays.equals(this.sinAddr, sockAddr.sinAddr);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.sinFamily;
      result = 31 * result + this.sinPort;
      result = 31 * result + Arrays.hashCode(this.sinAddr);
      return 31 * result + (int)(this.sinZero ^ this.sinZero >>> 32);
   }

   public static ByteBuf encode(SockAddr sockAddr, ByteBuf buffer) {
      buffer.order(ByteOrder.BIG_ENDIAN).writeShort(sockAddr.getSinFamily());
      buffer.order(ByteOrder.BIG_ENDIAN).writeShort(sockAddr.getSinPort());
      buffer.order(ByteOrder.BIG_ENDIAN).writeBytes(sockAddr.getSinAddr());
      buffer.order(ByteOrder.BIG_ENDIAN).writeLong(sockAddr.getSinZero());
      return buffer;
   }

   public static SockAddr decode(ByteBuf buffer) {
      int sinFamily = buffer.order(ByteOrder.BIG_ENDIAN).readUnsignedShort();
      int sinPort = buffer.order(ByteOrder.BIG_ENDIAN).readUnsignedShort();
      byte[] sinAddr = new byte[4];
      buffer.order(ByteOrder.BIG_ENDIAN).readBytes(sinAddr);
      long sinZero = buffer.order(ByteOrder.BIG_ENDIAN).readLong();
      return new SockAddr(sinFamily, sinPort, sinAddr, sinZero);
   }
}
