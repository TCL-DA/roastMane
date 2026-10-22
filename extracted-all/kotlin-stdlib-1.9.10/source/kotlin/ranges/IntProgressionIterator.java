package kotlin.ranges

import java.util.NoSuchElementException

internal class IntProgressionIterator(first: Int, last: Int, step: Int) : IntIterator {
   private final val finalElement: Int
   private final var hasNext: Boolean
   private final var next: Int
   public final val step: Int

   init {
      this.step = step;
      this.finalElement = last;
      this.hasNext = if (this.step > 0) first <= last else first >= last;
      this.next = if (this.hasNext) first else this.finalElement;
   }

   public override operator fun hasNext(): Boolean {
      return this.hasNext;
   }

   public override fun nextInt(): Int {
      var value: Int = this.next;
      if (this.next == this.finalElement) {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         }

         this.hasNext = false;
      } else {
         this.next = this.next + this.step;
      }

      return value;
   }
}
