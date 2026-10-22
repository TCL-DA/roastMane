package kotlin.jvm.internal

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.ArrayList
import java.util.HashMap
import java.util.LinkedHashMap
import java.util.Map.Entry
import kotlin.jvm.functions.Function0
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function10
import kotlin.jvm.functions.Function11
import kotlin.jvm.functions.Function12
import kotlin.jvm.functions.Function13
import kotlin.jvm.functions.Function14
import kotlin.jvm.functions.Function15
import kotlin.jvm.functions.Function16
import kotlin.jvm.functions.Function17
import kotlin.jvm.functions.Function18
import kotlin.jvm.functions.Function19
import kotlin.jvm.functions.Function2
import kotlin.jvm.functions.Function20
import kotlin.jvm.functions.Function21
import kotlin.jvm.functions.Function22
import kotlin.jvm.functions.Function3
import kotlin.jvm.functions.Function4
import kotlin.jvm.functions.Function5
import kotlin.jvm.functions.Function6
import kotlin.jvm.functions.Function7
import kotlin.jvm.functions.Function8
import kotlin.jvm.functions.Function9
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility

@SourceDebugExtension(["SMAP\nClassReference.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClassReference.kt\nkotlin/jvm/internal/ClassReference\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,205:1\n1559#2:206\n1590#2,4:207\n1253#2,4:211\n1238#2,4:217\n453#3:215\n403#3:216\n*S KotlinDebug\n*F\n+ 1 ClassReference.kt\nkotlin/jvm/internal/ClassReference\n*L\n107#1:206\n107#1:207,4\n155#1:211,4\n163#1:217,4\n163#1:215\n163#1:216\n*E\n"])
class ClassReference(jClass: Class<*>) : KClass<Object>, ClassBasedDeclarationContainer {
   @JvmStatic
   public ClassReference.Companion Companion = new ClassReference.Companion(null);
   @JvmStatic
   private java.util.Map<Class<? extends Function<?>>, Integer> FUNCTION_CLASSES;
   @JvmStatic
   private HashMap<java.lang.String, java.lang.String> primitiveFqNames;
   @JvmStatic
   private HashMap<java.lang.String, java.lang.String> primitiveWrapperFqNames;
   @JvmStatic
   private HashMap<java.lang.String, java.lang.String> classFqNames;
   @JvmStatic
   private java.util.Map<java.lang.String, java.lang.String> simpleNames;

   public open val annotations: List<Annotation>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val constructors: Collection<KFunction<Any>>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isAbstract: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isCompanion: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isData: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isFinal: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isFun: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isInner: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isOpen: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isSealed: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val isValue: Boolean
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val jClass: Class<*>
   public open val members: Collection<KCallable<*>>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val nestedClasses: Collection<KClass<*>>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val objectInstance: Any?
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val qualifiedName: String?
      public open get() {
         return Companion.getClassQualifiedName(this.getJClass());
      }

   public open val sealedSubclasses: List<KClass<out Any>>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val simpleName: String?
      public open get() {
         return Companion.getClassSimpleName(this.getJClass());
      }

