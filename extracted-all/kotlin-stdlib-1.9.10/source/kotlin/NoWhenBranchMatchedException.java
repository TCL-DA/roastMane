package kotlin

open class NoWhenBranchMatchedException : RuntimeException {

   public constructor(message: String?) : super(message)
   public constructor(message: String?, cause: Throwable?) : super(message, cause)
   public constructor(cause: Throwable?) : super(cause)}
