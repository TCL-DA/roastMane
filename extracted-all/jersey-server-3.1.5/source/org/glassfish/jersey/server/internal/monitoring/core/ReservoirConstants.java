package org.glassfish.jersey.server.internal.monitoring.core;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ReservoirConstants {
   private static final int DEFAULT_COLLISION_BUFFER_POWER = 8;
   public static final int COLLISION_BUFFER_POWER;
   public static final int COLLISION_BUFFER;
   public static final int TRIM_THRESHOLD = 256;

   private ReservoirConstants() {
      throw new AssertionError("Instantiation not allowed.");
   }

   static {
      PrivilegedAction<Integer> action = new PrivilegedAction<Integer>() {
         public Integer run() {
            return Integer.getInteger("jersey.config.server.monitoring.collision.buffer.power", 8);
         }
      };
      COLLISION_BUFFER_POWER = AccessController.doPrivileged(action);
      COLLISION_BUFFER = 1 << COLLISION_BUFFER_POWER;
   }
}
