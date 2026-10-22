package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
@ExperimentalMultiplatform
annotation class OptionalExpectation(

)
