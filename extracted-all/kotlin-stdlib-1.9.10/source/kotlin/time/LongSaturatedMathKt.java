@file:SourceDebugExtension(["SMAP\nlongSaturatedMath.kt\nKotlin\n*S Kotlin\n*F\n+ 1 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n1#1,81:1\n80#1:82\n80#1:83\n80#1:84\n80#1:85\n80#1:86\n80#1:87\n*S KotlinDebug\n*F\n+ 1 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n14#1:82\n17#1:83\n36#1:84\n46#1:85\n53#1:86\n57#1:87\n*E\n"])

package kotlin.time

import kotlin.jvm.internal.SourceDebugExtension

internal fun saturatingAdd(value: Long, unit: DurationUnit, duration: Duration): Long {
   var durationInUnit: Long = Duration.toLong-impl(duration, unit);
   if ((value - 1L or 1L) == java.lang.Long.MAX_VALUE) {
      return checkInfiniteSumDefined-PjuGub4(value, duration, durationInUnit);
   } else if ((durationInUnit - 1L or 1L) == java.lang.Long.MAX_VALUE) {
      return saturatingAddInHalves-NuflL3o(value, unit, duration);
   } else {
      var result: Long = value + durationInUnit;
      if (((value xor value + durationInUnit) and (durationInUnit xor value + durationInUnit)) < 0L) {
         return if (value < 0L) java.lang.Long.MIN_VALUE else java.lang.Long.MAX_VALUE;
      } else {
         return result;
      }
   }
}

private fun checkInfiniteSumDefined(value: Long, duration: Duration, durationInUnit: Long): Long {
   if (Duration.isInfinite-impl(duration) && (value xor durationInUnit) < 0L) {
      throw new IllegalArgumentException("Summing infinities of different signs");
   } else {
      return value;
   }
}

private fun saturatingAddInHalves(value: Long, unit: DurationUnit, duration: Duration): Long {
   var half: Long = Duration.div-UwyO8pc(duration, 2);
   var halfInUnit: Long = Duration.toLong-impl(half, unit);
   return if ((halfInUnit - 1L or 1L) == java.lang.Long.MAX_VALUE)
      halfInUnit
      else
      saturatingAdd-NuflL3o(saturatingAdd-NuflL3o(value, unit, half), unit, Duration.minus-LRDsOJo(duration, half));
}

private fun infinityOfSign(value: Long): Duration {
   return if (value < 0L) Duration.Companion.getNEG_INFINITE-UwyO8pc$kotlin_stdlib() else Duration.Companion.getINFINITE-UwyO8pc();
}

internal fun saturatingDiff(valueNs: Long, origin: Long, unit: DurationUnit): Duration {
   return if ((origin - 1L or 1L) == java.lang.Long.MAX_VALUE)
      Duration.unaryMinus-UwyO8pc(infinityOfSign(origin))
      else
      saturatingFiniteDiff(valueNs, origin, unit);
}

internal fun saturatingOriginsDiff(origin1: Long, origin2: Long, unit: DurationUnit): Duration {
   if ((origin2 - 1L or 1L) == java.lang.Long.MAX_VALUE) {
      return if (origin1 == origin2) Duration.Companion.getZERO-UwyO8pc() else Duration.unaryMinus-UwyO8pc(infinityOfSign(origin2));
   } else {
      return if ((origin1 - 1L or 1L) == java.lang.Long.MAX_VALUE) infinityOfSign(origin1) else saturatingFiniteDiff(origin1, origin2, unit);
   }
}

private fun saturatingFiniteDiff(value1: Long, value2: Long, unit: DurationUnit): Duration {
   var result: Long = value1 - value2;
   if (((value1 - value2 xor value1) and (value1 - value2 xor value2).inv()) < 0L) {
      if (unit.compareTo(DurationUnit.MILLISECONDS) < 0) {
         var unitsInMilli: Long = DurationUnitKt.convertDurationUnit(1L, DurationUnit.MILLISECONDS, unit);
         return Duration.plus-LRDsOJo(
            DurationKt.toDuration(value1 / unitsInMilli - value2 / unitsInMilli, DurationUnit.MILLISECONDS),
            DurationKt.toDuration(value1 % unitsInMilli - value2 % unitsInMilli, unit)
         );
      } else {
         return Duration.unaryMinus-UwyO8pc(infinityOfSign(result));
      }
   } else {
      return DurationKt.toDuration(result, unit);
   }
}

internal inline fun Long.isSaturated(): Boolean {
   return (`$this$isSaturated` - 1L or 1L) == java.lang.Long.MAX_VALUE;
}

