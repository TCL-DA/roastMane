package kotlin.experimental

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
@RequiresOptIn
@SinceKotlin(version = "1.8")
annotation class ExperimentalObjCRefinement(

)
