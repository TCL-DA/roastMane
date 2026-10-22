package com.yoctopuce.YoctoAPI;

import java.util.Date;

class YSSDPCacheEntry {
   private String mSerial;
   private String mUUID;
   private String mURL;
   private Date mDetectedTime;
   private long mMaxAgeInMS;
   private boolean mRegistered;

   YSSDPCacheEntry(String uuid, String URL, long maxAgeInMS) {
      this.mUUID = uuid;
      this.mURL = URL;
      this.mMaxAgeInMS = maxAgeInMS;
      this.mDetectedTime = new Date();
      this.mRegistered = false;
      StringBuilder serial = new StringBuilder(20);
      int i = 0;

      int j;
      for (j = 0; i < 4; j += 2) {
         String ch = uuid.substring(j, j + 2);
         serial.append((char)Integer.parseInt(ch, 16));
         i++;
      }

      j++;

      while (i < 6) {
         String ch = uuid.substring(j, j + 2);
         serial.append((char)Integer.parseInt(ch, 16));
         i++;
         j += 2;
      }

      j++;

      while (i < 8) {
         String ch = uuid.substring(j, j + 2);
         serial.append((char)Integer.parseInt(ch, 16));
         i++;
         j += 2;
      }

      serial.append('-');
      i = uuid.indexOf("-COFF-EE");
      i += 8;

      while (uuid.charAt(i) == '0') {
         i++;
      }

      String numPart = uuid.substring(i);

      for (int var11 = numPart.length(); var11 < 5; var11++) {
         serial.append('0');
      }

      serial.append(numPart);
      this.mSerial = serial.toString();
   }

   String getSerial() {
      return this.mSerial;
   }

   void setSerial(String serial) {
      this.mSerial = serial;
   }

   String getUUID() {
      return this.mUUID;
   }

   String getURL() {
      return this.mURL;
   }

   void setURL(String URL) {
      this.mURL = URL;
   }

   long getMaxAgeInMS() {
      return this.mMaxAgeInMS;
   }

   void resetExpiration(int cacheValidity) {
      this.mMaxAgeInMS = (long)cacheValidity;
      this.mDetectedTime = new Date();
   }

   boolean hasExpired() {
      Date now = new Date();
      return now.getTime() - this.mDetectedTime.getTime() > this.mMaxAgeInMS;
   }

   boolean isRegistered() {
      return this.mRegistered;
   }

   void setRegistered(boolean registered) {
      this.mRegistered = registered;
   }
}
