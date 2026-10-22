package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import java.util.Objects;

public class CipSecurityItem extends CpfItem {
   public static final int TYPE_ID = 134;
   private final int securityProfiles;
   private final int cipSecurityState;
   private final int enipSecurityState;
   private final int ianaPortState;

   public CipSecurityItem(int securityProfiles, int cipSecurityState, int enipSecurityState, int ianaPortState) {
      super(134);
      this.securityProfiles = securityProfiles;
      this.cipSecurityState = cipSecurityState;
      this.enipSecurityState = enipSecurityState;
      this.ianaPortState = ianaPortState;
   }

   public int getSecurityProfiles() {
      return this.securityProfiles;
   }

   public int getCipSecurityState() {
      return this.cipSecurityState;
   }

   public int getEnipSecurityState() {
      return this.enipSecurityState;
   }

   public int getIanaPortState() {
      return this.ianaPortState;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CipSecurityItem that = (CipSecurityItem)o;
         return this.securityProfiles == that.securityProfiles
            && this.cipSecurityState == that.cipSecurityState
            && this.enipSecurityState == that.enipSecurityState
            && this.ianaPortState == that.ianaPortState;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.securityProfiles, this.cipSecurityState, this.enipSecurityState, this.ianaPortState);
   }

   public static ByteBuf encode(CipSecurityItem item, ByteBuf buffer) {
      buffer.writeShort(134);
      int lengthStartIndex = buffer.writerIndex();
      buffer.writeShort(0);
      int itemStartIndex = buffer.writerIndex();
      buffer.writeShort(item.getSecurityProfiles());
      buffer.writeByte(item.getCipSecurityState() & 0xFF);
      buffer.writeByte(item.getEnipSecurityState() & 0xFF);
      buffer.writeByte(item.getIanaPortState() & 0xFF);
      int bytesWritten = buffer.writerIndex() - itemStartIndex;
      buffer.markWriterIndex();
      buffer.writerIndex(lengthStartIndex);
      buffer.writeShort(bytesWritten);
      buffer.resetWriterIndex();
      return buffer;
   }

   public static CipSecurityItem decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();

      assert typeId == 134;

      buffer.skipBytes(2);
      return new CipSecurityItem(buffer.readShort(), buffer.readUnsignedByte(), buffer.readUnsignedByte(), buffer.readUnsignedByte());
   }
}
