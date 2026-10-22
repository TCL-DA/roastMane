package kotlin.jvm

import java.lang.annotation.Documented
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([])
annotation class JvmWildcard(

)
