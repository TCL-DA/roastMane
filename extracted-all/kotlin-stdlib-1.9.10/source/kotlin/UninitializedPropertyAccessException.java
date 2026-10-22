package kotlin

class UninitializedPropertyAccessException : RuntimeException {

   public constructor(message: String?) : super(message)
   public constructor(message: String?, cause: Throwable?) : super(message, cause)
   public constructor(cause: Throwable?) : super(cause)}
