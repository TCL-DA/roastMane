package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.TYPEALIAS])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class SinceKotlin(
   val version: String
)
