package org.eclipse.jetty.io;

import java.io.Closeable;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.NetworkChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.eclipse.jetty.util.thread.Invocable.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SelectableChannelEndPoint extends AbstractEndPoint implements ManagedSelector.Selectable {
   private static final Logger LOG = LoggerFactory.getLogger(SelectableChannelEndPoint.class);
   private final AutoLock _lock = new AutoLock();
   private final SelectableChannel _channel;
   private final ManagedSelector _selector;
   private SelectionKey _key;
   private boolean _updatePending;
   private int _currentInterestOps;
   private int _desiredInterestOps;
   private final ManagedSelector.SelectorUpdate _updateKeyAction = this::updateKeyAction;
   private final Runnable _runFillable = new SelectableChannelEndPoint.RunnableCloseable("runFillable") {
      public void run() {
         SelectableChannelEndPoint.this.getFillInterest().fillable();
      }

      public InvocationType getInvocationType() {
         return SelectableChannelEndPoint.this.getFillInterest().getCallbackInvocationType();
      }
   };
   private final Runnable _runCompleteWrite = new SelectableChannelEndPoint.RunnableCloseable("runCompleteWrite") {
      public void run() {
         SelectableChannelEndPoint.this.getWriteFlusher().completeWrite();
      }

      public InvocationType getInvocationType() {
         return SelectableChannelEndPoint.this.getWriteFlusher().getCallbackInvocationType();
      }

      @Override
      public String toString() {
         return String.format(
            "%s:%s:%s->%s", SelectableChannelEndPoint.this, this._operation, this.getInvocationType(), SelectableChannelEndPoint.this.getWriteFlusher()
         );
      }
   };
   private final Runnable _runCompleteWriteFillable = new SelectableChannelEndPoint.RunnableCloseable("runCompleteWriteFillable") {
      public void run() {
         SelectableChannelEndPoint.this.getWriteFlusher().completeWrite();
         SelectableChannelEndPoint.this.getFillInterest().fillable();
      }

      public InvocationType getInvocationType() {
         InvocationType fillT = SelectableChannelEndPoint.this.getFillInterest().getCallbackInvocationType();
         InvocationType flushT = SelectableChannelEndPoint.this.getWriteFlusher().getCallbackInvocationType();
         if (fillT == flushT) {
            return fillT;
         } else if (fillT == InvocationType.EITHER && flushT == InvocationType.NON_BLOCKING) {
            return InvocationType.EITHER;
         } else {
            return fillT == InvocationType.NON_BLOCKING && flushT == InvocationType.EITHER ? InvocationType.EITHER : InvocationType.BLOCKING;
         }
      }
   };

   public SelectableChannelEndPoint(Scheduler scheduler, SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) {
      super(scheduler);
      this._channel = channel;
      this._selector = selector;
      this._key = selectionKey;
   }

   public SelectableChannel getChannel() {
      return this._channel;
   }

   @Override
   public Object getTransport() {
      return this.getChannel();
   }

   @Override
   public SocketAddress getLocalSocketAddress() {
      try {
         SelectableChannel channel = this.getChannel();
         return channel instanceof NetworkChannel ? ((NetworkChannel)channel).getLocalAddress() : super.getLocalSocketAddress();
      } catch (Throwable var2) {
         LOG.trace("Could not retrieve local socket address", var2);
         return null;
      }
   }

   @Override
   public boolean isOpen() {
      return this._channel.isOpen();
   }

   @Override
   public void doClose() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("doClose {}", this);
      }

      IO.close(this._channel);
      super.doClose();
   }

   @Override
   public void onClose(Throwable cause) {
      try {
         super.onClose(cause);
      } finally {
         if (this._selector != null) {
            this._selector.destroyEndPoint(this, cause);
         }
      }
   }

   @Override
   protected void needsFillInterest() {
      this.changeInterests(1);
   }

   @Override
   protected void onIncompleteFlush() {
      this.changeInterests(4);
   }

   private void changeInterests(int operation) {
      AutoLock l = this._lock.lock();

      int oldInterestOps;
      int newInterestOps;
      boolean pending;
      try {
         pending = this._updatePending;
         oldInterestOps = this._desiredInterestOps;
         newInterestOps = oldInterestOps | operation;
         if (newInterestOps != oldInterestOps) {
            this._desiredInterestOps = newInterestOps;
         }
      } catch (Throwable var9) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (l != null) {
         l.close();
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("changeInterests p={} {}->{} for {}", new Object[]{pending, oldInterestOps, newInterestOps, this});
      }

      if (!pending && this._selector != null) {
         this._selector.submit(this._updateKeyAction);
      }
   }

   @Override
   public Runnable onSelected() {
      int readyOps = this._key.readyOps();
      AutoLock l = this._lock.lock();

      int oldInterestOps;
      int newInterestOps;
      try {
         this._updatePending = true;
         oldInterestOps = this._desiredInterestOps;
         newInterestOps = oldInterestOps & ~readyOps;
         this._desiredInterestOps = newInterestOps;
      } catch (Throwable var8) {
         if (l != null) {
            try {
               l.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (l != null) {
         l.close();
      }

      boolean fillable = (readyOps & 1) != 0;
      boolean flushable = (readyOps & 4) != 0;
      if (LOG.isDebugEnabled()) {
         LOG.debug("onSelected {}->{} r={} w={} for {}", new Object[]{oldInterestOps, newInterestOps, fillable, flushable, this});
      }

      Runnable task = fillable ? (flushable ? this._runCompleteWriteFillable : this._runFillable) : (flushable ? this._runCompleteWrite : null);
      if (LOG.isDebugEnabled()) {
         LOG.debug("task {}", task);
      }

      return task;
   }

   private void updateKeyAction(Selector selector) {
      this.updateKey();
   }

   @Override
   public void updateKey() {
      try {
         AutoLock l = this._lock.lock();

         int oldInterestOps;
         int newInterestOps;
         try {
            this._updatePending = false;
            oldInterestOps = this._currentInterestOps;
            newInterestOps = this._desiredInterestOps;
            if (oldInterestOps != newInterestOps) {
               this._currentInterestOps = newInterestOps;
               this._key.interestOps(newInterestOps);
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

         if (LOG.isDebugEnabled()) {
            LOG.debug("Key interests updated {} -> {} on {}", new Object[]{oldInterestOps, newInterestOps, this});
         }
      } catch (CancelledKeyException var8) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Ignoring key update for cancelled key {}", this, var8);
         }

         this.close();
      } catch (Throwable var9) {
         LOG.warn("Ignoring key update for {}", this, var9);
         this.close();
      }
   }

   @Override
   public void replaceKey(SelectionKey newKey) {
      this._key = newKey;
   }

   @Override
   public String toEndPointString() {
      return String.format(
         "%s{io=%d/%d,kio=%d,kro=%d}",
         super.toEndPointString(),
         this._currentInterestOps,
         this._desiredInterestOps,
         ManagedSelector.safeInterestOps(this._key),
         ManagedSelector.safeReadyOps(this._key)
      );
   }

   private abstract class RunnableCloseable implements Task, Closeable {
      final String _operation;

      private RunnableCloseable(String operation) {
         this._operation = operation;
      }

      @Override
      public void close() {
         try {
            SelectableChannelEndPoint.this.close();
         } catch (Throwable var2) {
            SelectableChannelEndPoint.LOG.warn("Unable to close {}", SelectableChannelEndPoint.this, var2);
         }
      }

      @Override
      public String toString() {
         return String.format("%s:%s:%s", SelectableChannelEndPoint.this, this._operation, this.getInvocationType());
      }
   }
}
