package kotlin.jvm

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CONSTRUCTOR])
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class Throws(
   val exceptionClasses: Array<out KClass<out Throwable>>
)
