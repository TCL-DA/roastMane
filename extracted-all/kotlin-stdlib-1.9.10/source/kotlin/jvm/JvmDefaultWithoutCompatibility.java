package kotlin.jvm

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [AnnotationTarget.CLASS])
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE])
@SinceKotlin(version = "1.4")
annotation class JvmDefaultWithoutCompatibility(

)
