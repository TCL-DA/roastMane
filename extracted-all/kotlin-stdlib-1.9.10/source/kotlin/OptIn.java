package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.EXPRESSION, AnnotationTarget.FILE, AnnotationTarget.TYPEALIAS])
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE])
@SinceKotlin(version = "1.3")
annotation class OptIn(
   val markerClass: Array<out KClass<out Annotation>>
)
