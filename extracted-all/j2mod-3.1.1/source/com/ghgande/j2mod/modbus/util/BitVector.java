package com.ghgande.j2mod.modbus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitVector {
   private static final Logger logger = LoggerFactory.getLogger(BitVector.class);
   private static final int[] ODD_OFFSETS = new int[]{-1, -3, -5, -7};
   private static final int[] STRAIGHT_OFFSETS = new int[]{7, 5, 3, 1};
   private int size;
   private final byte[] data;
   private boolean msbAccess = false;

   public BitVector(int size) {
      this.size = size;
      if (size % 8 > 0) {
         size = size / 8 + 1;
      } else {
         size /= 8;
      }

      this.data = new byte[size];
   }

   public static BitVector createBitVector(byte[] data, int size) {
      BitVector bv = new BitVector(data.length * 8);
      bv.setBytes(data);
      bv.size = size;
      return bv;
   }

   public static BitVector createBitVector(byte[] data) {
      BitVector bv = new BitVector(data.length * 8);
      bv.setBytes(data);
      return bv;
   }

   public void toggleAccess(boolean b) {
      this.msbAccess = !this.msbAccess;
   }

   public boolean isLSBAccess() {
      return !this.msbAccess;
   }

   public boolean isMSBAccess() {
      return this.msbAccess;
   }

   public final synchronized byte[] getBytes() {
      byte[] dest = new byte[this.data.length];
      System.arraycopy(this.data, 0, dest, 0, dest.length);
      return dest;
   }

   public synchronized void setBytes(byte[] data) {
      System.arraycopy(data, 0, this.data, 0, data.length);
   }

   public void setBytes(byte[] data, int size) {
      System.arraycopy(data, 0, this.data, 0, data.length);
      this.size = size;
   }

   public boolean getBit(int index) throws IndexOutOfBoundsException {
      index = this.translateIndex(index);
      logger.debug("Get bit #{}", index);
      return (this.data[this.byteIndex(index)] & 1 << this.bitIndex(index)) != 0;
   }

   public void setBit(int index, boolean b) throws IndexOutOfBoundsException {
      index = this.translateIndex(index);
      logger.debug("Set bit #{}", index);
      int value = b ? 1 : 0;
      int byteNum = this.byteIndex(index);
      int bitNum = this.bitIndex(index);
      this.data[byteNum] = (byte)(this.data[byteNum] & ~(1 << bitNum) | (value & 1) << bitNum);
   }

   public int size() {
      return this.size;
   }

   public void forceSize(int size) {
      if (size > this.data.length * 8) {
         throw new IllegalArgumentException("Size exceeds byte[] store");
      } else {
         this.size = size;
      }
   }

   public int byteSize() {
      return this.data.length;
   }

   @Override
   public String toString() {
      StringBuilder sbuf = new StringBuilder();

      for (int i = 0; i < this.data.length; i++) {
         int numberOfBitsToPrint = 8;
         int remainingBits = this.size - i * 8;
         if (remainingBits < 8) {
            numberOfBitsToPrint = remainingBits;
         }

         sbuf.append(String.format("%" + numberOfBitsToPrint + "s", Integer.toBinaryString(this.data[i] & 255)).replace(' ', '0'));
         sbuf.append(" ");
      }

      return sbuf.toString();
   }

   private int byteIndex(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.data.length * 8) {
         return index / 8;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private int bitIndex(int index) throws IndexOutOfBoundsException {
      if (index >= 0 && index < this.data.length * 8) {
         return index % 8;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   private int translateIndex(int idx) {
      if (this.msbAccess) {
         int mod4 = idx % 4;
         int div4 = idx / 4;
         return div4 % 2 != 0 ? idx + ODD_OFFSETS[mod4] : idx + STRAIGHT_OFFSETS[mod4];
      } else {
         return idx;
      }
   }
}
