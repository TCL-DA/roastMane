package kotlin.io.path

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FIELD, AnnotationTarget.LOCAL_VARIABLE, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.TYPEALIAS])
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.ANNOTATION_TYPE])
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
@SinceKotlin(version = "1.4")
annotation class ExperimentalPathApi(

)
