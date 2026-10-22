@file:SourceDebugExtension(["SMAP\nDebugMetadata.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DebugMetadata.kt\nkotlin/coroutines/jvm/internal/DebugMetadataKt\n+ 2 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,134:1\n37#2,2:135\n*S KotlinDebug\n*F\n+ 1 DebugMetadata.kt\nkotlin/coroutines/jvm/internal/DebugMetadataKt\n*L\n131#1:135,2\n*E\n"])

package kotlin.coroutines.jvm.internal

import java.lang.reflect.Field
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension

private const val COROUTINES_DEBUG_METADATA_VERSION: Int = 1

@SinceKotlin(version = "1.3")
@JvmName(name = "getStackTraceElement")
internal fun BaseContinuationImpl.getStackTraceElementImpl(): StackTraceElement? {
   var var10000: DebugMetadata = getDebugMetadataAnnotation(`$this$getStackTraceElementImpl`);
   if (var10000 == null) {
      return null;
   } else {
      checkDebugMetadataVersion(1, var10000.v());
      var label: Int = getLabel(`$this$getStackTraceElementImpl`);
      var lineNumber: Int = if (label < 0) -1 else var10000.l()[label];
      var moduleName: java.lang.String = ModuleNameRetriever.INSTANCE.getModuleName(`$this$getStackTraceElementImpl`);
      return new StackTraceElement(if (moduleName == null) var10000.c() else moduleName + '/' + var10000.c(), var10000.m(), var10000.f(), lineNumber);
   }
}

private fun BaseContinuationImpl.getDebugMetadataAnnotation(): DebugMetadata? {
   return `$this$getDebugMetadataAnnotation`.getClass().getAnnotation(DebugMetadata.class);
}

private fun BaseContinuationImpl.getLabel(): Int {
   var field: Int;
   try {
      var var5: Field = `$this$getLabel`.getClass().getDeclaredField("label");
      var5.setAccessible(true);
      var var3: Any = var5.get(`$this$getLabel`);
      field = (if ((var3 as? Integer) != null) var3 as? Integer else 0) - 1;
   } catch (Exception var4) {
      field = -1;
   }

   return field;
}

private fun checkDebugMetadataVersion(expected: Int, actual: Int) {
   if (actual > expected) {
      throw new IllegalStateException(
         ("Debug metadata version mismatch. Expected: " + expected + ", got " + actual + ". Please update the Kotlin standard library.").toString()
      );
   }
}

@SinceKotlin(version = "1.3")
@JvmName(name = "getSpilledVariableFieldMapping")
internal fun BaseContinuationImpl.getSpilledVariableFieldMapping(): Array<String>? {
   var var10000: DebugMetadata = getDebugMetadataAnnotation(`$this$getSpilledVariableFieldMapping`);
   if (var10000 == null) {
      return null;
   } else {
      var debugMetadata: DebugMetadata = var10000;
      checkDebugMetadataVersion(1, var10000.v());
      var res: ArrayList = new ArrayList();
      var label: Int = getLabel(`$this$getSpilledVariableFieldMapping`);
      var `$this$toTypedArray$iv`: IntArray = var10000.i();
      var `$i$f$toTypedArray`: Int = 0;

      for (int thisCollection$iv = $this$toTypedArray$iv.length; $i$f$toTypedArray < thisCollection$iv; $i$f$toTypedArray++) {
         if (`$this$toTypedArray$iv`[`$i$f$toTypedArray`] == label) {
            res.add(debugMetadata.s()[`$i$f$toTypedArray`]);
            res.add(debugMetadata.n()[`$i$f$toTypedArray`]);
         }
      }

      return res.toArray(new java.lang.String[0]);
   }
}

