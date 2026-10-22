@file:JvmName(name = "KClasses")

@file:SourceDebugExtension(["SMAP\nKClasses.kt\nKotlin\n*S Kotlin\n*F\n+ 1 KClasses.kt\nkotlin/reflect/KClasses\n+ 2 KClassesImpl.kt\nkotlin/reflect/KClassesImplKt\n*L\n1#1,48:1\n9#2:49\n*S KotlinDebug\n*F\n+ 1 KClasses.kt\nkotlin/reflect/KClasses\n*L\n26#1:49\n*E\n"])

package kotlin.reflect

import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.internal.SourceDebugExtension

@SinceKotlin(version = "1.4")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
@LowPriorityInOverloadResolution
public fun <T : Any> KClass<T>.cast(value: Any?): T {
   if (!`$this$cast`.isInstance(value)) {
      throw new ClassCastException("Value cannot be cast to " + `$this$cast`.getQualifiedName());
   } else {
      return (T)value;
   }
}

@SinceKotlin(version = "1.4")
@WasExperimental(markerClass = [ExperimentalStdlibApi::class])
@LowPriorityInOverloadResolution
public fun <T : Any> KClass<T>.safeCast(value: Any?): T? {
   var var10000: Any;
   if (`$this$safeCast`.isInstance(value)) {
      var10000 = value;
   } else {
      var10000 = null;
   }

   return (T)var10000;
}

