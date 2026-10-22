package kotlin.internal

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

@Target(allowedTargets = [AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.TYPEALIAS])
@Retention(AnnotationRetention.SOURCE)
@Repeatable
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
@java.lang.annotation.Target([ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR])
@SinceKotlin(version = "1.2")
annotation class RequireKotlin(
   val version: String,
   val message: String = "",
   val level: DeprecationLevel = DeprecationLevel.ERROR,
   val versionKind: RequireKotlinVersionKind = RequireKotlinVersionKind.LANGUAGE_VERSION,
   val errorCode: Int = -1
)
