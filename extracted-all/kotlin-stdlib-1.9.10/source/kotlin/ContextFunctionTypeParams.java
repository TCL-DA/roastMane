package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE])
@MustBeDocumented
@Documented
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([])
@SinceKotlin(version = "1.7")
annotation class ContextFunctionTypeParams(
   val count: Int
)
