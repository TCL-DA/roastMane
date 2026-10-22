package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.3")
@SourceDebugExtension(["SMAP\nContinuationImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContinuationImpl.kt\nkotlin/coroutines/jvm/internal/ContinuationImpl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,168:1\n1#2:169\n*E\n"])
internal abstract class ContinuationImpl : BaseContinuationImpl {
   private final val _context: CoroutineContext?
   public open val context: CoroutineContext
      public open get() {
         var var10000: CoroutineContext = this._context;
         return var10000;
      }

   private final var intercepted: Continuation<Any?>?

   open fun ContinuationImpl(completion: Continuation<Object>?, _context: CoroutineContext?) {
      super(completion);
      this._context = _context;
   }

   open fun ContinuationImpl(completion: Continuation<Object>?) {
      this(completion, if (completion != null) completion.getContext() else null);
   }

   public fun intercepted(): Continuation<Any?> {
      var var10000: Continuation = this.intercepted;
      if (this.intercepted == null) {
         label13: {
            var var4: ContinuationInterceptor = this.getContext().get(ContinuationInterceptor.Key);
            if (var4 != null) {
               var10000 = var4.interceptContinuation(this);
               if (var10000 != null) {
                  break label13;
               }
            }

            var10000 = this;
         }

         this.intercepted = var10000;
         var10000 = var10000;
      }

      return var10000;
   }

   protected override fun releaseIntercepted() {
      var intercepted: Continuation = this.intercepted;
      if (this.intercepted != null && this.intercepted != this) {
         var var10000: CoroutineContext.Element = this.getContext().get(ContinuationInterceptor.Key);
         (var10000 as ContinuationInterceptor).releaseInterceptedContinuation(intercepted);
      }

      this.intercepted = CompletedContinuation.INSTANCE;
   }
}
