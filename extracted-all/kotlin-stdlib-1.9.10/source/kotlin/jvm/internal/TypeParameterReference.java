package kotlin.jvm.internal

import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVariance

@SinceKotlin(version = "1.4")
@SourceDebugExtension(["SMAP\nTypeParameterReference.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TypeParameterReference.kt\nkotlin/jvm/internal/TypeParameterReference\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,58:1\n1#2:59\n*E\n"])
class TypeParameterReference(container: Any?, name: String, variance: KVariance, isReified: Boolean) : KTypeParameter {
   @JvmStatic
   public TypeParameterReference.Companion Companion = new TypeParameterReference.Companion(null);

   private final var bounds: List<KType>?
   private final val container: Any?
   public open val isReified: Boolean
   public open val name: String
   public open val upperBounds: List<KType>
      public open get() {
         var var10000: java.util.List = this.bounds;
         if (this.bounds == null) {
            var var1: java.util.List = CollectionsKt.listOf(Reflection.nullableTypeOf(Object.class));
            this.bounds = var1;
            var10000 = var1;
         }

         return var10000;
      }

   public open val variance: KVariance

   init {
      this.container = container;
      this.name = name;
      this.variance = variance;
      this.isReified = isReified;
   }

   public fun setUpperBounds(upperBounds: List<KType>) {
      if (this.bounds != null) {
         throw new IllegalStateException(("Upper bounds of type parameter '" + this + "' have already been initialized.").toString());
      } else {
         this.bounds = upperBounds;
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is TypeParameterReference
         && this.container == (other as TypeParameterReference).container
         && this.getName() == (other as TypeParameterReference).getName();
   }

   public override fun hashCode(): Int {
      return (if (this.container != null) this.container.hashCode() else 0) * 31 + this.getName().hashCode();
   }

   public override fun toString(): String {
      return Companion.toString(this);
   }

   companion object Companion private constructor() {
      public fun toString(typeParameter: KTypeParameter): String {
         var var2: StringBuilder = new StringBuilder();
         switch (TypeParameterReference.Companion.WhenMappings.$EnumSwitchMapping$0[typeParameter.getVariance().ordinal()]) {
            case 1:
            default:
               break;
            case 2:
               var2.append("in ");
               break;
            case 3:
               var2.append("out ");
         }

         var2.append(typeParameter.getName());
         var var10000: java.lang.String = var2.toString();
         return var10000;
      }
   }
}
