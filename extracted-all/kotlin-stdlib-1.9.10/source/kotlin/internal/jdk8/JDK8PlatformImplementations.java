package kotlin.internal.jdk8

import java.util.regex.MatchResult
import java.util.regex.Matcher
import kotlin.internal.jdk7.JDK7PlatformImplementations
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.random.Random
import kotlin.random.jdk8.PlatformThreadLocalRandom

internal open class JDK8PlatformImplementations : JDK7PlatformImplementations {
   private fun sdkIsNullOrAtLeast(version: Int): Boolean {
      return JDK8PlatformImplementations.ReflectSdkVersion.sdkVersion == null || JDK8PlatformImplementations.ReflectSdkVersion.sdkVersion >= version;
   }

   public override fun getMatchResultNamedGroup(matchResult: MatchResult, name: String): MatchGroup? {
      var var10000: Matcher = matchResult as? Matcher;
      if ((matchResult as? Matcher) == null) {
         throw new UnsupportedOperationException("Retrieving groups by name is not supported on this platform.");
      } else {
         var range: IntRange = new IntRange(var10000.start(name), var10000.end(name) - 1);
         var var5: MatchGroup;
         if (range.getStart() >= 0) {
            var var10002: java.lang.String = var10000.group(name);
            var5 = new MatchGroup(var10002, range);
         } else {
            var5 = null;
         }

         return var5;
      }
   }

   public override fun defaultPlatformRandom(): Random {
      return if (this.sdkIsNullOrAtLeast(34)) new PlatformThreadLocalRandom() else super.defaultPlatformRandom();
   }

   @SourceDebugExtension(["SMAP\nJDK8PlatformImplementations.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JDK8PlatformImplementations.kt\nkotlin/internal/jdk8/JDK8PlatformImplementations$ReflectSdkVersion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,61:1\n1#2:62\n*E\n"])
   private object ReflectSdkVersion private constructor() {
      @JvmStatic
      public JDK8PlatformImplementations.ReflectSdkVersion INSTANCE = new JDK8PlatformImplementations.ReflectSdkVersion();

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
