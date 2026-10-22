package kotlin.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
annotation class Repeatable(

)
