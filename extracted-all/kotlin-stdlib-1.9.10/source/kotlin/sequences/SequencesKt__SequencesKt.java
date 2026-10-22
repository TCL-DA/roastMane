package kotlin.sequences

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.coroutines.jvm.internal.Boxing
import kotlin.internal.InlineOnly
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.random.Random
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

// $VF: Class flags could not be determined
internal class SequencesKt__SequencesKt : SequencesKt__SequencesJVMKt {
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence(crossinline iterator: () -> Iterator<T>): Sequence<T> {
      return new Sequence<T>(iterator) {
         {
            this.$iterator = `$iterator`;
         }

         @NotNull
         @Override
         public java.util.Iterator<T> iterator() {
            return this.$iterator.invoke() as java.util.Iterator<T>;
         }
      };
   }

   @JvmStatic
   public fun <T> Iterator<T>.asSequence(): Sequence<T> {
      return SequencesKt.constrainOnce(new SequencesKt__SequencesKt$asSequence$$inlined$Sequence$1(`$this$asSequence`));
   }

   @JvmStatic
   public fun <T> sequenceOf(vararg elements: T): Sequence<T> {
      return if (elements.length == 0) SequencesKt.emptySequence() else ArraysKt.asSequence(elements);
   }

   @JvmStatic
   public fun <T> emptySequence(): Sequence<T> {
      return EmptySequence.INSTANCE;
   }

   @SinceKotlin(version = "1.3")
   @InlineOnly
   @JvmStatic
   public inline fun <T> Sequence<T>?.orEmpty(): Sequence<T> {
      var var10000: Sequence = `$this$orEmpty`;
      if (`$this$orEmpty` == null) {
         var10000 = SequencesKt.emptySequence();
      }

      return var10000;
   }

