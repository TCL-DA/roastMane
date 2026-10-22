package kotlin.text

import java.util.NoSuchElementException
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

private class DelimitedRangesSequence(input: CharSequence, startIndex: Int, limit: Int, getNextMatch: (CharSequence, Int) -> Pair<Int, Int>?) :
   Sequence<IntRange> {
   private final val getNextMatch: (CharSequence, Int) -> Pair<Int, Int>?
   private final val input: CharSequence
   private final val limit: Int
   private final val startIndex: Int

   init {
      this.input = input;
      this.startIndex = startIndex;
      this.limit = limit;
      this.getNextMatch = getNextMatch;
   }

   public override operator fun iterator(): Iterator<IntRange> {
      return new java.util.Iterator<IntRange>(this) {
         private int nextState;
         private int currentStartIndex;
         private int nextSearchIndex;
         @Nullable
         private IntRange nextItem;
         private int counter;

         {
            this.this$0 = `$receiver`;
            this.nextState = -1;
            this.currentStartIndex = RangesKt.coerceIn(
               DelimitedRangesSequence.access$getStartIndex$p(`$receiver`), 0, DelimitedRangesSequence.access$getInput$p(`$receiver`).length()
            );
            this.nextSearchIndex = this.currentStartIndex;
         }

         public final int getNextState() {
            return this.nextState;
         }

         public final void setNextState(int var1) {
            this.nextState = var1;
         }

         public final int getCurrentStartIndex() {
            return this.currentStartIndex;
         }

         public final void setCurrentStartIndex(int var1) {
            this.currentStartIndex = var1;
         }

         public final int getNextSearchIndex() {
            return this.nextSearchIndex;
         }

         public final void setNextSearchIndex(int var1) {
            this.nextSearchIndex = var1;
         }

         @Nullable
         public final IntRange getNextItem() {
            return this.nextItem;
         }

         public final void setNextItem(@Nullable IntRange var1) {
            this.nextItem = var1;
         }

         public final int getCounter() {
            return this.counter;
         }

         public final void setCounter(int var1) {
            this.counter = var1;
         }

         private final void calcNext() {
            if (this.nextSearchIndex < 0) {
               this.nextState = 0;
               this.nextItem = null;
            } else {
               label27: {
                  label26: {
                     if (DelimitedRangesSequence.access$getLimit$p(this.this$0) > 0) {
                        this.counter++;
                        if (this.counter >= DelimitedRangesSequence.access$getLimit$p(this.this$0)) {
                           break label26;
                        }
                     }

                     if (this.nextSearchIndex <= DelimitedRangesSequence.access$getInput$p(this.this$0).length()) {
                        var match: Pair = DelimitedRangesSequence.access$getGetNextMatch$p(this.this$0)
                           .invoke(DelimitedRangesSequence.access$getInput$p(this.this$0), this.nextSearchIndex) as Pair;
                        if (match == null) {
                           this.nextItem = new IntRange(this.currentStartIndex, StringsKt.getLastIndex(DelimitedRangesSequence.access$getInput$p(this.this$0)));
                           this.nextSearchIndex = -1;
                        } else {
                           var index: Int = (match.component1() as java.lang.Number).intValue();
                           var length: Int = (match.component2() as java.lang.Number).intValue();
                           this.nextItem = RangesKt.until(this.currentStartIndex, index);
                           this.currentStartIndex = index + length;
                           this.nextSearchIndex = this.currentStartIndex + (if (length == 0) 1 else 0);
                        }
                        break label27;
                     }
                  }

                  this.nextItem = new IntRange(this.currentStartIndex, StringsKt.getLastIndex(DelimitedRangesSequence.access$getInput$p(this.this$0)));
                  this.nextSearchIndex = -1;
               }

               this.nextState = 1;
            }
         }

         @NotNull
         public IntRange next() {
            if (this.nextState == -1) {
               this.calcNext();
            }

            if (this.nextState == 0) {
               throw new NoSuchElementException();
            } else {
               var var10000: IntRange = this.nextItem;
               this.nextItem = null;
               this.nextState = -1;
               return var10000;
            }
         }

         @Override
         public boolean hasNext() {
            if (this.nextState == -1) {
               this.calcNext();
            }

            return this.nextState == 1;
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }
}
