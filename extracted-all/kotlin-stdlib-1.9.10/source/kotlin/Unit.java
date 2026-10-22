package kotlin

object Unit {
   @JvmStatic
   public Unit INSTANCE = new Unit();

   fun Unit() {
   }

   public override fun toString(): String {
      return "kotlin.Unit";
   }
}
