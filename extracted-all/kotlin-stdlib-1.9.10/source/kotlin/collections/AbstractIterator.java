package kotlin.collections

import java.util.NoSuchElementException
import kotlin.jvm.internal.markers.KMappedMarker

abstract class AbstractIterator<T> : java.util.Iterator<T>, KMappedMarker {
   private final var nextValue: Any?
   private final var state: State = State.NotReady

   public override operator fun hasNext(): Boolean {
      if (this.state === State.Failed) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         var var10000: Boolean;
         switch (AbstractIterator.WhenMappings.$EnumSwitchMapping$0[this.state.ordinal()]) {
            case 1:
               var10000 = false;
               break;
            case 2:
               var10000 = true;
               break;
            default:
               var10000 = this.tryToComputeNext();
         }

         return var10000;
      }
   }

   public override operator fun next(): Any {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         this.state = State.NotReady;
         return this.nextValue;
      }
   }

   private fun tryToComputeNext(): Boolean {
      this.state = State.Failed;
      this.computeNext();
      return this.state === State.Ready;
   }

   protected abstract fun computeNext() {
   }

   protected fun setNext(value: Any) {
      this.nextValue = (T)value;
      this.state = State.Ready;
   }

   protected fun done() {
      this.state = State.Done;
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
