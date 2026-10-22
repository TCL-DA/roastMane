package kotlin

@SinceKotlin(version = "1.4")
@PublishedApi
internal class KotlinNothingValueException : RuntimeException {

   public constructor(message: String?) : super(message)
   public constructor(message: String?, cause: Throwable?) : super(message, cause)
   public constructor(cause: Throwable?) : super(cause)}
