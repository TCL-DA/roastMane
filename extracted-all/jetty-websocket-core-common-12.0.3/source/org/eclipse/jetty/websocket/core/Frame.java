package org.eclipse.jetty.websocket.core;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.StringUtil;

public class Frame {
   public static final int MAX_CONTROL_PAYLOAD = 125;
   protected byte finRsvOp;
   protected byte[] mask;
   protected ByteBuffer payload;

   public static Frame copyWithoutPayload(Frame original) {
      Frame copy = new Frame(original.getOpCode());
      copy.copyHeaders(original);
      return copy;
   }

   public static Frame copy(Frame original) {
      Frame copy = copyWithoutPayload(original);
      copy.setPayload(BufferUtil.copy(original.getPayload()));
      return copy;
   }

   public Frame(byte opcode) {
      this((byte)(128 | opcode & 15), null, null);
   }

   public Frame(byte opCode, ByteBuffer payload) {
      this(opCode);
      this.setPayload(payload);
   }

   public Frame(byte opCode, String payload) {
      this(opCode);
      this.setPayload(payload);
   }

   public Frame(byte opCode, boolean fin, ByteBuffer payload) {
      this(opCode, payload);
      this.setFin(fin);
   }

   public Frame(byte opCode, boolean fin, String payload) {
      this(opCode, payload);
      this.setFin(fin);
   }

   protected Frame() {
      this((byte)-1, null, null);
   }

   public Frame(byte finRsvOp, byte[] mask, ByteBuffer payload) {
      this.finRsvOp = finRsvOp;
      this.mask = mask;
      this.payload = payload;
   }

   public boolean isControlFrame() {
      return OpCode.isControlFrame(this.getOpCode());
   }

   public boolean isDataFrame() {
      return OpCode.isDataFrame(this.getOpCode());
   }

   protected void copyHeaders(Frame frame) {
      byte opCode = (byte)(this.finRsvOp & 15);
      this.finRsvOp = 0;
      this.finRsvOp = (byte)(this.finRsvOp | (frame.isFin() ? 128 : 0));
      this.finRsvOp = (byte)(this.finRsvOp | (frame.isRsv1() ? 64 : 0));
      this.finRsvOp = (byte)(this.finRsvOp | (frame.isRsv2() ? 32 : 0));
      this.finRsvOp = (byte)(this.finRsvOp | (frame.isRsv3() ? 16 : 0));
      this.finRsvOp |= opCode;
      if (frame.isMasked()) {
         this.mask = Arrays.copyOf(frame.getMask(), frame.getMask().length);
      } else {
         this.mask = null;
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Frame other = (Frame)obj;
         if (this.payload == null) {
            if (other.payload != null) {
               return false;
            }
         } else if (!this.payload.equals(other.payload)) {
            return false;
         }

         return this.finRsvOp != other.finRsvOp ? false : Arrays.equals(this.mask, other.mask);
      }
   }

   public byte[] getMask() {
      return this.mask;
   }

   public byte getOpCode() {
      return OpCode.getOpCode(this.finRsvOp);
   }

   public ByteBuffer getPayload() {
      return this.payload == null ? BufferUtil.EMPTY_BUFFER : this.payload;
   }

   public String getPayloadAsUTF8() {
      return this.payload == null ? "" : BufferUtil.toUTF8String(this.payload);
   }

