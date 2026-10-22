package kotlin.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.METHOD])
annotation class InlineOnly(

)
