package kotlin.experimental

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@SinceKotlin(version = "1.3")
annotation class ExperimentalTypeInference(

)
