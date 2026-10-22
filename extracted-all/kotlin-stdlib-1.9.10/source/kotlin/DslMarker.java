package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.ANNOTATION_CLASS])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.ANNOTATION_TYPE])
@SinceKotlin(version = "1.1")
annotation class DslMarker(

)
