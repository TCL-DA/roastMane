@file:JvmName(name = "JvmClassMappingKt")

package kotlin.jvm

import kotlin.internal.InlineOnly
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.jvm.internal.Intrinsics
import kotlin.reflect.KClass

public final val annotationClass: KClass<out T>
   public final get() {
      var var10000: Class = `$this$annotationClass`.annotationType();
      var var1: KClass = getKotlinClass(var10000);
      return var1;
   }

public final val declaringJavaClass: Class<E>
   public final inline get() {
      var var10000: Class = `$this$declaringJavaClass`.getDeclaringClass();
      return var10000;
   }

public final val java: Class<T>
   public final get() {
      var var10000: Class = (`$this$java` as ClassBasedDeclarationContainer).getJClass();
      return var10000;
   }

public final val javaClass: Class<T>
   public final inline get() {
      var var10000: Class = `$this$javaClass`.getClass();
      return var10000;
   }

public final val javaClass: Class<KClass<T>>
   public final inline get() {
      var var10000: Class = `$this$javaClass`.getClass();
      return var10000;
   }

public final val javaObjectType: Class<T>
   public final get() {
      var thisJClass: Class = (`$this$javaObjectType` as ClassBasedDeclarationContainer).getJClass();
      if (!thisJClass.isPrimitive()) {
         return thisJClass;
      } else {
         var var10000: Class;
         label51: {
            var var2: java.lang.String = thisJClass.getName();
            if (var2 != null) {
               switch (var2.hashCode()) {
                  case -1325958191:
                     if (var2.equals("double")) {
                        var10000 = java.lang.Double::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 104431:
                     if (var2.equals("int")) {
                        var10000 = Integer::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 3039496:
                     if (var2.equals("byte")) {
                        var10000 = java.lang.Byte::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 3052374:
                     if (var2.equals("char")) {
                        var10000 = Character::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 3327612:
                     if (var2.equals("long")) {
                        var10000 = java.lang.Long::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 3625364:
                     if (var2.equals("void")) {
                        var10000 = Void::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 64711720:
                     if (var2.equals("boolean")) {
                        var10000 = java.lang.Boolean::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 97526364:
                     if (var2.equals("float")) {
                        var10000 = java.lang.Float::class.javaObjectType;
                        break label51;
                     }
                     break;
                  case 109413500:
                     if (var2.equals("short")) {
                        var10000 = java.lang.Short::class.javaObjectType;
                        break label51;
                     }
                  default:
               }
            }

            var10000 = thisJClass;
         }

         return var10000;
      }
   }

public final val javaPrimitiveType: Class<T>?
   public final get() {
      var thisJClass: Class = (`$this$javaPrimitiveType` as ClassBasedDeclarationContainer).getJClass();
      if (thisJClass.isPrimitive()) {
         return thisJClass;
      } else {
         var var2: java.lang.String = thisJClass.getName();
         if (var2 != null) {
            switch (var2.hashCode()) {
               case -2056817302:
                  if (var2.equals("java.lang.Integer")) {
                     return (Class<T>)Int::class.javaPrimitiveType;
                  }
                  break;
               case -527879800:
                  if (var2.equals("java.lang.Float")) {
                     return (Class<T>)java.lang.Float::class.javaPrimitiveType;
                  }
                  break;
               case -515992664:
                  if (var2.equals("java.lang.Short")) {
                     return (Class<T>)java.lang.Short::class.javaPrimitiveType;
                  }
                  break;
               case 155276373:
                  if (var2.equals("java.lang.Character")) {
                     return (Class<T>)Character::class.javaPrimitiveType;
                  }
                  break;
               case 344809556:
                  if (var2.equals("java.lang.Boolean")) {
                     return (Class<T>)java.lang.Boolean::class.javaPrimitiveType;
                  }
                  break;
               case 398507100:
                  if (var2.equals("java.lang.Byte")) {
                     return (Class<T>)java.lang.Byte::class.javaPrimitiveType;
                  }
                  break;
               case 398795216:
                  if (var2.equals("java.lang.Long")) {
                     return (Class<T>)java.lang.Long::class.javaPrimitiveType;
                  }
                  break;
               case 399092968:
                  if (var2.equals("java.lang.Void")) {
                     return (Class<T>)Void::class.javaPrimitiveType;
                  }
                  break;
               case 761287205:
                  if (var2.equals("java.lang.Double")) {
                     return (Class<T>)java.lang.Double::class.javaPrimitiveType;
                  }
               default:
            }
         }

         return null;
      }
   }

public final val kotlin: KClass<T>
   public final get() {
      return `$this$kotlin`.kotlin;
   }

/** @deprecated */
@JvmSynthetic
fun `getJavaClass$annotations`(var0: KClass) {
}

/** @deprecated */
@Deprecated(message = "Use 'java' property to get Java class corresponding to this Kotlin class or cast this instance to Any if you really want to get the runtime Java class of this implementation of KClass.", replaceWith = @ReplaceWith(expression = "(this as Any).javaClass", imports = []), level = DeprecationLevel.ERROR)
@JvmSynthetic
fun `getRuntimeClassOfKClassInstance$annotations`(var0: KClass) {
}

@JvmSynthetic
public fun <reified T : Any> Array<*>.isArrayOf(): Boolean {
   Intrinsics.reifiedOperationMarker(4, "T");
   return Object::class.java.isAssignableFrom(`$this$isArrayOf`.getClass().getComponentType());
}

/** @deprecated */
@SinceKotlin(version = "1.7")
@InlineOnly
@JvmSynthetic
fun `getDeclaringJavaClass$annotations`(var0: java.lang.Enum) {
}

