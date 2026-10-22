package org.eclipse.jetty.websocket.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.eclipse.jetty.websocket.core.WebSocketConstants;

public final class WebSocketUtils {
   public static String hashKey(String key) {
      try {
         MessageDigest md = MessageDigest.getInstance("SHA1");
         md.update(key.getBytes(StandardCharsets.UTF_8));
         md.update(WebSocketConstants.MAGIC);
         return Base64.getEncoder().encodeToString(md.digest());
      } catch (Exception var2) {
         throw new RuntimeException(var2);
      }
   }
}
