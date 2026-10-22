package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.TYPE])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([])
annotation class UnsafeVariance(

)
