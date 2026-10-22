package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerUtils;
import com.nimbusds.jose.util.StandardCharset;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PBKDF2 {
   public static final int MIN_SALT_LENGTH = 8;
   static final byte[] ZERO_BYTE = new byte[]{0};
   static final long MAX_DERIVED_KEY_LENGTH = 4294967295L;

   public static byte[] formatSalt(JWEAlgorithm alg, byte[] salt) throws JOSEException {
      byte[] algBytes = alg.toString().getBytes(StandardCharset.UTF_8);
      if (salt == null) {
         throw new JOSEException("The salt must not be null");
      } else if (salt.length < 8) {
         throw new JOSEException("The salt must be at least 8 bytes long");
      } else {
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         try {
            out.write(algBytes);
            out.write(ZERO_BYTE);
            out.write(salt);
         } catch (IOException var5) {
            throw new JOSEException(var5.getMessage(), var5);
         }

         return out.toByteArray();
      }
   }

   public static SecretKey deriveKey(byte[] password, byte[] formattedSalt, int iterationCount, PRFParams prfParams) throws JOSEException {
      if (formattedSalt == null) {
         throw new JOSEException("The formatted salt must not be null");
      } else if (iterationCount < 1) {
         throw new JOSEException("The iteration count must be greater than 0");
      } else {
         SecretKey macKey = new SecretKeySpec(password, prfParams.getMACAlgorithm());
         Mac prf = HMAC.getInitMac(macKey, prfParams.getMacProvider());
         int hLen = prf.getMacLength();
         if ((long)prfParams.getDerivedKeyByteLength() > 4294967295L) {
            throw new JOSEException("Derived key too long: " + prfParams.getDerivedKeyByteLength());
         } else {
            int l = (int)Math.ceil((double)prfParams.getDerivedKeyByteLength() / (double)hLen);
            int r = prfParams.getDerivedKeyByteLength() - (l - 1) * hLen;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            for (int i = 0; i < l; i++) {
               byte[] block = extractBlock(formattedSalt, iterationCount, i + 1, prf);
               if (i == l - 1) {
                  block = ByteUtils.subArray(block, 0, r);
               }

               byteArrayOutputStream.write(block, 0, block.length);
            }

            return new SecretKeySpec(byteArrayOutputStream.toByteArray(), "AES");
         }
      }
   }

   static byte[] extractBlock(byte[] formattedSalt, int iterationCount, int blockIndex, Mac prf) throws JOSEException {
      if (formattedSalt == null) {
         throw new JOSEException("The formatted salt must not be null");
      } else if (iterationCount < 1) {
         throw new JOSEException("The iteration count must be greater than 0");
      } else {
         byte[] lastU = null;
         byte[] xorU = null;

         for (int i = 1; i <= iterationCount; i++) {
            byte[] currentU;
            if (i == 1) {
               byte[] inputBytes = ByteUtils.concat(formattedSalt, IntegerUtils.toBytes(blockIndex));
               currentU = prf.doFinal(inputBytes);
               xorU = currentU;
            } else {
               currentU = prf.doFinal(lastU);

               for (int j = 0; j < currentU.length; j++) {
                  xorU[j] ^= currentU[j];
               }
            }

            lastU = currentU;
         }

         return xorU;
      }
   }

   private PBKDF2() {
   }
}
