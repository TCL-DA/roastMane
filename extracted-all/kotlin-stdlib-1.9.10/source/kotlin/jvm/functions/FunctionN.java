package kotlin.jvm.functions

import kotlin.jvm.internal.FunctionBase

@SinceKotlin(version = "1.3")
interface FunctionN<R> : Function<R>, FunctionBase<R> {
   public val arity: Int

   public abstract operator fun invoke(vararg args: Any?): Any {
   }
}
