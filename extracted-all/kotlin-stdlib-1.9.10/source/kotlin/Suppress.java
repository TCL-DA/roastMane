package kotlin

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.TYPE_PARAMETER, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.TYPE, AnnotationTarget.EXPRESSION, AnnotationTarget.FILE, AnnotationTarget.TYPEALIAS])
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE])
annotation class Suppress(
   val names: Array<out String>
)
