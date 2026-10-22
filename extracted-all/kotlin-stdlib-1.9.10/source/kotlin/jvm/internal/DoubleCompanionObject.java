package kotlin.jvm.internal

internal object DoubleCompanionObject private constructor() {
   @JvmStatic
   public DoubleCompanionObject INSTANCE = new DoubleCompanionObject();

   public const val MAX_VALUE: Double = java.lang.Double.MAX_VALUE
   public const val MIN_VALUE: Double = java.lang.Double.MIN_VALUE
   public const val NEGATIVE_INFINITY: Double = java.lang.Double.NEGATIVE_INFINITY
   public const val NaN: Double = java.lang.Double.NaN
   public const val POSITIVE_INFINITY: Double = java.lang.Double.POSITIVE_INFINITY
   public const val SIZE_BITS: Int = 64
   public const val SIZE_BYTES: Int = 8


   public fun getMIN_VALUE(): Double {
      return java.lang.Double.MIN_VALUE;
   }

   public fun getMAX_VALUE(): Double {
      return java.lang.Double.MAX_VALUE;
   }

   public fun getPOSITIVE_INFINITY(): Double {
      return java.lang.Double.POSITIVE_INFINITY;
   }

   public fun getNEGATIVE_INFINITY(): Double {
      return java.lang.Double.NEGATIVE_INFINITY;
   }

   public fun getNaN(): Double {
      return java.lang.Double.NaN;
   }
}
