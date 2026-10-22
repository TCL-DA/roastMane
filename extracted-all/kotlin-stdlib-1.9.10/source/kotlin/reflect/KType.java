package kotlin.reflect

interface KType : KAnnotatedElement {
   public val arguments: List<KTypeProjection>
   public val classifier: KClassifier?
   public val isMarkedNullable: Boolean

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
   }
}
