package kotlin.jvm.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [AnnotationTarget.CLASS])
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.6")
annotation class SerializedIr(
   val bytes: Array<String> = {}
)
