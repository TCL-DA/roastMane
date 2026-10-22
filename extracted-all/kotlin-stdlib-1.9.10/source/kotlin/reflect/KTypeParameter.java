package kotlin.reflect

@SinceKotlin(version = "1.1")
interface KTypeParameter : KClassifier {
   public val isReified: Boolean
   public val name: String
   public val upperBounds: List<KType>
   public val variance: KVariance
}
