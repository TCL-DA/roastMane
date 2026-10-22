package kotlin.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.METHOD, ElementType.CONSTRUCTOR])
@SinceKotlin(version = "1.7")
annotation class IntrinsicConstEvaluation(

)
