package org.glassfish.jersey.internal.guava;

final class Hashing {
   private static final int C1 = -862048943;
   private static final int C2 = 461845907;

   private Hashing() {
   }

   static int smear(int hashCode) {
      return 461845907 * Integer.rotateLeft(hashCode * -862048943, 15);
   }
}
