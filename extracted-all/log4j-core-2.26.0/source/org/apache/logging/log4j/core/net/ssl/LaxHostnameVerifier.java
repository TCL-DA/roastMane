package org.apache.logging.log4j.core.net.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;

public final class LaxHostnameVerifier implements HostnameVerifier {
   public static final HostnameVerifier INSTANCE = new LaxHostnameVerifier();

   private LaxHostnameVerifier() {
   }

   @SuppressFBWarnings({"WEAK_HOSTNAME_VERIFIER"})
   @Override
   public boolean verify(final String s, final SSLSession sslSession) {
      return true;
   }
}
