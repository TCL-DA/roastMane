package kotlin.io

import java.io.File

private fun constructMessage(file: File, other: File?, reason: String?): String {
   var sb: StringBuilder = new StringBuilder(file.toString());
   if (other != null) {
      sb.append(" -> " + other);
   }

   if (reason != null) {
      sb.append(": " + reason);
   }

   var var10000: java.lang.String = sb.toString();
   return var10000;
}

@JvmSynthetic
fun `access$constructMessage`(file: File, other: File, reason: java.lang.String): java.lang.String {
   return constructMessage(file, other, reason);
}

