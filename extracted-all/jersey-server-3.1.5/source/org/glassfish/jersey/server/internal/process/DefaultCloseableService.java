package org.glassfish.jersey.server.internal.process;

import java.io.Closeable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.CloseableService;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class DefaultCloseableService implements CloseableService {
   private static final Logger LOGGER = Logger.getLogger(DefaultCloseableService.class.getName());
   private final AtomicBoolean closed = new AtomicBoolean(false);
   private final Set<Closeable> closeables = Collections.newSetFromMap(new IdentityHashMap<>());

   @Override
   public boolean add(Closeable closeable) {
      return !this.closed.get() && this.closeables.add(closeable);
   }

   @Override
   public void close() {
      if (this.closed.compareAndSet(false, true)) {
         for (Closeable closeable : this.closeables) {
            try {
               closeable.close();
            } catch (Exception var4) {
               LOGGER.log(Level.WARNING, LocalizationMessages.CLOSEABLE_UNABLE_TO_CLOSE(closeable.getClass().getName()), (Throwable)var4);
            }
         }
      }
   }
}
