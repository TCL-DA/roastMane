package kotlin.jvm.internal

import java.io.Serializable

abstract class Lambda<R> : FunctionBase<R>, Serializable {
   public open val arity: Int

   open fun Lambda(arity: Int) {
      this.arity = arity;
   }

   public override fun toString(): String {
      var var10000: java.lang.String = Reflection.renderLambdaToString(this);
      return var10000;
   }
}
