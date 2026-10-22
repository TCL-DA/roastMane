package org.eclipse.jetty.websocket.core.util;

import java.util.ArrayDeque;
import java.util.Queue;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.StaticException;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.internal.FrameEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransformingFlusher {
   private final Logger log = LoggerFactory.getLogger(this.getClass());
   private static final Throwable SENTINEL_CLOSE_EXCEPTION = new StaticException("Closed");
   private final AutoLock lock = new AutoLock();
   private final Queue<FrameEntry> entries = new ArrayDeque<>();
   private final IteratingCallback flusher = new TransformingFlusher.Flusher();
   private boolean finished = true;
   private Throwable failure;

   protected abstract boolean onFrame(Frame var1, Callback var2, boolean var3);

   protected abstract boolean transform(Callback var1);

   public final void sendFrame(Frame frame, Callback callback, boolean batch) {
      FrameEntry entry = new FrameEntry(frame, callback, batch);
      if (this.log.isDebugEnabled()) {
         this.log.debug("Queuing {}", entry);
      }

      boolean enqueued = false;
      AutoLock l = this.lock.lock();

      try {
         if (this.failure == null) {
            enqueued = this.entries.add(entry);
         }
      } catch (Throwable var10) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (l != null) {
         l.close();
      }

      if (enqueued) {
         this.flusher.iterate();
      } else {
         this.notifyCallbackFailure(callback, this.failure);
      }
   }

   public void closeFlusher() {
      this.failFlusher(SENTINEL_CLOSE_EXCEPTION);
   }

   public void failFlusher(Throwable t) {
      boolean failed = false;
      AutoLock l = this.lock.lock();

      try {
         if (this.failure == null) {
            this.failure = t;
            failed = true;
         } else {
            this.failure.addSuppressed(t);
         }
      } catch (Throwable var7) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (l != null) {
         l.close();
      }

      if (failed) {
         this.flusher.failed(t);
         this.flusher.iterate();
      }
   }

   private void onFailure(Throwable t) {
      AutoLock l = this.lock.lock();

      try {
         if (this.failure == null) {
            this.failure = t;
         }
      } catch (Throwable var6) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (l != null) {
         l.close();
      }

      for (FrameEntry entry : this.entries) {
         this.notifyCallbackFailure(entry.callback, t);
      }

      this.entries.clear();
   }

   private FrameEntry pollEntry() {
      AutoLock l = this.lock.lock();

      FrameEntry var2;
      try {
         var2 = this.entries.poll();
      } catch (Throwable var5) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (l != null) {
         l.close();
      }

      return var2;
   }

   private void notifyCallbackSuccess(Callback callback) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("notifyCallbackSuccess {}", callback);
      }

      try {
         if (callback != null) {
            callback.succeeded();
         }
      } catch (Throwable var3) {
         this.log.warn("Exception while notifying success of callback {}", callback, var3);
      }
   }

   private void notifyCallbackFailure(Callback callback, Throwable failure) {
      if (this.log.isDebugEnabled()) {
         this.log.debug("notifyCallbackFailure {} {}", callback, failure.toString());
      }

      try {
         if (callback != null) {
            callback.failed(failure);
         }
      } catch (Throwable var4) {
         this.log.warn("Exception while notifying failure of callback {}", callback, var4);
      }
   }

   private class Flusher extends IteratingCallback implements Callback {
      private FrameEntry current;

      protected Action process() throws Throwable {
         AutoLock l = TransformingFlusher.this.lock.lock();

         try {
            if (TransformingFlusher.this.failure != null) {
               throw TransformingFlusher.this.failure;
            }
         } catch (Throwable var5) {
            if (l != null) {
               try {
                  l.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (l != null) {
            l.close();
         }

         if (TransformingFlusher.this.finished) {
            if (this.current != null) {
               TransformingFlusher.this.notifyCallbackSuccess(this.current.callback);
            }

            this.current = TransformingFlusher.this.pollEntry();
            if (this.current == null) {
               return Action.IDLE;
            } else {
               if (TransformingFlusher.this.log.isDebugEnabled()) {
                  TransformingFlusher.this.log.debug("onFrame {}", this.current);
               }

               TransformingFlusher.this.finished = TransformingFlusher.this.onFrame(this.current.frame, this, this.current.batch);
               return Action.SCHEDULED;
            }
         } else {
            if (TransformingFlusher.this.log.isDebugEnabled()) {
               TransformingFlusher.this.log.debug("transform {}", this.current);
            }

            TransformingFlusher.this.finished = TransformingFlusher.this.transform(this);
            return Action.SCHEDULED;
         }
      }

      protected void onCompleteFailure(Throwable t) {
         if (TransformingFlusher.this.log.isDebugEnabled()) {
            TransformingFlusher.this.log.debug("onCompleteFailure {}", t.toString());
         }

         if (this.current != null) {
            TransformingFlusher.this.notifyCallbackFailure(this.current.callback, t);
            this.current = null;
         }

         TransformingFlusher.this.onFailure(t);
      }
   }
}
