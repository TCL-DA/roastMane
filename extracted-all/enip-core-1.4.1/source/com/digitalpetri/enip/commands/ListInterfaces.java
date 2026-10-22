package com.digitalpetri.enip.commands;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;

public final class ListInterfaces extends Command {
   private final ListInterfaces.InterfaceInformation[] interfaces;

   public ListInterfaces(ListInterfaces.InterfaceInformation... interfaces) {
      super(CommandCode.ListInterfaces);
      this.interfaces = interfaces;
   }

   public ListInterfaces.InterfaceInformation[] getInterfaces() {
      return this.interfaces;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ListInterfaces that = (ListInterfaces)o;
         return Arrays.equals((Object[])this.interfaces, (Object[])that.interfaces);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.interfaces);
   }

   public static ByteBuf encode(ListInterfaces command, ByteBuf buffer) {
      if (command.getInterfaces().length != 0) {
         buffer.writeShort(command.getInterfaces().length);

         for (ListInterfaces.InterfaceInformation interfaceInformation : command.getInterfaces()) {
            ListInterfaces.InterfaceInformation.encode(interfaceInformation, buffer);
         }
      }

      return buffer;
   }

   public static ListInterfaces decode(ByteBuf buffer) {
      int itemCount = buffer.readableBytes() >= 2 ? buffer.readUnsignedShort() : 0;
      ListInterfaces.InterfaceInformation[] interfaces = new ListInterfaces.InterfaceInformation[itemCount];

      for (int i = 0; i < itemCount; i++) {
         interfaces[i] = ListInterfaces.InterfaceInformation.decode(buffer);
      }

      return new ListInterfaces(interfaces);
   }

   public static class InterfaceInformation {
      private final int itemId;
      private final byte[] data;

      public InterfaceInformation(int itemId, byte[] data) {
         this.itemId = itemId;
         this.data = data;
      }

      public int getItemId() {
         return this.itemId;
      }

      public byte[] getData() {
         return this.data;
      }

      public static ByteBuf encode(ListInterfaces.InterfaceInformation interfaceInformation, ByteBuf buffer) {
         buffer.writeShort(interfaceInformation.getItemId());
         buffer.writeShort(interfaceInformation.getData().length);
         buffer.writeBytes(interfaceInformation.getData());
         return buffer;
      }

      public static ListInterfaces.InterfaceInformation decode(ByteBuf buffer) {
         int itemId = buffer.readUnsignedShort();
         int dataLength = buffer.readUnsignedShort();
         byte[] data = new byte[dataLength];
         buffer.readBytes(data);
         return new ListInterfaces.InterfaceInformation(itemId, data);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ListInterfaces.InterfaceInformation that = (ListInterfaces.InterfaceInformation)o;
            return this.itemId == that.itemId && Arrays.equals(this.data, that.data);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int result = this.itemId;
         return 31 * result + Arrays.hashCode(this.data);
      }
   }
}
