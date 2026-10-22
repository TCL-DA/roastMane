package kotlin.internal.jdk7

import kotlin.internal.PlatformImplementations
import kotlin.jvm.internal.SourceDebugExtension

internal open class JDK7PlatformImplementations : PlatformImplementations {
   private fun sdkIsNullOrAtLeast(version: Int): Boolean {
      return JDK7PlatformImplementations.ReflectSdkVersion.sdkVersion == null || JDK7PlatformImplementations.ReflectSdkVersion.sdkVersion >= version;
   }

   public override fun addSuppressed(cause: Throwable, exception: Throwable) {
      if (this.sdkIsNullOrAtLeast(19)) {
         cause.addSuppressed(exception);
      } else {
         super.addSuppressed(cause, exception);
      }
   }

   public override fun getSuppressed(exception: Throwable): List<Throwable> {
      var var2: java.util.List;
      if (this.sdkIsNullOrAtLeast(19)) {
         var var10000: Array<java.lang.Throwable> = exception.getSuppressed();
         var2 = ArraysKt.asList(var10000);
      } else {
         var2 = super.getSuppressed(exception);
      }

      return var2;
   }

   @SourceDebugExtension(["SMAP\nJDK7PlatformImplementations.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JDK7PlatformImplementations.kt\nkotlin/internal/jdk7/JDK7PlatformImplementations$ReflectSdkVersion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,38:1\n1#2:39\n*E\n"])
   private object ReflectSdkVersion private constructor() {
      @JvmStatic
      public JDK7PlatformImplementations.ReflectSdkVersion INSTANCE = new JDK7PlatformImplementations.ReflectSdkVersion();

      public final val sdkVersion: Int?


      @JvmStatic
      fun {
         var var1: Int;
         try {
            var1 = (Integer)Class.forName("android.os.Build$VERSION").getField("SDK_INT").get(null);
            var1 = var1 as? Integer;
         } catch (java.lang.Throwable var4) {
            var1 = null;
         }

         sdkVersion = if (var1 != null) (if (var1.intValue() > 0) var1 else null) else null;
      }
   }
}
