package okhttp3.internal

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Retention(AnnotationRetention.BINARY)
@Documented
@Target(allowedTargets = [AnnotationTarget.CONSTRUCTOR, AnnotationTarget.CLASS, AnnotationTarget.FUNCTION])
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class SuppressSignatureCheck(

)
