@file:SourceDebugExtension(["SMAP\nDuration.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Duration.kt\nkotlin/time/DurationKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1495:1\n1447#1,6:1497\n1450#1,3:1503\n1447#1,6:1506\n1447#1,6:1512\n1450#1,3:1521\n1#2:1496\n1726#3,3:1518\n*S KotlinDebug\n*F\n+ 1 Duration.kt\nkotlin/time/DurationKt\n*L\n1371#1:1497,6\n1405#1:1503,3\n1408#1:1506,6\n1411#1:1512,6\n1447#1:1521,3\n1436#1:1518,3\n*E\n"])

package kotlin.time

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt

internal const val MAX_MILLIS: Long = 4611686018427387903L
internal const val MAX_NANOS: Long = 4611686018426999999L
private const val MAX_NANOS_IN_MILLIS: Long = 4611686018426L
internal const val NANOS_IN_MILLIS: Int = 1000000
public final val days: Duration
   public final get() {
      return toDuration(`$this$days`, DurationUnit.DAYS);
   }

public final val days: Duration
   public final get() {
      return toDuration(`$this$days`, DurationUnit.DAYS);
   }

public final val days: Duration
   public final get() {
      return toDuration(`$this$days`, DurationUnit.DAYS);
   }

public final val hours: Duration
   public final get() {
      return toDuration(`$this$hours`, DurationUnit.HOURS);
   }

public final val hours: Duration
   public final get() {
      return toDuration(`$this$hours`, DurationUnit.HOURS);
   }

public final val hours: Duration
   public final get() {
      return toDuration(`$this$hours`, DurationUnit.HOURS);
   }

public final val microseconds: Duration
   public final get() {
      return toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
   }

public final val microseconds: Duration
   public final get() {
      return toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
   }

public final val microseconds: Duration
   public final get() {
      return toDuration(`$this$microseconds`, DurationUnit.MICROSECONDS);
   }

public final val milliseconds: Duration
   public final get() {
      return toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
   }

public final val milliseconds: Duration
   public final get() {
      return toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
   }

public final val milliseconds: Duration
   public final get() {
      return toDuration(`$this$milliseconds`, DurationUnit.MILLISECONDS);
   }

public final val minutes: Duration
   public final get() {
      return toDuration(`$this$minutes`, DurationUnit.MINUTES);
   }

public final val minutes: Duration
   public final get() {
      return toDuration(`$this$minutes`, DurationUnit.MINUTES);
   }

public final val minutes: Duration
   public final get() {
      return toDuration(`$this$minutes`, DurationUnit.MINUTES);
   }

public final val nanoseconds: Duration
   public final get() {
      return toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
   }

public final val nanoseconds: Duration
   public final get() {
      return toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
   }

public final val nanoseconds: Duration
   public final get() {
      return toDuration(`$this$nanoseconds`, DurationUnit.NANOSECONDS);
   }

public final val seconds: Duration
   public final get() {
      return toDuration(`$this$seconds`, DurationUnit.SECONDS);
   }

public final val seconds: Duration
   public final get() {
      return toDuration(`$this$seconds`, DurationUnit.SECONDS);
   }

public final val seconds: Duration
   public final get() {
      return toDuration(`$this$seconds`, DurationUnit.SECONDS);
   }


@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
public fun Int.toDuration(unit: DurationUnit): Duration {
   return if (unit.compareTo(DurationUnit.SECONDS) <= 0)
      durationOfNanos(DurationUnitKt.convertDurationUnitOverflow((long)`$this$toDuration`, unit, DurationUnit.NANOSECONDS))
      else
      toDuration((long)`$this$toDuration`, unit);
}

@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
public fun Long.toDuration(unit: DurationUnit): Duration {
   var maxNsInUnit: Long = DurationUnitKt.convertDurationUnitOverflow(4611686018426999999L, DurationUnit.NANOSECONDS, unit);
   return if (new LongRange(-maxNsInUnit, maxNsInUnit).contains(`$this$toDuration`))
      durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(`$this$toDuration`, unit, DurationUnit.NANOSECONDS))
      else
      durationOfMillis(
         RangesKt.coerceIn(DurationUnitKt.convertDurationUnit(`$this$toDuration`, unit, DurationUnit.MILLISECONDS), -4611686018427387903L, 4611686018427387903L)
      );
}

@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
public fun Double.toDuration(unit: DurationUnit): Duration {
   var valueInNs: Double = DurationUnitKt.convertDurationUnit(`$this$toDuration`, unit, DurationUnit.NANOSECONDS);
   if (java.lang.Double.isNaN(valueInNs)) {
      throw new IllegalArgumentException("Duration value cannot be NaN.".toString());
   } else {
      var var9: Long = MathKt.roundToLong(valueInNs);
      return if (new LongRange(-4611686018426999999L, 4611686018426999999L).contains(var9))
         durationOfNanos(var9)
         else
         durationOfMillisNormalized(MathKt.roundToLong(DurationUnitKt.convertDurationUnit(`$this$toDuration`, unit, DurationUnit.MILLISECONDS)));
   }
}

