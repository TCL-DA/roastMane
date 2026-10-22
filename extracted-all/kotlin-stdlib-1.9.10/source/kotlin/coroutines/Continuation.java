package kotlin.coroutines

@SinceKotlin(version = "1.3")
interface Continuation<T> {
   public val context: CoroutineContext

   public abstract fun resumeWith(result: Result<Any>) {
   }
}
