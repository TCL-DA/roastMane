package kotlin.jvm.internal

internal object IntCompanionObject private constructor() {
   @JvmStatic
   public IntCompanionObject INSTANCE = new IntCompanionObject();

   public const val MAX_VALUE: Int = Integer.MAX_VALUE
   public const val MIN_VALUE: Int = Integer.MIN_VALUE
   public const val SIZE_BITS: Int = 32
   public const val SIZE_BYTES: Int = 4

}
