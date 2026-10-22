package org.eclipse.jetty.websocket.core.util;

import java.nio.channels.ReadPendingException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.CountingCallback;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.StaticException;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.websocket.core.Frame;
import org.eclipse.jetty.websocket.core.IncomingFrames;

public abstract class DemandingFlusher extends IteratingCallback implements DemandChain {
   private static final Throwable SENTINEL_CLOSE_EXCEPTION = new StaticException("Closed");
   private final IncomingFrames _emitFrame;
   private final AtomicBoolean _demand = new AtomicBoolean();
   private final AtomicReference<Throwable> _failure = new AtomicReference<>();
   private DemandChain _nextDemand;
   private Frame _frame;
   private Callback _callback;
   private boolean _needContent = true;
   private boolean _first = true;

   public DemandingFlusher(IncomingFrames emitFrame) {
      this._emitFrame = emitFrame;
   }

   protected abstract boolean handle(Frame var1, Callback var2, boolean var3);

   @Override
   public void demand() {
      if (!this._demand.compareAndSet(false, true)) {
         throw new ReadPendingException();
      } else {
         this.iterate();
      }
   }

   @Override
   public void setNextDemand(DemandChain nextDemand) {
      this._nextDemand = nextDemand;
   }

   public void onFrame(Frame frame, Callback callback) {
      if (this._frame == null && this._callback == null) {
         this._frame = frame;
         this._callback = new CountingCallback(callback, 1);
         this.succeeded();
      } else {
         throw new IllegalStateException("Not expecting onFrame");
      }
   }

   public void closeFlusher() {
      if (this._failure.compareAndSet(null, SENTINEL_CLOSE_EXCEPTION)) {
         this.failed(SENTINEL_CLOSE_EXCEPTION);
         this.iterate();
      }
   }

   public void failFlusher(Throwable t) {
      if (this._failure.compareAndSet(null, t)) {
         this.failed(t);
         this.iterate();
      }
   }

   public void emitFrame(Frame frame, Callback callback) {
      if (!this._demand.compareAndSet(true, false)) {
         throw new IllegalStateException("Demand already fulfilled");
      } else {
         this._emitFrame.onFrame(frame, callback);
      }
   }

   protected Action process() throws Throwable {
      while (true) {
         Throwable failure = this._failure.get();
         if (failure != null) {
            throw failure;
         }

         if (!this._demand.get()) {
            return Action.IDLE;
         }

         if (this._needContent) {
            this._needContent = false;
            this._nextDemand.demand();
            return Action.SCHEDULED;
         }

         boolean first = this._first;
         this._first = false;
         boolean needContent = this.handle(this._frame, this._callback, first);
         if (needContent) {
            this._needContent = true;
            this._first = true;
            this._frame = null;
            this._callback = null;
         }
      }
   }

   protected void onCompleteFailure(Throwable cause) {
      Throwable suppressed = this._failure.getAndSet(cause);
      if (suppressed != null && suppressed != cause) {
         cause.addSuppressed(suppressed);
      }

      if (this._callback != null) {
         this._callback.failed(cause);
      }

      this._frame = null;
      this._callback = null;
   }
}
