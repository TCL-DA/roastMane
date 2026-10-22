package kotlin.sequences

import java.util.NoSuchElementException
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nSequences.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Sequences.kt\nkotlin/sequences/SubSequence\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,680:1\n1#2:681\n*E\n"])
internal class SubSequence<T>(sequence: Sequence<Any>, startIndex: Int, endIndex: Int) : Sequence<T>, DropTakeSequence<T> {
   private final val count: Int
      private final get() {
         return this.endIndex - this.startIndex;
      }

   private final val endIndex: Int
   private final val sequence: Sequence<Any>
   private final val startIndex: Int

   init {
      this.sequence = sequence;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      if (this.startIndex < 0) {
         throw new IllegalArgumentException(("startIndex should be non-negative, but is " + this.startIndex).toString());
      } else if (this.endIndex < 0) {
         throw new IllegalArgumentException(("endIndex should be non-negative, but is " + this.endIndex).toString());
      } else if (this.endIndex < this.startIndex) {
         throw new IllegalArgumentException(("endIndex should be not less than startIndex, but was " + this.endIndex + " < " + this.startIndex).toString());
      }
   }

   public override fun drop(n: Int): Sequence<Any> {
      return (Sequence<T>)(if (n >= this.getCount()) SequencesKt.emptySequence() else new SubSequence<>(this.sequence, this.startIndex + n, this.endIndex));
   }

   public override fun take(n: Int): Sequence<Any> {
      return if (n >= this.getCount()) this else new SubSequence<>(this.sequence, this.startIndex, this.startIndex + n);
   }

   public override operator fun iterator(): Iterator<Any> {
      return new java.util.Iterator<T>(this) {
         @NotNull
         private final java.util.Iterator<T> iterator;
         private int position;

         {
            this.this$0 = `$receiver`;
            this.iterator = SubSequence.access$getSequence$p(`$receiver`).iterator();
         }

         @NotNull
         public final java.util.Iterator<T> getIterator() {
            return this.iterator;
         }

         public final int getPosition() {
            return this.position;
         }

         public final void setPosition(int var1) {
            this.position = var1;
         }

         private final void drop() {
            while (this.position < SubSequence.access$getStartIndex$p(this.this$0) && this.iterator.hasNext()) {
               this.iterator.next();
               var var1: Int = this.position++;
            }
         }

         @Override
         public boolean hasNext() {
            this.drop();
            return this.position < SubSequence.access$getEndIndex$p(this.this$0) && this.iterator.hasNext();
         }

         @Override
         public T next() {
            this.drop();
            if (this.position >= SubSequence.access$getEndIndex$p(this.this$0)) {
               throw new NoSuchElementException();
            } else {
               var var1: Int = this.position++;
               return this.iterator.next();
            }
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
