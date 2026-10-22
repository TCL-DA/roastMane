package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.experimental.ExperimentalTypeInference

@Target(allowedTargets = [AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.METHOD, ElementType.PARAMETER])
@SinceKotlin(version = "1.3")
@ExperimentalTypeInference
annotation class BuilderInference(

)
