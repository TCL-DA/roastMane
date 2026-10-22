package com.cropster.util;

public final class SoftwareVersionParser {
   public static final int NUMBER_SCALING = 10000;
   private final int[] versionData = new int[]{0, 0, 0};
   private long comparableVersionNumber = 0L;
   private String versionString = "";

   private SoftwareVersionParser() {
   }

   public static SoftwareVersionParser forString(String version) {
      SoftwareVersionParser parser = new SoftwareVersionParser();
      parser.versionString = version;
      String[] versionDataInput = version.split("\\.");
      if (versionDataInput.length > 3) {
         throw new IllegalArgumentException("Illegal version string provided");
      } else {
         int partNumber = 0;
         long scaleFactor = 100000000L;

         for (String versionPart : versionDataInput) {
            int num = Integer.parseInt(versionPart);
            parser.comparableVersionNumber += (long)num * scaleFactor;
            scaleFactor /= 10000L;
            parser.versionData[partNumber++] = num;
         }

         return parser;
      }
   }

   public static SoftwareVersionParser forNumber(Long version) {
      SoftwareVersionParser parser = new SoftwareVersionParser();
      parser.comparableVersionNumber = version;
      int currentIndex = 2;

      while (true) {
         int versionPart = (int)(version % 10000L);
         parser.versionData[currentIndex] = versionPart;
         version = version / 10000L;
         if (currentIndex == 0) {
            if (version > 0L) {
               throw new IllegalArgumentException("Illegal version number provided");
            } else {
               parser.buildVersionString();
               return parser;
            }
         }

         currentIndex--;
      }
   }

   private void buildVersionString() {
      StringBuilder sb = new StringBuilder();
      sb.append(this.versionData[0]);
      if (this.versionData[2] > 0) {
         sb.append(".").append(this.versionData[1]).append(".").append(this.versionData[2]);
      } else if (this.versionData[1] > 0) {
         sb.append(".").append(this.versionData[1]);
      }

      this.versionString = sb.toString();
   }

   public long getComparableVersionNumber() {
      return this.comparableVersionNumber;
   }

   public int getMajorVersion() {
      return this.versionData[0];
   }

   public int getMinorVersion() {
      return this.versionData[1];
   }

   public int getRevision() {
      return this.versionData[2];
   }

   public String getVersionString() {
      return this.versionString;
   }
}
