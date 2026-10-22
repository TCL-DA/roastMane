package kotlin.reflect

import java.lang.reflect.GenericDeclaration
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

@ExperimentalStdlibApi
@SourceDebugExtension(["SMAP\nTypesJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/TypeVariableImpl\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,230:1\n1549#2:231\n1620#2,3:232\n37#3,2:235\n26#4:237\n26#4:238\n*S KotlinDebug\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/TypeVariableImpl\n*L\n116#1:231\n116#1:232,3\n116#1:235,2\n134#1:237\n137#1:238\n*E\n"])
private class TypeVariableImpl(typeParameter: KTypeParameter) : TypeVariable<GenericDeclaration>, TypeImpl {
   private final val typeParameter: KTypeParameter

   init {
      this.typeParameter = typeParameter;
   }

   public override fun getName(): String {
      return this.typeParameter.getName();
   }

   public override fun getGenericDeclaration(): GenericDeclaration {
      throw new NotImplementedError(
         "An operation is not implemented: " + "getGenericDeclaration() is not yet supported for type variables created from KType: " + this.typeParameter
      );
   }

   public override fun getBounds(): Array<Type> {
      var `$this$toTypedArray$iv`: java.lang.Iterable = this.typeParameter.getUpperBounds();
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$toTypedArray$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(TypesJVMKt.access$computeJavaType(`item$iv$iv` as KType, true));
      }

      return (`destination$iv$iv` as java.util.List).toArray(new Type[0]);
   }

   public override fun getTypeName(): String {
      return this.getName();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is TypeVariable
         && this.getName() == (other as TypeVariable).getName()
         && this.getGenericDeclaration() == (other as TypeVariable).getGenericDeclaration();
   }

   public override fun hashCode(): Int {
      return this.getName().hashCode() xor this.getGenericDeclaration().hashCode();
   }

   public override fun toString(): String {
      return this.getTypeName();
   }

   public override fun <T : Annotation> getAnnotation(annotationClass: Class<T>): T? {
      return null;
   }

   public override fun getAnnotations(): Array<Annotation> {
      return new java.lang.annotation.Annotation[0];
   }

   public override fun getDeclaredAnnotations(): Array<Annotation> {
      return new java.lang.annotation.Annotation[0];
   }
}
