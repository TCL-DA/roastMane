package com.digitalpetri.enip.cip.epath;

import io.netty.buffer.ByteBuf;

public final class PortSegment extends EPathSegment {
   private final int portId;
   private final byte[] linkAddress;
   private static final int EXTENDED_LINKED_ADDRESS_SIZE = 16;

   public PortSegment(int portId, byte[] linkAddress) {
      this.portId = portId;
      this.linkAddress = linkAddress;
   }

   public int getPortId() {
      return this.portId;
   }

   public byte[] getLinkAddress() {
      return this.linkAddress;
   }

   public static ByteBuf encode(PortSegment segment, boolean padded, ByteBuf buffer) {
      int writerIndex = buffer.writerIndex();
      int linkAddressLength = segment.getLinkAddress().length;
      boolean needLinkAddressSize = linkAddressLength > 1;
      boolean needExtendedPort = segment.portId > 14;
      int segmentByte = needExtendedPort ? 15 : segment.getPortId();
      if (needLinkAddressSize) {
         segmentByte |= 16;
      }

      buffer.writeByte(segmentByte);
      if (needLinkAddressSize) {
         buffer.writeByte(linkAddressLength);
      }

      if (needExtendedPort) {
         buffer.writeShort(segment.getPortId());
      }

      buffer.writeBytes(segment.getLinkAddress());
      int bytesWritten = buffer.writerIndex() - writerIndex;
      if (bytesWritten % 2 != 0) {
         buffer.writeByte(0);
      }

      return buffer;
   }
}
