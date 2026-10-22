package org.eclipse.jetty.websocket.api;

public final class StatusCode {
   public static final int NORMAL = 1000;
   public static final int SHUTDOWN = 1001;
   public static final int PROTOCOL = 1002;
   public static final int BAD_DATA = 1003;
   public static final int UNDEFINED = 1004;
   public static final int NO_CODE = 1005;
   public static final int NO_CLOSE = 1006;
   public static final int ABNORMAL = 1006;
   public static final int BAD_PAYLOAD = 1007;
   public static final int POLICY_VIOLATION = 1008;
   public static final int MESSAGE_TOO_LARGE = 1009;
   public static final int REQUIRED_EXTENSION = 1010;
   public static final int SERVER_ERROR = 1011;
   public static final int SERVICE_RESTART = 1012;
   public static final int TRY_AGAIN_LATER = 1013;
   public static final int INVALID_UPSTREAM_RESPONSE = 1014;
   public static final int FAILED_TLS_HANDSHAKE = 1015;

   public static boolean isTransmittable(int statusCode) {
      return statusCode == 1000
         || statusCode == 1001
         || statusCode == 1002
         || statusCode == 1003
         || statusCode == 1007
         || statusCode == 1008
         || statusCode == 1009
         || statusCode == 1010
         || statusCode == 1011
         || statusCode == 1012
         || statusCode == 1013
         || statusCode == 1014;
   }
}
