package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FILE])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.METHOD])
annotation class JvmName(
   val name: String
)
