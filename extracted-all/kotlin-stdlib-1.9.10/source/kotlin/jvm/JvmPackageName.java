package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FILE])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([])
@SinceKotlin(version = "1.2")
annotation class JvmPackageName(
   val name: String
)
