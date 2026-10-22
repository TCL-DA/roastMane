package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.FIELD])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.FIELD])
annotation class JvmField(

)
