package com.sun.javafx.util;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DataURI {
   private final String originalUri;
   private final String originalData;
   private final String mimeType;
   private final String mimeSubtype;
   private final Map<String, String> parameters;
   private final boolean base64;
   private final byte[] data;

   public static boolean matchScheme(String var0) {
      if (var0 != null && var0.length() >= 6) {
         var0 = var0.stripLeading();
         return var0.length() > 5 && "data:".equalsIgnoreCase(var0.substring(0, 5));
      } else {
         return false;
      }
   }

   public static DataURI tryParse(String var0) {
      if (!matchScheme(var0)) {
         return null;
      } else {
         var0 = var0.trim();
         int var1 = var0.indexOf(44, 5);
         if (var1 < 0) {
            throw new IllegalArgumentException("Invalid URI: " + var0);
         } else {
            String var2 = "text";
            String var3 = "plain";
            boolean var4 = false;
            String[] var5 = var0.substring(5, var1).split(";");
            Object var6 = Collections.emptyMap();
            if (var5.length > 0) {
               byte var7 = 0;
               int var8 = var5[0].indexOf(47);
               if (var8 > 0) {
                  var2 = var5[0].substring(0, var8);
                  var3 = var5[0].substring(var8 + 1);
                  var7 = 1;
               }

               for (int var9 = var7; var9 < var5.length; var9++) {
                  String var10 = var5[var9];
                  int var11 = var10.indexOf(61);
                  if (var11 < 0) {
                     if (var9 < var5.length - 1) {
                        throw new IllegalArgumentException("Invalid URI: " + var0);
                     }

                     var4 = "base64".equalsIgnoreCase(var5[var5.length - 1]);
                  } else {
                     if (var6.isEmpty()) {
                        var6 = new HashMap();
                     }

                     var6.put(var10.substring(0, var11).toLowerCase(), var10.substring(var11 + 1));
                  }
               }
            }

            String var13 = var0.substring(var1 + 1);
            Charset var14 = Charset.defaultCharset();
            return new DataURI(
               var0,
               var13,
               var2,
               var3,
               (Map<String, String>)var6,
               var4,
               var4 ? Base64.getDecoder().decode(var13) : URLDecoder.decode(var13.replace("+", "%2B"), var14).getBytes(var14)
            );
         }
      }
   }

   private DataURI(String var1, String var2, String var3, String var4, Map<String, String> var5, boolean var6, byte[] var7) {
      this.originalUri = var1;
      this.originalData = var2;
      this.mimeType = var3;
      this.mimeSubtype = var4;
      this.parameters = var5;
      this.base64 = var6;
      this.data = var7;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public String getMimeSubtype() {
      return this.mimeSubtype;
   }

   public Map<String, String> getParameters() {
      return this.parameters;
   }

   public boolean isBase64() {
      return this.base64;
   }

   public byte[] getData() {
      return this.data;
   }

   @Override
   public String toString() {
      return this.originalData.length() < 32
         ? this.originalUri
         : this.originalUri.substring(0, this.originalUri.length() - this.originalData.length())
            + this.originalData.substring(0, 14)
            + "..."
            + this.originalData.substring(this.originalData.length() - 14);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DataURI)) {
         return false;
      } else {
         DataURI var2 = (DataURI)var1;
         return this.base64 == var2.base64
            && Objects.equals(this.mimeType, var2.mimeType)
            && Objects.equals(this.mimeSubtype, var2.mimeSubtype)
            && Arrays.equals(this.data, var2.data);
      }
   }

   @Override
   public int hashCode() {
      int var1 = Objects.hash(this.mimeType, this.mimeSubtype, this.base64);
      return 31 * var1 + Arrays.hashCode(this.data);
   }
}
