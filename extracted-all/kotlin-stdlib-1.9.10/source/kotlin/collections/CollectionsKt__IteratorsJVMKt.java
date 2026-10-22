package kotlin.collections

import java.util.Enumeration

// $VF: Class flags could not be determined
internal class CollectionsKt__IteratorsJVMKt : CollectionsKt__IterablesKt {
   @JvmStatic
   public operator fun <T> Enumeration<T>.iterator(): Iterator<T> {
      return new java.util.Iterator<T>(`$this$iterator`) {
         {
            this.$this_iterator = `$receiver`;
         }

         @Override
         public boolean hasNext() {
            return this.$this_iterator.hasMoreElements();
         }

         @Override
         public T next() {
            return (T)this.$this_iterator.nextElement();
         }

         @Override
         public void remove() {
            throw new UnsupportedOperationException("Operation is not supported for read-only collection");
         }
      };
   }

   open fun CollectionsKt__IteratorsJVMKt() {
   }
}
