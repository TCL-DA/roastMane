package kotlin.reflect

import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

@ExperimentalStdlibApi
private class GenericArrayTypeImpl(elementType: Type) : GenericArrayType, TypeImpl {
   private final val elementType: Type

   init {
      this.elementType = elementType;
   }

   public override fun getGenericComponentType(): Type {
      return this.elementType;
   }

   public override fun getTypeName(): String {
      return TypesJVMKt.access$typeToString(this.elementType) + "[]";
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is GenericArrayType && this.getGenericComponentType() == (other as GenericArrayType).getGenericComponentType();
   }

   public override fun hashCode(): Int {
      return this.getGenericComponentType().hashCode();
   }

   public override fun toString(): String {
      return this.getTypeName();
   }
}
