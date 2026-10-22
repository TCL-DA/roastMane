package kotlin.reflect

import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.util.ArrayList
import kotlin.internal.LowPriorityInOverloadResolution
import kotlin.jvm.internal.KTypeBase

public final val javaType: Type
   public final get() {
      if (`$this$javaType` is KTypeBase) {
         var var1: Type = (`$this$javaType` as KTypeBase).getJavaType();
         if (var1 != null) {
            return var1;
         }
      }

      return computeJavaType$default(`$this$javaType`, false, 1, null);
   }

private final val javaType: Type
   private final get() {
      var var10000: KVariance = `$this$javaType`.getVariance();
      if (var10000 == null) {
         return WildcardTypeImpl.Companion.getSTAR();
      } else {
         var var3: KType = `$this$javaType`.getType();
         var var4: Type;
         switch (TypesJVMKt.WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
            case 1:
               var4 = new WildcardTypeImpl(null, computeJavaType(var3, true));
               break;
            case 2:
               var4 = computeJavaType(var3, true);
               break;
            case 3:
               var4 = new WildcardTypeImpl(computeJavaType(var3, true), null);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         return var4;
      }
   }

/** @deprecated */
@SinceKotlin(version = "1.4")
@ExperimentalStdlibApi
@LowPriorityInOverloadResolution
@JvmSynthetic
fun `getJavaType$annotations`(var0: KType) {
}

@ExperimentalStdlibApi
private fun KType.computeJavaType(forceWrapper: Boolean): Type {
   var classifier: KClassifier = `$this$computeJavaType`.getClassifier();
   if (classifier is KTypeParameter) {
      return new TypeVariableImpl(classifier as KTypeParameter);
   } else if (classifier is KClass) {
      var jClass: Class = if (forceWrapper) JvmClassMappingKt.getJavaObjectType(classifier as KClass) else JvmClassMappingKt.getJavaClass(classifier as KClass);
      var arguments: java.util.List = `$this$computeJavaType`.getArguments();
      if (arguments.isEmpty()) {
         return jClass;
      } else if (jClass.isArray()) {
         if (jClass.getComponentType().isPrimitive()) {
            return jClass;
         } else {
            var var10000: KTypeProjection = CollectionsKt.singleOrNull(arguments);
            if (var10000 == null) {
               throw new IllegalArgumentException("kotlin.Array must have exactly one type argument: " + `$this$computeJavaType`);
            } else {
               var variance: KVariance = var10000.component1();
               var elementType: KType = var10000.component2();
               var var9: Type;
               switch (variance == null ? -1 : TypesJVMKt.WhenMappings.$EnumSwitchMapping$0[variance.ordinal()]) {
                  case -1:
                  case 1:
                     var9 = jClass;
                     break;
                  case 0:
                  default:
                     throw new NoWhenBranchMatchedException();
                  case 2:
                  case 3:
                     var javaElementType: Type = computeJavaType$default(elementType, false, 1, null);
                     var9 = if (javaElementType is Class<*>) jClass else new GenericArrayTypeImpl(javaElementType);
               }

               return var9;
            }
         }
      } else {
         return createPossiblyInnerType(jClass, arguments);
      }
   } else {
      throw new UnsupportedOperationException("Unsupported type classifier: " + `$this$computeJavaType`);
   }
}

@JvmSynthetic
fun `computeJavaType$default`(var0: KType, var1: Boolean, var2: Int, var3: Any): Type {
   if ((var2 and 1) != 0) {
      var1 = false;
   }

   return computeJavaType(var0, var1);
}

@ExperimentalStdlibApi
private fun createPossiblyInnerType(jClass: Class<*>, arguments: List<KTypeProjection>): Type {
   var n: Class = jClass.getDeclaringClass();
   if (n == null) {
      var var28: java.lang.Iterable = arguments;
      var var31: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(arguments, 10));

      for (Object item$iv$iv : var28) {
         var31.add(getJavaType(var37 as KTypeProjection));
      }

      return new ParameterizedTypeImpl(jClass, null, var31 as java.util.List<? extends Type>);
   } else if (Modifier.isStatic(jClass.getModifiers())) {
      var var45: Type = n;
      var var26: java.lang.Iterable = arguments;
      var `$this$mapTo$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(arguments, 10));

      for (Object item$iv$iv : var26) {
         `$this$mapTo$iv$iv`.add(getJavaType(var34 as KTypeProjection));
      }

      return new ParameterizedTypeImpl(jClass, var45, `$this$mapTo$iv$iv` as java.util.List<? extends Type>);
   } else {
      var var25: Int = jClass.getTypeParameters().length;
      var var10001: Type = createPossiblyInnerType(n, arguments.subList(var25, arguments.size()));
      var `$this$map$iv`: java.lang.Iterable = arguments.subList(0, var25);
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

      for (Object item$iv$iv : $this$map$iv) {
         `destination$iv$iv`.add(getJavaType(`item$iv$iv` as KTypeProjection));
      }

      return new ParameterizedTypeImpl(jClass, var10001, `destination$iv$iv` as java.util.List<? extends Type>);
   }
}

/** @deprecated */
@ExperimentalStdlibApi
@JvmSynthetic
fun `getJavaType$annotations`(var0: KTypeProjection) {
}

private fun typeToString(type: Type): String {
   var var3: java.lang.String;
   if (type is Class<*>) {
      if ((type as Class<*>).isArray()) {
         var unwrap: Sequence = SequencesKt.generateSequence(type, <unrepresentable>.INSTANCE);
         var3 = SequencesKt.<Class>last(unwrap).getName() + StringsKt.repeat("[]", SequencesKt.count(unwrap));
      } else {
         var3 = (type as Class<*>).getName();
      }

      var3 = var3;
   } else {
      var3 = type.toString();
   }

   return var3;
}

@JvmSynthetic
fun `access$computeJavaType`(`$receiver`: KType, forceWrapper: Boolean): Type {
   return computeJavaType(`$receiver`, forceWrapper);
}

@JvmSynthetic
fun `access$typeToString`(type: Type): java.lang.String {
   return typeToString(type);
}

@JvmSynthetic
// $VF: Class flags could not be determined
internal class WhenMappings {
   @JvmStatic
   fun {
      var var0: IntArray = new int[KVariance.values().length];

      try {
         var0[KVariance.IN.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[KVariance.INVARIANT.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[KVariance.OUT.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
