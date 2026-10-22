package kotlin.coroutines.jvm.internal

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.3")
annotation class DebugMetadata(
   val version: Int = 1,
   val sourceFile: String = "",
   val lineNumbers: IntArray = {},
   val localNames: Array<String> = {},
   val spilled: Array<String> = {},
   val indexToLabel: IntArray = {},
   val methodName: String = "",
   val className: String = ""
)
