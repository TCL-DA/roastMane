package kotlin.time

import java.util.concurrent.TimeUnit

// $VF: Class flags could not be determined
internal class DurationUnitKt__DurationUnitJvmKt {
   @SinceKotlin(version = "1.8")
   @WasExperimental(markerClass = [ExperimentalTime::class])
   @JvmStatic
   public fun DurationUnit.toTimeUnit(): TimeUnit {
      return `$this$toTimeUnit`.getTimeUnit$kotlin_stdlib();
   }

   @SinceKotlin(version = "1.8")
   @WasExperimental(markerClass = [ExperimentalTime::class])
   @JvmStatic
   public fun TimeUnit.toDurationUnit(): DurationUnit {
      var var10000: DurationUnit;
      switch (DurationUnitKt__DurationUnitJvmKt.WhenMappings.$EnumSwitchMapping$0[$this$toDurationUnit.ordinal()]) {
         case 1:
            var10000 = DurationUnit.NANOSECONDS;
            break;
         case 2:
            var10000 = DurationUnit.MICROSECONDS;
            break;
         case 3:
            var10000 = DurationUnit.MILLISECONDS;
            break;
         case 4:
            var10000 = DurationUnit.SECONDS;
            break;
         case 5:
            var10000 = DurationUnit.MINUTES;
            break;
         case 6:
            var10000 = DurationUnit.HOURS;
            break;
         case 7:
            var10000 = DurationUnit.DAYS;
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   internal fun convertDurationUnit(value: Double, sourceUnit: DurationUnit, targetUnit: DurationUnit): Double {
      var sourceInTargets: Long = targetUnit.getTimeUnit$kotlin_stdlib().convert(1L, sourceUnit.getTimeUnit$kotlin_stdlib());
      return if (sourceInTargets > 0L)
         value * (double)sourceInTargets
         else
         value / (double)sourceUnit.getTimeUnit$kotlin_stdlib().convert(1L, targetUnit.getTimeUnit$kotlin_stdlib());
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   internal fun convertDurationUnitOverflow(value: Long, sourceUnit: DurationUnit, targetUnit: DurationUnit): Long {
      return targetUnit.getTimeUnit$kotlin_stdlib().convert(value, sourceUnit.getTimeUnit$kotlin_stdlib());
   }

   @SinceKotlin(version = "1.5")
   @JvmStatic
   internal fun convertDurationUnit(value: Long, sourceUnit: DurationUnit, targetUnit: DurationUnit): Long {
      return targetUnit.getTimeUnit$kotlin_stdlib().convert(value, sourceUnit.getTimeUnit$kotlin_stdlib());
   }

   open fun DurationUnitKt__DurationUnitJvmKt() {
   }
}
