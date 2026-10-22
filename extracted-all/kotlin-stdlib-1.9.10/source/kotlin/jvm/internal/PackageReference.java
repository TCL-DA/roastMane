package kotlin.jvm.internal

import kotlin.reflect.KCallable

@SinceKotlin(version = "1.1")
class PackageReference(jClass: Class<*>, moduleName: String) : ClassBasedDeclarationContainer {
   public open val jClass: Class<*>
   public open val members: Collection<KCallable<*>>
      public open get() {
         throw new KotlinReflectionNotSupportedError();
      }

   private final val moduleName: String

   init {
      this.jClass = jClass;
      this.moduleName = moduleName;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is PackageReference && this.getJClass() == (other as PackageReference).getJClass();
   }

   public override fun hashCode(): Int {
      return this.getJClass().hashCode();
   }

   public override fun toString(): String {
      return this.getJClass().toString() + " (Kotlin reflection is not available)";
   }
}
