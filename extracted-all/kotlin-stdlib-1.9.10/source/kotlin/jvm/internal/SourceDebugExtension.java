package kotlin.jvm.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.8")
annotation class SourceDebugExtension(
   val value: Array<String>
)
