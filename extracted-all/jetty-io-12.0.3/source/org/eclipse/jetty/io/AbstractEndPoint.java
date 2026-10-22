package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEndPoint extends IdleTimeout implements EndPoint {
   private static final Logger LOG = LoggerFactory.getLogger(AbstractEndPoint.class);
   private final AtomicReference<AbstractEndPoint.State> _state = new AtomicReference<>(AbstractEndPoint.State.OPEN);
   private final long _created = System.currentTimeMillis();
   private volatile Connection _connection;
   private final FillInterest _fillInterest = new FillInterest() {
      @Override
      protected void needsFillInterest() throws IOException {
         AbstractEndPoint.this.needsFillInterest();
      }
   };
   private final WriteFlusher _writeFlusher = new WriteFlusher(this) {
      @Override
      protected void onIncompleteFlush() {
         AbstractEndPoint.this.onIncompleteFlush();
      }
   };

   protected AbstractEndPoint(Scheduler scheduler) {
      super(scheduler);
   }

   @Override
   public InetSocketAddress getLocalAddress() {
      SocketAddress local = this.getLocalSocketAddress();
      return local instanceof InetSocketAddress ? (InetSocketAddress)local : null;
   }

   @Override
   public SocketAddress getLocalSocketAddress() {
      return null;
   }

   @Override
   public InetSocketAddress getRemoteAddress() {
      SocketAddress remote = this.getRemoteSocketAddress();
      return remote instanceof InetSocketAddress ? (InetSocketAddress)remote : null;
   }

   @Override
   public SocketAddress getRemoteSocketAddress() {
      return null;
   }

   protected final void shutdownInput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("shutdownInput {}", this);
      }

      while (true) {
         AbstractEndPoint.State s = this._state.get();
         switch (s) {
            case OPEN:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.ISHUTTING)) {
                  try {
                     this.doShutdownInput();
                  } finally {
                     if (!this._state.compareAndSet(AbstractEndPoint.State.ISHUTTING, AbstractEndPoint.State.ISHUT)
                        && this._state.get() == AbstractEndPoint.State.CLOSED) {
                        this.doOnClose(null);
                     }
                  }

                  return;
               }
               break;
            case ISHUTTING:
            case ISHUT:
               return;
            case OSHUTTING:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  return;
               }
               break;
            case OSHUT:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  this.doOnClose(null);
                  return;
               }
               break;
            case CLOSED:
               return;
            default:
               throw new IllegalStateException(s.toString());
         }
      }
   }

   @Override
   public final void shutdownOutput() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("shutdownOutput {}", this);
      }

      while (true) {
         AbstractEndPoint.State s = this._state.get();
         switch (s) {
            case OPEN:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.OSHUTTING)) {
                  try {
                     this.doShutdownOutput();
                  } finally {
                     if (!this._state.compareAndSet(AbstractEndPoint.State.OSHUTTING, AbstractEndPoint.State.OSHUT)
                        && this._state.get() == AbstractEndPoint.State.CLOSED) {
                        this.doOnClose(null);
                     }
                  }

                  return;
               }
               break;
            case ISHUTTING:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  return;
               }
               break;
            case ISHUT:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  this.doOnClose(null);
                  return;
               }
               break;
            case OSHUTTING:
            case OSHUT:
               return;
            case CLOSED:
               return;
            default:
               throw new IllegalStateException(s.toString());
         }
      }
   }

   @Override
   public final void close() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("close {}", this);
      }

      this.close(null);
   }

   @Override
   public final void close(Throwable failure) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("close({}) {}", failure, this);
      }

      while (true) {
         AbstractEndPoint.State s = this._state.get();
         switch (s) {
            case OPEN:
            case ISHUT:
            case OSHUT:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  this.doOnClose(failure);
                  return;
               }
               break;
            case ISHUTTING:
            case OSHUTTING:
               if (this._state.compareAndSet(s, AbstractEndPoint.State.CLOSED)) {
                  return;
               }
               break;
            case CLOSED:
               return;
            default:
               throw new IllegalStateException(s.toString());
         }
      }
   }

   protected void doShutdownInput() {
   }

   protected void doShutdownOutput() {
   }

   private void doOnClose(Throwable failure) {
      try {
         this.doClose();
      } finally {
         if (failure == null) {
            this.onClose();
         } else {
            this.onClose(failure);
         }
      }
   }

   protected void doClose() {
   }

   @Override
   public boolean isOutputShutdown() {
      return switch ((AbstractEndPoint.State)this._state.get()) {
         case OSHUTTING, OSHUT, CLOSED -> true;
         default -> false;
      };
   }

   @Override
   public boolean isInputShutdown() {
      return switch ((AbstractEndPoint.State)this._state.get()) {
         case ISHUTTING, ISHUT, CLOSED -> true;
         default -> false;
      };
   }

   @Override
   public boolean isOpen() {
      return this._state.get() != AbstractEndPoint.State.CLOSED;
   }

   @Override
   public long getCreatedTimeStamp() {
      return this._created;
   }

   @Override
   public Connection getConnection() {
      return this._connection;
   }

   @Override
   public void setConnection(Connection connection) {
      this._connection = connection;
   }

   protected void reset() {
      this._state.set(AbstractEndPoint.State.OPEN);
      this._writeFlusher.onClose();
      this._fillInterest.onClose();
   }

   @Override
   public void onOpen() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onOpen {}", this);
      }

      if (this._state.get() != AbstractEndPoint.State.OPEN) {
         throw new IllegalStateException();
      }
   }

   @Override
   public final void onClose() {
      this.onClose(null);
   }

   @Override
   public void onClose(Throwable failure) {
      super.onClose();
      if (failure == null) {
         this._writeFlusher.onClose();
         this._fillInterest.onClose();
      } else {
         this._writeFlusher.onFail(failure);
         this._fillInterest.onFail(failure);
      }
   }

   @Override
   public void fillInterested(Callback callback) {
      this.notIdle();
      this._fillInterest.register(callback);
   }

   @Override
   public boolean tryFillInterested(Callback callback) {
      this.notIdle();
      return this._fillInterest.tryRegister(callback);
   }

   @Override
   public boolean isFillInterested() {
      return this._fillInterest.isInterested();
   }

   @Override
   public void write(Callback callback, ByteBuffer... buffers) throws WritePendingException {
      this._writeFlusher.write(callback, buffers);
   }

   protected abstract void onIncompleteFlush();

   protected abstract void needsFillInterest() throws IOException;

   public FillInterest getFillInterest() {
      return this._fillInterest;
   }

   public WriteFlusher getWriteFlusher() {
      return this._writeFlusher;
   }

   @Override
   protected void onIdleExpired(TimeoutException timeout) {
      Connection connection = this._connection;
      if (connection == null || connection.onIdleExpired(timeout)) {
         boolean outputShutdown = this.isOutputShutdown();
         boolean inputShutdown = this.isInputShutdown();
         boolean fillFailed = this._fillInterest.onFail(timeout);
         boolean writeFailed = this._writeFlusher.onFail(timeout);
         boolean isOpen = this.isOpen();
         if (LOG.isDebugEnabled()) {
            LOG.debug(
               "handled idle isOpen={} inputShutdown={} outputShutdown={} fillFailed={} writeFailed={} for {}",
               new Object[]{isOpen, inputShutdown, outputShutdown, fillFailed, writeFailed, this}
            );
         }

         if (isOpen && !fillFailed && !writeFailed) {
            this.close(timeout);
         }
      }
   }

   @Override
   public void upgrade(Connection newConnection) {
      Connection oldConnection = this.getConnection();
      ByteBuffer buffer = oldConnection instanceof Connection.UpgradeFrom ? ((Connection.UpgradeFrom)oldConnection).onUpgradeFrom() : null;
      oldConnection.onClose(null);
      oldConnection.getEndPoint().setConnection(newConnection);
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} upgrading from {} to {} with {}", new Object[]{this, oldConnection, newConnection, BufferUtil.toDetailString(buffer)});
      }

      if (BufferUtil.hasContent(buffer)) {
         if (!(newConnection instanceof Connection.UpgradeTo)) {
            throw new IllegalStateException("Cannot upgrade: " + newConnection + " does not implement " + Connection.UpgradeTo.class.getName());
         }

         ((Connection.UpgradeTo)newConnection).onUpgradeTo(buffer);
      }

      newConnection.onOpen();
   }

   @Override
   public String toString() {
      return String.format("%s@%x[%s]->[%s]", this.getClass().getSimpleName(), this.hashCode(), this.toEndPointString(), this.toConnectionString());
   }

   public String toEndPointString() {
      return String.format(
         "{l=%s,r=%s,%s,fill=%s,flush=%s,to=%d/%d}",
         this.getLocalSocketAddress(),
         this.getRemoteSocketAddress(),
         this._state.get(),
         this._fillInterest.toStateString(),
         this._writeFlusher.toStateString(),
         this.getIdleFor(),
         this.getIdleTimeout()
      );
   }

   public String toConnectionString() {
      Connection connection = this.getConnection();
      if (connection == null) {
         return "<null>";
      } else {
         return connection instanceof AbstractConnection
            ? ((AbstractConnection)connection).toConnectionString()
            : String.format("%s@%x", connection.getClass().getSimpleName(), connection.hashCode());
      }
   }

   private static enum State {
      OPEN,
      ISHUTTING,
      ISHUT,
      OSHUTTING,
      OSHUT,
      CLOSED;
   }
}
