package kotlin.jvm.functions

interface Function3<P1, P2, P3, R> : Function<R> {
   public abstract operator fun invoke(p1: Any, p2: Any, p3: Any): Any {
   }
}
