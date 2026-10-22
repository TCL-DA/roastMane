package kotlin.sequences

import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/DropSequence\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,680:1\n1#2:681\n*E\n"])
internal class DropSequence<T>(sequence: Sequence<Any>, count: Int) : Sequence<T>, DropTakeSequence<T> {
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
      return if (this.count + n < 0) new DropSequence<>(this, n) else new DropSequence<>(this.sequence, this.count + n);
   }

   public override fun take(n: Int): Sequence<Any> {
      return (if (this.count + n < 0) new TakeSequence<>(this, n) else new SubSequence<>(this.sequence, this.count, this.count + n)) as Sequence<T>;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<T>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         private int left;

         {
            this.iterator = DropSequence.access$getSequence$p(`$receiver`).iterator();
            this.left = DropSequence.access$getCount$p(`$receiver`);
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         public final int getLeft() {
            return this.left;
         }

         public final void setLeft(int var1) {
            this.left = var1;
         }

         private final void drop() {
            while (this.left > 0 && this.iterator.hasNext()) {
               this.iterator.next();
               this.left += -1;
            }
         }

         @Override
         public T next() {
            this.drop();
            return this.iterator.next();
         }

         @Override
         public boolean hasNext() {
            this.drop();
            return this.iterator.hasNext();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