   @SinceKotlin(version = "1.3")
   @JvmStatic
   public fun <T> Sequence<T>.ifEmpty(defaultValue: () -> Sequence<T>): Sequence<T> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super T>, Continuation<? super Unit>, Object>(`$this$ifEmpty`, defaultValue, null) {
         int label;

         {
            super(2, `$completion`);
            this.$this_ifEmpty = `$receiver`;
            this.$defaultValue = `$defaultValue`;
         }

         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var4: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  var `$this$sequence`: SequenceScope = this.L$0 as SequenceScope;
                  var iterator: java.util.Iterator = this.$this_ifEmpty.iterator();
                  if (iterator.hasNext()) {
                     var var5: Continuation = this;
                     this.label = 1;
                     if (`$this$sequence`.yieldAll(iterator, var5) === var4) {
                        return var4;
                     }
                  } else {
                     var var10001: Sequence = this.$defaultValue.invoke() as Sequence;
                     var var10002: Continuation = this;
                     this.label = 2;
                     if (`$this$sequence`.yieldAll(var10001, var10002) === var4) {
                        return var4;
                     }
                  }
                  break;
               case 1:
                  ResultKt.throwOnFailure(`$result`);
                  break;
               case 2:
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            return Unit.INSTANCE;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$this_ifEmpty, this.$defaultValue, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super T> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @JvmStatic
   public fun <T> Sequence<Sequence<T>>.flatten(): Sequence<T> {
      return flatten$SequencesKt__SequencesKt(`$this$flatten`, <unrepresentable>.INSTANCE);
   }

   @JvmName(name = "flattenSequenceOfIterable")
   @JvmStatic
   public fun <T> Sequence<Iterable<T>>.flatten(): Sequence<T> {
      return flatten$SequencesKt__SequencesKt(`$this$flatten`, <unrepresentable>.INSTANCE);
   }

   @JvmStatic
   private fun <T, R> Sequence<T>.flatten(iterator: (T) -> Iterator<R>): Sequence<R> {
      return (Sequence<R>)(if (`$this$flatten` is TransformingSequence)
         (`$this$flatten` as TransformingSequence).flatten$kotlin_stdlib(iterator)
         else
         new FlatteningSequence(`$this$flatten`, <unrepresentable>.INSTANCE, iterator));
   }

   @JvmStatic
   public fun <T, R> Sequence<Pair<T, R>>.unzip(): Pair<List<T>, List<R>> {
      var listT: ArrayList = new ArrayList();
      var listR: ArrayList = new ArrayList();

      for (Pair pair : $this$unzip) {
         listT.add(pair.getFirst());
         listR.add(pair.getSecond());
      }

      return TuplesKt.to(listT, listR);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Sequence<T>.shuffled(): Sequence<T> {
      return SequencesKt.shuffled(`$this$shuffled`, Random.Default);
   }

   @SinceKotlin(version = "1.4")
   @JvmStatic
   public fun <T> Sequence<T>.shuffled(random: Random): Sequence<T> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super T>, Continuation<? super Unit>, Object>(`$this$shuffled`, random, null) {
         Object L$1;
         int label;

         {
            super(2, `$completion`);
            this.$this_shuffled = `$receiver`;
            this.$random = `$random`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var7: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var buffer: java.util.List;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  buffer = SequencesKt.toMutableList(this.$this_shuffled);
                  break;
               case 1:
                  buffer = this.L$1 as java.util.List;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while (!buffer.isEmpty()) {
               var j: Int = this.$random.nextInt(buffer.size());
               var last: Any = CollectionsKt.removeLast(buffer);
               var value: Any = if (j < buffer.size()) buffer.set(j, last) else last;
               var var10002: Continuation = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = buffer;
               this.label = 1;
               if (`$this$sequence`.yield(value, var10002) === var7) {
                  return var7;
               }
            }

            return Unit.INSTANCE;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$this_shuffled, this.$random, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super T> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @JvmStatic
   internal fun <T, C, R> flatMapIndexed(source: Sequence<T>, transform: (Int, T) -> C, iterator: (C) -> Iterator<R>): Sequence<R> {
      return SequencesKt.sequence((new Function2<SequenceScope<? super R>, Continuation<? super Unit>, Object>(source, transform, iterator, null) {
         Object L$1;
         int I$0;
         int label;

         {
            super(2, `$completion`);
            this.$source = `$source`;
            this.$transform = `$transform`;
            this.$iterator = `$iterator`;
         }

         // $VF: Irreducible bytecode was duplicated to produce valid code
         @Nullable
         @Override
         public final Object invokeSuspend(@NotNull Object $result) {
            var var8: Any = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            var `$this$sequence`: SequenceScope;
            var index: Int;
            var var4: java.util.Iterator;
            switch (this.label) {
               case 0:
                  ResultKt.throwOnFailure(`$result`);
                  `$this$sequence` = this.L$0 as SequenceScope;
                  index = 0;
                  var4 = this.$source.iterator();
                  break;
               case 1:
                  index = this.I$0;
                  var4 = this.L$1 as java.util.Iterator;
                  `$this$sequence` = this.L$0 as SequenceScope;
                  ResultKt.throwOnFailure(`$result`);
                  break;
               default:
                  throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            while (var4.hasNext()) {
               var element: Any = var4.next();
               var var7: Int = index++;
               if (var7 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               var var9: java.util.Iterator = this.$iterator.invoke(this.$transform.invoke(Boxing.boxInt(var7), element)) as java.util.Iterator;
               var var10002: Continuation = this;
               this.L$0 = `$this$sequence`;
               this.L$1 = var4;
               this.I$0 = index;
               this.label = 1;
               if (`$this$sequence`.yieldAll(var9, var10002) === var8) {
                  return var8;
               }
            }

            return Unit.INSTANCE;
         }

         @NotNull
         @Override
         public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
            var var3: Function2 = new <anonymous constructor>(this.$source, this.$transform, this.$iterator, `$completion`);
            var3.L$0 = value;
            return var3 as Continuation<Unit>;
         }

         @Nullable
         public final Object invoke(@NotNull SequenceScope<? super R> p1, @Nullable Continuation<? super Unit> p2) {
            return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
         }
      }) as Function2);
   }

   @JvmStatic
   public fun <T> Sequence<T>.constrainOnce(): Sequence<T> {
      return (Sequence<T>)(if (`$this$constrainOnce` is ConstrainedOnceSequence) `$this$constrainOnce` else new ConstrainedOnceSequence(`$this$constrainOnce`));
   }

   @JvmStatic
   public fun <T : Any> generateSequence(nextFunction: () -> T?): Sequence<T> {
      return SequencesKt.constrainOnce(new GeneratorSequence(nextFunction, (new Function1<T, T>(nextFunction) {
         {
            super(1);
            this.$nextFunction = `$nextFunction`;
         }

         @Nullable
         @Override
         public final T invoke(@NotNull T it) {
            return (T)this.$nextFunction.invoke();
         }
      }) as Function1));
   }

   @LowPriorityInOverloadResolution
   @JvmStatic
   public fun <T : Any> generateSequence(seed: T?, nextFunction: (T) -> T?): Sequence<T> {
      return (Sequence<T>)(if (seed == null) EmptySequence.INSTANCE else new GeneratorSequence((new Function0<T>(seed) {
         {
            super(0);
            this.$seed = (T)`$seed`;
         }

         @Nullable
         @Override
         public final T invoke() {
            return this.$seed;
         }
      }) as Function0, nextFunction));
   }

   @JvmStatic
   public fun <T : Any> generateSequence(seedFunction: () -> T?, nextFunction: (T) -> T?): Sequence<T> {
      return new GeneratorSequence(seedFunction, nextFunction);
   }

   open fun SequencesKt__SequencesKt() {
   }
}
