package kotlin

@PublishedApi
internal object _Assertions private constructor() {
   @JvmStatic
   public _Assertions INSTANCE = new _Assertions();

   internal final val ENABLED: Boolean = INSTANCE.getClass().desiredAssertionStatus()

}
