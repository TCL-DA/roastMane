package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER])
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.METHOD])
annotation class JvmStatic(

)
