package kotlin.ranges

import java.util.NoSuchElementException
import kotlin.jvm.internal.Intrinsics

internal class CharProgressionIterator(first: Char, last: Char, step: Int) : CharIterator {
   private final val finalElement: Int
   private final var hasNext: Boolean
   private final var next: Int
   public final val step: Int

   init {
      this.step = step;
      this.finalElement = last;
      this.hasNext = if (this.step > 0) Intrinsics.compare(first, last) <= 0 else Intrinsics.compare(first, last) >= 0;
      this.next = if (this.hasNext) first else this.finalElement;
   }

   public override operator fun hasNext(): Boolean {
      return this.hasNext;
   }

   public override fun nextChar(): Char {
      var value: Int = this.next;
      if (this.next == this.finalElement) {
         if (!this.hasNext) {
            throw new NoSuchElementException();
         }

         this.hasNext = false;
      } else {
         this.next = this.next + this.step;
      }

      return (char)value;
   }
}
