@file:SourceDebugExtension(["SMAP\nDurationJvm.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DurationJvm.kt\nkotlin/time/DurationJvmKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,33:1\n1#2:34\n*E\n"])

package kotlin.time

import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.jvm.internal.SourceDebugExtension

internal final val durationAssertionsEnabled: Boolean = Duration.class.desiredAssertionStatus()
private final val precisionFormats: Array<ThreadLocal<DecimalFormat>>
private ThreadLocal<DecimalFormat>[] precisionFormats;

private fun createFormatForDecimals(decimals: Int): DecimalFormat {
   var var1: DecimalFormat = new DecimalFormat("0");
   if (decimals > 0) {
      var1.setMinimumFractionDigits(decimals);
   }

   var1.setRoundingMode(RoundingMode.HALF_UP);
   return var1;
}

internal fun formatToExactDecimals(value: Double, decimals: Int): String {
   var var9: DecimalFormat;
   if (decimals < precisionFormats.length) {
      var var4: ThreadLocal = precisionFormats[decimals];
      var var5: Any = precisionFormats[decimals].get();
      var var10000: Any;
      if (var5 == null) {
         var var8: DecimalFormat = createFormatForDecimals(decimals);
         var4.set(var8);
         var10000 = var8;
      } else {
         var10000 = var5;
      }

      var9 = var10000 as DecimalFormat;
   } else {
      var9 = createFormatForDecimals(decimals);
   }

   var var10: java.lang.String = var9.format(value);
   return var10;
}

internal fun formatUpToDecimals(value: Double, decimals: Int): String {
   var var3: DecimalFormat = createFormatForDecimals(0);
   var3.setMaximumFractionDigits(decimals);
   var var10000: java.lang.String = var3.format(value);
   return var10000;
}

