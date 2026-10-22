package kotlin.coroutines.jvm.internal

import kotlin.coroutines.Continuation
import kotlin.jvm.internal.FunctionBase
import kotlin.jvm.internal.Reflection

@SinceKotlin(version = "1.3")
internal abstract class RestrictedSuspendLambda : RestrictedContinuationImpl, FunctionBase<Object>, SuspendFunction {
   public open val arity: Int

   open fun RestrictedSuspendLambda(arity: Int, completion: Continuation<Object>?) {
      super(completion);
      this.arity = arity;
   }

   open fun RestrictedSuspendLambda(arity: Int) {
      this(arity, null);
   }

   public override fun toString(): String {
      var var10000: java.lang.String;
      if (this.getCompletion() == null) {
         var10000 = Reflection.renderLambdaToString(this);
      } else {
         var10000 = super.toString();
      }

      return var10000;
   }
}
