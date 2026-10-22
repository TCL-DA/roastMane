package com.sun.javafx.iio;

import java.util.Arrays;
import java.util.List;

public interface ImageFormatDescription {
   String getFormatName();

   List<String> getExtensions();

   List<ImageFormatDescription.Signature> getSignatures();

   public static final class Signature {
      private final byte[] bytes;

      public Signature(byte... var1) {
         this.bytes = var1;
      }

      public int getLength() {
         return this.bytes.length;
      }

      public boolean matches(byte[] var1) {
         if (var1.length < this.bytes.length) {
            return false;
         } else {
            for (int var2 = 0; var2 < this.bytes.length; var2++) {
               if (var1[var2] != this.bytes[var2]) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public int hashCode() {
         return Arrays.hashCode(this.bytes);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else {
            return !(var1 instanceof ImageFormatDescription.Signature) ? false : Arrays.equals(this.bytes, ((ImageFormatDescription.Signature)var1).bytes);
         }
      }
   }
}
