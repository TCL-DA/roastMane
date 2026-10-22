package okhttp3

import okhttp3.internal.authenticator.JavaNetAuthenticator

fun interface Authenticator {
   @JvmStatic
   Authenticator.Companion Companion = Authenticator.Companion.$$INSTANCE;
   @JvmField
   @JvmStatic
   Authenticator NONE = new Authenticator.Companion.AuthenticatorNone();
   @JvmField
   @JvmStatic
   Authenticator JAVA_NET_AUTHENTICATOR = new JavaNetAuthenticator(null, 1, null);

   @Throws(java/io/IOException::class)
   public abstract fun authenticate(route: Route?, response: Response): Request? {
   }

   companion object Companion private constructor() {
      public final val JAVA_NET_AUTHENTICATOR: Authenticator
      public final val NONE: Authenticator


      private class AuthenticatorNone : Authenticator {
         public override fun authenticate(route: Route?, response: Response): Request? {
            return null;
         }
      }
   }
}
