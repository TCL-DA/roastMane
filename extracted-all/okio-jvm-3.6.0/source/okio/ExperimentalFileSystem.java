package okio

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Deprecated(message = "This annotation is obsolete and should be removed.", level = DeprecationLevel.HIDDEN)
@Retention(AnnotationRetention.SOURCE)
@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY])
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD])
annotation class ExperimentalFileSystem(

)
