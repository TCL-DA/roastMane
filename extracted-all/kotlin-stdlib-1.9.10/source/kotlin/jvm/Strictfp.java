package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class Strictfp(

)
