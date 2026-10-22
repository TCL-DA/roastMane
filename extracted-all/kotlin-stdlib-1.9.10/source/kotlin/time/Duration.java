package kotlin.time

import kotlin.contracts.InvocationKind
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

@JvmInline
@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
@SourceDebugExtension(["SMAP\nDuration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Duration.kt\nkotlin/time/Duration\n+ 2 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1495:1\n38#1:1496\n38#1:1497\n38#1:1498\n38#1:1499\n38#1:1500\n683#1,2:1501\n700#1,2:1510\n163#2,6:1503\n1#3:1509\n*S KotlinDebug\n*F\n+ 1 Duration.kt\nkotlin/time/Duration\n*L\n39#1:1496\n40#1:1497\n458#1:1498\n478#1:1499\n662#1:1500\n979#1:1501,2\n1070#1:1510,2\n1021#1:1503,6\n*E\n"])
inline class Duration : java.lang.Comparable<Duration> {
   @JvmStatic
   public Duration.Companion Companion = new Duration.Companion(null);
   @JvmStatic
   private long ZERO = constructor-impl(0L);
   @JvmStatic
   private long INFINITE = DurationKt.access$durationOfMillis(4611686018427387903L);
   @JvmStatic
   private long NEG_INFINITE = DurationKt.access$durationOfMillis(-4611686018427387903L);

   public final val absoluteValue: Duration
      public final get() {
         return if (isNegative-impl(arg0)) unaryMinus-UwyO8pc(arg0) else arg0;
      }

   internal final val hoursComponent: Int
      internal final get() {
         return if (isInfinite-impl(arg0)) 0 else (int)(getInWholeHours-impl(arg0) % (long)24);
      }

   public final val inDays: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.DAYS);
      }

   public final val inHours: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.HOURS);
      }

   public final val inMicroseconds: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.MICROSECONDS);
      }

   public final val inMilliseconds: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.MILLISECONDS);
      }

   public final val inMinutes: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.MINUTES);
      }

   public final val inNanoseconds: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.NANOSECONDS);
      }

   public final val inSeconds: Double
      public final get() {
         return toDouble-impl(arg0, DurationUnit.SECONDS);
      }

   public final val inWholeDays: Long
      public final get() {
         return toLong-impl(arg0, DurationUnit.DAYS);
      }

   public final val inWholeHours: Long
      public final get() {
         return toLong-impl(arg0, DurationUnit.HOURS);
      }

   public final val inWholeMicroseconds: Long
      public final get() {
         return toLong-impl(arg0, DurationUnit.MICROSECONDS);
      }

   public final val inWholeMilliseconds: Long
      public final get() {
         return if (isInMillis-impl(arg0) && isFinite-impl(arg0)) getValue-impl(arg0) else toLong-impl(arg0, DurationUnit.MILLISECONDS);
      }

   public final val inWholeMinutes: Long
      public final get() {
         return toLong-impl(arg0, DurationUnit.MINUTES);
      }

   public final val inWholeNanoseconds: Long
      public final get() {
         var value: Long = getValue-impl(arg0);
         return if (isInNanos-impl(arg0))
            value
            else
            (
               if (value > 9223372036854L)
                  java.lang.Long.MAX_VALUE
                  else
                  (if (value < -9223372036854L) java.lang.Long.MIN_VALUE else DurationKt.access$millisToNanos(value))
            );
      }

   public final val inWholeSeconds: Long
      public final get() {
         return toLong-impl(arg0, DurationUnit.SECONDS);
      }

   internal final val minutesComponent: Int
      internal final get() {
         return if (isInfinite-impl(arg0)) 0 else (int)(getInWholeMinutes-impl(arg0) % (long)60);
      }

   internal final val nanosecondsComponent: Int
      internal final get() {
         return if (isInfinite-impl(arg0))
            0
            else
            (
               if (isInMillis-impl(arg0))
                  (int)DurationKt.access$millisToNanos(getValue-impl(arg0) % (long)1000)
                  else
                  (int)(getValue-impl(arg0) % (long)1000000000)
            );
      }

   private final val rawValue: Long
   internal final val secondsComponent: Int
      internal final get() {
         return if (isInfinite-impl(arg0)) 0 else (int)(getInWholeSeconds-impl(arg0) % (long)60);
      }

   private final val storageUnit: DurationUnit
      private final get() {
         return if (isInNanos-impl(arg0)) DurationUnit.NANOSECONDS else DurationUnit.MILLISECONDS;
      }

   private final val unitDiscriminator: Int
      private final inline get() {
         return (int)arg0 and 1;
      }

   private final val value: Long
      private final get() {
         return arg0 shr 1;
      }


   @JvmStatic
   private fun isInNanos(): Boolean {
      return ((int)arg0 and 1) == 0;
   }

   @JvmStatic
   private fun isInMillis(): Boolean {
      return ((int)arg0 and 1) == 1;
   }

   @JvmStatic
   public operator fun unaryMinus(): Duration {
      return DurationKt.access$durationOf(-getValue-impl(arg0), (int)arg0 and 1);
   }

   @JvmStatic
   public operator fun plus(other: Duration): Duration {
      if (isInfinite-impl(arg0)) {
         if (!isFinite-impl(other) && (arg0 xor other) < 0L) {
            throw new IllegalArgumentException("Summing infinite durations of different signs yields an undefined result.");
         } else {
            return arg0;
         }
      } else if (isInfinite-impl(other)) {
         return other;
      } else {
         var var8: Long;
         if (((int)arg0 and 1) == ((int)other and 1)) {
            var var7: Long = getValue-impl(arg0) + getValue-impl(other);
            var8 = if (isInNanos-impl(arg0)) DurationKt.access$durationOfNanosNormalized(var7) else DurationKt.access$durationOfMillisNormalized(var7);
         } else {
            var8 = if (isInMillis-impl(arg0))
               addValuesMixedRanges-UwyO8pc(arg0, getValue-impl(arg0), getValue-impl(other))
               else
               addValuesMixedRanges-UwyO8pc(arg0, getValue-impl(other), getValue-impl(arg0));
         }

         return var8;
      }
   }

   @JvmStatic
   private fun addValuesMixedRanges(thisMillis: Long, otherNanos: Long): Duration {
      var otherMillis: Long = DurationKt.access$nanosToMillis(otherNanos);
      return if (new LongRange(-4611686018426L, 4611686018426L).contains(thisMillis + otherMillis))
         DurationKt.access$durationOfNanos(
            DurationKt.access$millisToNanos(thisMillis + otherMillis) + (otherNanos - DurationKt.access$millisToNanos(otherMillis))
         )
         else
         DurationKt.access$durationOfMillis(RangesKt.coerceIn(thisMillis + otherMillis, -4611686018427387903L, 4611686018427387903L));
   }

   @JvmStatic
   public operator fun minus(other: Duration): Duration {
      return plus-LRDsOJo(arg0, unaryMinus-UwyO8pc(other));
   }

   @JvmStatic
   public operator fun times(scale: Int): Duration {
      if (isInfinite-impl(arg0)) {
         if (scale == 0) {
            throw new IllegalArgumentException("Multiplying infinite duration by zero yields an undefined result.");
         } else {
            return if (scale > 0) arg0 else unaryMinus-UwyO8pc(arg0);
         }
      } else if (scale == 0) {
         return ZERO;
      } else {
         var value: Long = getValue-impl(arg0);
         var result: Long = value * (long)scale;
         var var10000: Long;
         if (isInNanos-impl(arg0)) {
            if (new LongRange(-2147483647L, 2147483647L).contains(value)) {
               var10000 = DurationKt.access$durationOfNanos(result);
            } else if (result / (long)scale == value) {
               var10000 = DurationKt.access$durationOfNanosNormalized(result);
            } else {
               var millis: Long = DurationKt.access$nanosToMillis(value);
               var totalMillis: Long = millis * (long)scale + DurationKt.access$nanosToMillis((value - DurationKt.access$millisToNanos(millis)) * (long)scale);
               var10000 = if (millis * (long)scale / (long)scale == millis && (totalMillis xor millis * (long)scale) >= 0L)
                  DurationKt.access$durationOfMillis(RangesKt.coerceIn(totalMillis, new LongRange(-4611686018427387903L, 4611686018427387903L)))
                  else
                  (if (MathKt.getSign(value) * MathKt.getSign(scale) > 0) INFINITE else NEG_INFINITE);
            }
         } else {
            var10000 = if (result / (long)scale == value)
               DurationKt.access$durationOfMillis(RangesKt.coerceIn(result, new LongRange(-4611686018427387903L, 4611686018427387903L)))
               else
               (if (MathKt.getSign(value) * MathKt.getSign(scale) > 0) INFINITE else NEG_INFINITE);
         }

         return var10000;
      }
   }

   @JvmStatic
   public operator fun times(scale: Double): Duration {
      var intScale: Int = MathKt.roundToInt(scale);
      if ((double)intScale == scale) {
         return times-UwyO8pc(arg0, intScale);
      } else {
         var unit: DurationUnit = getStorageUnit-impl(arg0);
         return DurationKt.toDuration(toDouble-impl(arg0, unit) * scale, unit);
      }
   }

   @JvmStatic
   public operator fun div(scale: Int): Duration {
      if (scale == 0) {
         var var10000: Long;
         if (isPositive-impl(arg0)) {
            var10000 = INFINITE;
         } else {
            if (!isNegative-impl(arg0)) {
               throw new IllegalArgumentException("Dividing zero duration by zero yields an undefined result.");
            }

            var10000 = NEG_INFINITE;
         }

         return var10000;
      } else if (isInNanos-impl(arg0)) {
         return DurationKt.access$durationOfNanos(getValue-impl(arg0) / (long)scale);
      } else {
         label24:
         if (isInfinite-impl(arg0)) {
            return times-UwyO8pc(arg0, MathKt.getSign(scale));
         } else {
            var result: Long = getValue-impl(arg0) / (long)scale;
            return if (new LongRange(-4611686018426L, 4611686018426L).contains(result))
               DurationKt.access$durationOfNanos(
                  DurationKt.access$millisToNanos(result) + DurationKt.access$millisToNanos(getValue-impl(arg0) - result * (long)scale) / (long)scale
               )
               else
               DurationKt.access$durationOfMillis(result);
         }
      }
   }

   @JvmStatic
   public operator fun div(scale: Double): Duration {
      var intScale: Int = MathKt.roundToInt(scale);
      if ((double)intScale == scale && intScale != 0) {
         return div-UwyO8pc(arg0, intScale);
      } else {
         var unit: DurationUnit = getStorageUnit-impl(arg0);
         return DurationKt.toDuration(toDouble-impl(arg0, unit) / scale, unit);
      }
   }

   @JvmStatic
   public operator fun div(other: Duration): Double {
      var coarserUnit: DurationUnit = ComparisonsKt.maxOf(getStorageUnit-impl(arg0), getStorageUnit-impl(other));
      return toDouble-impl(arg0, coarserUnit) / toDouble-impl(other, coarserUnit);
   }

   @JvmStatic
   internal fun truncateTo(unit: DurationUnit): Duration {
      var storageUnit: DurationUnit = getStorageUnit-impl(arg0);
      return if (unit.compareTo(storageUnit) > 0 && !isInfinite-impl(arg0))
         DurationKt.toDuration(getValue-impl(arg0) - getValue-impl(arg0) % DurationUnitKt.convertDurationUnit(1L, unit, storageUnit), storageUnit)
         else
         arg0;
   }

   @JvmStatic
   public fun isNegative(): Boolean {
      return arg0 < 0L;
   }

   @JvmStatic
   public fun isPositive(): Boolean {
      return arg0 > 0L;
   }

   @JvmStatic
   public fun isInfinite(): Boolean {
      return arg0 == INFINITE || arg0 == NEG_INFINITE;
   }

   @JvmStatic
   public fun isFinite(): Boolean {
      return !isInfinite-impl(arg0);
   }

   @JvmStatic
   public open operator fun compareTo(other: Duration): Int {
      if ((arg0 xor other) >= 0L && ((int)(arg0 xor other) and 1) != 0) {
         return if (isNegative-impl(arg0)) -(((int)arg0 and 1) - ((int)other and 1)) else ((int)arg0 and 1) - ((int)other and 1);
      } else {
         return Intrinsics.compare(arg0, other);
      }
   }

   fun `compareTo-LRDsOJo`(other: Long): Int {
      return compareTo-LRDsOJo(this.rawValue, other);
   }

   @JvmStatic
   public inline fun <T> toComponents(action: (Long, Int, Int, Int, Int) -> T): T {
      contract {
         callsInPlace(action, InvocationKind.EXACTLY_ONCE)
      }

      return (T)action.invoke(
         getInWholeDays-impl(arg0),
         getHoursComponent-impl(arg0),
         getMinutesComponent-impl(arg0),
         getSecondsComponent-impl(arg0),
         getNanosecondsComponent-impl(arg0)
      );
   }

   @JvmStatic
   public inline fun <T> toComponents(action: (Long, Int, Int, Int) -> T): T {
      contract {
         callsInPlace(action, InvocationKind.EXACTLY_ONCE)
      }

      return (T)action.invoke(getInWholeHours-impl(arg0), getMinutesComponent-impl(arg0), getSecondsComponent-impl(arg0), getNanosecondsComponent-impl(arg0));
   }

   @JvmStatic
   public inline fun <T> toComponents(action: (Long, Int, Int) -> T): T {
      contract {
         callsInPlace(action, InvocationKind.EXACTLY_ONCE)
      }

      return (T)action.invoke(getInWholeMinutes-impl(arg0), getSecondsComponent-impl(arg0), getNanosecondsComponent-impl(arg0));
   }

   @JvmStatic
   public inline fun <T> toComponents(action: (Long, Int) -> T): T {
      contract {
         callsInPlace(action, InvocationKind.EXACTLY_ONCE)
      }

      return (T)action.invoke(getInWholeSeconds-impl(arg0), getNanosecondsComponent-impl(arg0));
   }

   @JvmStatic
   public fun toDouble(unit: DurationUnit): Double {
      return if (arg0 == INFINITE)
         java.lang.Double.POSITIVE_INFINITY
         else
         (
            if (arg0 == NEG_INFINITE)
               java.lang.Double.NEGATIVE_INFINITY
               else
               DurationUnitKt.convertDurationUnit((double)getValue-impl(arg0), getStorageUnit-impl(arg0), unit)
         );
   }

   @JvmStatic
   public fun toLong(unit: DurationUnit): Long {
      return if (arg0 == INFINITE)
         java.lang.Long.MAX_VALUE
         else
         (if (arg0 == NEG_INFINITE) java.lang.Long.MIN_VALUE else DurationUnitKt.convertDurationUnit(getValue-impl(arg0), getStorageUnit-impl(arg0), unit));
   }

   @JvmStatic
   public fun toInt(unit: DurationUnit): Int {
      return (int)RangesKt.coerceIn(toLong-impl(arg0, unit), -2147483648L, 2147483647L);
   }

   @JvmStatic
   public open fun toString(): String {
      var var10000: java.lang.String;
      if (arg0 == 0L) {
         var10000 = "0s";
      } else if (arg0 == INFINITE) {
         var10000 = "Infinity";
      } else if (arg0 == NEG_INFINITE) {
         var10000 = "-Infinity";
      } else {
         var isNegative: Boolean = isNegative-impl(arg0);
         var var5: StringBuilder = new StringBuilder();
         if (isNegative) {
            var5.append('-');
         }

         var `arg0$iv`: Long = getAbsoluteValue-UwyO8pc(arg0);
         var var23: Long = getInWholeDays-impl(`arg0$iv`);
         var var10001: Int = getHoursComponent-impl(`arg0$iv`);
         var var10002: Int = getMinutesComponent-impl(`arg0$iv`);
         var nanoseconds: Int = getNanosecondsComponent-impl(`arg0$iv`);
         var seconds: Int = getSecondsComponent-impl(`arg0$iv`);
         var hasDays: Boolean = var23 != 0L;
         var hasHours: Boolean = var10001 != 0;
         var hasMinutes: Boolean = var10002 != 0;
         var hasSeconds: Boolean = seconds != 0 || nanoseconds != 0;
         if (hasDays) {
            var5.append(var23).append('d');
            0++;
         }

         if (hasHours || hasDays && (hasMinutes || hasSeconds)) {
            if (0++ > 0) {
               var5.append(' ');
            }

            var5.append(var10001).append('h');
         }

         if (hasMinutes || hasSeconds && (hasHours || hasDays)) {
            if (0++ > 0) {
               var5.append(' ');
            }

            var5.append(var10002).append('m');
         }

         if (hasSeconds) {
            if (0++ > 0) {
               var5.append(' ');
            }

            if (seconds != 0 || hasDays || hasHours || hasMinutes) {
               appendFractional-impl(arg0, var5, seconds, nanoseconds, 9, "s", false);
            } else if (nanoseconds >= 1000000) {
               appendFractional-impl(arg0, var5, nanoseconds / 1000000, nanoseconds % 1000000, 6, "ms", false);
            } else if (nanoseconds >= 1000) {
               appendFractional-impl(arg0, var5, nanoseconds / 1000, nanoseconds % 1000, 3, "us", false);
            } else {
               var5.append(nanoseconds).append("ns");
            }
         }

         if (isNegative && 0 > 1) {
            var5.insert(1, '(').append(')');
         }

         var10000 = var5.toString();
      }

      return var10000;
   }

   override fun toString(): java.lang.String {
      return toString-impl(this.rawValue);
   }

   @JvmStatic
   private fun StringBuilder.appendFractional(whole: Int, fractional: Int, fractionalSize: Int, unit: String, isoZeroes: Boolean) {
      `$this$appendFractional`.append(whole);
      if (fractional != 0) {
         var fracString: java.lang.String;
         var var10000: Int;
         label31: {
            `$this$appendFractional`.append('.');
            fracString = StringsKt.padStart(java.lang.String.valueOf(fractional), fractionalSize, '0');
            var `$this$indexOfLast$iv`: java.lang.CharSequence = fracString;
            var var12: Int = fracString.length() + -1;
            if (0 <= var12) {
               do {
                  var `index$iv`: Int = var12--;
                  if (`$this$indexOfLast$iv`.charAt(`index$iv`) != '0') {
                     var10000 = `index$iv`;
                     break label31;
                  }
               } while (0 <= var12);
            }

            var10000 = -1;
         }

         var nonZeroDigits: Int = var10000 + 1;
         if (!isoZeroes && var10000 + 1 < 3) {
            ;
         }
      }

      `$this$appendFractional`.append(unit);
   }

   @JvmStatic
   public fun toString(unit: DurationUnit, decimals: Int): String {
      if (decimals < 0) {
         throw new IllegalArgumentException(("decimals must be not negative, but was " + decimals).toString());
      } else {
         var var6: Double = toDouble-impl(arg0, unit);
         return if (java.lang.Double.isInfinite(var6))
            java.lang.String.valueOf(var6)
            else
            DurationJvmKt.formatToExactDecimals(var6, RangesKt.coerceAtMost(decimals, 12)) + DurationUnitKt.shortName(unit);
      }
   }

   @JvmStatic
   public fun toIsoString(): String {
      var var2: StringBuilder = new StringBuilder();
      if (isNegative-impl(arg0)) {
         var2.append('-');
      }

      var2.append("PT");
      var `arg0$iv`: Long = getAbsoluteValue-UwyO8pc(arg0);
      var var10000: Long = getInWholeHours-impl(`arg0$iv`);
      var var10001: Int = getMinutesComponent-impl(`arg0$iv`);
      var nanoseconds: Int = getNanosecondsComponent-impl(`arg0$iv`);
      var seconds: Int = getSecondsComponent-impl(`arg0$iv`);
      var hours: Long = var10000;
      if (isInfinite-impl(arg0)) {
         hours = 9999999999999L;
      }

      var hasHours: Boolean = hours != 0L;
      var hasSeconds: Boolean = seconds != 0 || nanoseconds != 0;
      var hasMinutes: Boolean = var10001 != 0 || (seconds != 0 || nanoseconds != 0) && hasHours;
      if (hasHours) {
         var2.append(hours).append('H');
      }

      if (hasMinutes) {
         var2.append(var10001).append('M');
      }

      if (hasSeconds || !hasHours && !hasMinutes) {
         appendFractional-impl(arg0, var2, seconds, nanoseconds, 9, "S", true);
      }

      var var19: java.lang.String = var2.toString();
      return var19;
   }

   @JvmStatic
   public open fun hashCode(): Int {
      return java.lang.Long.hashCode(arg0);
   }

   override fun hashCode(): Int {
      return hashCode-impl(this.rawValue);
   }

   @JvmStatic
   public open operator fun equals(other: Any?): Boolean {
      if (other !is Duration) {
         return false;
      } else {
         return arg0 == (other as Duration).unbox-impl();
      }
   }

   override fun equals(other: Any): Boolean {
      return equals-impl(this.rawValue, other);
   }

   @JvmStatic
   fun `constructor-impl`(rawValue: Long): Long {
      if (DurationJvmKt.getDurationAssertionsEnabled()) {
         if (isInNanos-impl(rawValue)) {
            if (!new LongRange(-4611686018426999999L, 4611686018426999999L).contains(getValue-impl(rawValue))) {
               throw new AssertionError(getValue-impl(rawValue) + " ns is out of nanoseconds range");
            }
         } else {
            if (!new LongRange(-4611686018427387903L, 4611686018427387903L).contains(getValue-impl(rawValue))) {
               throw new AssertionError(getValue-impl(rawValue) + " ms is out of milliseconds range");
            }

            if (new LongRange(-4611686018426L, 4611686018426L).contains(getValue-impl(rawValue))) {
               throw new AssertionError(getValue-impl(rawValue) + " ms is denormalized");
            }
         }
      }

      return rawValue;
   }

   @JvmStatic
   fun `equals-impl0`(p1: Long, p2: Long): Boolean {
      return p1 == p2;
   }

   companion object Companion private constructor() {
      public final val INFINITE: Duration
      internal final val NEG_INFINITE: Duration
      public final val ZERO: Duration
      public final val days: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$days`, DurationUnit.DAYS);
         }

      public final val days: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$days`, DurationUnit.DAYS);
         }

      public final val days: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$days`, DurationUnit.DAYS);
         }

      public final val hours: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$hours`, DurationUnit.HOURS);
         }

      public final val hours: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$hours`, DurationUnit.HOURS);
         }

      public final val hours: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$hours`, DurationUnit.HOURS);
         }

      public final val microseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
         }

      public final val microseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
         }

      public final val microseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
         }

      public final val milliseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
         }

      public final val milliseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
         }

      public final val milliseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
         }

      public final val minutes: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$minutes`, DurationUnit.MINUTES);
         }

      public final val minutes: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$minutes`, DurationUnit.MINUTES);
         }

      public final val minutes: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$minutes`, DurationUnit.MINUTES);
         }

      public final val nanoseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
         }

      public final val nanoseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
         }

      public final val nanoseconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
         }

      public final val seconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$seconds`, DurationUnit.SECONDS);
         }

      public final val seconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$seconds`, DurationUnit.SECONDS);
         }

      public final val seconds: Duration
         public final inline get() {
            return DurationKt.toDuration(`$this$seconds`, DurationUnit.SECONDS);
         }



      @ExperimentalTime
      public fun convert(value: Double, sourceUnit: DurationUnit, targetUnit: DurationUnit): Double {
         return DurationUnitKt.convertDurationUnit(value, sourceUnit, targetUnit);
      }

      public fun parse(value: String): Duration {
         try {
            return DurationKt.access$parseDuration(value, false);
         } catch (IllegalArgumentException var5) {
            throw new IllegalArgumentException("Invalid duration string format: '" + value + "'.", var5);
         }
      }

      public fun parseIsoString(value: String): Duration {
         try {
            return DurationKt.access$parseDuration(value, true);
         } catch (IllegalArgumentException var5) {
            throw new IllegalArgumentException("Invalid ISO duration string format: '" + value + "'.", var5);
         }
      }

      public fun parseOrNull(value: String): Duration? {
         var var2: Duration;
         try {
            var2 = Duration.box-impl(DurationKt.access$parseDuration(value, false));
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }

      public fun parseIsoStringOrNull(value: String): Duration? {
         var var2: Duration;
         try {
            var2 = Duration.box-impl(DurationKt.access$parseDuration(value, true));
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }
   }
}
