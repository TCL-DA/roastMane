package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.TYPEALIAS])
@MustBeDocumented
@Documented
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.ANNOTATION_TYPE])
annotation class Deprecated(
   val message: String,
   val replaceWith: ReplaceWith = @ReplaceWith(
expression = "",
imports = []),
   val level: DeprecationLevel = DeprecationLevel.WARNING
)
