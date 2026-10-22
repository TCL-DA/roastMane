package kotlin.jvm.functions

interface Function2<P1, P2, R> : Function<R> {
   public abstract operator fun invoke(p1: Any, p2: Any): Any {
   }
}
