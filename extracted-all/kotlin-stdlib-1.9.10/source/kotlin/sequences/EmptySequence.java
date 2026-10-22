package kotlin.sequences

private object EmptySequence private constructor() : Sequence, DropTakeSequence {
   @JvmStatic
   public EmptySequence INSTANCE = new EmptySequence();


   public override operator fun iterator(): Iterator<Nothing> {
      return EmptyIterator.INSTANCE;
   }

   public open fun drop(n: Int): EmptySequence {
      return INSTANCE;
   }

   public open fun take(n: Int): EmptySequence {
      return INSTANCE;
   }
}
