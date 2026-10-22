package org.cropster.roastingintelligence.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
   public static final Pattern PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)+)(?:\\-(\\p{Alpha}+\\p{Alnum}+)(?:[\\.\\-](\\d+))?)?");
   private final String _version;
   private final Integer[] _versionArray;
   private final String _classifier;
   private final Integer _classifierVersion;

   public Version(String versionString) {
      Matcher matcher = PATTERN.matcher(versionString.toLowerCase().trim());
      if (!matcher.matches()) {
         throw new IllegalArgumentException("Version string \"" + versionString + "\" not supported!");
      } else {
         this._version = matcher.group(1);
         List<Integer> versionPartList = new ArrayList<>();
         String[] versionParts = this._version.split("\\.");

         for (String versionPart : versionParts) {
            versionPartList.add(Integer.valueOf(versionPart));
         }

         if (versionPartList.size() < 2) {
            throw new IllegalArgumentException("Illegal version - MAJOR.MINOR is required: " + this._version);
         } else {
            this._versionArray = versionPartList.toArray(new Integer[versionPartList.size()]);
            if (matcher.group(2) != null) {
               this._classifier = matcher.group(2);
               if (matcher.group(3) != null) {
                  this._classifierVersion = Integer.valueOf(matcher.group(3));
               } else {
                  this._classifierVersion = null;
               }
            } else {
               this._classifier = null;
               this._classifierVersion = null;
            }
         }
      }
   }

   public String getVersion() {
      return this._version;
   }

   public String getClassifier() {
      return this._classifier;
   }

   public Integer getClassifierVersion() {
      return this._classifierVersion;
   }

   @Override
   public String toString() {
      String s = this._version;
      if (this._classifier != null) {
         s = s + "-" + this._classifier;
         if (this._classifierVersion != null) {
            s = s + "-" + this._classifierVersion;
         }
      }

      return s;
   }

   public int compareTo(Version o) {
      int result = 0;

      for (int i = 0; i < Math.max(this._versionArray.length, o._versionArray.length); i++) {
         int v1 = this._versionArray.length > i ? this._versionArray[i] : 0;
         int v2 = o._versionArray.length > i ? o._versionArray[i] : 0;
         result = v1 - v2;
         if (result != 0) {
            return result;
         }
      }

      if (result == 0) {
         if (this._classifier == null) {
            if (o._classifier != null) {
               result = 1;
            }
         } else if (o._classifier == null) {
            result = -1;
         } else {
            result = this._classifier.compareTo(o._classifier);
         }

         if (result == 0) {
            if (this._classifierVersion == null) {
               if (o._classifierVersion != null) {
                  result = -1;
               }
            } else if (o._classifierVersion == null) {
               result = 1;
            } else {
               result = this._classifierVersion.compareTo(o._classifierVersion);
            }
         }
      }

      return result;
   }

   public int compareMajorAndMinorVersionTo(Version o) {
      int result = 0;

      for (int i = 0; i < 2; i++) {
         int v1 = this._versionArray.length > i ? this._versionArray[i] : 0;
         int v2 = o._versionArray.length > i ? o._versionArray[i] : 0;
         result = v1 - v2;
         if (result != 0) {
            return result;
         }
      }

      return result;
   }
}
