package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.5")
annotation class JvmRecord(

)
