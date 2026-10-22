package kotlin.io.encoding

import java.io.InputStream
import java.io.OutputStream

// $VF: Class flags could not be determined
internal class StreamEncodingKt__Base64IOStreamKt {
   @SinceKotlin(version = "1.8")
   @ExperimentalEncodingApi
   @JvmStatic
   public fun InputStream.decodingWith(base64: Base64): InputStream {
      return new DecodeInputStream(`$this$decodingWith`, base64);
   }

   @SinceKotlin(version = "1.8")
   @ExperimentalEncodingApi
   @JvmStatic
   public fun OutputStream.encodingWith(base64: Base64): OutputStream {
      return new EncodeOutputStream(`$this$encodingWith`, base64);
   }

   open fun StreamEncodingKt__Base64IOStreamKt() {
   }
}
