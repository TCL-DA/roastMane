package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import java.security.AlgorithmParameters;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.MGF1ParameterSpec;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PSource.PSpecified;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class RSA_OAEP_SHA2 {
   private static final String RSA_OEAP_256_JCA_ALG = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
   private static final String RSA_OEAP_384_JCA_ALG = "RSA/ECB/OAEPWithSHA-384AndMGF1Padding";
   private static final String RSA_OEAP_512_JCA_ALG = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
   private static final String SHA_256_JCA_ALG = "SHA-256";
   private static final String SHA_384_JCA_ALG = "SHA-384";
   private static final String SHA_512_JCA_ALG = "SHA-512";

   public static byte[] encryptCEK(RSAPublicKey pub, SecretKey cek, int shaBitSize, Provider provider) throws JOSEException {
      String jcaAlgName;
      String jcaShaAlgName;
      MGF1ParameterSpec mgf1ParameterSpec;
      if (256 == shaBitSize) {
         jcaAlgName = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
         jcaShaAlgName = "SHA-256";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA256;
      } else if (384 == shaBitSize) {
         jcaAlgName = "RSA/ECB/OAEPWithSHA-384AndMGF1Padding";
         jcaShaAlgName = "SHA-384";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA384;
      } else {
         if (512 != shaBitSize) {
            throw new JOSEException("Unsupported SHA-2 bit size: " + shaBitSize);
         }

         jcaAlgName = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
         jcaShaAlgName = "SHA-512";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA512;
      }

      try {
         AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
         AlgorithmParameterSpec paramSpec = new OAEPParameterSpec(jcaShaAlgName, "MGF1", mgf1ParameterSpec, PSpecified.DEFAULT);
         algp.init(paramSpec);
         Cipher cipher = CipherHelper.getInstance(jcaAlgName, provider);
         cipher.init(1, pub, algp);
         return cipher.doFinal(cek.getEncoded());
      } catch (IllegalBlockSizeException var10) {
         throw new JOSEException("RSA block size exception: The RSA key is too short, use a longer one", var10);
      } catch (Exception var11) {
         throw new JOSEException(var11.getMessage(), var11);
      }
   }

   public static SecretKey decryptCEK(PrivateKey priv, byte[] encryptedCEK, int shaBitSize, Provider provider) throws JOSEException {
      String jcaAlgName;
      String jcaShaAlgName;
      MGF1ParameterSpec mgf1ParameterSpec;
      if (256 == shaBitSize) {
         jcaAlgName = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
         jcaShaAlgName = "SHA-256";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA256;
      } else if (384 == shaBitSize) {
         jcaAlgName = "RSA/ECB/OAEPWithSHA-384AndMGF1Padding";
         jcaShaAlgName = "SHA-384";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA384;
      } else {
         if (512 != shaBitSize) {
            throw new JOSEException("Unsupported SHA-2 bit size: " + shaBitSize);
         }

         jcaAlgName = "RSA/ECB/OAEPWithSHA-512AndMGF1Padding";
         jcaShaAlgName = "SHA-512";
         mgf1ParameterSpec = MGF1ParameterSpec.SHA512;
      }

      try {
         AlgorithmParameters algp = AlgorithmParametersHelper.getInstance("OAEP", provider);
         AlgorithmParameterSpec paramSpec = new OAEPParameterSpec(jcaShaAlgName, "MGF1", mgf1ParameterSpec, PSpecified.DEFAULT);
         algp.init(paramSpec);
         Cipher cipher = CipherHelper.getInstance(jcaAlgName, provider);
         cipher.init(2, priv, algp);
         return new SecretKeySpec(cipher.doFinal(encryptedCEK), "AES");
      } catch (Exception var10) {
         throw new JOSEException(var10.getMessage(), var10);
      }
   }

   private RSA_OAEP_SHA2() {
   }
}
