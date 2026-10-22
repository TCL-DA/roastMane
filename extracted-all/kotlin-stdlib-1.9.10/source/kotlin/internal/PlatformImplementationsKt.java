package kotlin.internal

import kotlin.jvm.internal.Intrinsics

internal final val IMPLEMENTATIONS: PlatformImplementations

@InlineOnly
@JvmSynthetic
private inline fun <reified T : Any> castToBaseType(instance: Any): T {
   try {
      Intrinsics.reifiedOperationMarker(1, "T");
      return (T)(instance as Object);
   } catch (ClassCastException var4) {
      var instanceCL: ClassLoader = instance.getClass().getClassLoader();
      Intrinsics.reifiedOperationMarker(4, "T");
      var baseTypeCL: ClassLoader = Object::class.java.getClassLoader();
      if (!(instanceCL == baseTypeCL)) {
         throw new ClassNotFoundException(
            "Instance class was loaded from a different classloader: " + instanceCL + ", base type classloader: " + baseTypeCL, var4
         );
      } else {
         throw var4;
      }
   }
}

@PublishedApi
@SinceKotlin(version = "1.2")
internal fun apiVersionIsAtLeast(major: Int, minor: Int, patch: Int): Boolean {
   return KotlinVersion.CURRENT.isAtLeast(major, minor, patch);
}

