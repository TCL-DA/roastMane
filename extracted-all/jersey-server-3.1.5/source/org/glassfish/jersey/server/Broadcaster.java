package org.glassfish.jersey.server;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.server.internal.LocalizationMessages;

public class Broadcaster<T> implements BroadcasterListener<T> {
   private final CopyOnWriteArrayList<BroadcasterListener<T>> listeners = new CopyOnWriteArrayList<>();
   private final ConcurrentLinkedQueue<ChunkedOutput<T>> chunkedOutputs = new ConcurrentLinkedQueue<>();

   public Broadcaster() {
      this(Broadcaster.class);
   }

   protected Broadcaster(Class<? extends Broadcaster> subclass) {
      if (subclass != this.getClass()) {
         this.listeners.add(this);
      }
   }

   public <OUT extends ChunkedOutput<T>> boolean add(OUT chunkedOutput) {
      return this.chunkedOutputs.offer(chunkedOutput);
   }

   public <OUT extends ChunkedOutput<T>> boolean remove(OUT chunkedOutput) {
      return this.chunkedOutputs.remove(chunkedOutput);
   }

   public boolean add(BroadcasterListener<T> listener) {
      return this.listeners.add(listener);
   }

   public boolean remove(BroadcasterListener<T> listener) {
      return this.listeners.remove(listener);
   }

   public void broadcast(final T chunk) {
      this.forEachOutput(new Broadcaster.Task<ChunkedOutput<T>>() {
         public void run(ChunkedOutput<T> cr) throws IOException {
            cr.write(chunk);
         }
      });
   }

   public void closeAll() {
      this.forEachOutput(new Broadcaster.Task<ChunkedOutput<T>>() {
         public void run(ChunkedOutput<T> cr) throws IOException {
            cr.close();
         }
      });
   }

   @Override
   public void onException(ChunkedOutput<T> chunkedOutput, Exception exception) {
   }

   @Override
   public void onClose(ChunkedOutput<T> chunkedOutput) {
   }

   private void forEachOutput(Broadcaster.Task<ChunkedOutput<T>> t) {
      Iterator<ChunkedOutput<T>> iterator = this.chunkedOutputs.iterator();

      while (iterator.hasNext()) {
         ChunkedOutput<T> chunkedOutput = iterator.next();
         if (!chunkedOutput.isClosed()) {
            try {
               t.run(chunkedOutput);
            } catch (Exception var5) {
               this.fireOnException(chunkedOutput, var5);
            }
         }

         if (chunkedOutput.isClosed()) {
            iterator.remove();
            this.fireOnClose(chunkedOutput);
         }
      }
   }

   private void forEachListener(Broadcaster.Task<BroadcasterListener<T>> t) {
      for (BroadcasterListener<T> listener : this.listeners) {
         try {
            t.run(listener);
         } catch (Exception var5) {
            Logger.getLogger(Broadcaster.class.getName())
               .log(Level.WARNING, LocalizationMessages.BROADCASTER_LISTENER_EXCEPTION(var5.getClass().getSimpleName()), (Throwable)var5);
         }
      }
   }

   private void fireOnException(final ChunkedOutput<T> chunkedOutput, final Exception exception) {
      this.forEachListener(new Broadcaster.Task<BroadcasterListener<T>>() {
         public void run(BroadcasterListener<T> parameter) throws IOException {
            parameter.onException(chunkedOutput, exception);
         }
      });
   }

   private void fireOnClose(final ChunkedOutput<T> chunkedOutput) {
      this.forEachListener(new Broadcaster.Task<BroadcasterListener<T>>() {
         public void run(BroadcasterListener<T> parameter) throws IOException {
            parameter.onClose(chunkedOutput);
         }
      });
   }

   private interface Task<T> {
      void run(T var1) throws IOException;
   }
}
