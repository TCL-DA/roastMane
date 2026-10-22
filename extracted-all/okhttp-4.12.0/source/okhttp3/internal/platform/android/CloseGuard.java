package okhttp3.internal.platform.android

import java.lang.reflect.Method

internal class CloseGuard(getMethod: Method?, openMethod: Method?, warnIfOpenMethod: Method?) {
   @JvmStatic
   public CloseGuard.Companion Companion = new CloseGuard.Companion(null);

   private final val getMethod: Method?
   private final val openMethod: Method?
   private final val warnIfOpenMethod: Method?

   init {
      this.getMethod = getMethod;
      this.openMethod = openMethod;
      this.warnIfOpenMethod = warnIfOpenMethod;
   }

   public fun createAndOpen(closer: String): Any? {
      if (this.getMethod != null) {
         try {
            var closeGuardInstance: Any = this.getMethod.invoke(null);
            var var10000: Method = this.openMethod;
            var10000.invoke(closeGuardInstance, closer);
            return closeGuardInstance;
         } catch (Exception var4) {
         }
      }

      return null;
   }

   public fun warnIfOpen(closeGuardInstance: Any?): Boolean {
      var reported: Boolean = false;
      if (closeGuardInstance != null) {
         try {
            var var10000: Method = this.warnIfOpenMethod;
            var10000.invoke(closeGuardInstance);
            reported = true;
         } catch (Exception var4) {
         }
      }

      return reported;
   }

   companion object Companion private constructor() {
      public fun get(): CloseGuard {
         var var7: Method;
         var var8: Method;
         var var9: Method;
         try {
            var _: Class = Class.forName("dalvik.system.CloseGuard");
            var7 = _.getMethod("get");
            var8 = _.getMethod("open", java.lang.String.class);
            var9 = _.getMethod("warnIfOpen");
         } catch (Exception var6) {
            var7 = null;
            var8 = null;
            var9 = null;
         }

         return new CloseGuard(var7, var8, var9);
      }
   }
}
