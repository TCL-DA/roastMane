package kotlin.jvm.internal

internal object LongCompanionObject private constructor() {
   @JvmStatic
   public LongCompanionObject INSTANCE = new LongCompanionObject();

   public const val MAX_VALUE: Long = java.lang.Long.MAX_VALUE
   public const val MIN_VALUE: Long = java.lang.Long.MIN_VALUE
   public const val SIZE_BITS: Int = 64
   public const val SIZE_BYTES: Int = 8

}
