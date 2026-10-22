package kotlin.jvm.internal

internal object ByteCompanionObject private constructor() {
   @JvmStatic
   public ByteCompanionObject INSTANCE = new ByteCompanionObject();

   public const val MAX_VALUE: Byte = 127
   public const val MIN_VALUE: Byte = -128
   public const val SIZE_BITS: Int = 8
   public const val SIZE_BYTES: Int = 1

}
