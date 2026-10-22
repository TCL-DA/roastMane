package kotlin.sequences

import java.util.NoSuchElementException
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/TakeSequence\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,680:1\n1#2:681\n*E\n"])
internal class TakeSequence<T>(sequence: Sequence<Any>, count: Int) : Sequence<T>, DropTakeSequence<T> {
   private final val count: Int
   private final val sequence: Sequence<Any>

   init {
      this.sequence = sequence;
      this.count = count;
      if (this.count < 0) {
         throw new IllegalArgumentException(("count must be non-negative, but was " + this.count + '.').toString());
      }
   }

   public override fun drop(n: Int): Sequence<Any> {
      return (Sequence<T>)(if (n >= this.count) SequencesKt.emptySequence() else new SubSequence<>(this.sequence, n, this.count));
   }

   public override fun take(n: Int): Sequence<Any> {
      return if (n >= this.count) this else new TakeSequence<>(this.sequence, n);
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<T>(this) {
         private int left;
         @NotNull
         private final java.util.Iterator<T> iterator;

         {
            this.left = TakeSequence.access$getCount$p(`$receiver`);
            this.iterator = TakeSequence.access$getSequence$p(`$receiver`).iterator();
         }

         public final int getLeft() {
            return this.left;
         }

         public final void setLeft(int var1) {
            this.left = var1;
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         @Override
         public T next() {
            if (this.left == 0) {
               throw new NoSuchElementException();
            } else {
               this.left += -1;
               return this.iterator.next();
            }
         }

         @Override
         public boolean hasNext() {
            return this.left > 0 && this.iterator.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
