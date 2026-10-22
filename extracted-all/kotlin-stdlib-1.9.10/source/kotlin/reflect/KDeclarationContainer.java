package kotlin.reflect

interface KDeclarationContainer {
   public val members: Collection<KCallable<*>>
}
