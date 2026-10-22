package kotlin.jvm.internal

@SinceKotlin(version = "1.3")
internal object BooleanCompanionObject private constructor() {
   @JvmStatic
   public BooleanCompanionObject INSTANCE = new BooleanCompanionObject();

}
