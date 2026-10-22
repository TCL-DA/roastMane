package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritePendingException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WriteFlusher {
   private static final Logger LOG = LoggerFactory.getLogger(WriteFlusher.class);
   private static final boolean DEBUG = LOG.isDebugEnabled();
   private static final ByteBuffer[] EMPTY_BUFFERS = new ByteBuffer[]{BufferUtil.EMPTY_BUFFER};
   private static final EnumMap<WriteFlusher.StateType, Set<WriteFlusher.StateType>> __stateTransitions = new EnumMap<>(WriteFlusher.StateType.class);
   private static final WriteFlusher.State __IDLE = new WriteFlusher.IdleState();
   private static final WriteFlusher.State __WRITING = new WriteFlusher.WritingState();
   private static final WriteFlusher.State __COMPLETING = new WriteFlusher.CompletingState();
   private final EndPoint _endPoint;
   private final AtomicReference<WriteFlusher.State> _state = new AtomicReference<>();

   protected WriteFlusher(EndPoint endPoint) {
      this._state.set(__IDLE);
      this._endPoint = endPoint;
   }

   private boolean updateState(WriteFlusher.State previous, WriteFlusher.State next) {
      if (!this.isTransitionAllowed(previous, next)) {
         throw new IllegalStateException();
      } else {
         boolean updated = this._state.compareAndSet(previous, next);
         if (DEBUG) {
            LOG.debug("update {}:{}{}{}", new Object[]{this, previous, updated ? "-->" : "!->", next});
         }

         return updated;
      }
   }

   private boolean isTransitionAllowed(WriteFlusher.State currentState, WriteFlusher.State newState) {
      Set<WriteFlusher.StateType> allowedNewStateTypes = __stateTransitions.get(currentState.getType());
      if (!allowedNewStateTypes.contains(newState.getType())) {
         LOG.warn("{}: {} -> {} not allowed", new Object[]{this, currentState, newState});
         return false;
      } else {
         return true;
      }
   }

   public InvocationType getCallbackInvocationType() {
      WriteFlusher.State s = this._state.get();
      return s instanceof WriteFlusher.PendingState ? ((WriteFlusher.PendingState)s).getCallbackInvocationType() : InvocationType.BLOCKING;
   }

   protected abstract void onIncompleteFlush();

   public void write(Callback callback, ByteBuffer... buffers) throws WritePendingException {
      this.write(callback, null, buffers);
   }

   public void write(Callback callback, SocketAddress address, ByteBuffer... buffers) throws WritePendingException {
      Objects.requireNonNull(callback);
      if (this.isFailed()) {
         this.fail(callback);
      } else {
         if (DEBUG) {
            LOG.debug("write: {} {}", this, BufferUtil.toDetailString(buffers));
         }

         if (!this.updateState(__IDLE, __WRITING)) {
            throw new WritePendingException();
         } else {
            try {
               buffers = this.flush(address, buffers);
               if (buffers != null) {
                  if (DEBUG) {
                     LOG.debug("flush incomplete {}", this);
                  }

                  WriteFlusher.PendingState pending = new WriteFlusher.PendingState(callback, address, buffers);
                  if (this.updateState(__WRITING, pending)) {
                     this.onIncompleteFlush();
                  } else {
                     this.fail(callback);
                  }

                  return;
               }

               if (this.updateState(__WRITING, __IDLE)) {
                  callback.succeeded();
               } else {
                  this.fail(callback);
               }
            } catch (Throwable var5) {
               if (DEBUG) {
                  LOG.debug("write exception", var5);
               }

               if (this.updateState(__WRITING, new WriteFlusher.FailedState(var5))) {
                  callback.failed(var5);
               } else {
                  this.fail(callback, var5);
               }
            }
         }
      }
   }

   private void fail(Callback callback, Throwable... suppressed) {
      while (true) {
         WriteFlusher.State state = this._state.get();
         Throwable cause;
         switch (state.getType()) {
            case IDLE:
               for (Throwable t : suppressed) {
                  LOG.warn("Failed Write Cause", t);
               }

               return;
            case FAILED:
               WriteFlusher.FailedState failed = (WriteFlusher.FailedState)state;
               cause = failed.getCause();
               break;
            default:
               Throwable t = new IllegalStateException();
               if (!this._state.compareAndSet(state, new WriteFlusher.FailedState(t))) {
                  continue;
               }

               cause = t;
         }

         for (Throwable t : suppressed) {
            if (t != cause) {
               cause.addSuppressed(t);
            }
         }

         callback.failed(cause);
         return;
      }
   }

   public void completeWrite() {
      if (DEBUG) {
         LOG.debug("completeWrite: {}", this);
      }

      WriteFlusher.State previous = this._state.get();
      if (previous.getType() == WriteFlusher.StateType.PENDING) {
         WriteFlusher.PendingState pending = (WriteFlusher.PendingState)previous;
         if (this.updateState(pending, __COMPLETING)) {
            Callback callback = pending._callback;

            try {
               ByteBuffer[] buffers = pending._buffers;
               SocketAddress address = pending._address;
               buffers = this.flush(address, buffers);
               if (buffers != null) {
                  if (DEBUG) {
                     LOG.debug("flushed incomplete {}", BufferUtil.toDetailString(buffers));
                  }

                  if (buffers != pending._buffers) {
                     pending = new WriteFlusher.PendingState(callback, address, buffers);
                  }

                  if (this.updateState(__COMPLETING, pending)) {
                     this.onIncompleteFlush();
                  } else {
                     this.fail(callback);
                  }

                  return;
               }

               if (this.updateState(__COMPLETING, __IDLE)) {
                  callback.succeeded();
               } else {
                  this.fail(callback);
               }
            } catch (Throwable var6) {
               if (DEBUG) {
                  LOG.debug("completeWrite exception", var6);
               }

               if (this.updateState(__COMPLETING, new WriteFlusher.FailedState(var6))) {
                  callback.failed(var6);
               } else {
                  this.fail(callback, var6);
               }
            }
         }
      }
   }

   protected ByteBuffer[] flush(SocketAddress address, ByteBuffer[] buffers) throws IOException {
      boolean progress = true;

      while (progress && buffers != null) {
         long before = BufferUtil.remaining(buffers);
         boolean flushed = address == null ? this._endPoint.flush(buffers) : ((DatagramChannelEndPoint)this._endPoint).send(address, buffers);
         long after = BufferUtil.remaining(buffers);
         long written = before - after;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Flushed={} written={} remaining={} {}", new Object[]{flushed, written, after, this});
         }

         if (written > 0L) {
            Connection connection = this._endPoint.getConnection();
            if (connection instanceof WriteFlusher.Listener) {
               ((WriteFlusher.Listener)connection).onFlushed(written);
            }
         }

         if (flushed) {
            return null;
         }

         progress = written > 0L;
         int index = 0;

         while (true) {
            if (index == buffers.length) {
               buffers = null;
               index = 0;
            } else {
               int remaining = buffers[index].remaining();
               if (remaining <= 0) {
                  index++;
                  progress = true;
                  continue;
               }
            }

            if (index > 0) {
               buffers = Arrays.copyOfRange(buffers, index, buffers.length);
            }
            break;
         }
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("!fully flushed {}", this);
      }

      return buffers == null ? EMPTY_BUFFERS : buffers;
   }

   public boolean onFail(Throwable cause) {
      while (true) {
         WriteFlusher.State current = this._state.get();
         switch (current.getType()) {
            case IDLE:
            case FAILED:
               if (DEBUG) {
                  LOG.debug("ignored: {} {}", cause, this);
                  LOG.trace("IGNORED", cause);
               }

               return false;
            case WRITING:
            case COMPLETING:
               if (DEBUG) {
                  LOG.debug("failed: {}", this, cause);
               }

               if (!this.updateState(current, new WriteFlusher.FailedState(cause))) {
                  break;
               }

               return true;
            case PENDING:
               if (DEBUG) {
                  LOG.debug("failed: {}", this, cause);
               }

               WriteFlusher.PendingState pending = (WriteFlusher.PendingState)current;
               if (!this.updateState(pending, new WriteFlusher.FailedState(cause))) {
                  break;
               }

               pending._callback.failed(cause);
               return true;
            default:
               throw new IllegalStateException();
         }
      }
   }

   public void onClose() {
      switch (this._state.get().getType()) {
         case IDLE:
         case FAILED:
            return;
         default:
            this.onFail(new ClosedChannelException());
      }
   }

   boolean isFailed() {
      return this.isState(WriteFlusher.StateType.FAILED);
   }

   boolean isIdle() {
      return this.isState(WriteFlusher.StateType.IDLE);
   }

   public boolean isPending() {
      return this.isState(WriteFlusher.StateType.PENDING);
   }

   private boolean isState(WriteFlusher.StateType type) {
      return this._state.get().getType() == type;
   }

   public String toStateString() {
      switch (this._state.get().getType()) {
         case IDLE:
            return "-";
         case WRITING:
            return "W";
         case PENDING:
            return "P";
         case COMPLETING:
            return "C";
         case FAILED:
            return "F";
         default:
            return "?";
      }
   }

   @Override
   public String toString() {
      WriteFlusher.State s = this._state.get();
      return String.format(
         "WriteFlusher@%x{%s}->%s", this.hashCode(), s, s instanceof WriteFlusher.PendingState ? ((WriteFlusher.PendingState)s)._callback : null
      );
   }

   static {
      __stateTransitions.put(WriteFlusher.StateType.IDLE, EnumSet.of(WriteFlusher.StateType.WRITING));
      __stateTransitions.put(
         WriteFlusher.StateType.WRITING, EnumSet.of(WriteFlusher.StateType.IDLE, WriteFlusher.StateType.PENDING, WriteFlusher.StateType.FAILED)
      );
      __stateTransitions.put(
         WriteFlusher.StateType.PENDING, EnumSet.of(WriteFlusher.StateType.COMPLETING, WriteFlusher.StateType.IDLE, WriteFlusher.StateType.FAILED)
      );
      __stateTransitions.put(
         WriteFlusher.StateType.COMPLETING, EnumSet.of(WriteFlusher.StateType.IDLE, WriteFlusher.StateType.PENDING, WriteFlusher.StateType.FAILED)
      );
      __stateTransitions.put(WriteFlusher.StateType.FAILED, EnumSet.noneOf(WriteFlusher.StateType.class));
   }

   private static class CompletingState extends WriteFlusher.State {
      private CompletingState() {
         super(WriteFlusher.StateType.COMPLETING);
      }
   }

   private static class FailedState extends WriteFlusher.State {
      private final Throwable _cause;

      private FailedState(Throwable cause) {
         super(WriteFlusher.StateType.FAILED);
         this._cause = cause;
      }

      public Throwable getCause() {
         return this._cause;
      }
   }

   private static class IdleState extends WriteFlusher.State {
      private IdleState() {
         super(WriteFlusher.StateType.IDLE);
      }
   }

   public interface Listener {
      void onFlushed(long var1) throws IOException;
   }

   private class PendingState extends WriteFlusher.State {
      private final Callback _callback;
      private final SocketAddress _address;
      private final ByteBuffer[] _buffers;

      private PendingState(Callback callback, SocketAddress address, ByteBuffer[] buffers) {
         super(WriteFlusher.StateType.PENDING);
         this._callback = callback;
         this._address = address;
         this._buffers = buffers;
      }

      InvocationType getCallbackInvocationType() {
         return Invocable.getInvocationType(this._callback);
      }
   }

   private static class State {
      private final WriteFlusher.StateType _type;

      private State(WriteFlusher.StateType stateType) {
         this._type = stateType;
      }

      public WriteFlusher.StateType getType() {
         return this._type;
      }

      @Override
      public String toString() {
         return String.format("%s", this._type);
      }
   }

   private static enum StateType {
      IDLE,
      WRITING,
      PENDING,
      COMPLETING,
      FAILED;
   }

   private static class WritingState extends WriteFlusher.State {
      private WritingState() {
         super(WriteFlusher.StateType.WRITING);
      }
   }
}
