package kotlin.coroutines.jvm.internal

@SinceKotlin(version = "1.3")
interface CoroutineStackFrame {
   public val callerFrame: CoroutineStackFrame?

   public abstract fun getStackTraceElement(): StackTraceElement? {
   }
}