/** @deprecated */
@Deprecated(message = "Use 'Int.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = ["kotlin.time.Duration.Companion.nanoseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getNanoseconds$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = ["kotlin.time.Duration.Companion.nanoseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getNanoseconds$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.nanoseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.nanoseconds", imports = ["kotlin.time.Duration.Companion.nanoseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getNanoseconds$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = ["kotlin.time.Duration.Companion.microseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMicroseconds$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = ["kotlin.time.Duration.Companion.microseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMicroseconds$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.microseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.microseconds", imports = ["kotlin.time.Duration.Companion.microseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMicroseconds$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = ["kotlin.time.Duration.Companion.milliseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMilliseconds$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = ["kotlin.time.Duration.Companion.milliseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMilliseconds$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.milliseconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.milliseconds", imports = ["kotlin.time.Duration.Companion.milliseconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMilliseconds$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = ["kotlin.time.Duration.Companion.seconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getSeconds$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = ["kotlin.time.Duration.Companion.seconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getSeconds$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.seconds' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.seconds", imports = ["kotlin.time.Duration.Companion.seconds"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getSeconds$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = ["kotlin.time.Duration.Companion.minutes"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMinutes$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = ["kotlin.time.Duration.Companion.minutes"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMinutes$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.minutes' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.minutes", imports = ["kotlin.time.Duration.Companion.minutes"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getMinutes$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = ["kotlin.time.Duration.Companion.hours"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getHours$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = ["kotlin.time.Duration.Companion.hours"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getHours$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.hours' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.hours", imports = ["kotlin.time.Duration.Companion.hours"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getHours$annotations`(var0: Double) {
}

/** @deprecated */
@Deprecated(message = "Use 'Int.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = ["kotlin.time.Duration.Companion.days"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getDays$annotations`(var0: Int) {
}

/** @deprecated */
@Deprecated(message = "Use 'Long.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = ["kotlin.time.Duration.Companion.days"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getDays$annotations`(var0: Long) {
}

/** @deprecated */
@Deprecated(message = "Use 'Double.days' extension property from Duration.Companion instead.", replaceWith = @ReplaceWith(expression = "this.days", imports = ["kotlin.time.Duration.Companion.days"]))
@DeprecatedSinceKotlin(warningSince = "1.5", errorSince = "1.8", hiddenSince = "1.9")
@SinceKotlin(version = "1.3")
@ExperimentalTime
@JvmSynthetic
fun `getDays$annotations`(var0: Double) {
}

@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
@InlineOnly
public inline operator fun Int.times(duration: Duration): Duration {
   return Duration.times-UwyO8pc(duration, `$this$times_u2dmvk6XK0`);
}

@SinceKotlin(version = "1.6")
@WasExperimental(markerClass = [ExperimentalTime::class])
@InlineOnly
public inline operator fun Double.times(duration: Duration): Duration {
   return Duration.times-UwyO8pc(duration, `$this$times_u2dkIfJnKk`);
}

private fun parseDuration(value: String, strictIso: Boolean): Duration {
   var length: Int = value.length();
   if (length == 0) {
      throw new IllegalArgumentException("The string is empty");
   } else {
      var var23: Int = 0;
      var result: Long = Duration.Companion.getZERO-UwyO8pc();
      var hasSign: Char = value.charAt(0);
      if (hasSign == '+' || hasSign == '-') {
         0++;
      }

      var var27: Boolean = 0 > 0;
      var isNegative: Boolean = 0 > 0 && StringsKt.startsWith$default(value, '-', false, 2, null);
      if (length <= 0) {
         throw new IllegalArgumentException("No components");
      } else {
         if (value.charAt(0) != 'P') {
            if (strictIso) {
               throw new IllegalArgumentException();
            }

            if (StringsKt.regionMatches(value, 0, "Infinity", 0, Math.max(length - 0, "Infinity".length()), true)) {
               result = Duration.Companion.getINFINITE-UwyO8pc();
            } else {
               var var28: DurationUnit = null;
               var var29: Boolean = false;
               var var30: Boolean = !var27;
               if (var27 && value.charAt(0) == '(' && StringsKt.last(value) == ')') {
                  var30 = true;
                  0++;
                  if (0 == --length) {
                     throw new IllegalArgumentException("No components");
                  }
               }

               while (index < length) {
                  if (var29 && var30) {
                     var var31: java.lang.String = value;

                     var var37: Int;
                     for (i$iv = index; i$iv < $this$skipWhile$iv.length(); i$iv++) {
                        if (var31.charAt(var37) != ' ') {
                           break;
                        }
                     }

                     var23 = var37;
                  }

                  var29 = true;
                  var var46: java.lang.String = value;

                  var var51: Int;
                  for (i$iv$iv = index; i$iv$iv < $this$skipWhile$iv$iv.length(); i$iv$iv++) {
                     var var54: Char = var46.charAt(var51);
                     if (!new CharRange('0', '9').contains(var54) && var54 != '.') {
                        break;
                     }
                  }

                  var var59: java.lang.String = value.substring(var23, var51);
                  if (var59.length() == 0) {
                     throw new IllegalArgumentException();
                  }

                  var23 = var23 + var59.length();
                  var `$this$skipWhile$iv$ivx`: java.lang.String = value;

                  var `i$iv$ivx`: Int;
                  for (i$iv$ivx = index; i$iv$ivx < $this$skipWhile$iv$ivx.length(); i$iv$ivx++) {
                     if (!new CharRange('a', 'z').contains(`$this$skipWhile$iv$ivx`.charAt(`i$iv$ivx`))) {
                        break;
                     }
                  }

                  var59 = value.substring(var23, `i$iv$ivx`);
                  var23 = var23 + var59.length();
                  var var39: DurationUnit = DurationUnitKt.durationUnitByShortName(var59);
                  if (var28 != null && var28.compareTo(var39) <= 0) {
                     throw new IllegalArgumentException("Unexpected order of duration components");
                  }

                  var28 = var39;
                  var var42: Int = StringsKt.indexOf$default(var59, '.', 0, false, 6, null);
                  if (var42 > 0) {
                     var var61: java.lang.String = var59.substring(0, var42);
                     result = Duration.plus-LRDsOJo(result, toDuration(java.lang.Long.parseLong(var61), var39));
                     var var62: java.lang.String = var59.substring(var42);
                     result = Duration.plus-LRDsOJo(result, toDuration(java.lang.Double.parseDouble(var62), var39));
                     if (var23 < length) {
                        throw new IllegalArgumentException("Fractional component must be last");
                     }
                  } else {
                     result = Duration.plus-LRDsOJo(result, toDuration(java.lang.Long.parseLong(var59), var39));
                  }
               }
            }
         } else {
            if (++0 == length) {
               throw new IllegalArgumentException();
            }

            var prevUnit: java.lang.String = "+-.";
            var afterFirst: Boolean = false;
            var allowSpaces: DurationUnit = null;

            while (index < length) {
               if (value.charAt(var23) == 'T') {
                  if (afterFirst || ++var23 == length) {
                     throw new IllegalArgumentException();
                  }

                  afterFirst = true;
               } else {
                  var whole: java.lang.String = value;

                  var `i$iv$ivxx`: Int;
                  for (i$iv$ivxx = index; i$iv$ivxx < $this$skipWhile$iv$iv.length(); i$iv$ivxx++) {
                     var `i$iv$iv`: Char = whole.charAt(`i$iv$ivxx`);
                     if (!new CharRange('0', '9').contains(`i$iv$iv`) && !StringsKt.contains$default(prevUnit, `i$iv$iv`, false, 2, null)) {
                        break;
                     }
                  }

                  var var10000: java.lang.String = value.substring(var23, `i$iv$ivxx`);
                  if (var10000.length() == 0) {
                     throw new IllegalArgumentException();
                  }

                  var23 += var10000.length();
                  var var35: java.lang.CharSequence = value;
                  if (var23 < 0 || var23 > StringsKt.getLastIndex(value)) {
                     throw new IllegalArgumentException("Missing unit for value " + var10000);
                  }

                  var unitName: Char = var35.charAt(var23);
                  var23++;
                  var var36: DurationUnit = DurationUnitKt.durationUnitByIsoChar(unitName, afterFirst);
                  if (allowSpaces != null && allowSpaces.compareTo(var36) <= 0) {
                     throw new IllegalArgumentException("Unexpected order of duration components");
                  }

                  allowSpaces = var36;
                  var dotIndex: Int = StringsKt.indexOf$default(var10000, '.', 0, false, 6, null);
                  if (var36 === DurationUnit.SECONDS && dotIndex > 0) {
                     var var58: java.lang.String = var10000.substring(0, dotIndex);
                     result = Duration.plus-LRDsOJo(result, toDuration(parseOverLongIsoComponent(var58), var36));
                     var var10001: java.lang.String = var10000.substring(dotIndex);
                     result = Duration.plus-LRDsOJo(result, toDuration(java.lang.Double.parseDouble(var10001), var36));
                  } else {
                     result = Duration.plus-LRDsOJo(result, toDuration(parseOverLongIsoComponent(var10000), var36));
                  }
               }
            }
         }

         return if (isNegative) Duration.unaryMinus-UwyO8pc(result) else result;
      }
   }
}

private fun parseOverLongIsoComponent(value: String): Long {
   var length: Int = value.length();
   if (length > 0 && StringsKt.contains$default("+-", value.charAt(0), false, 2, null)) {
      0++;
   }

   if (length - 0 > 16) {
      var `$this$all$iv`: java.lang.Iterable = new IntRange(0, StringsKt.getLastIndex(value));
      var var10000: Boolean;
      if (`$this$all$iv` is java.util.Collection && (`$this$all$iv` as java.util.Collection).isEmpty()) {
         var10000 = true;
      } else {
         var var5: java.util.Iterator = `$this$all$iv`.iterator();

         while (true) {
            if (!var5.hasNext()) {
               var10000 = true;
               break;
            }

            if (!new CharRange('0', '9').contains(value.charAt((var5 as IntIterator).nextInt()))) {
               var10000 = false;
               break;
            }
         }
      }

      if (var10000) {
         return if (value.charAt(0) == '-') java.lang.Long.MIN_VALUE else java.lang.Long.MAX_VALUE;
      }
   }

   return if (StringsKt.startsWith$default(value, "+", false, 2, null)) java.lang.Long.parseLong(StringsKt.drop(value, 1)) else java.lang.Long.parseLong(value);
}

private inline fun String.substringWhile(startIndex: Int, predicate: (Char) -> Boolean): String {
   var `$this$skipWhile$iv`: java.lang.String = `$this$substringWhile`;
   var `i$iv`: Int = startIndex;

   while (i$iv < $this$skipWhile$iv.length() && predicate.invoke($this$skipWhile$iv.charAt(i$iv))) {
      `i$iv`++;
   }

   var var10000: java.lang.String = `$this$substringWhile`.substring(startIndex, `i$iv`);
   return var10000;
}

private inline fun String.skipWhile(startIndex: Int, predicate: (Char) -> Boolean): Int {
   var i: Int = startIndex;

   while (i < $this$skipWhile.length() && predicate.invoke($this$skipWhile.charAt(i))) {
      i++;
   }

   return i;
}

private fun nanosToMillis(nanos: Long): Long {
   return nanos / (long)1000000;
}

private fun millisToNanos(millis: Long): Long {
   return millis * (long)1000000;
}

private fun durationOfNanos(normalNanos: Long): Duration {
   return Duration.constructor-impl(normalNanos shl 1);
}

private fun durationOfMillis(normalMillis: Long): Duration {
   return Duration.constructor-impl((normalMillis shl 1) + 1L);
}

private fun durationOf(normalValue: Long, unitDiscriminator: Int): Duration {
   return Duration.constructor-impl((normalValue shl 1) + (long)unitDiscriminator);
}

private fun durationOfNanosNormalized(nanos: Long): Duration {
   return if (new LongRange(-4611686018426999999L, 4611686018426999999L).contains(nanos)) durationOfNanos(nanos) else durationOfMillis(nanosToMillis(nanos));
}

private fun durationOfMillisNormalized(millis: Long): Duration {
   return if (new LongRange(-4611686018426L, 4611686018426L).contains(millis))
      durationOfNanos(millisToNanos(millis))
      else
      durationOfMillis(RangesKt.coerceIn(millis, -4611686018427387903L, 4611686018427387903L));
}

@JvmSynthetic
fun `access$parseDuration`(value: java.lang.String, strictIso: Boolean): Long {
   return parseDuration(value, strictIso);
}

@JvmSynthetic
fun `access$durationOf`(normalValue: Long, unitDiscriminator: Int): Long {
   return durationOf(normalValue, unitDiscriminator);
}

@JvmSynthetic
fun `access$durationOfNanosNormalized`(nanos: Long): Long {
   return durationOfNanosNormalized(nanos);
}

@JvmSynthetic
fun `access$durationOfMillisNormalized`(millis: Long): Long {
   return durationOfMillisNormalized(millis);
}

@JvmSynthetic
fun `access$nanosToMillis`(nanos: Long): Long {
   return nanosToMillis(nanos);
}

@JvmSynthetic
fun `access$millisToNanos`(millis: Long): Long {
   return millisToNanos(millis);
}

@JvmSynthetic
fun `access$durationOfNanos`(normalNanos: Long): Long {
   return durationOfNanos(normalNanos);
}

@JvmSynthetic
fun `access$durationOfMillis`(normalMillis: Long): Long {
   return durationOfMillis(normalMillis);
}

