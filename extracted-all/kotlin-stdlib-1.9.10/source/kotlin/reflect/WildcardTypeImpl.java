package kotlin.reflect

import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.Arrays
import kotlin.jvm.internal.SourceDebugExtension

@ExperimentalStdlibApi
@SourceDebugExtension(["SMAP\nTypesJVM.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/WildcardTypeImpl\n+ 2 ArrayIntrinsics.kt\nkotlin/ArrayIntrinsicsKt\n*L\n1#1,230:1\n26#2:231\n*S KotlinDebug\n*F\n+ 1 TypesJVM.kt\nkotlin/reflect/WildcardTypeImpl\n*L\n163#1:231\n*E\n"])
private class WildcardTypeImpl(upperBound: Type?, lowerBound: Type?) : WildcardType, TypeImpl {
   @JvmStatic
   public WildcardTypeImpl.Companion Companion = new WildcardTypeImpl.Companion(null);
   @JvmStatic
   private WildcardTypeImpl STAR = new WildcardTypeImpl(null, null);

   private final val lowerBound: Type?
   private final val upperBound: Type?

   init {
      this.upperBound = upperBound;
      this.lowerBound = lowerBound;
   }

   public override fun getUpperBounds(): Array<Type> {
      var var1: Array<Type> = new Type[1];
      var var10002: Type = this.upperBound;
      if (this.upperBound == null) {
         var10002 = Object::class.java;
      }

      var1[0] = var10002;
      return var1;
   }

   public override fun getLowerBounds(): Array<Type> {
      return if (this.lowerBound == null) new Type[0] else new Type[]{this.lowerBound};
   }

   public override fun getTypeName(): String {
      return if (this.lowerBound != null)
         "? super " + TypesJVMKt.access$typeToString(this.lowerBound)
         else
         (if (this.upperBound != null && !(this.upperBound == Object::class.java)) "? extends " + TypesJVMKt.access$typeToString(this.upperBound) else "?");
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is WildcardType
         && Arrays.equals((Object[])this.getUpperBounds(), (Object[])(other as WildcardType).getUpperBounds())
         && Arrays.equals((Object[])this.getLowerBounds(), (Object[])(other as WildcardType).getLowerBounds());
   }

   public override fun hashCode(): Int {
      return Arrays.hashCode((Object[])this.getUpperBounds()) xor Arrays.hashCode((Object[])this.getLowerBounds());
   }

   public override fun toString(): String {
      return this.getTypeName();
   }

   companion object Companion private constructor() {
      public final val STAR: WildcardTypeImpl

   }
}
