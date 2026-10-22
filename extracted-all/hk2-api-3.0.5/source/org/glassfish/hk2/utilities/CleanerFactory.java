package org.glassfish.hk2.utilities;

import java.lang.ref.Cleaner;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ThreadFactory;

public final class CleanerFactory {
   private static final Cleaner commonCleaner = Cleaner.create(new ThreadFactory() {
      @Override
      public Thread newThread(final Runnable r) {
         return AccessController.doPrivileged(new PrivilegedAction<Thread>() {
            public Thread run() {
               Thread t = new Thread(null, r, "Common-Cleaner");
               t.setPriority(8);
               return t;
            }
         });
      }
   });

   public static Cleaner create() {
      return commonCleaner;
   }
}
