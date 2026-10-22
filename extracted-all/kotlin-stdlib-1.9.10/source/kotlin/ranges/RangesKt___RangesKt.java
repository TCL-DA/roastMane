package kotlin.ranges

import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import kotlin.random.RandomKt

@SourceDebugExtension(["SMAP\n_Ranges.kt\nKotlin\n*S Kotlin\n*F\n+ 1 _Ranges.kt\nkotlin/ranges/RangesKt___RangesKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1537:1\n1#2:1538\n*E\n"])
// $VF: Class flags could not be determined
internal class RangesKt___RangesKt : RangesKt__RangesKt {
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun IntProgression.first(): Int {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$first` + " is empty.");
      } else {
         return `$this$first`.getFirst();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun LongProgression.first(): Long {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$first` + " is empty.");
      } else {
         return `$this$first`.getFirst();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun CharProgression.first(): Char {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$first` + " is empty.");
      } else {
         return `$this$first`.getFirst();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun IntProgression.firstOrNull(): Int? {
      return if (`$this$firstOrNull`.isEmpty()) null else `$this$firstOrNull`.getFirst();
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun LongProgression.firstOrNull(): Long? {
      return if (`$this$firstOrNull`.isEmpty()) null else `$this$firstOrNull`.getFirst();
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun CharProgression.firstOrNull(): Char? {
      return if (`$this$firstOrNull`.isEmpty()) null else `$this$firstOrNull`.getFirst();
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun IntProgression.last(): Int {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$last` + " is empty.");
      } else {
         return `$this$last`.getLast();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun LongProgression.last(): Long {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$last` + " is empty.");
      } else {
         return `$this$last`.getLast();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun CharProgression.last(): Char {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$last` + " is empty.");
      } else {
         return `$this$last`.getLast();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun IntProgression.lastOrNull(): Int? {
      return if (`$this$lastOrNull`.isEmpty()) null else `$this$lastOrNull`.getLast();
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun LongProgression.lastOrNull(): Long? {
      return if (`$this$lastOrNull`.isEmpty()) null else `$this$lastOrNull`.getLast();
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun CharProgression.lastOrNull(): Char? {
      return if (`$this$lastOrNull`.isEmpty()) null else `$this$lastOrNull`.getLast();
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun IntRange.random(): Int {
      return RangesKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun LongRange.random(): Long {
      return RangesKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun CharRange.random(): Char {
      return RangesKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun IntRange.random(random: Random): Int {
      try {
         return RandomKt.nextInt(random, `$this$random`);
      } catch (IllegalArgumentException var3) {
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun LongRange.random(random: Random): Long {
      try {
         return RandomKt.nextLong(random, `$this$random`);
      } catch (IllegalArgumentException var3) {
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun CharRange.random(random: Random): Char {
      try {
         return (char)random.nextInt(`$this$random`.getFirst(), `$this$random`.getLast() + 1);
      } catch (IllegalArgumentException var3) {
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun IntRange.randomOrNull(): Int? {
      return RangesKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun LongRange.randomOrNull(): Long? {
      return RangesKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @InlineOnly
   @JvmStatic
   public inline fun CharRange.randomOrNull(): Char? {
      return RangesKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun IntRange.randomOrNull(random: Random): Int? {
      return if (`$this$randomOrNull`.isEmpty()) null else RandomKt.nextInt(random, `$this$randomOrNull`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun LongRange.randomOrNull(random: Random): Long? {
      return if (`$this$randomOrNull`.isEmpty()) null else RandomKt.nextLong(random, `$this$randomOrNull`);
   }

   @SinceKotlin(version = "1.4")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public fun CharRange.randomOrNull(random: Random): Char? {
      return if (`$this$randomOrNull`.isEmpty()) null else (char)random.nextInt(`$this$randomOrNull`.getFirst(), `$this$randomOrNull`.getLast() + 1);
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline operator fun IntRange.contains(element: Int?): Boolean {
      return element != null && `$this$contains`.contains(element.intValue());
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline operator fun LongRange.contains(element: Long?): Boolean {
      return element != null && `$this$contains`.contains(element.longValue());
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline operator fun CharRange.contains(element: Char?): Boolean {
      return element != null && `$this$contains`.contains(element.charValue());
   }

   @JvmName(name = "intRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Int>.contains(value: Byte): Boolean {
      return `$this$contains`.contains(Integer.valueOf(value));
   }

   @JvmName(name = "longRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Long>.contains(value: Byte): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "shortRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Short>.contains(value: Byte): Boolean {
      return `$this$contains`.contains((short)value);
   }

   @JvmName(name = "intRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Int>.contains(value: Byte): Boolean {
      return `$this$contains`.contains(Integer.valueOf(value));
   }

   @JvmName(name = "longRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Long>.contains(value: Byte): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "shortRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Short>.contains(value: Byte): Boolean {
      return `$this$contains`.contains((short)value);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntRange.contains(value: Byte): Boolean {
      return RangesKt.intRangeContains(`$this$contains`, value);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongRange.contains(value: Byte): Boolean {
      return RangesKt.longRangeContains(`$this$contains`, value);
   }

   @JvmName(name = "floatRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Float>.contains(value: Double): Boolean {
      return `$this$contains`.contains((float)value);
   }

   @JvmName(name = "doubleRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Double>.contains(value: Float): Boolean {
      return `$this$contains`.contains((double)value);
   }

   @JvmName(name = "doubleRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Double>.contains(value: Float): Boolean {
      return `$this$contains`.contains((double)value);
   }

   @JvmName(name = "longRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Long>.contains(value: Int): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "byteRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Byte>.contains(value: Int): Boolean {
      var var2: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @JvmName(name = "shortRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Short>.contains(value: Int): Boolean {
      var var2: java.lang.Short = RangesKt.toShortExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @JvmName(name = "longRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Long>.contains(value: Int): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "byteRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Byte>.contains(value: Int): Boolean {
      var var2: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @JvmName(name = "shortRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Short>.contains(value: Int): Boolean {
      var var2: java.lang.Short = RangesKt.toShortExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongRange.contains(value: Int): Boolean {
      return RangesKt.longRangeContains(`$this$contains`, value);
   }

   @JvmName(name = "intRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Int>.contains(value: Long): Boolean {
      var var3: Int = RangesKt.toIntExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @JvmName(name = "byteRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Byte>.contains(value: Long): Boolean {
      var var3: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @JvmName(name = "shortRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Short>.contains(value: Long): Boolean {
      var var3: java.lang.Short = RangesKt.toShortExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @JvmName(name = "intRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Int>.contains(value: Long): Boolean {
      var var3: Int = RangesKt.toIntExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @JvmName(name = "byteRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Byte>.contains(value: Long): Boolean {
      var var3: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @JvmName(name = "shortRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Short>.contains(value: Long): Boolean {
      var var3: java.lang.Short = RangesKt.toShortExactOrNull(value);
      return var3 != null && `$this$contains`.contains(var3);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntRange.contains(value: Long): Boolean {
      return RangesKt.intRangeContains(`$this$contains`, value);
   }

   @JvmName(name = "intRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Int>.contains(value: Short): Boolean {
      return `$this$contains`.contains(Integer.valueOf(value));
   }

   @JvmName(name = "longRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Long>.contains(value: Short): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "byteRangeContains")
   @JvmStatic
   public operator fun ClosedRange<Byte>.contains(value: Short): Boolean {
      var var2: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @JvmName(name = "intRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Int>.contains(value: Short): Boolean {
      return `$this$contains`.contains(Integer.valueOf(value));
   }

   @JvmName(name = "longRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Long>.contains(value: Short): Boolean {
      return `$this$contains`.contains((long)value);
   }

   @JvmName(name = "byteRangeContains")
   @SinceKotlin(version = "1.9")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class])
   @JvmStatic
   public operator fun OpenEndRange<Byte>.contains(value: Short): Boolean {
      var var2: java.lang.Byte = RangesKt.toByteExactOrNull(value);
      return var2 != null && `$this$contains`.contains(var2);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun IntRange.contains(value: Short): Boolean {
      return RangesKt.intRangeContains(`$this$contains`, value);
   }

   @InlineOnly
   @JvmStatic
   public inline operator fun LongRange.contains(value: Short): Boolean {
      return RangesKt.longRangeContains(`$this$contains`, value);
   }

   @JvmStatic
   public infix fun Int.downTo(to: Byte): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Long.downTo(to: Byte): LongProgression {
      return LongProgression.Companion.fromClosedRange(`$this$downTo`, (long)to, -1L);
   }

   @JvmStatic
   public infix fun Byte.downTo(to: Byte): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Short.downTo(to: Byte): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Char.downTo(to: Char): CharProgression {
      return CharProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Int.downTo(to: Int): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Long.downTo(to: Int): LongProgression {
      return LongProgression.Companion.fromClosedRange(`$this$downTo`, (long)to, -1L);
   }

   @JvmStatic
   public infix fun Byte.downTo(to: Int): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Short.downTo(to: Int): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Int.downTo(to: Long): LongProgression {
      return LongProgression.Companion.fromClosedRange((long)`$this$downTo`, to, -1L);
   }

   @JvmStatic
   public infix fun Long.downTo(to: Long): LongProgression {
      return LongProgression.Companion.fromClosedRange(`$this$downTo`, to, -1L);
   }

   @JvmStatic
   public infix fun Byte.downTo(to: Long): LongProgression {
      return LongProgression.Companion.fromClosedRange((long)`$this$downTo`, to, -1L);
   }

   @JvmStatic
   public infix fun Short.downTo(to: Long): LongProgression {
      return LongProgression.Companion.fromClosedRange((long)`$this$downTo`, to, -1L);
   }

   @JvmStatic
   public infix fun Int.downTo(to: Short): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Long.downTo(to: Short): LongProgression {
      return LongProgression.Companion.fromClosedRange(`$this$downTo`, (long)to, -1L);
   }

   @JvmStatic
   public infix fun Byte.downTo(to: Short): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public infix fun Short.downTo(to: Short): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$downTo`, to, -1);
   }

   @JvmStatic
   public fun IntProgression.reversed(): IntProgression {
      return IntProgression.Companion.fromClosedRange(`$this$reversed`.getLast(), `$this$reversed`.getFirst(), -`$this$reversed`.getStep());
   }

   @JvmStatic
   public fun LongProgression.reversed(): LongProgression {
      return LongProgression.Companion.fromClosedRange(`$this$reversed`.getLast(), `$this$reversed`.getFirst(), -`$this$reversed`.getStep());
   }

   @JvmStatic
   public fun CharProgression.reversed(): CharProgression {
      return CharProgression.Companion.fromClosedRange(`$this$reversed`.getLast(), `$this$reversed`.getFirst(), -`$this$reversed`.getStep());
   }

   @JvmStatic
   public infix fun IntProgression.step(step: Int): IntProgression {
      RangesKt.checkStepIsPositive(step > 0, step);
      return IntProgression.Companion.fromClosedRange(`$this$step`.getFirst(), `$this$step`.getLast(), if (`$this$step`.getStep() > 0) step else -step);
   }

   @JvmStatic
   public infix fun LongProgression.step(step: Long): LongProgression {
      RangesKt.checkStepIsPositive(step > 0L, step);
      return LongProgression.Companion.fromClosedRange(`$this$step`.getFirst(), `$this$step`.getLast(), if (`$this$step`.getStep() > 0L) step else -step);
   }

   @JvmStatic
   public infix fun CharProgression.step(step: Int): CharProgression {
      RangesKt.checkStepIsPositive(step > 0, step);
      return CharProgression.Companion.fromClosedRange(`$this$step`.getFirst(), `$this$step`.getLast(), if (`$this$step`.getStep() > 0) step else -step);
   }

   @JvmStatic
   internal fun Int.toByteExactOrNull(): Byte? {
      return if (new IntRange(-128, 127).contains(`$this$toByteExactOrNull`)) (byte)`$this$toByteExactOrNull` else null;
   }

   @JvmStatic
   internal fun Long.toByteExactOrNull(): Byte? {
      return if (new LongRange(-128L, 127L).contains(`$this$toByteExactOrNull`)) (byte)((int)`$this$toByteExactOrNull`) else null;
   }

   @JvmStatic
   internal fun Short.toByteExactOrNull(): Byte? {
      return if (RangesKt.intRangeContains(new IntRange(-128, 127), `$this$toByteExactOrNull`)) (byte)`$this$toByteExactOrNull` else null;
   }

   @JvmStatic
   internal fun Double.toByteExactOrNull(): Byte? {
      return if (-128.0 <= `$this$toByteExactOrNull` && `$this$toByteExactOrNull` <= 127.0) (byte)((int)`$this$toByteExactOrNull`) else null;
   }

   @JvmStatic
   internal fun Float.toByteExactOrNull(): Byte? {
      return if (-128.0F <= `$this$toByteExactOrNull` && `$this$toByteExactOrNull` <= 127.0F) (byte)((int)`$this$toByteExactOrNull`) else null;
   }

   @JvmStatic
   internal fun Long.toIntExactOrNull(): Int? {
      return if (new LongRange(-2147483648L, 2147483647L).contains(`$this$toIntExactOrNull`)) (int)`$this$toIntExactOrNull` else null;
   }

   @JvmStatic
   internal fun Double.toIntExactOrNull(): Int? {
      return if (-2.1474836E9F <= `$this$toIntExactOrNull` && `$this$toIntExactOrNull` <= 2.147483647E9) (int)`$this$toIntExactOrNull` else null;
   }

   @JvmStatic
   internal fun Float.toIntExactOrNull(): Int? {
      return if (-2.1474836E9F <= `$this$toIntExactOrNull` && `$this$toIntExactOrNull` <= 2.1474836E9F) (int)`$this$toIntExactOrNull` else null;
   }

   @JvmStatic
   internal fun Double.toLongExactOrNull(): Long? {
      return if (-9.223372E18F <= `$this$toLongExactOrNull` && `$this$toLongExactOrNull` <= 9.223372E18F) (long)`$this$toLongExactOrNull` else null;
   }

   @JvmStatic
   internal fun Float.toLongExactOrNull(): Long? {
      return if (-9.223372E18F <= `$this$toLongExactOrNull` && `$this$toLongExactOrNull` <= 9.223372E18F) (long)`$this$toLongExactOrNull` else null;
   }

   @JvmStatic
   internal fun Int.toShortExactOrNull(): Short? {
      return if (new IntRange(-32768, 32767).contains(`$this$toShortExactOrNull`)) (short)`$this$toShortExactOrNull` else null;
   }

   @JvmStatic
   internal fun Long.toShortExactOrNull(): Short? {
      return if (new LongRange(-32768L, 32767L).contains(`$this$toShortExactOrNull`)) (short)((int)`$this$toShortExactOrNull`) else null;
   }

   @JvmStatic
   internal fun Double.toShortExactOrNull(): Short? {
      return if (-32768.0 <= `$this$toShortExactOrNull` && `$this$toShortExactOrNull` <= 32767.0) (short)((int)`$this$toShortExactOrNull`) else null;
   }

   @JvmStatic
   internal fun Float.toShortExactOrNull(): Short? {
      return if (-32768.0F <= `$this$toShortExactOrNull` && `$this$toShortExactOrNull` <= 32767.0F) (short)((int)`$this$toShortExactOrNull`) else null;
   }

   @JvmStatic
   public infix fun Int.until(to: Byte): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Long.until(to: Byte): LongRange {
      return new LongRange(`$this$until`, (long)to - 1L);
   }

   @JvmStatic
   public infix fun Byte.until(to: Byte): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Short.until(to: Byte): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Char.until(to: Char): CharRange {
      return if (Intrinsics.compare(to, 0) <= 0) CharRange.Companion.getEMPTY() else new CharRange(`$this$until`, (char)(to - 1));
   }

   @JvmStatic
   public infix fun Int.until(to: Int): IntRange {
      return if (to <= Integer.MIN_VALUE) IntRange.Companion.getEMPTY() else new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Long.until(to: Int): LongRange {
      return new LongRange(`$this$until`, (long)to - 1L);
   }

   @JvmStatic
   public infix fun Byte.until(to: Int): IntRange {
      return if (to <= Integer.MIN_VALUE) IntRange.Companion.getEMPTY() else new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Short.until(to: Int): IntRange {
      return if (to <= Integer.MIN_VALUE) IntRange.Companion.getEMPTY() else new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Int.until(to: Long): LongRange {
      return if (to <= java.lang.Long.MIN_VALUE) LongRange.Companion.getEMPTY() else new LongRange((long)`$this$until`, to - 1L);
   }

   @JvmStatic
   public infix fun Long.until(to: Long): LongRange {
      return if (to <= java.lang.Long.MIN_VALUE) LongRange.Companion.getEMPTY() else new LongRange(`$this$until`, to - 1L);
   }

   @JvmStatic
   public infix fun Byte.until(to: Long): LongRange {
      return if (to <= java.lang.Long.MIN_VALUE) LongRange.Companion.getEMPTY() else new LongRange((long)`$this$until`, to - 1L);
   }

   @JvmStatic
   public infix fun Short.until(to: Long): LongRange {
      return if (to <= java.lang.Long.MIN_VALUE) LongRange.Companion.getEMPTY() else new LongRange((long)`$this$until`, to - 1L);
   }

   @JvmStatic
   public infix fun Int.until(to: Short): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Long.until(to: Short): LongRange {
      return new LongRange(`$this$until`, (long)to - 1L);
   }

   @JvmStatic
   public infix fun Byte.until(to: Short): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public infix fun Short.until(to: Short): IntRange {
      return new IntRange(`$this$until`, to - 1);
   }

   @JvmStatic
   public fun <T : Comparable<T>> T.coerceAtLeast(minimumValue: T): T {
      return (T)(if (`$this$coerceAtLeast`.compareTo(minimumValue) < 0) minimumValue else `$this$coerceAtLeast`);
   }

   @JvmStatic
   public fun Byte.coerceAtLeast(minimumValue: Byte): Byte {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun Short.coerceAtLeast(minimumValue: Short): Short {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun Int.coerceAtLeast(minimumValue: Int): Int {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun Long.coerceAtLeast(minimumValue: Long): Long {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun Float.coerceAtLeast(minimumValue: Float): Float {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun Double.coerceAtLeast(minimumValue: Double): Double {
      return if (`$this$coerceAtLeast` < minimumValue) minimumValue else `$this$coerceAtLeast`;
   }

   @JvmStatic
   public fun <T : Comparable<T>> T.coerceAtMost(maximumValue: T): T {
      return (T)(if (`$this$coerceAtMost`.compareTo(maximumValue) > 0) maximumValue else `$this$coerceAtMost`);
   }

   @JvmStatic
   public fun Byte.coerceAtMost(maximumValue: Byte): Byte {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun Short.coerceAtMost(maximumValue: Short): Short {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun Int.coerceAtMost(maximumValue: Int): Int {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun Long.coerceAtMost(maximumValue: Long): Long {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun Float.coerceAtMost(maximumValue: Float): Float {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun Double.coerceAtMost(maximumValue: Double): Double {
      return if (`$this$coerceAtMost` > maximumValue) maximumValue else `$this$coerceAtMost`;
   }

   @JvmStatic
   public fun <T : Comparable<T>> T.coerceIn(minimumValue: T?, maximumValue: T?): T {
      if (minimumValue != null && maximumValue != null) {
         if (minimumValue.compareTo(maximumValue) > 0) {
            throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
         }

         if (`$this$coerceIn`.compareTo(minimumValue) < 0) {
            return (T)minimumValue;
         }

         if (`$this$coerceIn`.compareTo(maximumValue) > 0) {
            return (T)maximumValue;
         }
      } else {
         if (minimumValue != null && `$this$coerceIn`.compareTo(minimumValue) < 0) {
            return (T)minimumValue;
         }

         if (maximumValue != null && `$this$coerceIn`.compareTo(maximumValue) > 0) {
            return (T)maximumValue;
         }
      }

      return (T)`$this$coerceIn`;
   }

   @JvmStatic
   public fun Byte.coerceIn(minimumValue: Byte, maximumValue: Byte): Byte {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @JvmStatic
   public fun Short.coerceIn(minimumValue: Short, maximumValue: Short): Short {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @JvmStatic
   public fun Int.coerceIn(minimumValue: Int, maximumValue: Int): Int {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + 46);
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @JvmStatic
   public fun Long.coerceIn(minimumValue: Long, maximumValue: Long): Long {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @JvmStatic
   public fun Float.coerceIn(minimumValue: Float, maximumValue: Float): Float {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @JvmStatic
   public fun Double.coerceIn(minimumValue: Double, maximumValue: Double): Double {
      if (minimumValue > maximumValue) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: maximum " + maximumValue + " is less than minimum " + minimumValue + '.');
      } else if (`$this$coerceIn` < minimumValue) {
         return minimumValue;
      } else {
         return if (`$this$coerceIn` > maximumValue) maximumValue else `$this$coerceIn`;
      }
   }

   @SinceKotlin(version = "1.1")
   @JvmStatic
   public fun <T : Comparable<T>> T.coerceIn(range: ClosedFloatingPointRange<T>): T {
      if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
      } else {
         return (T)(if (range.lessThanOrEquals(`$this$coerceIn`, range.getStart()) && !range.lessThanOrEquals(range.getStart(), `$this$coerceIn`))
            range.getStart()
            else
            (
               if (range.lessThanOrEquals(range.getEndInclusive(), `$this$coerceIn`) && !range.lessThanOrEquals(`$this$coerceIn`, range.getEndInclusive()))
                  range.getEndInclusive()
                  else
                  `$this$coerceIn`
            ));
      }
   }

   @JvmStatic
   public fun <T : Comparable<T>> T.coerceIn(range: ClosedRange<T>): T {
      if (range is ClosedFloatingPointRange) {
         return (T)RangesKt.coerceIn(`$this$coerceIn`, range as ClosedFloatingPointRange);
      } else if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
      } else {
         return (T)(if (`$this$coerceIn`.compareTo(range.getStart()) < 0)
            range.getStart()
            else
            (if (`$this$coerceIn`.compareTo(range.getEndInclusive()) > 0) range.getEndInclusive() else `$this$coerceIn`));
      }
   }

   @JvmStatic
   public fun Int.coerceIn(range: ClosedRange<Int>): Int {
      if (range is ClosedFloatingPointRange) {
         return RangesKt.coerceIn(`$this$coerceIn`, range as ClosedFloatingPointRange<Integer>).intValue();
      } else if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + 46);
      } else {
         return if (`$this$coerceIn` < (range.getStart() as java.lang.Number).intValue())
            (range.getStart() as java.lang.Number).intValue()
            else
            (
               if (`$this$coerceIn` > (range.getEndInclusive() as java.lang.Number).intValue())
                  (range.getEndInclusive() as java.lang.Number).intValue()
                  else
                  `$this$coerceIn`
            );
      }
   }

   @JvmStatic
   public fun Long.coerceIn(range: ClosedRange<Long>): Long {
      if (range is ClosedFloatingPointRange) {
         return RangesKt.coerceIn(`$this$coerceIn`, range as ClosedFloatingPointRange<java.lang.Long>).longValue();
      } else if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
      } else {
         return if (`$this$coerceIn` < (range.getStart() as java.lang.Number).longValue())
            (range.getStart() as java.lang.Number).longValue()
            else
            (
               if (`$this$coerceIn` > (range.getEndInclusive() as java.lang.Number).longValue())
                  (range.getEndInclusive() as java.lang.Number).longValue()
                  else
                  `$this$coerceIn`
            );
      }
   }

   open fun RangesKt___RangesKt() {
   }
}
