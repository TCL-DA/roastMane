package kotlin.text

private object SystemProperties private constructor() {
   @JvmStatic
   public SystemProperties INSTANCE = new SystemProperties();

   public final val LINE_SEPARATOR: String


   @JvmStatic
   fun {
      var var10000: java.lang.String = System.getProperty("line.separator");
      LINE_SEPARATOR = var10000;
   }
}
