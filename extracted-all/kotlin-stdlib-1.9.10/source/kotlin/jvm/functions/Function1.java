package kotlin.jvm.functions

interface Function1<P1, R> : Function<R> {
   public abstract operator fun invoke(p1: Any): Any {
   }
}
