package org.eclipse.jetty.util.thread;

import java.security.PrivilegedAction;
import org.eclipse.jetty.util.security.SecurityUtils;

class PrivilegedThreadFactory {
   static <T extends Thread> T newThread(PrivilegedAction<T> creator) {
      return SecurityUtils.doPrivileged(creator);
   }

   private PrivilegedThreadFactory() {
   }
}
