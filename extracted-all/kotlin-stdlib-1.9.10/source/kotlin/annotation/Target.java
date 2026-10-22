package kotlin.annotation

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
annotation class Target(
   val allowedTargets: Array<out AnnotationTarget>
)