   public open val supertypes: List<KType>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val typeParameters: List<KTypeParameter>
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }

   public open val visibility: KVisibility?
      public open get() {
         this.error();
         throw new KotlinNothingValueException();
      }


   init {
      this.jClass = jClass;
   }

   @SinceKotlin(version = "1.1")
   public override fun isInstance(value: Any?): Boolean {
      return Companion.isInstance(value, this.getJClass());
   }

   private fun error(): Nothing {
      throw new KotlinReflectionNotSupportedError();
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is ClassReference
         && JvmClassMappingKt.<Object>getJavaObjectType(this as KClass<Object>) == JvmClassMappingKt.getJavaObjectType(other as KClass);
   }

   public override fun hashCode(): Int {
      return JvmClassMappingKt.getJavaObjectType(this as KClass<Object>).hashCode();
   }

   public override fun toString(): String {
      return this.getJClass().toString() + " (Kotlin reflection is not available)";
   }

   @JvmStatic
   fun {
      var var18: java.lang.Iterable = CollectionsKt.listOf(
         new Class[]{
            Function0.class,
            Function1.class,
            Function2.class,
            Function3.class,
            Function4.class,
            Function5.class,
            Function6.class,
            Function7.class,
            Function8.class,
            Function9.class,
            Function10.class,
            Function11.class,
            Function12.class,
            Function13.class,
            Function14.class,
            Function15.class,
            Function16.class,
            Function17.class,
            Function18.class,
            Function19.class,
            Function20.class,
            Function21.class,
            Function22.class
         }
      );
      var `destination$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var18, 10));
      var `$this$associateByTo$iv$iv$iv`: Int = 0;

      for (Object item$iv$iv : var18) {
         var `element$iv$iv$iv`: Int = `$this$associateByTo$iv$iv$iv`++;
         if (`element$iv$iv$iv` < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         `destination$iv$iv`.add(TuplesKt.to(`item$iv$iv` as Class<*>, `element$iv$iv$iv`));
      }

      FUNCTION_CLASSES = MapsKt.toMap(`destination$iv$iv`);
      var var19: HashMap = new HashMap();
      var19.put("boolean", "kotlin.Boolean");
      var19.put("char", "kotlin.Char");
      var19.put("byte", "kotlin.Byte");
      var19.put("short", "kotlin.Short");
      var19.put("int", "kotlin.Int");
      var19.put("float", "kotlin.Float");
      var19.put("long", "kotlin.Long");
      var19.put("double", "kotlin.Double");
      primitiveFqNames = var19;
      var var20: HashMap = new HashMap();
      var20.put("java.lang.Boolean", "kotlin.Boolean");
      var20.put("java.lang.Character", "kotlin.Char");
      var20.put("java.lang.Byte", "kotlin.Byte");
      var20.put("java.lang.Short", "kotlin.Short");
      var20.put("java.lang.Integer", "kotlin.Int");
      var20.put("java.lang.Float", "kotlin.Float");
      var20.put("java.lang.Long", "kotlin.Long");
      var20.put("java.lang.Double", "kotlin.Double");
      primitiveWrapperFqNames = var20;
      var var21: HashMap = new HashMap();
      var var23: HashMap = var21;
      var21.put("java.lang.Object", "kotlin.Any");
      var21.put("java.lang.String", "kotlin.String");
      var21.put("java.lang.CharSequence", "kotlin.CharSequence");
      var21.put("java.lang.Throwable", "kotlin.Throwable");
      var21.put("java.lang.Cloneable", "kotlin.Cloneable");
      var21.put("java.lang.Number", "kotlin.Number");
      var21.put("java.lang.Comparable", "kotlin.Comparable");
      var21.put("java.lang.Enum", "kotlin.Enum");
      var21.put("java.lang.annotation.Annotation", "kotlin.Annotation");
      var21.put("java.lang.Iterable", "kotlin.collections.Iterable");
      var21.put("java.util.Iterator", "kotlin.collections.Iterator");
      var21.put("java.util.Collection", "kotlin.collections.Collection");
      var21.put("java.util.List", "kotlin.collections.List");
      var21.put("java.util.Set", "kotlin.collections.Set");
      var21.put("java.util.ListIterator", "kotlin.collections.ListIterator");
      var21.put("java.util.Map", "kotlin.collections.Map");
      var21.put("java.util.Map$Entry", "kotlin.collections.Map.Entry");
      var21.put("kotlin.jvm.internal.StringCompanionObject", "kotlin.String.Companion");
      var21.put("kotlin.jvm.internal.EnumCompanionObject", "kotlin.Enum.Companion");
      var21.putAll(primitiveFqNames);
      var21.putAll(primitiveWrapperFqNames);

      for (Object element$iv : var27) {
         var var39: java.util.Map = var23;
         var var41: java.lang.String = var36 as java.lang.String;
         var var47: StringBuilder = new StringBuilder().append("kotlin.jvm.internal.");
         var var42: Pair = TuplesKt.to(
            var47.append(StringsKt.substringAfterLast$default(var41, '.', null, 2, null)).append("CompanionObject").toString(), var41 + ".Companion"
         );
         var39.put(var42.getFirst(), var42.getSecond());
      }

      for (Entry var31 : FUNCTION_CLASSES.entrySet()) {
         var23.put((var31.getKey() as Class<*>).getName(), "kotlin.Function" + (var31.getValue() as java.lang.Number).intValue());
      }

      classFqNames = var21;
      var var22: java.util.Map = classFqNames;
      var `destination$iv$ivx`: java.util.Map = new LinkedHashMap(MapsKt.mapCapacity(classFqNames.size()));

      for (Object element$iv$iv$iv : var35) {
         `destination$iv$ivx`.put(
            (var43 as Entry).getKey(), StringsKt.substringAfterLast$default((var43 as Entry).getValue() as java.lang.String, '.', null, 2, null)
         );
      }

      simpleNames = `destination$iv$ivx`;
   }

   @SourceDebugExtension(["SMAP\nClassReference.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClassReference.kt\nkotlin/jvm/internal/ClassReference$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,205:1\n1#2:206\n*E\n"])
   companion object Companion private constructor() {
      private final val FUNCTION_CLASSES: Map<Class<out () -> *>, Int>
      private final val classFqNames: HashMap<String, String>
      private final val primitiveFqNames: HashMap<String, String>
      private final val primitiveWrapperFqNames: HashMap<String, String>
      private final val simpleNames: Map<String, String>


      public fun getClassSimpleName(jClass: Class<*>): String? {
         var var10000: java.lang.String;
         if (jClass.isAnonymousClass()) {
            var10000 = null;
         } else if (jClass.isLocalClass()) {
            var componentType: java.lang.String = jClass.getSimpleName();
            var var9: Method = jClass.getEnclosingMethod();
            if (var9 != null) {
               var10000 = StringsKt.substringAfter$default(componentType, var9.getName() + '$', null, 2, null);
               if (var10000 != null) {
                  return var10000;
               }
            }

            var var10: Constructor = jClass.getEnclosingConstructor();
            if (var10 != null) {
               var10000 = StringsKt.substringAfter$default(componentType, var10.getName() + '$', null, 2, null);
            } else {
               var10000 = StringsKt.substringAfter$default(componentType, '$', null, 2, null);
            }
         } else if (jClass.isArray()) {
            var var8: Class = jClass.getComponentType();
            if (var8.isPrimitive()) {
               var var3: java.lang.String = ClassReference.access$getSimpleNames$cp().get(var8.getName()) as java.lang.String;
               var10000 = if (var3 != null) var3 + "Array" else null;
            } else {
               var10000 = null;
            }

            if (var10000 == null) {
               var10000 = "Array";
            }
         } else {
            var10000 = ClassReference.access$getSimpleNames$cp().get(jClass.getName()) as java.lang.String;
            if (var10000 == null) {
               var10000 = jClass.getSimpleName();
            }
         }

         return var10000;
      }

      public fun getClassQualifiedName(jClass: Class<*>): String? {
         var var10000: java.lang.String;
         if (jClass.isAnonymousClass()) {
            var10000 = null;
         } else if (jClass.isLocalClass()) {
            var10000 = null;
         } else if (jClass.isArray()) {
            var componentType: Class = jClass.getComponentType();
            if (componentType.isPrimitive()) {
               var var3: java.lang.String = ClassReference.access$getClassFqNames$cp().get(componentType.getName()) as java.lang.String;
               var10000 = if (var3 != null) var3 + "Array" else null;
            } else {
               var10000 = null;
            }

            if (var10000 == null) {
               var10000 = "kotlin.Array";
            }
         } else {
            var10000 = ClassReference.access$getClassFqNames$cp().get(jClass.getName()) as java.lang.String;
            if (var10000 == null) {
               var10000 = jClass.getCanonicalName();
            }
         }

         return var10000;
      }

      public fun isInstance(value: Any?, jClass: Class<*>): Boolean {
         var var10000: java.util.Map = ClassReference.access$getFUNCTION_CLASSES$cp();
         var objectType: Int = var10000.get(jClass) as Integer;
         return if (objectType != null)
            TypeIntrinsics.isFunctionOfArity(value, objectType.intValue())
            else
            (if (jClass.isPrimitive()) JvmClassMappingKt.getJavaObjectType(JvmClassMappingKt.getKotlinClass(jClass)) else jClass).isInstance(value);
      }
   }
}
