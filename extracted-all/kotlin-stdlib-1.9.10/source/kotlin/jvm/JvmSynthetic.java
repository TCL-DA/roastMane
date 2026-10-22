package kotlin.jvm

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FIELD])
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.FIELD, ElementType.METHOD])
annotation class JvmSynthetic(

)
