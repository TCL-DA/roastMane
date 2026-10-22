package okhttp3.internal.http

import java.text.DateFormat
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.internal.Util
import org.jetbrains.annotations.NotNull

private final val BROWSER_COMPATIBLE_DATE_FORMATS: Array<DateFormat?>
private final val BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS: Array<String>
internal const val MAX_DATE: Long = 253402300799999L
private final val STANDARD_DATE_FORMAT: <unrepresentable> = new ThreadLocal<DateFormat>() {
   @NotNull
   protected DateFormat initialValue() {
      var var1: SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
      var1.setLenient(false);
      var1.setTimeZone(Util.UTC);
      return var1;
   }
}
private java.lang.String[] BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS = new java.lang.String[]{
   "EEE, dd MMM yyyy HH:mm:ss zzz",
   "EEEE, dd-MMM-yy HH:mm:ss zzz",
   "EEE MMM d HH:mm:ss yyyy",
   "EEE, dd-MMM-yyyy HH:mm:ss z",
   "EEE, dd-MMM-yyyy HH-mm-ss z",
   "EEE, dd MMM yy HH:mm:ss z",
   "EEE dd-MMM-yyyy HH:mm:ss z",
   "EEE dd MMM yyyy HH:mm:ss z",
   "EEE dd-MMM-yyyy HH-mm-ss z",
   "EEE dd-MMM-yy HH:mm:ss z",
   "EEE dd MMM yy HH:mm:ss z",
   "EEE,dd-MMM-yy HH:mm:ss z",
   "EEE,dd-MMM-yyyy HH:mm:ss z",
   "EEE, dd-MM-yyyy HH:mm:ss z",
   "EEE MMM d yyyy HH:mm:ss z"
};
private DateFormat[] BROWSER_COMPATIBLE_DATE_FORMATS = new DateFormat[BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length];

internal fun String.toHttpDateOrNull(): Date? {
   if (`$this$toHttpDateOrNull`.length() == 0) {
      return null;
   } else {
      var position: ParsePosition = new ParsePosition(0);
      var var14: Date = STANDARD_DATE_FORMAT.get().parse(`$this$toHttpDateOrNull`, position);
      if (position.getIndex() == `$this$toHttpDateOrNull`.length()) {
         return var14;
      } else {
         label84: {
            var var3: Array<java.lang.String> = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS;
            synchronized (BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS){} // $VF: monitorenter 

            label44: {
               try {
                  var i: Int = 0;

                  for (int var6 = BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS.length; i < var6; i++) {
                     var format: DateFormat = BROWSER_COMPATIBLE_DATE_FORMATS[i];
                     if (BROWSER_COMPATIBLE_DATE_FORMATS[i] == null) {
                        var var8: SimpleDateFormat = new SimpleDateFormat(BROWSER_COMPATIBLE_DATE_FORMAT_STRINGS[i], Locale.US);
                        var8.setTimeZone(Util.UTC);
                        format = var8;
                        BROWSER_COMPATIBLE_DATE_FORMATS[i] = var8;
                     }

                     position.setIndex(0);
                     var14 = format.parse(`$this$toHttpDateOrNull`, position);
                     if (position.getIndex() != 0) {
                        break label44;
                     }
                  }
               } catch (java.lang.Throwable var12) {
                  // $VF: monitorexit
               }

               // $VF: monitorexit
            }

            // $VF: monitorexit
         }
      }
   }
}

internal fun Date.toHttpDateString(): String {
   var var10000: java.lang.String = STANDARD_DATE_FORMAT.get().format(`$this$toHttpDateString`);
   return var10000;
}

