package kotlin.reflect

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

@ExperimentalStdlibApi
@SourceDebugExtension(["SMAP\nTypesJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/ParameterizedTypeImpl\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,230:1\n37#2,2:231\n*S KotlinDebug\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/ParameterizedTypeImpl\n*L\n190#1:231,2\n*E\n"])
private class ParameterizedTypeImpl(rawType: Class<*>, ownerType: Type?, typeArguments: List<Type>) : ParameterizedType, TypeImpl {
   private Type[] typeArguments;

   private final val ownerType: Type?
   private final val rawType: Class<*>
   private final val typeArguments: Array<Type>

   init {
      this.rawType = rawType;
      this.ownerType = ownerType;
      this.typeArguments = typeArguments.toArray(new Type[0]);
   }

   public override fun getRawType(): Type {
      return this.rawType;
   }

   public override fun getOwnerType(): Type? {
      return this.ownerType;
   }

   public override fun getActualTypeArguments(): Array<Type> {
      return this.typeArguments;
   }

   public override fun getTypeName(): String {
      var var1: StringBuilder = new StringBuilder();
      if (this.ownerType != null) {
         var1.append(TypesJVMKt.access$typeToString(this.ownerType));
         var1.append("$");
         var1.append(this.rawType.getSimpleName());
      } else {
         var1.append(TypesJVMKt.access$typeToString(this.rawType));
      }

      if (this.typeArguments.length != 0) {
         ArraysKt.joinTo$default(this.typeArguments, var1, null, "<", ">", 0, null, <unrepresentable>.INSTANCE, 50, null);
      }

      var var10000: java.lang.String = var1.toString();
      return var10000;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ParameterizedType
         && this.rawType == (other as ParameterizedType).getRawType()
         && this.ownerType == (other as ParameterizedType).getOwnerType()
         && Arrays.equals((Object[])this.getActualTypeArguments(), (Object[])(other as ParameterizedType).getActualTypeArguments());
   }

   public override fun hashCode(): Int {
      return this.rawType.hashCode() xor (if (this.ownerType != null) this.ownerType.hashCode() else 0) xor Arrays.hashCode(
         (Object[])this.getActualTypeArguments()
      );
   }

   public override fun toString(): String {
      return this.getTypeName();
   }
}
