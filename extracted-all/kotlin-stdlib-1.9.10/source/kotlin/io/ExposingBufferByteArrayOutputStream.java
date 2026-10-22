package kotlin.io

import java.io.ByteArrayOutputStream

private class ExposingBufferByteArrayOutputStream(size: Int) : ByteArrayOutputStream(size) {
   public final val buffer: ByteArray
      public final get() {
         var var10000: ByteArray = this.buf;
         return var10000;
      }


}
