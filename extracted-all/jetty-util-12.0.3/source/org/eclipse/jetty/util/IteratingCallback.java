package org.eclipse.jetty.util;

import java.io.IOException;
import org.eclipse.jetty.util.thread.AutoLock;

public abstract class IteratingCallback implements Callback {
   private final AutoLock _lock = new AutoLock();
   private IteratingCallback.State _state;
   private Throwable _failure;
   private boolean _iterate;

   protected IteratingCallback() {
      this._state = IteratingCallback.State.IDLE;
   }

   protected IteratingCallback(boolean needReset) {
      this._state = needReset ? IteratingCallback.State.SUCCEEDED : IteratingCallback.State.IDLE;
   }

   protected abstract IteratingCallback.Action process() throws Throwable;

   protected void onCompleteSuccess() {
   }

   protected void onCompleteFailure(Throwable cause) {
   }

   public void iterate() {
      boolean process = false;

      try (AutoLock ignored = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
               this._state = IteratingCallback.State.PROCESSING;
               process = true;
               break;
            case PROCESSING:
               this._iterate = true;
            case PENDING:
            case CALLED:
            case SUCCEEDED:
            case FAILED:
               break;
            case CLOSED:
            default:
               throw new IllegalStateException(this.toString());
         }
      }

      if (process) {
         this.processing();
      }
   }

   private void processing() {
      boolean notifyCompleteSuccess = false;
      Throwable notifyCompleteFailure = null;

      while (true) {
         IteratingCallback.Action action = null;

         try {
            action = this.process();
         } catch (Throwable var8) {
            this.failed(var8);
         }

         try (AutoLock ignored = this._lock.lock()) {
            label85:
            switch (this._state) {
               case IDLE:
               case PENDING:
               default:
                  throw new IllegalStateException(String.format("%s[action=%s]", this, action));
               case PROCESSING:
                  if (action != null) {
                     switch (action) {
                        case IDLE:
                           if (this._iterate) {
                              this._iterate = false;
                              continue;
                           }

                           this._state = IteratingCallback.State.IDLE;
                           break label85;
                        case SCHEDULED:
                           this._state = IteratingCallback.State.PENDING;
                           break label85;
                        case SUCCEEDED:
                           this._iterate = false;
                           this._state = IteratingCallback.State.SUCCEEDED;
                           notifyCompleteSuccess = true;
                           break label85;
                     }
                  }

                  throw new IllegalStateException(String.format("%s[action=%s]", this, action));
               case CALLED:
                  if (action != IteratingCallback.Action.SCHEDULED) {
                     throw new IllegalStateException(String.format("%s[action=%s]", this, action));
                  }

                  this._state = IteratingCallback.State.PROCESSING;
                  continue;
               case SUCCEEDED:
                  break;
               case FAILED:
               case CLOSED:
                  notifyCompleteFailure = this._failure;
                  this._failure = null;
            }
         }

         if (notifyCompleteSuccess) {
            this.onCompleteSuccess();
         } else if (notifyCompleteFailure != null) {
            this.onCompleteFailure(notifyCompleteFailure);
         }

         return;
      }
   }

   @Override
   public void succeeded() {
      boolean process = false;

      try (AutoLock ignored = this._lock.lock()) {
         switch (this._state) {
            case PROCESSING:
               this._state = IteratingCallback.State.CALLED;
               break;
            case PENDING:
               this._state = IteratingCallback.State.PROCESSING;
               process = true;
               break;
            case CALLED:
            case SUCCEEDED:
            default:
               throw new IllegalStateException(this.toString());
            case FAILED:
            case CLOSED:
         }
      }

      if (process) {
         this.processing();
      }
   }

   @Override
   public void failed(Throwable x) {
      boolean failure = false;

      try (AutoLock ignored = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
            case CALLED:
            case SUCCEEDED:
            case FAILED:
            case CLOSED:
               break;
            case PROCESSING:
               this._state = IteratingCallback.State.FAILED;
               this._failure = x;
               break;
            case PENDING:
               this._state = IteratingCallback.State.FAILED;
               failure = true;
               break;
            default:
               throw new IllegalStateException(this.toString());
         }
      }

      if (failure) {
         this.onCompleteFailure(x);
      }
   }

   public void close() {
      String failure = null;

      try (AutoLock ignored = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
            case SUCCEEDED:
            case FAILED:
               this._state = IteratingCallback.State.CLOSED;
               break;
            case PROCESSING:
               this._failure = new IOException(String.format("Close %s in state %s", this, this._state));
               this._state = IteratingCallback.State.CLOSED;
               break;
            case PENDING:
            case CALLED:
            default:
               failure = String.format("Close %s in state %s", this, this._state);
               this._state = IteratingCallback.State.CLOSED;
            case CLOSED:
         }
      }

      if (failure != null) {
         this.onCompleteFailure(new IOException(failure));
      }
   }

   boolean isIdle() {
      boolean var2;
      try (AutoLock ignored = this._lock.lock()) {
         var2 = this._state == IteratingCallback.State.IDLE;
      }

      return var2;
   }

   public boolean isClosed() {
      boolean var2;
      try (AutoLock ignored = this._lock.lock()) {
         var2 = this._state == IteratingCallback.State.CLOSED;
      }

      return var2;
   }

   public boolean isFailed() {
      boolean var2;
      try (AutoLock ignored = this._lock.lock()) {
         var2 = this._state == IteratingCallback.State.FAILED;
      }

      return var2;
   }

   public boolean isSucceeded() {
      boolean var2;
      try (AutoLock ignored = this._lock.lock()) {
         var2 = this._state == IteratingCallback.State.SUCCEEDED;
      }

      return var2;
   }

   public boolean reset() {
      boolean var2;
      try (AutoLock ignored = this._lock.lock()) {
         switch (this._state) {
            case IDLE:
               return true;
            case SUCCEEDED:
            case FAILED:
               this._state = IteratingCallback.State.IDLE;
               this._failure = null;
               this._iterate = false;
               return true;
         }

         var2 = false;
      }

      return var2;
   }

   @Override
   public String toString() {
      return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this._state);
   }

   protected static enum Action {
      IDLE,
      SCHEDULED,
      SUCCEEDED;
   }

   private static enum State {
      IDLE,
      PROCESSING,
      PENDING,
      CALLED,
      SUCCEEDED,
      FAILED,
      CLOSED;
   }
}
