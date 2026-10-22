@file:SourceDebugExtension(["SMAP\nmeasureTime.kt\nKotlin\n*S Kotlin\n*F\n+ 1 measureTime.kt\nkotlin/time/MeasureTimeKt\n*L\n1#1,121:1\n50#1,7:122\n113#1,7:129\n*S KotlinDebug\n*F\n+ 1 measureTime.kt\nkotlin/time/MeasureTimeKt\n*L\n21#1:122,7\n83#1:129,7\n*E\n"])

package kotlin.time

import kotlin.contracts.InvocationKind
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.time.TimeSource.Monotonic

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun measureTime(block: () -> Unit): Duration {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var `mark$iv`: Long = TimeSource.Monotonic.INSTANCE.markNow-z9LOYto();
   block.invoke();
   return TimeSource.Monotonic.ValueTimeMark.elapsedNow-UwyO8pc(`mark$iv`);
}

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun TimeSource.measureTime(block: () -> Unit): Duration {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var mark: TimeMark = `$this$measureTime`.markNow();
   block.invoke();
   return mark.elapsedNow-UwyO8pc();
}

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun Monotonic.measureTime(block: () -> Unit): Duration {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var mark: Long = `$this$measureTime`.markNow-z9LOYto();
   block.invoke();
   return TimeSource.Monotonic.ValueTimeMark.elapsedNow-UwyO8pc(mark);
}

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun <T> measureTimedValue(block: () -> T): TimedValue<T> {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   return new TimedValue(block.invoke(), TimeSource.Monotonic.ValueTimeMark.elapsedNow-UwyO8pc(TimeSource.Monotonic.INSTANCE.markNow-z9LOYto()), null);
}

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun <T> TimeSource.measureTimedValue(block: () -> T): TimedValue<T> {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   return new TimedValue(block.invoke(), `$this$measureTimedValue`.markNow().elapsedNow-UwyO8pc(), null);
}

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
public inline fun <T> Monotonic.measureTimedValue(block: () -> T): TimedValue<T> {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   return new TimedValue(block.invoke(), TimeSource.Monotonic.ValueTimeMark.elapsedNow-UwyO8pc(`$this$measureTimedValue`.markNow-z9LOYto()), null);
}

