package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.METHOD, ElementType.CONSTRUCTOR])
annotation class JvmOverloads(

)
