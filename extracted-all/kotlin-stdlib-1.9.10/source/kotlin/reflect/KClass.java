package kotlin.reflect

interface KClass<T> : KDeclarationContainer, KAnnotatedElement, KClassifier {
   public val constructors: Collection<KFunction<Any>>
   public val isAbstract: Boolean
   public val isCompanion: Boolean
   public val isData: Boolean
   public val isFinal: Boolean
   public val isFun: Boolean
   public val isInner: Boolean
   public val isOpen: Boolean
   public val isSealed: Boolean
   public val isValue: Boolean
   public val members: Collection<KCallable<*>>
   public val nestedClasses: Collection<KClass<*>>
   public val objectInstance: Any?
   public val qualifiedName: String?
   public val sealedSubclasses: List<KClass<out Any>>
   public val simpleName: String?
   public val supertypes: List<KType>
   public val typeParameters: List<KTypeParameter>
   public val visibility: KVisibility?

   @SinceKotlin(version = "1.1")
   public abstract fun isInstance(value: Any?): Boolean {
   }

   public abstract override operator fun equals(other: Any?): Boolean {
   }

   public abstract override fun hashCode(): Int {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
   }
}
