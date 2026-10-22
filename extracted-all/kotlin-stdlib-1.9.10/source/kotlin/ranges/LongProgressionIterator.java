package kotlin.ranges

import java.util.NoSuchElementException

internal class LongProgressionIterator(first: Long, last: Long, step: Long) : LongIterator {
   private final val finalElement: Long
   private final var hasNext: Boolean
   private final var next: Long
   public final val step: Long

   init {
      this.step = step;
      this.finalElement = last;
      this.hasNext = if (this.step > 0L) first <= last else first >= last;
      this.next = if (this.hasNext) first else this.finalElement;
   }

   public override operator fun hasNext(): Boolean {
      return this.hasNext;
   }

   public override fun nextLong(): Long {
      var value: Long = this.next;
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
