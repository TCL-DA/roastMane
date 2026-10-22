package org.cropster.roastingintelligence.util;

public abstract class NetworkUtil {
   public static byte[] getSubnetMask(byte[] address, int subnetMaskLength) {
      int processedBits = 0;
      byte[] subnetMask = new byte[address.length];

      for (int i = 0; i < address.length; i++) {
         processedBits += 8;
         if (subnetMaskLength >= processedBits) {
            subnetMask[i] = address[i];
         } else if (subnetMaskLength > processedBits - 8) {
            int remainingBits = processedBits - subnetMaskLength;
            int byteMask = (byte)(255 << remainingBits);
            subnetMask[i] = (byte)(address[i] & byteMask);
            break;
         }
      }

      return subnetMask;
   }
}
