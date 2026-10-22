package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class WasExperimental(
   val markerClass: Array<out KClass<out Annotation>>
)
