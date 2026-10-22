package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public final class SequencedAddressItem extends CpfItem {
   public static final int TYPE_ID = 32770;
   private final long connectionId;
   private final long sequenceNumber;
   private static final int ITEM_LENGTH = 8;

   public SequencedAddressItem(long connectionId, long sequenceNumber) {
      super(32770);
      this.connectionId = connectionId;
      this.sequenceNumber = sequenceNumber;
   }

   public long getConnectionId() {
      return this.connectionId;
   }

   public long getSequenceNumber() {
      return this.sequenceNumber;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SequencedAddressItem that = (SequencedAddressItem)o;
         return this.connectionId == that.connectionId && this.sequenceNumber == that.sequenceNumber;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = (int)(this.connectionId ^ this.connectionId >>> 32);
      return 31 * result + (int)(this.sequenceNumber ^ this.sequenceNumber >>> 32);
   }

   public static ByteBuf encode(SequencedAddressItem item, ByteBuf buffer) {
      buffer.writeShort(32770);
      buffer.writeShort(8);
      buffer.writeInt((int)item.getConnectionId());
      buffer.writeInt((int)item.getSequenceNumber());
      return buffer;
   }

   public static SequencedAddressItem decode(ByteBuf buffer) {
      int typeId = buffer.readUnsignedShort();
      int length = buffer.readUnsignedShort();
      long connectionId = buffer.readUnsignedInt();
      long sequenceNumber = buffer.readUnsignedInt();

      assert typeId == 32770;

      assert length == 8;

      return new SequencedAddressItem(connectionId, sequenceNumber);
   }
}