   public int getPayloadLength() {
      return this.payload == null ? 0 : this.payload.remaining();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.payload == null ? 0 : this.payload.hashCode());
      result = 31 * result + this.finRsvOp;
      return 31 * result + Arrays.hashCode(this.mask);
   }

   public boolean hasPayload() {
      return this.getPayload().remaining() > 0;
   }

   public boolean isFin() {
      return (byte)(this.finRsvOp & 128) != 0;
   }

   public boolean isMasked() {
      return this.mask != null;
   }

   public boolean isRsv1() {
      return (byte)(this.finRsvOp & 64) != 0;
   }

   public boolean isRsv2() {
      return (byte)(this.finRsvOp & 32) != 0;
   }

   public boolean isRsv3() {
      return (byte)(this.finRsvOp & 16) != 0;
   }

   public void reset() {
      this.finRsvOp = -128;
      this.payload = null;
      this.mask = null;
   }

   public Frame setFin(boolean fin) {
      this.finRsvOp = (byte)(this.finRsvOp & 127 | (fin ? 128 : 0));
      return this;
   }

   public Frame setMask(byte[] maskingKey) {
      this.mask = maskingKey;
      return this;
   }

   protected Frame setOpCode(byte op) {
      this.finRsvOp = (byte)(this.finRsvOp & 240 | op & 15);
      return this;
   }

   public Frame setPayload(ByteBuffer buf) {
      this.payload = buf;
      return this;
   }

   public Frame setPayload(String str) {
      this.setPayload(ByteBuffer.wrap(StringUtil.getUtf8Bytes(str)));
      return this;
   }

   public Frame setPayload(byte[] buf) {
      this.setPayload(ByteBuffer.wrap(buf));
      return this;
   }

   public Frame setRsv1(boolean rsv1) {
      this.finRsvOp = (byte)(this.finRsvOp & 191 | (rsv1 ? 64 : 0));
      return this;
   }

   public Frame setRsv2(boolean rsv2) {
      this.finRsvOp = (byte)(this.finRsvOp & 223 | (rsv2 ? 32 : 0));
      return this;
   }

   public Frame setRsv3(boolean rsv3) {
      this.finRsvOp = (byte)(this.finRsvOp & 239 | (rsv3 ? 16 : 0));
      return this;
   }

   public Frame asReadOnly() {
      return new Frame.ReadOnly(this);
   }

   public boolean hasRsv() {
      return (this.finRsvOp & 112) != 0;
   }

   public void demask() {
      if (this.isMasked() && this.hasPayload()) {
         int maskInt = 0;

         for (byte maskByte : this.mask) {
            maskInt = (maskInt << 8) + (maskByte & 255);
         }

         int maskOffset = 0;
         int start = this.payload.position();
         int end = this.payload.limit();
         int offset = maskOffset;

         int remaining;
         while ((remaining = end - start) > 0) {
            if (remaining >= 4 && (offset & 3) == 0) {
               this.payload.putInt(start, this.payload.getInt(start) ^ maskInt);
               start += 4;
               offset += 4;
            } else {
               this.payload.put(start, (byte)(this.payload.get(start) ^ this.mask[offset & 3]));
               start++;
               offset++;
            }
         }

         Arrays.fill(this.mask, (byte)0);
      }
   }

   @Override
   public String toString() {
      StringBuilder b = new StringBuilder();
      b.append(OpCode.name(OpCode.getOpCode(this.finRsvOp)));
      b.append('@');
      b.append(Integer.toHexString(super.hashCode()));
      b.append('[');
      b.append("len=").append(this.getPayloadLength());
      b.append(",fin=").append((this.finRsvOp & 128) != 0);
      b.append(",rsv=");
      b.append((char)((this.finRsvOp & 64) != 0 ? '1' : '0'));
      b.append((char)((this.finRsvOp & 32) != 0 ? '1' : '0'));
      b.append((char)((this.finRsvOp & 16) != 0 ? '1' : '0'));
      b.append(",m=").append(this.mask == null ? "null" : StringUtil.toHexString(this.mask));
      b.append(']');
      if (this.payload != null) {
         b.append(BufferUtil.toDetailString(this.payload));
      }

      return b.toString();
   }

   public static class Parsed extends Frame implements Closeable, CloseStatus.Supplier {
      final CloseStatus closeStatus;
      final Runnable releaser;

      public Parsed(byte firstByte, byte[] mask, ByteBuffer payload, Runnable releaser) {
         super(firstByte, mask, payload);
         this.demask();
         this.releaser = releaser;
         if (this.getOpCode() == 8) {
            if (this.hasPayload()) {
               this.closeStatus = new CloseStatus(payload.duplicate());
            } else {
               this.closeStatus = CloseStatus.NO_CODE_STATUS;
            }
         } else {
            this.closeStatus = null;
         }
      }

      @Override
      public void close() {
         if (this.releaser != null) {
            this.releaser.run();
         }
      }

      @Override
      public CloseStatus getCloseStatus() {
         return this.closeStatus;
      }

      public boolean isReleaseable() {
         return this.releaser != null;
      }

      @Override
      public String toString() {
         return this.closeStatus == null ? super.toString() : super.toString() + ":" + this.closeStatus;
      }
   }

   private static class ReadOnly extends Frame {
      private ReadOnly(Frame frame) {
         super(frame.finRsvOp, frame.isMasked() ? frame.getMask() : null, frame.getPayload());
      }

      @Override
      public ByteBuffer getPayload() {
         ByteBuffer buffer = super.getPayload();
         return buffer == null ? null : buffer.asReadOnlyBuffer();
      }

      @Override
      protected void copyHeaders(Frame frame) {
         throw new UnsupportedOperationException();
      }

      @Override
      public void reset() {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setFin(boolean fin) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setMask(byte[] maskingKey) {
         throw new UnsupportedOperationException();
      }

      @Override
      protected Frame setOpCode(byte op) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setPayload(ByteBuffer buf) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setPayload(String str) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setPayload(byte[] buf) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setRsv1(boolean rsv1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setRsv2(boolean rsv2) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame setRsv3(boolean rsv3) {
         throw new UnsupportedOperationException();
      }

      @Override
      public Frame asReadOnly() {
         return this;
      }
   }
}
