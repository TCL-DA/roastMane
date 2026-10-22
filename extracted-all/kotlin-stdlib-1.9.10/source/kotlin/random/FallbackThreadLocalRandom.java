package kotlin.random

import org.jetbrains.annotations.NotNull

internal class FallbackThreadLocalRandom : AbstractPlatformRandom {
   public open val impl: java.util.Random
      public open get() {
         var var10000: Any = this.implStorage.get();
         return var10000 as java.util.Random;
      }

   private final val implStorage: <unrepresentable> = new ThreadLocal<java.util.Random>() {
      @NotNull
      protected java.util.Random initialValue() {
         return new java.util.Random();
      }
   }

}
