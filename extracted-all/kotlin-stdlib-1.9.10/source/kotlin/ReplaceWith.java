package kotlin

import java.lang.annotation.Documented
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [])
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
@Documented
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([])
annotation class ReplaceWith(
   val expression: String,
   val imports: Array<out String>
)
