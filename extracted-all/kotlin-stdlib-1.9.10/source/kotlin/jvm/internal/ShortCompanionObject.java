package kotlin.jvm.internal

internal object ShortCompanionObject private constructor() {
   @JvmStatic
   public ShortCompanionObject INSTANCE = new ShortCompanionObject();

   public const val MAX_VALUE: Short = 32767
   public const val MIN_VALUE: Short = -32768
   public const val SIZE_BITS: Int = 16
   public const val SIZE_BYTES: Int = 2

}
