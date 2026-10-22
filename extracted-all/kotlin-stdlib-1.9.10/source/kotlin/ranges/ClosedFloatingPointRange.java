package kotlin.ranges

@SinceKotlin(version = "1.1")
interface ClosedFloatingPointRange<T extends java.lang.Comparable<? super T>> : ClosedRange<T> {
   public override operator fun contains(value: Any): Boolean {
   }

   public override fun isEmpty(): Boolean {
   }

   public abstract fun lessThanOrEquals(a: Any, b: Any): Boolean {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <T extends java.lang.Comparable<? super T>> contains(`$this`: ClosedFloatingPointRange<T>, value: T): Boolean {
         return `$this`.lessThanOrEquals((T)`$this`.getStart(), (T)value) && `$this`.lessThanOrEquals((T)value, (T)`$this`.getEndInclusive());
      }

      @JvmStatic
      fun <T extends java.lang.Comparable<? super T>> isEmpty(`$this`: ClosedFloatingPointRange<T>): Boolean {
         return !`$this`.lessThanOrEquals((T)`$this`.getStart(), (T)`$this`.getEndInclusive());
      }
   }
}
