package kotlin.jvm.functions

interface Function0<R> : Function<R> {
   public abstract operator fun invoke(): Any {
   }
}
