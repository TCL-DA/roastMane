package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;

public abstract class CpfItem {
   private final int typeId;

   protected CpfItem(int typeId) {
      this.typeId = typeId;
   }

   public int getTypeId() {
      return this.typeId;
   }

   public static ByteBuf encode(CpfItem item, ByteBuf buffer) {
      switch (item.getTypeId()) {
         case 0:
            return NullAddressItem.encode((NullAddressItem)item, buffer);
         case 12:
            return CipIdentityItem.encode((CipIdentityItem)item, buffer);
         case 134:
            return CipSecurityItem.encode((CipSecurityItem)item, buffer);
         case 161:
            return ConnectedAddressItem.encode((ConnectedAddressItem)item, buffer);
         case 177:
            return ConnectedDataItemRequest.encode((ConnectedDataItemRequest)item, buffer);
         case 178:
            return UnconnectedDataItemRequest.encode((UnconnectedDataItemRequest)item, buffer);
         case 32768:
            return SockAddrItemO2t.encode((SockAddrItemO2t)item, buffer);
         case 32769:
            return SockAddrItemT2o.encode((SockAddrItemT2o)item, buffer);
         case 32770:
            return SequencedAddressItem.encode((SequencedAddressItem)item, buffer);
         default:
            throw new RuntimeException(String.format("unhandled item type: 0x%02X", item.getTypeId()));
      }
   }

   public static CpfItem decode(ByteBuf buffer) {
      int typeId = buffer.getUnsignedShort(buffer.readerIndex());
      switch (typeId) {
         case 0:
            return NullAddressItem.decode(buffer);
         case 12:
            return CipIdentityItem.decode(buffer);
         case 134:
            return CipSecurityItem.decode(buffer);
         case 161:
            return ConnectedAddressItem.decode(buffer);
         case 177:
            return ConnectedDataItemResponse.decode(buffer);
         case 178:
            return UnconnectedDataItemResponse.decode(buffer);
         case 32768:
            return SockAddrItemO2t.decode(buffer);
         case 32769:
            return SockAddrItemT2o.decode(buffer);
         case 32770:
            return SequencedAddressItem.decode(buffer);
         default:
            throw new RuntimeException(String.format("unhandled item type: 0x%02X", typeId));
      }
   }
}
