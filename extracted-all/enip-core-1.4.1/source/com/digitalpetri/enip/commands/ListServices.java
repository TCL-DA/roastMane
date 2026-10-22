package com.digitalpetri.enip.commands;

import io.netty.buffer.ByteBuf;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

public final class ListServices extends Command {
   private final ListServices.ServiceInformation[] services;

   public ListServices(ListServices.ServiceInformation... services) {
      super(CommandCode.ListServices);
      this.services = services;
   }

   public ListServices.ServiceInformation[] getServices() {
      return this.services;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ListServices that = (ListServices)o;
         return Arrays.equals((Object[])this.services, (Object[])that.services);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.services);
   }

   public static ByteBuf encode(ListServices command, ByteBuf buffer) {
      if (command.getServices().length != 0) {
         buffer.writeShort(command.getServices().length);

         for (ListServices.ServiceInformation serviceInformation : command.getServices()) {
            ListServices.ServiceInformation.encode(serviceInformation, buffer);
         }
      }

      return buffer;
   }

   public static ListServices decode(ByteBuf buffer) {
      int itemCount = buffer.readableBytes() >= 2 ? buffer.readUnsignedShort() : 0;
      ListServices.ServiceInformation[] services = new ListServices.ServiceInformation[itemCount];

      for (int i = 0; i < itemCount; i++) {
         services[i] = ListServices.ServiceInformation.decode(buffer);
      }

      return new ListServices(services);
   }

   public static class ServiceInformation {
      private final int typeCode;
      private final int version;
      private final int capabilityFlags;
      private final String name;

      public ServiceInformation(int typeCode, int version, int capabilityFlags, String name) {
         this.typeCode = typeCode;
         this.version = version;
         this.capabilityFlags = capabilityFlags;
         this.name = name;
      }

      public int getTypeCode() {
         return this.typeCode;
      }

      public int getVersion() {
         return this.version;
      }

      public int getCapabilityFlags() {
         return this.capabilityFlags;
      }

      public String getName() {
         return this.name;
      }

      public static ByteBuf encode(ListServices.ServiceInformation serviceInformation, ByteBuf buffer) {
         buffer.writeShort(serviceInformation.getTypeCode());
         buffer.writeShort(20);
         buffer.writeShort(serviceInformation.getVersion());
         buffer.writeShort(serviceInformation.getCapabilityFlags());
         writeString(serviceInformation.getName(), buffer);
         return buffer;
      }

      public static ListServices.ServiceInformation decode(ByteBuf buffer) {
         int typeCode = buffer.readUnsignedShort();
         int itemLength = buffer.readShort();
         int version = buffer.readShort();
         int capabilityFlags = buffer.readShort();
         String name = readString(buffer, itemLength - 4).trim();
         return new ListServices.ServiceInformation(typeCode, version, capabilityFlags, name);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            ListServices.ServiceInformation that = (ListServices.ServiceInformation)o;
            return this.typeCode == that.typeCode
               && this.version == that.version
               && this.capabilityFlags == that.capabilityFlags
               && this.name.equals(that.name);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.getTypeCode(), this.getVersion(), this.getCapabilityFlags(), this.getName());
      }

      private static String readString(ByteBuf buffer, int length) {
         length = Math.min(Math.min(length, 255), buffer.readableBytes());
         byte[] bs = new byte[length];
         buffer.readBytes(bs);
         return new String(bs, Charset.forName("US-ASCII"));
      }

      private static void writeString(String s, ByteBuf buffer) {
         byte[] fullBytes = new byte[16];
         byte[] bytes = s.getBytes(Charset.forName("US-ASCII"));
         System.arraycopy(bytes, 0, fullBytes, 0, Math.min(bytes.length, 16));
         buffer.writeBytes(fullBytes);
      }
   }
}
