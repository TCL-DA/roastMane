package kotlin.jvm

open class KotlinReflectionNotSupportedError : Error {
   public constructor() : super("Kotlin reflection implementation is not found at runtime. Make sure you have kotlin-reflect.jar in the classpath")
   public constructor(message: String?) : super(message)
   public constructor(message: String?, cause: Throwable?) : super(message, cause)
   public constructor(cause: Throwable?) : super(cause)}
