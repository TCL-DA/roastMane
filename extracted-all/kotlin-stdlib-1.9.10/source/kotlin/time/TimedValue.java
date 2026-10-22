package kotlin.time

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
data class TimedValue<T>(value: Any, duration: Duration) : TimedValue((T)value, duration) {
   private long duration;

   public final val duration: Duration
   public final val value: Any

   fun TimedValue(value: T, duration: Long) {
      this.value = (T)value;
      this.duration = duration;
   }

   public operator fun component1(): Any {
      return this.value;
   }

   public operator fun component2(): Duration {
      return this.duration;
   }

   public fun copy(value: Any, duration: Duration): TimedValue<Any> {
      return new TimedValue<>(value, duration, null);
   }

   public override fun toString(): String {
      return "TimedValue(value=" + this.value + ", duration=" + Duration.toString-impl(this.duration) + ')';
   }

   public override fun hashCode(): Int {
      return (if (this.value == null) 0 else this.value.hashCode()) * 31 + Duration.hashCode-impl(this.duration);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is TimedValue) {
         return false;
      } else {
         var var2: TimedValue = other as TimedValue;
         if (!(this.value == (other as TimedValue).value)) {
            return false;
         } else {
            return Duration.equals-impl0(this.duration, var2.duration);
         }
      }
   }
}
