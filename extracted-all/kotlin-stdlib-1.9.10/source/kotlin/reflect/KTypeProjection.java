package kotlin.reflect

@SinceKotlin(version = "1.1")
data class KTypeProjection(variance: KVariance?, type: KType?) {
   @JvmStatic
   public KTypeProjection.Companion Companion = new KTypeProjection.Companion(null);
   @JvmField
   @JvmStatic
   public KTypeProjection star = new KTypeProjection(null, null);

   public final val type: KType?
   public final val variance: KVariance?

   init {
      this.variance = variance;
      this.type = type;
      if (this.variance == null != (this.type == null)) {
         throw new IllegalArgumentException(
            (if (this.variance == null)
                  "Star projection must have no type specified."
                  else
                  "The projection variance " + this.variance + " requires type to be specified.")
               .toString()
         );
      }
   }

   public override fun toString(): String {
      var var10000: java.lang.String;
      switch (this.variance == null ? -1 : KTypeProjection.WhenMappings.$EnumSwitchMapping$0[this.variance.ordinal()]) {
         case -1:
            var10000 = "*";
            break;
         case 0:
         default:
            throw new NoWhenBranchMatchedException();
         case 1:
            var10000 = java.lang.String.valueOf(this.type);
            break;
         case 2:
            var10000 = "in " + this.type;
            break;
         case 3:
            var10000 = "out " + this.type;
      }

      return var10000;
   }

   public operator fun component1(): KVariance? {
      return this.variance;
   }

   public operator fun component2(): KType? {
      return this.type;
   }

   public fun copy(variance: KVariance?, type: KType?): KTypeProjection {
      return new KTypeProjection(variance, type);
   }

   public override fun hashCode(): Int {
      return (if (this.variance == null) 0 else this.variance.hashCode()) * 31 + (if (this.type == null) 0 else this.type.hashCode());
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is KTypeProjection) {
         return false;
      } else {
         var var2: KTypeProjection = other as KTypeProjection;
         if (this.variance != (other as KTypeProjection).variance) {
            return false;
         } else {
            return this.type == var2.type;
         }
      }
   }

   @JvmStatic
   fun invariant(type: KType): KTypeProjection {
      return Companion.invariant(type);
   }

   @JvmStatic
   fun contravariant(type: KType): KTypeProjection {
      return Companion.contravariant(type);
   }

   @JvmStatic
   fun covariant(type: KType): KTypeProjection {
      return Companion.covariant(type);
   }

   companion object Companion private constructor() {
      public final val STAR: KTypeProjection
         public final get() {
            return KTypeProjection.star;
         }

      internal final val star: KTypeProjection


      public fun invariant(type: KType): KTypeProjection {
         return new KTypeProjection(KVariance.INVARIANT, type);
      }

      public fun contravariant(type: KType): KTypeProjection {
         return new KTypeProjection(KVariance.IN, type);
      }

      public fun covariant(type: KType): KTypeProjection {
         return new KTypeProjection(KVariance.OUT, type);
      }
   }
}
