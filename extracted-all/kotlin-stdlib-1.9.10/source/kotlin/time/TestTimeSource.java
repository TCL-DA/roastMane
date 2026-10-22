package kotlin.time

import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
@SourceDebugExtension(["SMAP\nTimeSources.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TimeSources.kt\nkotlin/time/TestTimeSource\n+ 2 longSaturatedMath.kt\nkotlin/time/LongSaturatedMathKt\n*L\n1#1,199:1\n80#2:200\n80#2:201\n*S KotlinDebug\n*F\n+ 1 TimeSources.kt\nkotlin/time/TestTimeSource\n*L\n173#1:200\n180#1:201\n*E\n"])
class TestTimeSource : AbstractLongTimeSource(DurationUnit.NANOSECONDS) {
   private final var reading: Long


   protected override fun read(): Long {
      return this.reading;
   }

   public operator fun plusAssign(duration: Duration) {
      var longDelta: Long = Duration.toLong-impl(duration, this.getUnit());
      if ((longDelta - 1L or 1L) != java.lang.Long.MAX_VALUE) {
         var half: Long = this.reading + longDelta;
         if ((this.reading xor longDelta) >= 0L && (this.reading xor this.reading + longDelta) < 0L) {
            this.overflow-LRDsOJo(duration);
         }

         this.reading = half;
      } else {
         var var11: Long = Duration.div-UwyO8pc(duration, 2);
         if ((Duration.toLong-impl(var11, this.getUnit()) - 1L or 1L) != java.lang.Long.MAX_VALUE) {
            try {
               this.plusAssign-LRDsOJo(var11);
               this.plusAssign-LRDsOJo(Duration.minus-LRDsOJo(duration, var11));
            } catch (IllegalStateException var10) {
               this.reading = this.reading;
               throw var10;
            }
         } else {
            this.overflow-LRDsOJo(duration);
         }
      }
   }

   private fun overflow(duration: Duration) {
      throw new IllegalStateException(
         "TestTimeSource will overflow if its reading "
            + this.reading
            + DurationUnitKt.shortName(this.getUnit())
            + " is advanced by "
            + Duration.toString-impl(duration)
            + '.'
      );
   }
}
