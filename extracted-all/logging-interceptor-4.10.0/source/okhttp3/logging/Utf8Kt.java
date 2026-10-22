package okhttp3.logging

import java.io.EOFException
import okio.Buffer

internal fun Buffer.isProbablyUtf8(): Boolean {
   try {
      var _: Buffer = new Buffer();
      `$this$isProbablyUtf8`.copyTo(_, 0L, RangesKt.coerceAtMost(`$this$isProbablyUtf8`.size(), 64L));
      var var4: Int = 0;

      while (var4 < 16) {
         var4++;
         if (_.exhausted()) {
            break;
         }

         var codePoint: Int = _.readUtf8CodePoint();
         if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
            return false;
         }
      }

      return true;
   } catch (EOFException var7) {
      return false;
   }
}

