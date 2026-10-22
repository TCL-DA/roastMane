package kotlin.ranges

import java.util.NoSuchElementException
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.Intrinsics
import kotlin.random.Random
import kotlin.random.URandomKt

// $VF: Class flags could not be determined
internal class URangesKt___URangesKt {
   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun UIntProgression.first(): UInt {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$first` + " is empty.");
      } else {
         return `$this$first`.getFirst-pVg5ArA();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun ULongProgression.first(): ULong {
      if (`$this$first`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$first` + " is empty.");
      } else {
         return `$this$first`.getFirst-s-VKNKU();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun UIntProgression.firstOrNull(): UInt? {
      return if (`$this$firstOrNull`.isEmpty()) null else UInt.box-impl(`$this$firstOrNull`.getFirst-pVg5ArA());
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun ULongProgression.firstOrNull(): ULong? {
      return if (`$this$firstOrNull`.isEmpty()) null else ULong.box-impl(`$this$firstOrNull`.getFirst-s-VKNKU());
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun UIntProgression.last(): UInt {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$last` + " is empty.");
      } else {
         return `$this$last`.getLast-pVg5ArA();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun ULongProgression.last(): ULong {
      if (`$this$last`.isEmpty()) {
         throw new NoSuchElementException("Progression " + `$this$last` + " is empty.");
      } else {
         return `$this$last`.getLast-s-VKNKU();
      }
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun UIntProgression.lastOrNull(): UInt? {
      return if (`$this$lastOrNull`.isEmpty()) null else UInt.box-impl(`$this$lastOrNull`.getLast-pVg5ArA());
   }

   @SinceKotlin(version = "1.7")
   @JvmStatic
   public fun ULongProgression.lastOrNull(): ULong? {
      return if (`$this$lastOrNull`.isEmpty()) null else ULong.box-impl(`$this$lastOrNull`.getLast-s-VKNKU());
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntRange.random(): UInt {
      return URangesKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongRange.random(): ULong {
      return URangesKt.random(`$this$random`, Random.Default);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UIntRange.random(random: Random): UInt {
      try {
         return URandomKt.nextUInt(random, `$this$random`);
      } catch (IllegalArgumentException var3) {
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULongRange.random(random: Random): ULong {
      try {
         return URandomKt.nextULong(random, `$this$random`);
      } catch (IllegalArgumentException var3) {
         throw new NoSuchElementException(var3.getMessage());
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun UIntRange.randomOrNull(): UInt? {
      return URangesKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline fun ULongRange.randomOrNull(): ULong? {
      return URangesKt.randomOrNull(`$this$randomOrNull`, Random.Default);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UIntRange.randomOrNull(random: Random): UInt? {
      return if (`$this$randomOrNull`.isEmpty()) null else UInt.box-impl(URandomKt.nextUInt(random, `$this$randomOrNull`));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULongRange.randomOrNull(random: Random): ULong? {
      return if (`$this$randomOrNull`.isEmpty()) null else ULong.box-impl(URandomKt.nextULong(random, `$this$randomOrNull`));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun UIntRange.contains(element: UInt?): Boolean {
      return element != null && `$this$contains_u2dbiwQdVI`.contains-WZ4Q5Ns(element.unbox-impl());
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @InlineOnly
   @JvmStatic
   public inline operator fun ULongRange.contains(element: ULong?): Boolean {
      return element != null && `$this$contains_u2dGYNo2lE`.contains-VKZWuLQ(element.unbox-impl());
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun UIntRange.contains(value: UByte): Boolean {
      return `$this$contains_u2d68kG9v0`.contains-WZ4Q5Ns(UInt.constructor-impl(value and 255));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun ULongRange.contains(value: UByte): Boolean {
      return `$this$contains_u2dULb_u2dyJY`.contains-VKZWuLQ(ULong.constructor-impl((long)value and 255L));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun ULongRange.contains(value: UInt): Boolean {
      return `$this$contains_u2dGab390E`.contains-VKZWuLQ(ULong.constructor-impl((long)value and 4294967295L));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun UIntRange.contains(value: ULong): Boolean {
      return ULong.constructor-impl(value ushr 32) == 0L && `$this$contains_u2dfz5IDCE`.contains-WZ4Q5Ns(UInt.constructor-impl((int)value));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun UIntRange.contains(value: UShort): Boolean {
      return `$this$contains_u2dZsK3CEQ`.contains-WZ4Q5Ns(UInt.constructor-impl(value and '\uffff'));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public operator fun ULongRange.contains(value: UShort): Boolean {
      return `$this$contains_u2duhHAxoY`.contains-VKZWuLQ(ULong.constructor-impl((long)value and 65535L));
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UByte.downTo(to: UByte): UIntProgression {
      return UIntProgression.Companion.fromClosedRange-Nkh28Cs(UInt.constructor-impl(`$this$downTo_u2dKr8caGY` and 255), UInt.constructor-impl(to and 255), -1);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UInt.downTo(to: UInt): UIntProgression {
      return UIntProgression.Companion.fromClosedRange-Nkh28Cs(`$this$downTo_u2dJ1ME1BU`, to, -1);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun ULong.downTo(to: ULong): ULongProgression {
      return ULongProgression.Companion.fromClosedRange-7ftBX0g(`$this$downTo_u2deb3DHEI`, to, -1L);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UShort.downTo(to: UShort): UIntProgression {
      return UIntProgression.Companion
         .fromClosedRange-Nkh28Cs(UInt.constructor-impl(`$this$downTo_u2d5PvTz6A` and '\uffff'), UInt.constructor-impl(to and '\uffff'), -1);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UIntProgression.reversed(): UIntProgression {
      return UIntProgression.Companion
         .fromClosedRange-Nkh28Cs(`$this$reversed`.getLast-pVg5ArA(), `$this$reversed`.getFirst-pVg5ArA(), -`$this$reversed`.getStep());
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULongProgression.reversed(): ULongProgression {
      return ULongProgression.Companion
         .fromClosedRange-7ftBX0g(`$this$reversed`.getLast-s-VKNKU(), `$this$reversed`.getFirst-s-VKNKU(), -`$this$reversed`.getStep());
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UIntProgression.step(step: Int): UIntProgression {
      RangesKt.checkStepIsPositive(step > 0, step);
      return UIntProgression.Companion
         .fromClosedRange-Nkh28Cs(`$this$step`.getFirst-pVg5ArA(), `$this$step`.getLast-pVg5ArA(), if (`$this$step`.getStep() > 0) step else -step);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun ULongProgression.step(step: Long): ULongProgression {
      RangesKt.checkStepIsPositive(step > 0L, step);
      return ULongProgression.Companion
         .fromClosedRange-7ftBX0g(`$this$step`.getFirst-s-VKNKU(), `$this$step`.getLast-s-VKNKU(), if (`$this$step`.getStep() > 0L) step else -step);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UByte.until(to: UByte): UIntRange {
      return if (Intrinsics.compare(to and 255, 0 and 255) <= 0)
         UIntRange.Companion.getEMPTY()
         else
         new UIntRange(UInt.constructor-impl(`$this$until_u2dKr8caGY` and 255), UInt.constructor-impl(UInt.constructor-impl(to and 255) - 1), null);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UInt.until(to: UInt): UIntRange {
      return if (Integer.compareUnsigned(to, 0) <= 0)
         UIntRange.Companion.getEMPTY()
         else
         new UIntRange(`$this$until_u2dJ1ME1BU`, UInt.constructor-impl(to - 1), null);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun ULong.until(to: ULong): ULongRange {
      return if (java.lang.Long.compareUnsigned(to, 0L) <= 0)
         ULongRange.Companion.getEMPTY()
         else
         new ULongRange(`$this$until_u2deb3DHEI`, ULong.constructor-impl(to - ULong.constructor-impl((long)1 and 4294967295L)), null);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public infix fun UShort.until(to: UShort): UIntRange {
      return if (Intrinsics.compare(to and '\uffff', 0 and '\uffff') <= 0)
         UIntRange.Companion.getEMPTY()
         else
         new UIntRange(UInt.constructor-impl(`$this$until_u2d5PvTz6A` and '\uffff'), UInt.constructor-impl(UInt.constructor-impl(to and '\uffff') - 1), null);
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UInt.coerceAtLeast(minimumValue: UInt): UInt {
      return if (Integer.compareUnsigned(`$this$coerceAtLeast_u2dJ1ME1BU`, minimumValue) < 0) minimumValue else `$this$coerceAtLeast_u2dJ1ME1BU`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULong.coerceAtLeast(minimumValue: ULong): ULong {
      return if (java.lang.Long.compareUnsigned(`$this$coerceAtLeast_u2deb3DHEI`, minimumValue) < 0) minimumValue else `$this$coerceAtLeast_u2deb3DHEI`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UByte.coerceAtLeast(minimumValue: UByte): UByte {
      return if (Intrinsics.compare(`$this$coerceAtLeast_u2dKr8caGY` and 255, minimumValue and 255) < 0) minimumValue else `$this$coerceAtLeast_u2dKr8caGY`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UShort.coerceAtLeast(minimumValue: UShort): UShort {
      return if (Intrinsics.compare(`$this$coerceAtLeast_u2d5PvTz6A` and '\uffff', minimumValue and '\uffff') < 0)
         minimumValue
         else
         `$this$coerceAtLeast_u2d5PvTz6A`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UInt.coerceAtMost(maximumValue: UInt): UInt {
      return if (Integer.compareUnsigned(`$this$coerceAtMost_u2dJ1ME1BU`, maximumValue) > 0) maximumValue else `$this$coerceAtMost_u2dJ1ME1BU`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULong.coerceAtMost(maximumValue: ULong): ULong {
      return if (java.lang.Long.compareUnsigned(`$this$coerceAtMost_u2deb3DHEI`, maximumValue) > 0) maximumValue else `$this$coerceAtMost_u2deb3DHEI`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UByte.coerceAtMost(maximumValue: UByte): UByte {
      return if (Intrinsics.compare(`$this$coerceAtMost_u2dKr8caGY` and 255, maximumValue and 255) > 0) maximumValue else `$this$coerceAtMost_u2dKr8caGY`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UShort.coerceAtMost(maximumValue: UShort): UShort {
      return if (Intrinsics.compare(`$this$coerceAtMost_u2d5PvTz6A` and '\uffff', maximumValue and '\uffff') > 0)
         maximumValue
         else
         `$this$coerceAtMost_u2d5PvTz6A`;
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UInt.coerceIn(minimumValue: UInt, maximumValue: UInt): UInt {
      if (Integer.compareUnsigned(minimumValue, maximumValue) > 0) {
         throw new IllegalArgumentException(
            "Cannot coerce value to an empty range: maximum "
               + UInt.toString-impl(maximumValue)
               + " is less than minimum "
               + UInt.toString-impl(minimumValue)
               + 46
         );
      } else if (Integer.compareUnsigned(`$this$coerceIn_u2dWZ9TVnA`, minimumValue) < 0) {
         return minimumValue;
      } else {
         return if (Integer.compareUnsigned(`$this$coerceIn_u2dWZ9TVnA`, maximumValue) > 0) maximumValue else `$this$coerceIn_u2dWZ9TVnA`;
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULong.coerceIn(minimumValue: ULong, maximumValue: ULong): ULong {
      if (java.lang.Long.compareUnsigned(minimumValue, maximumValue) > 0) {
         throw new IllegalArgumentException(
            "Cannot coerce value to an empty range: maximum "
               + ULong.toString-impl(maximumValue)
               + " is less than minimum "
               + ULong.toString-impl(minimumValue)
               + '.'
         );
      } else if (java.lang.Long.compareUnsigned(`$this$coerceIn_u2dsambcqE`, minimumValue) < 0) {
         return minimumValue;
      } else {
         return if (java.lang.Long.compareUnsigned(`$this$coerceIn_u2dsambcqE`, maximumValue) > 0) maximumValue else `$this$coerceIn_u2dsambcqE`;
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UByte.coerceIn(minimumValue: UByte, maximumValue: UByte): UByte {
      if (Intrinsics.compare(minimumValue and 255, maximumValue and 255) > 0) {
         throw new IllegalArgumentException(
            "Cannot coerce value to an empty range: maximum "
               + UByte.toString-impl(maximumValue)
               + " is less than minimum "
               + UByte.toString-impl(minimumValue)
               + '.'
         );
      } else if (Intrinsics.compare(`$this$coerceIn_u2db33U2AM` and 255, minimumValue and 255) < 0) {
         return minimumValue;
      } else {
         return if (Intrinsics.compare(`$this$coerceIn_u2db33U2AM` and 255, maximumValue and 255) > 0) maximumValue else `$this$coerceIn_u2db33U2AM`;
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UShort.coerceIn(minimumValue: UShort, maximumValue: UShort): UShort {
      if (Intrinsics.compare(minimumValue and '\uffff', maximumValue and '\uffff') > 0) {
         throw new IllegalArgumentException(
            "Cannot coerce value to an empty range: maximum "
               + UShort.toString-impl(maximumValue)
               + " is less than minimum "
               + UShort.toString-impl(minimumValue)
               + '.'
         );
      } else if (Intrinsics.compare(`$this$coerceIn_u2dVKSA0NQ` and '\uffff', minimumValue and '\uffff') < 0) {
         return minimumValue;
      } else {
         return if (Intrinsics.compare(`$this$coerceIn_u2dVKSA0NQ` and '\uffff', maximumValue and '\uffff') > 0) maximumValue else `$this$coerceIn_u2dVKSA0NQ`;
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun UInt.coerceIn(range: ClosedRange<UInt>): UInt {
      if (range is ClosedFloatingPointRange) {
         return RangesKt.coerceIn(UInt.box-impl(`$this$coerceIn_u2dwuiCnnA`), range as ClosedFloatingPointRange<UInt>).unbox-impl();
      } else if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + 46);
      } else {
         return if (Integer.compareUnsigned(`$this$coerceIn_u2dwuiCnnA`, (range.getStart() as UInt).unbox-impl()) < 0)
            (range.getStart() as UInt).unbox-impl()
            else
            (
               if (Integer.compareUnsigned(`$this$coerceIn_u2dwuiCnnA`, (range.getEndInclusive() as UInt).unbox-impl()) > 0)
                  (range.getEndInclusive() as UInt).unbox-impl()
                  else
                  `$this$coerceIn_u2dwuiCnnA`
            );
      }
   }

   @SinceKotlin(version = "1.5")
   @WasExperimental(markerClass = [ExperimentalUnsignedTypes::class])
   @JvmStatic
   public fun ULong.coerceIn(range: ClosedRange<ULong>): ULong {
      if (range is ClosedFloatingPointRange) {
         return RangesKt.coerceIn(ULong.box-impl(`$this$coerceIn_u2dJPwROB0`), range as ClosedFloatingPointRange<ULong>).unbox-impl();
      } else if (range.isEmpty()) {
         throw new IllegalArgumentException("Cannot coerce value to an empty range: " + range + '.');
      } else {
         return if (java.lang.Long.compareUnsigned(`$this$coerceIn_u2dJPwROB0`, (range.getStart() as ULong).unbox-impl()) < 0)
            (range.getStart() as ULong).unbox-impl()
            else
            (
               if (java.lang.Long.compareUnsigned(`$this$coerceIn_u2dJPwROB0`, (range.getEndInclusive() as ULong).unbox-impl()) > 0)
                  (range.getEndInclusive() as ULong).unbox-impl()
                  else
                  `$this$coerceIn_u2dJPwROB0`
            );
      }
   }

   open fun URangesKt___URangesKt() {
   }
}
