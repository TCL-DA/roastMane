package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;

public final class CipIdentityItem extends CpfItem {
   public static final int TYPE_ID = 12;
   private final int protocolVersion;
   private final SockAddr socketAddress;
   private final int vendorId;
   private final int deviceType;
   private final int productCode;
   private final short revisionMajor;
   private final short revisionMinor;
   private final short status;
   private final long serialNumber;
   private final String productName;
   private final short state;

   public CipIdentityItem(
      int protocolVersion,
      SockAddr socketAddress,
      int vendorId,
      int deviceType,
      int productCode,
      short revisionMajor,
      short revisionMinor,
      short status,
      long serialNumber,
      String productName,
      short state
   ) {
      super(12);
      this.protocolVersion = protocolVersion;
      this.socketAddress = socketAddress;
      this.vendorId = vendorId;
      this.deviceType = deviceType;
      this.productCode = productCode;
      this.revisionMajor = revisionMajor;
      this.revisionMinor = revisionMinor;
      this.status = status;
      this.serialNumber = serialNumber;
      this.productName = productName;
      this.state = state;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public SockAddr getSocketAddress() {
      return this.socketAddress;
   }

   public int getVendorId() {
      return this.vendorId;
   }

   public int getDeviceType() {
      return this.deviceType;
   }

   public int getProductCode() {
      return this.productCode;
   }

   public short getRevisionMajor() {
      return this.revisionMajor;
   }

   public short getRevisionMinor() {
      return this.revisionMinor;
   }

   public short getStatus() {
      return this.status;
   }

   public long getSerialNumber() {
      return this.serialNumber;
   }

   public String getProductName() {
      return this.productName;
   }

   public short getState() {
      return this.state;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CipIdentityItem that = (CipIdentityItem)o;
         return this.deviceType == that.deviceType
            && this.productCode == that.productCode
            && this.protocolVersion == that.protocolVersion
            && this.revisionMajor == that.revisionMajor
            && this.revisionMinor == that.revisionMinor
            && this.serialNumber == that.serialNumber
            && this.state == that.state
            && this.status == that.status
            && this.vendorId == that.vendorId
            && this.productName.equals(that.productName)
            && this.socketAddress.equals(that.socketAddress);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.protocolVersion;
      result = 31 * result + this.socketAddress.hashCode();
      result = 31 * result + this.vendorId;
      result = 31 * result + this.deviceType;
      result = 31 * result + this.productCode;
      result = 31 * result + this.revisionMajor;
      result = 31 * result + this.revisionMinor;
      result = 31 * result + this.status;
      result = 31 * result + (int)(this.serialNumber ^ this.serialNumber >>> 32);
      result = 31 * result + this.productName.hashCode();
      return 31 * result + this.state;
   }

   public static ByteBuf encode(CipIdentityItem item, ByteBuf buffer) {
      buffer.writeShort(12);
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      int itemStartIndex = buffer.writerIndex();
      buffer.writeShort(item.getProtocolVersion());
      SockAddr.encode(item.getSocketAddress(), buffer);
      buffer.writeShort(item.getVendorId());
      buffer.writeShort(item.getDeviceType());
      buffer.writeShort(item.getProductCode());
      buffer.writeByte(item.getRevisionMajor());
      buffer.writeByte(item.getRevisionMinor());
      buffer.writeShort(item.getStatus());
      buffer.writeInt((int)item.getSerialNumber());
      writeString(item.getProductName(), buffer);
      buffer.writeByte(item.getState());
      int bytesWritten = buffer.writerIndex() - itemStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return buffer;
   }

   public static CipIdentityItem decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      buffer.skipBytes(2);

      assert typeId == 12;

      return new CipIdentityItem(
         buffer.readUnsignedShort(),
         SockAddr.decode(buffer),
         buffer.readUnsignedShort(),
         buffer.readUnsignedShort(),
         buffer.readUnsignedShort(),
         buffer.readUnsignedByte(),
         buffer.readUnsignedByte(),
         buffer.readShort(),
         buffer.readUnsignedInt(),
         readString(buffer),
         buffer.readUnsignedByte()
      );
   }

   private static String readString(ByteBuf buffer) {
      int length = Math.min(buffer.readUnsignedByte(), 255);
      byte[] bs = new byte[length];
      buffer.readBytes(bs);
      return new String(bs, Charset.forName("US-ASCII"));
   }

   private static void writeString(String s, ByteBuf buffer) {
      int length = Math.min(s.length(), 255);
      buffer.writeByte(length);
      buffer.writeBytes(s.getBytes(Charset.forName("US-ASCII")), 0, length);
   }
}
