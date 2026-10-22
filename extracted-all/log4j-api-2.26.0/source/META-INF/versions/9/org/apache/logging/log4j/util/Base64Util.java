package org.apache.logging.log4j.util;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Base64.Encoder;

public final class Base64Util {
   private static final Encoder encoder = Base64.getEncoder();

   private Base64Util() {
   }

   public static String encode(final String str) {
      return str != null ? encoder.encodeToString(str.getBytes(Charset.defaultCharset())) : null;
   }
}
