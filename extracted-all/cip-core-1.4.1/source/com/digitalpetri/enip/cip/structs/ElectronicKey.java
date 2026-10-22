package com.digitalpetri.enip.cip.structs;

import io.netty.buffer.ByteBuf;

public final class ElectronicKey {
   private final int vendorId;
   private final int deviceType;
   private final int productCode;
   private final boolean compatibilitySet;
   private final short majorRevision;
   private final short minorRevision;

   public ElectronicKey(int vendorId, int deviceType, int productCode, boolean compatibilitySet, short majorRevision, short minorRevision) {
      this.vendorId = vendorId;
      this.deviceType = deviceType;
      this.productCode = productCode;
      this.compatibilitySet = compatibilitySet;
      this.majorRevision = majorRevision;
      this.minorRevision = minorRevision;
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

   public boolean isCompatibilitySet() {
      return this.compatibilitySet;
   }

   public short getMajorRevision() {
      return this.majorRevision;
   }

   public short getMinorRevision() {
      return this.minorRevision;
   }

   public static ByteBuf encode(ElectronicKey key, ByteBuf buffer) {
      buffer.writeShort(key.getVendorId());
      buffer.writeShort(key.getDeviceType());
      buffer.writeShort(key.getProductCode());
      int majorRevisionAndCompatibility = key.isCompatibilitySet() ? 128 : 0;
      majorRevisionAndCompatibility |= key.getMajorRevision() & 127;
      buffer.writeByte(majorRevisionAndCompatibility);
      buffer.writeByte(key.getMinorRevision());
      return buffer;
   }
}
