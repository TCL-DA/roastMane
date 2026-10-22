package kotlin.text

@SinceKotlin(version = "1.1")
interface MatchNamedGroupCollection : MatchGroupCollection {
   public abstract operator fun get(name: String): MatchGroup? {
   }
}
