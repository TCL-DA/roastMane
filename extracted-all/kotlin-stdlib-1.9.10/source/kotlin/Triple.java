package kotlin

import java.io.Serializable

data class Triple<A, B, C>(first: Any, second: Any, third: Any) : Serializable {
   public final val first: Any
   public final val second: Any
   public final val third: Any

   init {
      this.first = (A)first;
      this.second = (B)second;
      this.third = (C)third;
   }

   public override fun toString(): String {
      return "" + '(' + this.first + ", " + this.second + ", " + this.third + ')';
   }

   public operator fun component1(): Any {
      return this.first;
   }

   public operator fun component2(): Any {
      return this.second;
   }

   public operator fun component3(): Any {
      return this.third;
   }

   public fun copy(first: Any, second: Any, third: Any): Triple<Any, Any, Any> {
      return new Triple<>((A)first, (B)second, (C)third);
   }

   public override fun hashCode(): Int {
      return ((if (this.first == null) 0 else this.first.hashCode()) * 31 + (if (this.second == null) 0 else this.second.hashCode())) * 31
         + (if (this.third == null) 0 else this.third.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Triple) {
         return false;
      } else {
         var var2: Triple = other as Triple;
         if (!(this.first == (other as Triple).first)) {
            return false;
         } else if (!(this.second == var2.second)) {
            return false;
         } else {
            return this.third == var2.third;
         }
      }
   }
}
