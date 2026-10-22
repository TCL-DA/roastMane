package kotlin.random

import java.io.Serializable

private class PlatformRandom(impl: java.util.Random) : AbstractPlatformRandom, Serializable {
   @JvmStatic
   private PlatformRandom.Companion Companion = new PlatformRandom.Companion(null);
   @JvmStatic
   private long serialVersionUID = 0L;

   public open val impl: java.util.Random

   init {
      this.impl = impl;
   }

   private companion object Companion private constructor() {
      private const val serialVersionUID: Long

   }
}
