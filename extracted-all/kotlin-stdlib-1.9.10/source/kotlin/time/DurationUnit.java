package kotlin.time

import java.util.concurrent.TimeUnit
import kotlin.enums.EnumEntries

@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
enum class DurationUnit {
   @JvmStatic
   NANOSECONDS(TimeUnit.NANOSECONDS),
   @JvmStatic
   MICROSECONDS(TimeUnit.MICROSECONDS),
   @JvmStatic
   MILLISECONDS(TimeUnit.MILLISECONDS),
   @JvmStatic
   SECONDS(TimeUnit.SECONDS),
   @JvmStatic
   MINUTES(TimeUnit.MINUTES),
   @JvmStatic
   HOURS(TimeUnit.HOURS),
   @JvmStatic
   DAYS(TimeUnit.DAYS);
   private TimeUnit timeUnit;

   internal final val timeUnit: TimeUnit

   init {
      this.timeUnit = timeUnit;
   }

   @JvmStatic
   fun getEntries(): EnumEntries<DurationUnit> {
      return $ENTRIES;
   }
}
