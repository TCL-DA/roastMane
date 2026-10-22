package org.eclipse.jetty.websocket.core;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.websocket.core.exception.BadPayloadException;
import org.eclipse.jetty.websocket.core.exception.ProtocolException;
import org.eclipse.jetty.websocket.core.internal.NullAppendable;

public class CloseStatus {
   public static final int NORMAL = 1000;
   public static final int SHUTDOWN = 1001;
   public static final int PROTOCOL = 1002;
   public static final int BAD_DATA = 1003;
   public static final int RESERVED = 1004;
   public static final int NO_CODE = 1005;
   public static final int NO_CLOSE = 1006;
   public static final int BAD_PAYLOAD = 1007;
   public static final int POLICY_VIOLATION = 1008;
   public static final int MESSAGE_TOO_LARGE = 1009;
   public static final int EXTENSION_ERROR = 1010;
   public static final int SERVER_ERROR = 1011;
   public static final int SERVICE_RESTART = 1012;
   public static final int TRY_AGAIN_LATER = 1013;
   public static final int BAD_GATEWAY = 1014;
   public static final int FAILED_TLS_HANDSHAKE = 1015;
   public static final CloseStatus NO_CODE_STATUS = new CloseStatus(1005);
   public static final CloseStatus NO_CLOSE_STATUS = new CloseStatus(1006);
   public static final CloseStatus NORMAL_STATUS = new CloseStatus(1000);
   static final int MAX_REASON_PHRASE = 123;
   private final int code;
   private final String reason;
   private final Throwable cause;

   public CloseStatus() {
      this(1005, null, null);
   }

   public CloseStatus(int statusCode) {
      this(statusCode, null, null);
   }

   public CloseStatus(int statusCode, String reasonPhrase) {
      this(statusCode, reasonPhrase, null);
   }

   public CloseStatus(int statusCode, Throwable cause) {
      this(statusCode, cause.getMessage(), cause);
   }

   public CloseStatus(int statusCode, String reasonPhrase, Throwable cause) {
      this.code = statusCode;
      this.cause = cause;
      if (reasonPhrase != null) {
         byte[] reasonBytes = truncateToFit(reasonPhrase.getBytes(StandardCharsets.UTF_8), 123);
         this.reason = new String(reasonBytes, StandardCharsets.UTF_8);
      } else {
         this.reason = null;
      }
   }

   public CloseStatus(Frame frame) {
      this(frame.getPayload());
   }

   public CloseStatus(ByteBuffer payload) {
      this.cause = null;
      int statusCode = 1005;
      if (payload != null && payload.remaining() != 0) {
         ByteBuffer data = payload.slice();
         if (data.remaining() == 1) {
            throw new ProtocolException("Invalid CLOSE payload");
         } else if (data.remaining() > 125) {
            throw new ProtocolException("Invalid control frame length of " + data.remaining() + " bytes");
         } else {
            if (data.remaining() >= 2) {
               int var8 = 0;
               var8 |= (data.get() & 255) << 8;
               statusCode = var8 | data.get() & 255;
               if (!isTransmittableStatusCode(statusCode)) {
                  throw new ProtocolException("Invalid CLOSE Code: " + statusCode);
               }

               if (data.remaining() > 0) {
                  int len = Math.min(data.remaining(), 123);
                  byte[] reasonBytes = new byte[len];
                  data.get(reasonBytes, 0, len);
                  Utf8StringBuilder utf = new Utf8StringBuilder();
                  utf.append(reasonBytes, 0, reasonBytes.length);
                  String reason = utf.takeCompleteString(BadPayloadException.InvalidUtf8::new);
                  this.code = statusCode;
                  this.reason = reason;
                  return;
               }
            }

            this.code = statusCode;
            this.reason = null;
         }
      } else {
         this.code = statusCode;
         this.reason = null;
      }
   }

   public static CloseStatus getCloseStatus(Frame frame) {
      if (frame instanceof CloseStatus.Supplier) {
         return ((CloseStatus.Supplier)frame).getCloseStatus();
      } else if (frame.getOpCode() == 8) {
         return new CloseStatus(frame);
      } else {
         throw new IllegalArgumentException("not a close frame");
      }
   }

   public static boolean isOrdinary(int closeCode) {
      return closeCode == 1000 || closeCode == 1005 || closeCode >= 3000;
   }

