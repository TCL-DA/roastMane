package kotlin.time

@SinceKotlin(version = "1.9")
@WasExperimental(markerClass = [ExperimentalTime::class])
interface ComparableTimeMark : TimeMark, java.lang.Comparable<ComparableTimeMark> {
   public abstract operator fun plus(duration: Duration): ComparableTimeMark {
   }

   public open operator fun minus(duration: Duration): ComparableTimeMark {
   }

   public abstract operator fun minus(other: ComparableTimeMark): Duration {
   }

   public open operator fun compareTo(other: ComparableTimeMark): Int {
   }

   public abstract override operator fun equals(other: Any?): Boolean {
   }

   public abstract override fun hashCode(): Int {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun `minus-LRDsOJo`(`$this`: ComparableTimeMark, duration: Long): ComparableTimeMark {
         return `$this`.plus-LRDsOJo(Duration.unaryMinus-UwyO8pc(duration));
      }

      @JvmStatic
      fun compareTo(`$this`: ComparableTimeMark, other: ComparableTimeMark): Int {
         return Duration.compareTo-LRDsOJo(`$this`.minus-UwyO8pc(other), Duration.Companion.getZERO-UwyO8pc());
      }

      @JvmStatic
      fun hasPassedNow(`$this`: ComparableTimeMark): Boolean {
         return TimeMark.DefaultImpls.hasPassedNow(`$this`);
      }

      @JvmStatic
      fun hasNotPassedNow(`$this`: ComparableTimeMark): Boolean {
         return TimeMark.DefaultImpls.hasNotPassedNow(`$this`);
      }
   }
}
