package kotlin

import java.io.Serializable

data class Pair<A, B>(first: Any, second: Any) : Serializable {
   public final val first: Any
   public final val second: Any

   init {
      this.first = (A)first;
      this.second = (B)second;
   }

   public override fun toString(): String {
      return "" + '(' + this.first + ", " + this.second + ')';
   }

   public operator fun component1(): Any {
      return this.first;
   }

   public operator fun component2(): Any {
      return this.second;
   }

   public fun copy(first: Any, second: Any): Pair<Any, Any> {
      return new Pair<>((A)first, (B)second);
   }

   public override fun hashCode(): Int {
      return (if (this.first == null) 0 else this.first.hashCode()) * 31 + (if (this.second == null) 0 else this.second.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is Pair) {
         return false;
      } else {
         var var2: Pair = other as Pair;
         if (!(this.first == (other as Pair).first)) {
            return false;
         } else {
            return this.second == var2.second;
         }
      }
   }
}
