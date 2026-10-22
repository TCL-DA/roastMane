package okhttp3.internal.http2

import okhttp3.internal.Util
import okio.ByteString

object Http2 private constructor() {
   @JvmStatic
   public Http2 INSTANCE = new Http2();
   @JvmStatic
   private java.lang.String[] FRAME_NAMES = new java.lang.String[]{
      "DATA", "HEADERS", "PRIORITY", "RST_STREAM", "SETTINGS", "PUSH_PROMISE", "PING", "GOAWAY", "WINDOW_UPDATE", "CONTINUATION"
   };
   @JvmStatic
   private java.lang.String[] FLAGS = new java.lang.String[64];
   @JvmStatic
   private java.lang.String[] BINARY;

   private final val BINARY: Array<String>
   public final val CONNECTION_PREFACE: ByteString = ByteString.Companion.encodeUtf8("PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n")
   private final val FLAGS: Array<String?>
   public const val FLAG_ACK: Int = 1
   public const val FLAG_COMPRESSED: Int = 32
   public const val FLAG_END_HEADERS: Int = 4
   public const val FLAG_END_PUSH_PROMISE: Int = 4
   public const val FLAG_END_STREAM: Int = 1
   public const val FLAG_NONE: Int = 0
   public const val FLAG_PADDED: Int = 8
   public const val FLAG_PRIORITY: Int = 32
   private final val FRAME_NAMES: Array<String>
   public const val INITIAL_MAX_FRAME_SIZE: Int = 16384
   public const val TYPE_CONTINUATION: Int = 9
   public const val TYPE_DATA: Int = 0
   public const val TYPE_GOAWAY: Int = 7
   public const val TYPE_HEADERS: Int = 1
   public const val TYPE_PING: Int = 6
   public const val TYPE_PRIORITY: Int = 2
   public const val TYPE_PUSH_PROMISE: Int = 5
   public const val TYPE_RST_STREAM: Int = 3
   public const val TYPE_SETTINGS: Int = 4
   public const val TYPE_WINDOW_UPDATE: Int = 8


   public fun frameLog(inbound: Boolean, streamId: Int, length: Int, type: Int, flags: Int): String {
      return Util.format(
         "%s 0x%08x %5d %-13s %s", if (inbound) "<<" else ">>", streamId, length, this.formattedType$okhttp(type), this.formatFlags(type, flags)
      );
   }

   internal fun formattedType(type: Int): String {
      return if (type < FRAME_NAMES.length) FRAME_NAMES[type] else Util.format("0x%02x", type);
   }

   public fun formatFlags(type: Int, flags: Int): String {
      if (flags == 0) {
         return "";
      } else {
         switch (type) {
            case 2:
            case 3:
            case 7:
            case 8:
               return BINARY[flags];
            case 4:
            case 6:
               return if (flags == 1) "ACK" else BINARY[flags];
            case 5:
            default:
               var var10000: java.lang.String;
               if (flags < FLAGS.length) {
                  var10000 = FLAGS[flags];
               } else {
                  var10000 = BINARY[flags];
               }

               return if (type == 5 && (flags and 4) != 0)
                  StringsKt.replace$default(var10000, "HEADERS", "PUSH_PROMISE", false, 4, null)
                  else
                  (if (type == 0 && (flags and 32) != 0) StringsKt.replace$default(var10000, "PRIORITY", "COMPRESSED", false, 4, null) else var10000);
         }
      }
   }

   @JvmStatic
   fun {
      var var8: Int = 0;

      var frameFlags: Array<java.lang.String>;
      for (frameFlags = new java.lang.String[256]; var8 < 256; var8++) {
         var prefixFlag: Array<Any> = new Object[1];
         var var10005: java.lang.String = Integer.toBinaryString(var8);
         prefixFlag[0] = var10005;
         frameFlags[var8] = StringsKt.replace$default(Util.format("%8s", prefixFlag), ' ', '0', false, 4, null);
      }

      BINARY = frameFlags;
      FLAGS[0] = "";
      FLAGS[1] = "END_STREAM";
      var var10: IntArray = new int[]{1};
      var var9: IntArray = var10;
      FLAGS[8] = "PADDED";

      for (int prefixFlag : var10) {
         FLAGS[var16 or 8] = FLAGS[var16] + "|PADDED";
      }

      FLAGS[4] = "END_HEADERS";
      FLAGS[32] = "PRIORITY";
      FLAGS[36] = "END_HEADERS|PRIORITY";

      var var13: IntArray;
      for (int frameFlag : var13) {
         for (int prefixFlag : prefixFlags) {
            FLAGS[prefixFlag or frameFlag] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag];
            FLAGS[prefixFlag or frameFlag or 8] = FLAGS[prefixFlag] + '|' + FLAGS[frameFlag] + "|PADDED";
         }
      }

      var var15: Int = 0;

      for (int var18 = FLAGS.length; i < var18; i++) {
         if (FLAGS[var15] == null) {
            FLAGS[var15] = BINARY[var15];
         }
      }
   }
}