   public boolean isAbnormal() {
      return !isOrdinary(this.code);
   }

   public Throwable getCause() {
      return this.cause;
   }

   public int getCode() {
      return this.code;
   }

   public String getReason() {
      return this.reason;
   }

   public ByteBuffer asPayloadBuffer() {
      return asPayloadBuffer(this.code, this.reason);
   }

   public static ByteBuffer asPayloadBuffer(int statusCode, String reason) {
      if (!isTransmittableStatusCode(statusCode)) {
         throw new ProtocolException("Invalid close status code: " + statusCode);
      } else {
         int len = 2;
         byte[] reasonBytes = null;
         if (reason != null) {
            byte[] utf8Bytes = reason.getBytes(StandardCharsets.UTF_8);
            reasonBytes = truncateToFit(utf8Bytes, 123);
            if (reasonBytes.length > 0) {
               len += reasonBytes.length;
            }
         }

         ByteBuffer buf = BufferUtil.allocate(len);
         BufferUtil.flipToFill(buf);
         buf.put((byte)(statusCode >>> 8 & 0xFF));
         buf.put((byte)(statusCode & 0xFF));
         if (reasonBytes != null && reasonBytes.length > 0) {
            buf.put(reasonBytes, 0, reasonBytes.length);
         }

         BufferUtil.flipToFlush(buf, 0);
         return buf;
      }
   }

   private static byte[] truncateToFit(byte[] bytes, int maxBytes) {
      if (bytes.length <= maxBytes) {
         return bytes;
      } else {
         int lastIndex = -1;
         NullAppendable a = new NullAppendable();

         for (int i = 0; i < maxBytes; i++) {
            a.append(bytes[i]);
            if (a.isComplete()) {
               lastIndex = i;
            }
         }

         return Arrays.copyOf(bytes, lastIndex + 1);
      }
   }

   public static boolean isTransmittableStatusCode(int statusCode) {
      return (statusCode < 1000 || statusCode > 1003) && (statusCode < 1007 || statusCode > 1014) ? statusCode >= 3000 && statusCode < 5000 : true;
   }

   public Frame toFrame() {
      return isTransmittableStatusCode(this.code)
         ? new CloseStatus.CloseFrame((byte)8, true, asPayloadBuffer(this.code, this.reason))
         : new CloseStatus.CloseFrame((byte)8);
   }

   public static Frame toFrame(int closeStatus) {
      return new CloseStatus(closeStatus).toFrame();
   }

   public static Frame toFrame(int closeStatus, String reason) {
      return new CloseStatus(closeStatus, reason).toFrame();
   }

   public static String codeString(int closeStatus) {
      switch (closeStatus) {
         case 1000:
            return "NORMAL";
         case 1001:
            return "SHUTDOWN";
         case 1002:
            return "PROTOCOL";
         case 1003:
            return "BAD_DATA";
         case 1004:
            return "RESERVED";
         case 1005:
            return "NO_CODE";
         case 1006:
            return "NO_CLOSE";
         case 1007:
            return "BAD_PAYLOAD";
         case 1008:
            return "POLICY_VIOLATION";
         case 1009:
            return "MESSAGE_TOO_LARGE";
         case 1010:
            return "EXTENSION_ERROR";
         case 1011:
            return "SERVER_ERROR";
         case 1012:
            return "SERVICE_RESTART";
         case 1013:
            return "TRY_AGAIN_LATER";
         case 1014:
            return "BAD_GATEWAY";
         case 1015:
            return "FAILED_TLS_HANDSHAKE";
         default:
            return "UNKNOWN";
      }
   }

   public boolean isNormal() {
      return this.code == 1000;
   }

   @Override
   public String toString() {
      return String.format("{%04d=%s,%s}", this.code, codeString(this.code), this.reason);
   }

   class CloseFrame extends Frame implements CloseStatus.Supplier {
      public CloseFrame(byte opcode) {
         super(opcode);
      }

      public CloseFrame(byte opCode, boolean fin, ByteBuffer payload) {
         super(opCode, fin, payload);
      }

      @Override
      public CloseStatus getCloseStatus() {
         return CloseStatus.this;
      }

      @Override
      public String toString() {
         return super.toString() + ":" + CloseStatus.this.toString();
      }
   }

   public interface Supplier {
      CloseStatus getCloseStatus();
   }
}
