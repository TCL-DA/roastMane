package kotlin.internal

import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE_PARAMETER])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([])
annotation class PureReifiable(

)
