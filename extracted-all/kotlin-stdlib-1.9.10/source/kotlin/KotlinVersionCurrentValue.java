package kotlin

private object KotlinVersionCurrentValue private constructor() {
   @JvmStatic
   public KotlinVersionCurrentValue INSTANCE = new KotlinVersionCurrentValue();


   @JvmStatic
   public fun get(): KotlinVersion {
      return new KotlinVersion(1, 9, 10);
   }
}
