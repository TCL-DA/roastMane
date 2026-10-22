package kotlin.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.VALUE_PARAMETER])
@Retention(AnnotationRetention.BINARY)
@java.lang.annotation.Retention(RetentionPolicy.CLASS)
@java.lang.annotation.Target([ElementType.PARAMETER])
@SinceKotlin(version = "1.2")
annotation class AccessibleLateinitPropertyLiteral(

)
