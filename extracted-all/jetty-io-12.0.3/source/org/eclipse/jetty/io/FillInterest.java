package org.eclipse.jetty.io;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ReadPendingException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FillInterest {
   private static final Logger LOG = LoggerFactory.getLogger(FillInterest.class);
   private final AtomicReference<Callback> _interested = new AtomicReference<>(null);

   protected FillInterest() {
   }

   public void register(Callback callback) throws ReadPendingException {
      if (!this.tryRegister(callback)) {
         LOG.warn("Read pending for {} prevented {}", this._interested, callback);
         throw new ReadPendingException();
      }
   }

   public boolean tryRegister(Callback callback) {
      if (callback == null) {
         throw new IllegalArgumentException();
      } else if (!this._interested.compareAndSet(null, callback)) {
         return false;
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("interested {}", this);
         }

         try {
            this.needsFillInterest();
         } catch (Throwable var3) {
            this.onFail(var3);
         }

         return true;
      }
   }

   public boolean fillable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("fillable {}", this);
      }

      Callback callback = this._interested.get();
      if (callback != null && this._interested.compareAndSet(callback, null)) {
         callback.succeeded();
         return true;
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} lost race {}", this, callback);
         }

         return false;
      }
   }

   public boolean isInterested() {
      return this._interested.get() != null;
   }

   public InvocationType getCallbackInvocationType() {
      Callback callback = this._interested.get();
      return Invocable.getInvocationType(callback);
   }

   public boolean onFail(Throwable cause) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onFail {}", this, cause);
      }

      Callback callback = this._interested.get();
      if (callback != null && this._interested.compareAndSet(callback, null)) {
         callback.failed(cause);
         return true;
      } else {
         return false;
      }
   }

   public void onClose() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onClose {}", this);
      }

      Callback callback = this._interested.get();
      if (callback != null && this._interested.compareAndSet(callback, null)) {
         callback.failed(new ClosedChannelException());
      }
   }

   @Override
   public String toString() {
      return String.format("FillInterest@%x{%s}", this.hashCode(), this._interested.get());
   }

   public String toStateString() {
      return this._interested.get() == null ? "-" : "FI";
   }

   protected abstract void needsFillInterest() throws IOException;
}
