package okhttp3.internal.http2

import kotlin.jvm.internal.SourceDebugExtension

enum class ErrorCode {
   @JvmStatic
   NO_ERROR(0),
   @JvmStatic
   PROTOCOL_ERROR(1),
   @JvmStatic
   INTERNAL_ERROR(2),
   @JvmStatic
   FLOW_CONTROL_ERROR(3),
   @JvmStatic
   SETTINGS_TIMEOUT(4),
   @JvmStatic
   STREAM_CLOSED(5),
   @JvmStatic
   FRAME_SIZE_ERROR(6),
   @JvmStatic
   REFUSED_STREAM(7),
   @JvmStatic
   CANCEL(8),
   @JvmStatic
   COMPRESSION_ERROR(9),
   @JvmStatic
   CONNECT_ERROR(10),
   @JvmStatic
   ENHANCE_YOUR_CALM(11),
   @JvmStatic
   INADEQUATE_SECURITY(12),
   @JvmStatic
   HTTP_1_1_REQUIRED(13);
   @JvmStatic
   public ErrorCode.Companion Companion = new ErrorCode.Companion(null);
   private int httpCode;

   public final val httpCode: Int

   init {
      this.httpCode = httpCode;
   }

   @SourceDebugExtension(["SMAP\nErrorCode.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ErrorCode.kt\nokhttp3/internal/http2/ErrorCode$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,53:1\n1#2:54\n*E\n"])
   companion object Companion private constructor() {
      public fun fromHttp2(code: Int): ErrorCode? {
         var var2: Array<ErrorCode> = ErrorCode.values();
         var var3: Int = 0;
         var var4: Int = var2.length;

         var var10000: ErrorCode;
         while (true) {
            if (var3 >= var4) {
               var10000 = null;
               break;
            }

            var var5: ErrorCode = var2[var3];
            if (var2[var3].getHttpCode() == code) {
               var10000 = var5;
               break;
            }

            var3++;
         }

         return var10000;
      }
   }
}
