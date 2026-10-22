package com.sun.webkit.perf;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.webkit.security.WCMessageDigest;
import java.nio.ByteBuffer;

public class WCMessageDigestPerfLogger extends WCMessageDigest {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCMessageDigestPerfLogger.class.getName());
   private static final PerfLogger logger = PerfLogger.getLogger(log);
   private final WCMessageDigest digest;

   public WCMessageDigestPerfLogger(WCMessageDigest var1) {
      this.digest = var1;
   }

   public static synchronized boolean isEnabled() {
      return logger.isEnabled();
   }

   @Override
   public void addBytes(ByteBuffer var1) {
      logger.resumeCount("ADDBYTES");
      this.digest.addBytes(var1);
      logger.suspendCount("ADDBYTES");
   }

   @Override
   public byte[] computeHash() {
      logger.resumeCount("COMPUTEHASH");
      byte[] var1 = this.digest.computeHash();
      logger.suspendCount("COMPUTEHASH");
      return var1;
   }
}
