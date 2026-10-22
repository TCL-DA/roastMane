package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE])
@MustBeDocumented
@Documented
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([])
@SinceKotlin(version = "1.1")
annotation class ParameterName(
   val name: String
)
