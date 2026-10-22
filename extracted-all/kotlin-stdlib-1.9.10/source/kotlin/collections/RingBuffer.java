package kotlin.collections

import java.util.Arrays
import java.util.RandomAccess
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nSlidingWindow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SlidingWindow.kt\nkotlin/collections/RingBuffer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,207:1\n205#1:209\n205#1:210\n205#1:211\n1#2:208\n*S KotlinDebug\n*F\n+ 1 SlidingWindow.kt\nkotlin/collections/RingBuffer\n*L\n106#1:209\n176#1:210\n189#1:211\n*E\n"])
private class RingBuffer<T>(vararg buffer: Any, filledSize: Int) : AbstractList<T>, RandomAccess {
   private Object[] buffer;

   private final val buffer: Array<Any?>
   private final val capacity: Int
   public open var size: Int
      private set
   private final var startIndex: Int

   init {
      this.buffer = buffer;
      if (filledSize < 0) {
         throw new IllegalArgumentException(("ring buffer filled size should not be negative but it is " + filledSize).toString());
      } else if (filledSize > this.buffer.length) {
         throw new IllegalArgumentException(
            ("ring buffer filled size: " + filledSize + " cannot be larger than the buffer size: " + this.buffer.length).toString()
         );
      } else {
         this.capacity = this.buffer.length;
         this.size = filledSize;
      }
   }

   public constructor(capacity: Int) : this(new Object[capacity], 0)
   public override operator fun get(index: Int): Any {
      AbstractList.Companion.checkElementIndex$kotlin_stdlib(index, this.size());
      return (T)this.buffer[(this.startIndex + index) % access$getCapacity$p(this)];
   }

   public fun isFull(): Boolean {
      return this.size() == this.capacity;
   }

   public override operator fun iterator(): Iterator<Any> {
      return new AbstractIterator<T>(this) {
         private int count;
         private int index;

         {
            this.this$0 = `$receiver`;
            this.count = `$receiver`.size();
            this.index = RingBuffer.access$getStartIndex$p(`$receiver`);
         }

         @Override
         protected void computeNext() {
            if (this.count == 0) {
               this.done();
            } else {
               this.setNext((T)RingBuffer.access$getBuffer$p(this.this$0)[this.index]);
               this.index = (this.index + 1) % RingBuffer.access$getCapacity$p(this.this$0);
               this.count += -1;
            }
         }
      };
   }

   protected override fun <T> toArray(array: Array<T>): Array<T> {
      var var10000: Array<Any>;
      if (array.length < this.size()) {
         var10000 = Arrays.copyOf(array, this.size());
      } else {
         var10000 = array;
      }

      var result: Array<Any> = var10000;
      var size: Int = this.size();
      var widx: Int = 0;

      for (int idx = this.startIndex; widx < size && idx < this.capacity; idx++) {
         result[widx] = this.buffer[idx];
         widx++;
      }

      for (int var6 = 0; widx < size; var6++) {
         result[widx] = this.buffer[var6];
         widx++;
      }

      if (result.length > this.size()) {
         result[this.size()] = null;
      }

      return (T[])result;
   }

   protected override fun toArray(): Array<Any?> {
      return this.toArray(new Object[this.size()]);
   }

   public fun expanded(maxCapacity: Int): RingBuffer<Any> {
      var newCapacity: Int = RangesKt.coerceAtMost(this.capacity + (this.capacity shr 1) + 1, maxCapacity);
      var var10000: Array<Any>;
      if (this.startIndex == 0) {
         var10000 = Arrays.copyOf(this.buffer, newCapacity);
      } else {
         var10000 = this.toArray(new Object[newCapacity]);
      }

      return new RingBuffer<>(var10000, this.size());
   }

   public fun add(element: Any) {
      if (this.isFull()) {
         throw new IllegalStateException("ring buffer is full");
      } else {
         this.buffer[(this.startIndex + this.size()) % access$getCapacity$p(this)] = element;
         this.size = this.size() + 1;
      }
   }

   public fun removeFirst(n: Int) {
      if (n < 0) {
         throw new IllegalArgumentException(("n shouldn't be negative but it is " + n).toString());
      } else if (n > this.size()) {
         throw new IllegalArgumentException(("n shouldn't be greater than the buffer size: n = " + n + ", size = " + this.size()).toString());
      } else {
         if (n > 0) {
            var var8: Int = this.startIndex;
            var end: Int = (this.startIndex + n) % access$getCapacity$p(this);
            if (var8 > end) {
               ArraysKt.fill(this.buffer, null, var8, this.capacity);
               ArraysKt.fill(this.buffer, null, 0, end);
            } else {
               ArraysKt.fill(this.buffer, null, var8, end);
            }

            this.startIndex = end;
            this.size = this.size() - n;
         }
      }
   }

   private inline fun Int.forward(n: Int): Int {
      return (`$this$forward` + n) % access$getCapacity$p(this);
   }
}
