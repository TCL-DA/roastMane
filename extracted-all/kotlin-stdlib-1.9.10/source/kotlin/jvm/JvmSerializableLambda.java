package kotlin.jvm

import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.EXPRESSION])
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([])
@SinceKotlin(version = "1.8")
annotation class JvmSerializableLambda(

)
