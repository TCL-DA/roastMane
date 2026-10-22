package kotlin

import kotlin.coroutines.Continuation

@SinceKotlin(version = "1.7")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
class DeepRecursiveFunction<T, R>(block: (DeepRecursiveScope<Any, Any>, Any, Continuation<Any>) -> Any?) {
   internal final val block: (DeepRecursiveScope<Any, Any>, Any, Continuation<Any>) -> Any?

   init {
      this.block = block;
   }
}
