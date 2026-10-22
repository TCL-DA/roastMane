package kotlin.sequences

import java.util.NoSuchElementException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.DebugProbesKt
import kotlin.jvm.internal.markers.KMappedMarker

private class SequenceBuilderIterator<T> : SequenceScope<T>, java.util.Iterator<T>, Continuation<Unit>, KMappedMarker {
   public open val context: CoroutineContext
      public open get() {
         return EmptyCoroutineContext.INSTANCE;
      }

   private final var nextIterator: Iterator<Any>?
   public final var nextStep: Continuation<Unit>?
      internal set
   private final var nextValue: Any?
   private final var state: Int


   public override operator fun hasNext(): Boolean {
      while (true) {
         switch (this.state) {
            case 1:
               var var10000: java.util.Iterator = this.nextIterator;
               if (var10000.hasNext()) {
                  this.state = 2;
                  return true;
               }

               this.nextIterator = null;
            case 0:
               this.state = 5;
               var var2: Continuation = this.nextStep;
               this.nextStep = null;
               var2.resumeWith(Result.constructor-impl(Unit.INSTANCE));
               break;
            case 2:
            case 3:
               return true;
            case 4:
               return false;
            default:
               throw this.exceptionalState();
         }
      }
   }

   public override operator fun next(): Any {
      switch (this.state) {
         case 0:
         case 1:
            return this.nextNotReady();
         case 2:
            this.state = 1;
            var var10000: java.util.Iterator = this.nextIterator;
            return (T)var10000.next();
         case 3:
            this.state = 0;
            var result: Any = this.nextValue;
            this.nextValue = null;
            return (T)result;
         default:
            throw this.exceptionalState();
      }
   }

   private fun nextNotReady(): Any {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         return this.next();
      }
   }

   private fun exceptionalState(): Throwable {
      var var10000: java.lang.Throwable;
      switch (this.state) {
         case 4:
            var10000 = new NoSuchElementException();
            break;
         case 5:
            var10000 = new IllegalStateException("Iterator has failed.");
            break;
         default:
            var10000 = new IllegalStateException("Unexpected state of the iterator: " + this.state);
      }

      return var10000;
   }

   public override suspend fun yield(value: Any) {
      this.nextValue = (T)value;
      this.state = 3;
      this.nextStep = `$completion`;
      var var10000: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
         DebugProbesKt.probeCoroutineSuspended(`$completion`);
      }

      return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
   }

   public override suspend fun yieldAll(iterator: Iterator<Any>) {
      if (!iterator.hasNext()) {
         return Unit.INSTANCE;
      } else {
         this.nextIterator = iterator;
         this.state = 2;
         this.nextStep = `$completion`;
         var var10000: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(`$completion`);
         }

         return if (var10000 === IntrinsicsKt.getCOROUTINE_SUSPENDED()) var10000 else Unit.INSTANCE;
      }
   }

   public override fun resumeWith(result: Result<Unit>) {
      ResultKt.throwOnFailure(result);
      this.state = 4;
   }

   override fun remove() {
      throw new UnsupportedOperationException("Operation is not supported for read-only collection");
   }
}
