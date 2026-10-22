package kotlin.internal

import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([])
annotation class NoInfer(

)
