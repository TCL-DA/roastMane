package com.nimbusds.jose.crypto.impl;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class CipherHelper {
   public static Cipher getInstance(String name, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
      return provider == null ? Cipher.getInstance(name) : Cipher.getInstance(name, provider);
   }
}
