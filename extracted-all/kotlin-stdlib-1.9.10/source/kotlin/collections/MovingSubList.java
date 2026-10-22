package kotlin.collections

import java.util.RandomAccess

internal class MovingSubList<E>(list: List<Any>) : AbstractList<E>, RandomAccess {
   private final var _size: Int
   private final var fromIndex: Int
   private final val list: List<Any>
   public open val size: Int
      public open get() {
         return this._size;
      }


   init {
      this.list = list;
   }

   public fun move(fromIndex: Int, toIndex: Int) {
      AbstractList.Companion.checkRangeIndexes$kotlin_stdlib(fromIndex, toIndex, this.list.size());
      this.fromIndex = fromIndex;
      this._size = toIndex - fromIndex;
   }

   public override operator fun get(index: Int): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this._size);
      return this.list.get(this.fromIndex + index);
   }
}
