package okhttp3.internal.ws

import kotlin.jvm.internal.SourceDebugExtension
import okio.ByteString
import okio.Buffer.UnsafeCursor

@SourceDebugExtension(["SMAP\nWebSocketProtocol.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketProtocol.kt\nokhttp3/internal/ws/WebSocketProtocol\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,141:1\n1#2:142\n*E\n"])
object WebSocketProtocol private constructor() {
   @JvmStatic
   public WebSocketProtocol INSTANCE = new WebSocketProtocol();

   internal const val ACCEPT_MAGIC: String = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
   internal const val B0_FLAG_FIN: Int = 128
   internal const val B0_FLAG_RSV1: Int = 64
   internal const val B0_FLAG_RSV2: Int = 32
   internal const val B0_FLAG_RSV3: Int = 16
   internal const val B0_MASK_OPCODE: Int = 15
   internal const val B1_FLAG_MASK: Int = 128
   internal const val B1_MASK_LENGTH: Int = 127
   internal const val CLOSE_CLIENT_GOING_AWAY: Int = 1001
   internal const val CLOSE_MESSAGE_MAX: Long = 123L
   internal const val CLOSE_NO_STATUS_CODE: Int = 1005
   internal const val OPCODE_BINARY: Int = 2
   internal const val OPCODE_CONTINUATION: Int = 0
   internal const val OPCODE_CONTROL_CLOSE: Int = 8
   internal const val OPCODE_CONTROL_PING: Int = 9
   internal const val OPCODE_CONTROL_PONG: Int = 10
   internal const val OPCODE_FLAG_CONTROL: Int = 8
   internal const val OPCODE_TEXT: Int = 1
   internal const val PAYLOAD_BYTE_MAX: Long = 125L
   internal const val PAYLOAD_LONG: Int = 127
   internal const val PAYLOAD_SHORT: Int = 126
   internal const val PAYLOAD_SHORT_MAX: Long = 65535L


   public fun toggleMask(cursor: UnsafeCursor, key: ByteArray) {
      var var10: Int = 0;
      var keyLength: Int = key.length;

      do {
         var buffer: ByteArray = cursor.data;
         var i: Int = cursor.start;
         var end: Int = cursor.end;
         if (cursor.data != null) {
            while (i < end) {
               var10 %= keyLength;
               buffer[i] ^= key[var10];
               i++;
               var10++;
            }
         }
      } while (cursor.next() != -1);
   }

   public fun closeCodeExceptionMessage(code: Int): String? {
      return if (code >= 1000 && code < 5000)
         (if ((1004 > code || code >= 1007) && (1015 > code || code >= 3000)) null else "Code " + code + " is reserved and may not be used.")
         else
         "Code must be in range [1000,5000): " + code;
   }

   public fun validateCloseCode(code: Int) {
      var message: java.lang.String = this.closeCodeExceptionMessage(code);
      if (message != null) {
         throw new IllegalArgumentException(message.toString());
      }
   }

   public fun acceptHeader(key: String): String {
      return ByteString.Companion.encodeUtf8(key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").sha1().base64();
   }
}
