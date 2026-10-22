package kotlin.collections

import java.util.ArrayList
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.IntrinsicsKt
import kotlin.jvm.functions.Function2
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

internal fun checkWindowSizeStep(size: Int, step: Int) {
   if (size <= 0 || step <= 0) {
      throw new IllegalArgumentException(
         (if (size != step) "Both size " + size + " and step " + step + " must be greater than zero." else "size " + size + " must be greater than zero.")
            .toString()
      );
   }
}

internal fun <T> Sequence<T>.windowedSequence(size: Int, step: Int, partialWindows: Boolean, reuseBuffer: Boolean): Sequence<List<T>> {
   checkWindowSizeStep(size, step);
   return new SlidingWindowKt$windowedSequence$$inlined$Sequence$1(`$this$windowedSequence`, size, step, partialWindows, reuseBuffer);
}

internal fun <T> windowedIterator(iterator: Iterator<T>, size: Int, step: Int, partialWindows: Boolean, reuseBuffer: Boolean): Iterator<List<T>> {
   return (java.util.Iterator<java.util.List<T>>)(if (!iterator.hasNext())
      EmptyIterator.INSTANCE
      else
      SequencesKt.iterator(
         (
            new Function2<SequenceScope<? super java.util.List<? extends T>>, Continuation<? super Unit>, Object>(
               size, step, iterator, reuseBuffer, partialWindows, null
            ) {
               Object L$1;
               Object L$2;
               int I$0;
               int label;

               {
                  super(2, `$completion`);
                  this.$size = `$size`;
                  this.$step = `$step`;
                  this.$iterator = `$iterator`;
                  this.$reuseBuffer = `$reuseBuffer`;
                  this.$partialWindows = `$partialWindows`;
               }

               // $VF: Irreducible bytecode was duplicated to produce valid code
               @Nullable
               @Override
               public final Object invokeSuspend(@NotNull Object $result) {
                  var `$this$iterator`: SequenceScope;
                  var buffer: RingBuffer;
                  var var9: Any;
                  label154: {
                     var skip: java.util.Iterator;
                     label171: {
                        var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        var gap: Int;
                        var e: java.util.Iterator;
                        var bufferx: ArrayList;
                        var var11: Int;
                        switch (this.label) {
                           case 0:
                              ResultKt.throwOnFailure(`$result`);
                              `$this$iterator` = this.L$0 as SequenceScope;
                              var bufferInitialCapacity: Int = RangesKt.coerceAtMost(this.$size, 1024);
                              gap = this.$step - this.$size;
                              if (this.$step - this.$size < 0) {
                                 buffer = new RingBuffer(bufferInitialCapacity);
                                 skip = this.$iterator;
                                 break label171;
                              }

                              bufferx = new ArrayList(bufferInitialCapacity);
                              var11 = 0;
                              e = this.$iterator;
                              break;
                           case 1:
                              gap = this.I$0;
                              e = this.L$2 as java.util.Iterator;
                              bufferx = this.L$1 as ArrayList;
                              `$this$iterator` = this.L$0 as SequenceScope;
                              ResultKt.throwOnFailure(`$result`);
                              if (this.$reuseBuffer) {
                                 bufferx.clear();
                              } else {
                                 bufferx = new ArrayList(this.$size);
                              }

                              var11 = gap;
                              break;
                           case 2:
                              ResultKt.throwOnFailure(`$result`);
                              return Unit.INSTANCE;
                           case 3:
                              skip = this.L$2 as java.util.Iterator;
                              buffer = this.L$1 as RingBuffer;
                              `$this$iterator` = this.L$0 as SequenceScope;
                              ResultKt.throwOnFailure(`$result`);
                              buffer.removeFirst(this.$step);
                              break label171;
                           case 4:
                              buffer = this.L$1 as RingBuffer;
                              `$this$iterator` = this.L$0 as SequenceScope;
                              ResultKt.throwOnFailure(`$result`);
                              buffer.removeFirst(this.$step);
                              break label154;
                           case 5:
                              ResultKt.throwOnFailure(`$result`);
                              return Unit.INSTANCE;
                           default:
                              throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }

                        while (e.hasNext()) {
                           var e: Any = e.next();
                           if (var11 > 0) {
                              var11--;
                           } else {
                              bufferx.add(e);
                              if (bufferx.size() == this.$size) {
                                 var var10002: Continuation = this;
                                 this.L$0 = `$this$iterator`;
                                 this.L$1 = bufferx;
                                 this.L$2 = e;
                                 this.I$0 = gap;
                                 this.label = 1;
                                 if (`$this$iterator`.yield(bufferx, var10002) === var9) {
                                    return var9;
                                 }

                                 if (this.$reuseBuffer) {
                                    bufferx.clear();
                                 } else {
                                    bufferx = new ArrayList(this.$size);
                                 }

                                 var11 = gap;
                              }
                           }
                        }

                        if (bufferx.isEmpty() || !this.$partialWindows && bufferx.size() != this.$size) {
                           return Unit.INSTANCE;
                        }

                        var var14: Continuation = this;
                        this.L$0 = null;
                        this.L$1 = null;
                        this.L$2 = null;
                        this.label = 2;
                        if (`$this$iterator`.yield(bufferx, var14) === var9) {
                           return var9;
                        }

                        return Unit.INSTANCE;
                     }

                     while (skip.hasNext()) {
                        buffer.add(skip.next());
                        if (buffer.isFull()) {
                           if (buffer.size() < this.$size) {
                              buffer = buffer.expanded(this.$size);
                           } else {
                              var var10001: java.util.List = if (this.$reuseBuffer) buffer else new ArrayList(buffer);
                              var var15: Continuation = this;
                              this.L$0 = `$this$iterator`;
                              this.L$1 = buffer;
                              this.L$2 = skip;
                              this.label = 3;
                              if (`$this$iterator`.yield(var10001, var15) === var9) {
                                 return var9;
                              }

                              buffer.removeFirst(this.$step);
                           }
                        }
                     }

                     if (!this.$partialWindows) {
                        return Unit.INSTANCE;
                     }
                  }

                  while (buffer.size() > this.$step) {
                     var var13: java.util.List = if (this.$reuseBuffer) buffer else new ArrayList(buffer);
                     var var16: Continuation = this;
                     this.L$0 = `$this$iterator`;
                     this.L$1 = buffer;
                     this.L$2 = null;
                     this.label = 4;
                     if (`$this$iterator`.yield(var13, var16) === var9) {
                        return var9;
                     }

                     buffer.removeFirst(this.$step);
                  }

                  if (!buffer.isEmpty()) {
                     var var17: Continuation = this;
                     this.L$0 = null;
                     this.L$1 = null;
                     this.L$2 = null;
                     this.label = 5;
                     if (`$this$iterator`.yield(buffer, var17) === var9) {
                        return var9;
                     }
                  }

                  return Unit.INSTANCE;
               }

               @NotNull
               @Override
               public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> $completion) {
                  var var3: Function2 = new <anonymous constructor>(
                     this.$size, this.$step, this.$iterator, this.$reuseBuffer, this.$partialWindows, `$completion`
                  );
                  var3.L$0 = value;
                  return var3 as Continuation<Unit>;
               }

               @Nullable
               public final Object invoke(@NotNull SequenceScope<? super java.util.List<? extends T>> p1, @Nullable Continuation<? super Unit> p2) {
                  return (this.create(p1, p2) as <unrepresentable>).invokeSuspend(Unit.INSTANCE);
               }
            }
         ) as Function2
      ));
}

