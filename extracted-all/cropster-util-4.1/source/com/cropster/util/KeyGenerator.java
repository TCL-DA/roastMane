package com.cropster.util;

import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

public final class KeyGenerator {
   private KeyGenerator() {
   }

   public static String createKey() {
      try {
         javax.crypto.KeyGenerator keyGen = javax.crypto.KeyGenerator.getInstance("AES");
         keyGen.init(256);
         SecretKey secretKey = keyGen.generateKey();
         byte[] encoded = secretKey.getEncoded();
         return DatatypeConverter.printHexBinary(encoded).toLowerCase();
      } catch (NoSuchAlgorithmException var3) {
         throw new RuntimeException(var3);
      }
   }
}
