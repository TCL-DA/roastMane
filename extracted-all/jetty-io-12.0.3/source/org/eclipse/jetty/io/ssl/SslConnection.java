package org.eclipse.jetty.io.ssl;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.ToIntFunction;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.AbstractEndPoint;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.WriteFlusher;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.eclipse.jetty.util.thread.Invocable.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SslConnection extends AbstractConnection implements Connection.UpgradeTo {
   private static final Logger LOG = LoggerFactory.getLogger(SslConnection.class);
   private static final String TLS_1_3 = "TLSv1.3";
   private final AutoLock _lock = new AutoLock();
   private final AtomicReference<SslConnection.HandshakeState> _handshake = new AtomicReference<>(SslConnection.HandshakeState.INITIAL);
   private final List<SslHandshakeListener> handshakeListeners = new ArrayList<>();
   private final AtomicLong _bytesIn = new AtomicLong();
   private final AtomicLong _bytesOut = new AtomicLong();
   private final ByteBufferPool _bufferPool;
   private final SSLEngine _sslEngine;
   private final SslConnection.SslEndPoint _sslEndPoint;
   private final boolean _encryptedDirectBuffers;
   private final boolean _decryptedDirectBuffers;
   private RetainableByteBuffer _decryptedInput;
   private RetainableByteBuffer _encryptedInput;
   private RetainableByteBuffer _encryptedOutput;
   private boolean _renegotiationAllowed;
   private int _renegotiationLimit = -1;
   private boolean _closedOutbound;
   private boolean _requireCloseMessage;
   private SslConnection.FlushState _flushState = SslConnection.FlushState.IDLE;
   private SslConnection.FillState _fillState = SslConnection.FillState.IDLE;
   private boolean _underflown;
   private final Runnable _runFillable = new SslConnection.RunnableTask("runFillable") {
      public void run() {
         SslConnection.this._sslEndPoint.getFillInterest().fillable();
      }

      public InvocationType getInvocationType() {
         return SslConnection.this._sslEndPoint.getFillInterest().getCallbackInvocationType();
      }
   };
   private final Callback _sslReadCallback = new Callback() {
      public void succeeded() {
         SslConnection.this.onFillable();
      }

      public void failed(Throwable x) {
         SslConnection.this.onFillInterestedFailed(x);
      }

      public InvocationType getInvocationType() {
         return SslConnection.this.getSslEndPoint().getFillInterest().getCallbackInvocationType();
      }

      @Override
      public String toString() {
         return String.format("SSLC.NBReadCB@%x{%s}", SslConnection.this.hashCode(), SslConnection.this);
      }
   };

   public SslConnection(ByteBufferPool byteBufferPool, Executor executor, EndPoint endPoint, SSLEngine sslEngine) {
      this(byteBufferPool, executor, endPoint, sslEngine, false, false);
   }

   public SslConnection(
      ByteBufferPool byteBufferPool,
      Executor executor,
      EndPoint endPoint,
      SSLEngine sslEngine,
      boolean useDirectBuffersForEncryption,
      boolean useDirectBuffersForDecryption
   ) {
      super(endPoint, executor);
      this._bufferPool = byteBufferPool;
      this._sslEngine = sslEngine;
      this._sslEndPoint = this.newSslEndPoint();
      this._encryptedDirectBuffers = useDirectBuffersForEncryption;
      this._decryptedDirectBuffers = useDirectBuffersForDecryption;
   }

   @Override
   public long getBytesIn() {
      return this._bytesIn.get();
   }

   @Override
   public long getBytesOut() {
      return this._bytesOut.get();
   }

   public void addHandshakeListener(SslHandshakeListener listener) {
      this.handshakeListeners.add(listener);
   }

   public boolean removeHandshakeListener(SslHandshakeListener listener) {
      return this.handshakeListeners.remove(listener);
   }

   protected SslConnection.SslEndPoint newSslEndPoint() {
      return new SslConnection.SslEndPoint();
   }

   public SSLEngine getSSLEngine() {
      return this._sslEngine;
   }

   public SslConnection.SslEndPoint getSslEndPoint() {
      return this._sslEndPoint;
   }

   public boolean isRenegotiationAllowed() {
      return this._renegotiationAllowed;
   }

   public void setRenegotiationAllowed(boolean renegotiationAllowed) {
      this._renegotiationAllowed = renegotiationAllowed;
   }

   public int getRenegotiationLimit() {
      return this._renegotiationLimit;
   }

   public void setRenegotiationLimit(int renegotiationLimit) {
      this._renegotiationLimit = renegotiationLimit;
   }

   public boolean isRequireCloseMessage() {
      return this._requireCloseMessage;
   }

   public void setRequireCloseMessage(boolean requireCloseMessage) {
      this._requireCloseMessage = requireCloseMessage;
   }

   private boolean isHandshakeInitial() {
      return this._handshake.get() == SslConnection.HandshakeState.INITIAL;
   }

   private boolean isHandshakeSucceeded() {
      return this._handshake.get() == SslConnection.HandshakeState.SUCCEEDED;
   }

   private boolean isHandshakeComplete() {
      SslConnection.HandshakeState state = this._handshake.get();
      return state == SslConnection.HandshakeState.SUCCEEDED || state == SslConnection.HandshakeState.FAILED;
   }

   private int getApplicationBufferSize() {
      return this.getBufferSize(SSLSession::getApplicationBufferSize);
   }

   private int getPacketBufferSize() {
      return this.getBufferSize(SSLSession::getPacketBufferSize);
   }

   private int getBufferSize(ToIntFunction<SSLSession> bufferSizeFn) {
      SSLSession hsSession = this._sslEngine.getHandshakeSession();
      SSLSession session = this._sslEngine.getSession();
      int size = bufferSizeFn.applyAsInt(session);
      if (hsSession != null && hsSession != session) {
         int hsSize = bufferSizeFn.applyAsInt(hsSession);
         return Math.max(hsSize, size);
      } else {
         return size;
      }
   }

   private void acquireEncryptedInput() {
      if (this._encryptedInput == null) {
         this._encryptedInput = this._bufferPool.acquire(this.getPacketBufferSize(), this._encryptedDirectBuffers);
      }
   }

   private void acquireEncryptedOutput() {
      if (this._encryptedOutput == null) {
         this._encryptedOutput = this._bufferPool.acquire(this.getPacketBufferSize(), this._encryptedDirectBuffers);
      }
   }

   @Override
   public void onUpgradeTo(ByteBuffer buffer) {
      this.acquireEncryptedInput();
      BufferUtil.append(this._encryptedInput.getByteBuffer(), buffer);
   }

   @Override
   public void onOpen() {
      super.onOpen();
      this.getSslEndPoint().getConnection().onOpen();
   }

   @Override
   public void onClose(Throwable cause) {
      this.getSslEndPoint().getConnection().onClose(cause);
      super.onClose(cause);
   }

   @Override
   public void close() {
      this.getSslEndPoint().getConnection().close();
   }

   @Override
   public boolean onIdleExpired(TimeoutException timeoutException) {
      return this.getSslEndPoint().getConnection().onIdleExpired(timeoutException);
   }

   @Override
   public void onFillable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug(">c.onFillable {}", this);
      }

      if (this._sslEndPoint.isInputShutdown()) {
         this._sslEndPoint.close();
      }

      this._sslEndPoint.onFillable();
      if (LOG.isDebugEnabled()) {
         LOG.debug("<c.onFillable {}", this);
      }
   }

   @Override
   public void onFillInterestedFailed(Throwable cause) {
      this._sslEndPoint.onFillableFail((Throwable)(cause == null ? new IOException() : cause));
   }

   protected SSLEngineResult wrap(SSLEngine sslEngine, ByteBuffer[] input, ByteBuffer output) throws SSLException {
      return sslEngine.wrap(input, output);
   }

   protected SSLEngineResult unwrap(SSLEngine sslEngine, ByteBuffer input, ByteBuffer output) throws SSLException {
      return sslEngine.unwrap(input, output);
   }

   @Override
   public String toConnectionString() {
      ByteBuffer b = this._encryptedInput == null ? null : this._encryptedInput.getByteBuffer();
      int ei = b == null ? -1 : b.remaining();
      b = this._encryptedOutput == null ? null : this._encryptedOutput.getByteBuffer();
      int eo = b == null ? -1 : b.remaining();
      b = this._decryptedInput == null ? null : this._decryptedInput.getByteBuffer();
      int di = b == null ? -1 : b.remaining();
      Connection connection = this._sslEndPoint.getConnection();
      return String.format(
         "%s@%x{%s,eio=%d/%d,di=%d,fill=%s,flush=%s}~>%s=>%s",
         this.getClass().getSimpleName(),
         this.hashCode(),
         this._sslEngine.getHandshakeStatus(),
         ei,
         eo,
         di,
         this._fillState,
         this._flushState,
         this._sslEndPoint.toEndPointString(),
         connection instanceof AbstractConnection ? ((AbstractConnection)connection).toConnectionString() : connection
      );
   }

   private void releaseEmptyEncryptedInputBuffer() {
      if (!this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException();
      } else {
         if (this._encryptedInput != null && !this._encryptedInput.hasRemaining()) {
            this._encryptedInput.release();
            this._encryptedInput = null;
         }
      }
   }

   private void releaseEmptyDecryptedInputBuffer() {
      if (!this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException();
      } else {
         if (this._decryptedInput != null && !this._decryptedInput.hasRemaining()) {
            this._decryptedInput.release();
            this._decryptedInput = null;
         }
      }
   }

   private void discardInputBuffers() {
      if (!this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException();
      } else {
         if (this._encryptedInput != null) {
            this._encryptedInput.clear();
         }

         if (this._decryptedInput != null) {
            this._decryptedInput.clear();
         }

         this.releaseEmptyInputBuffers();
      }
   }

   private void releaseEmptyInputBuffers() {
      this.releaseEmptyEncryptedInputBuffer();
      this.releaseEmptyDecryptedInputBuffer();
   }

   private void discardEncryptedOutputBuffer() {
      if (!this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException();
      } else {
         if (this._encryptedOutput != null) {
            this._encryptedOutput.clear();
         }

         this.releaseEmptyEncryptedOutputBuffer();
      }
   }

   private void releaseEmptyEncryptedOutputBuffer() {
      if (!this._lock.isHeldByCurrentThread()) {
         throw new IllegalStateException();
      } else {
         if (this._encryptedOutput != null && !this._encryptedOutput.hasRemaining()) {
            this._encryptedOutput.release();
            this._encryptedOutput = null;
         }
      }
   }

   protected int networkFill(ByteBuffer input) throws IOException {
      return this.getEndPoint().fill(input);
   }

   protected boolean networkFlush(ByteBuffer output) throws IOException {
      return this.getEndPoint().flush(output);
   }

   private static enum FillState {
      IDLE,
      INTERESTED,
      WAIT_FOR_FLUSH;
   }

   private static enum FlushState {
      IDLE,
      WRITING,
      WAIT_FOR_FILL;
   }

   private static enum HandshakeState {
      INITIAL,
      HANDSHAKE,
      SUCCEEDED,
      FAILED;
   }

   private abstract class RunnableTask implements Task {
      private final String _operation;

      protected RunnableTask(String op) {
         this._operation = op;
      }

      @Override
      public String toString() {
         return String.format("SSL:%s:%s:%s", SslConnection.this, this._operation, this.getInvocationType());
      }
   }

   public class SslEndPoint extends AbstractEndPoint implements EndPoint.Wrapper {
      private final Callback _incompleteWriteCallback = new SslConnection.SslEndPoint.IncompleteWriteCallback();
      private Throwable _failure;

      public SslEndPoint() {
         super(null);
         super.setIdleTimeout(-1L);
      }

      @Override
      public EndPoint unwrap() {
         return SslConnection.this.getEndPoint();
      }

      @Override
      public long getIdleTimeout() {
         return SslConnection.this.getEndPoint().getIdleTimeout();
      }

      @Override
      public void setIdleTimeout(long idleTimeout) {
         SslConnection.this.getEndPoint().setIdleTimeout(idleTimeout);
      }

      @Override
      public boolean isOpen() {
         return SslConnection.this.getEndPoint().isOpen();
      }

      @Override
      public SocketAddress getLocalSocketAddress() {
         return SslConnection.this.getEndPoint().getLocalSocketAddress();
      }

      @Override
      public SocketAddress getRemoteSocketAddress() {
         return SslConnection.this.getEndPoint().getRemoteSocketAddress();
      }

      @Override
      public WriteFlusher getWriteFlusher() {
         return super.getWriteFlusher();
      }

      protected void onFillable() {
         try {
            AutoLock l = SslConnection.this._lock.lock();

            boolean waitingForFill;
            try {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("onFillable {}", SslConnection.this);
               }

               SslConnection.this._fillState = SslConnection.FillState.IDLE;
               waitingForFill = SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL;
            } catch (Throwable var8) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (l != null) {
               l.close();
            }

            this.getFillInterest().fillable();
            if (waitingForFill) {
               l = SslConnection.this._lock.lock();

               try {
                  waitingForFill = SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL;
               } catch (Throwable var7) {
                  if (l != null) {
                     try {
                        l.close();
                     } catch (Throwable var5) {
                        var7.addSuppressed(var5);
                     }
                  }

                  throw var7;
               }

               if (l != null) {
                  l.close();
               }

               if (waitingForFill) {
                  this.fill(BufferUtil.EMPTY_BUFFER);
               }
            }
         } catch (Throwable var9) {
            this.close(var9);
         }
      }

      protected void onFillableFail(Throwable failure) {
         boolean fail = false;
         AutoLock l = SslConnection.this._lock.lock();

         try {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("onFillableFail {}", SslConnection.this, failure);
            }

            SslConnection.this._fillState = SslConnection.FillState.IDLE;
            if (SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL) {
               SslConnection.this._flushState = SslConnection.FlushState.IDLE;
               fail = true;
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

         this.getFillInterest().onFail(failure);
         if (fail && !this.getWriteFlusher().onFail(failure)) {
            this.close(failure);
         }
      }

      @Override
      public void setConnection(Connection connection) {
         if (connection instanceof AbstractConnection c) {
            int appBufferSize = SslConnection.this.getApplicationBufferSize();
            if (c.getInputBufferSize() < appBufferSize) {
               c.setInputBufferSize(appBufferSize);
            }
         }

         super.setConnection(connection);
      }

      public SslConnection getSslConnection() {
         return SslConnection.this;
      }

      @Override
      public int fill(ByteBuffer buffer) throws IOException {
         try {
            AutoLock l = SslConnection.this._lock.lock();

            int var34;
            label770: {
               label771: {
                  int appBufferSize;
                  label772: {
                     byte var35;
                     label773: {
                        int var41;
                        label774: {
                           label775: {
                              label776: {
                                 byte handshakeFailure;
                                 label777: {
                                    label778: {
                                       try {
                                          if (SslConnection.LOG.isDebugEnabled()) {
                                             SslConnection.LOG.debug(">fill {}", SslConnection.this);
                                          }

                                          int filled = -2;

                                          try {
                                             if (SslConnection.this._fillState != SslConnection.FillState.IDLE) {
                                                filled = 0;
                                                var34 = 0;
                                                break label770;
                                             }

                                             if (SslConnection.this._decryptedInput != null && SslConnection.this._decryptedInput.hasRemaining()) {
                                                var34 = filled = BufferUtil.append(buffer, SslConnection.this._decryptedInput.getByteBuffer());
                                                break label771;
                                             }

                                             label751:
                                             while (true) {
                                                HandshakeStatus status = SslConnection.this._sslEngine.getHandshakeStatus();
                                                if (SslConnection.LOG.isDebugEnabled()) {
                                                   SslConnection.LOG.debug("fill {}", status);
                                                }

                                                switch (status) {
                                                   case NEED_UNWRAP:
                                                   case NOT_HANDSHAKING:
                                                      SslConnection.this.acquireEncryptedInput();
                                                      appBufferSize = SslConnection.this.getApplicationBufferSize();
                                                      ByteBuffer appIn;
                                                      if (SslConnection.this._decryptedInput == null) {
                                                         if (BufferUtil.space(buffer) > appBufferSize) {
                                                            appIn = buffer;
                                                         } else {
                                                            SslConnection.this._decryptedInput = SslConnection.this._bufferPool
                                                               .acquire(appBufferSize, SslConnection.this._decryptedDirectBuffers);
                                                            appIn = SslConnection.this._decryptedInput.getByteBuffer();
                                                         }
                                                      } else {
                                                         appIn = SslConnection.this._decryptedInput.getByteBuffer();
                                                      }

                                                      int netFilled = SslConnection.this.networkFill(SslConnection.this._encryptedInput.getByteBuffer());
                                                      if (netFilled > 0) {
                                                         SslConnection.this._bytesIn.addAndGet((long)netFilled);
                                                      }

                                                      if (SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG.debug("net filled={}", netFilled);
                                                      }

                                                      if (netFilled < 0
                                                         && SslConnection.this.isHandshakeInitial()
                                                         && (SslConnection.this._encryptedInput == null || !SslConnection.this._encryptedInput.hasRemaining())) {
                                                         this.closeInbound();
                                                      }

                                                      if (netFilled > 0 && !SslConnection.this.isHandshakeComplete() && this.isOutboundDone()) {
                                                         throw new SSLHandshakeException("Closed during handshake");
                                                      }

                                                      if (SslConnection.this._handshake
                                                            .compareAndSet(SslConnection.HandshakeState.INITIAL, SslConnection.HandshakeState.HANDSHAKE)
                                                         && SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG.debug("fill starting handshake {}", SslConnection.this);
                                                      }

                                                      int pos = BufferUtil.flipToFill(appIn);

                                                      SSLEngineResult unwrapResult;
                                                      try {
                                                         SslConnection.this._underflown = false;
                                                         unwrapResult = SslConnection.this.unwrap(
                                                            SslConnection.this._sslEngine, SslConnection.this._encryptedInput.getByteBuffer(), appIn
                                                         );
                                                      } finally {
                                                         BufferUtil.flipToFlush(appIn, pos);
                                                      }

                                                      if (SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG
                                                            .debug(
                                                               "unwrap net_filled={} {} encryptedBuffer={} unwrapBuffer={} appBuffer={}",
                                                               new Object[]{
                                                                  netFilled,
                                                                  StringUtil.replace(unwrapResult.toString(), '\n', ' '),
                                                                  SslConnection.this._encryptedInput,
                                                                  BufferUtil.toDetailString(appIn),
                                                                  BufferUtil.toDetailString(buffer)
                                                               }
                                                            );
                                                      }

                                                      Status unwrap = unwrapResult.getStatus();
                                                      if (unwrap == Status.OK && unwrapResult.bytesConsumed() == 0 && unwrapResult.bytesProduced() == 0) {
                                                         unwrap = Status.BUFFER_UNDERFLOW;
                                                      }

                                                      switch (unwrap) {
                                                         case CLOSED:
                                                            Throwable failurex = this._failure;
                                                            if (failurex != null) {
                                                               throw IO.rethrow(failurex);
                                                            }

                                                            filled = -1;
                                                            var44 = -1;
                                                            break label751;
                                                         case BUFFER_UNDERFLOW:
                                                            if (!BufferUtil.compact(SslConnection.this._encryptedInput.getByteBuffer())) {
                                                               if (BufferUtil.space(SslConnection.this._encryptedInput.getByteBuffer()) == 0) {
                                                                  BufferUtil.clear(SslConnection.this._encryptedInput.getByteBuffer());
                                                                  throw new SSLHandshakeException("Encrypted buffer max length exceeded");
                                                               }

                                                               if (netFilled <= 0) {
                                                                  SslConnection.this._underflown = true;
                                                                  if (netFilled < 0 && SslConnection.this._sslEngine.getUseClientMode()) {
                                                                     Throwable closeFailure = this.closeInbound();
                                                                     if (SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL) {
                                                                        Throwable handshakeFailurex = new SSLHandshakeException("Abruptly closed by peer");
                                                                        if (closeFailure != null) {
                                                                           handshakeFailurex.addSuppressed(closeFailure);
                                                                        }

                                                                        throw handshakeFailurex;
                                                                     }

                                                                     filled = -1;
                                                                     handshakeFailure = -1;
                                                                     break label777;
                                                                  }

                                                                  filled = netFilled;
                                                                  var41 = netFilled;
                                                                  break label778;
                                                               }
                                                            }
                                                            continue;
                                                         case BUFFER_OVERFLOW:
                                                            if ((
                                                                  SslConnection.this._decryptedInput == null
                                                                     || !SslConnection.this._decryptedInput.hasRemaining()
                                                               )
                                                               && appBufferSize < SslConnection.this.getApplicationBufferSize()) {
                                                               SslConnection.this.releaseEmptyDecryptedInputBuffer();
                                                               continue;
                                                            }

                                                            throw new IllegalStateException("Unexpected unwrap result " + unwrap);
                                                         case OK:
                                                            if (unwrapResult.getHandshakeStatus() == HandshakeStatus.FINISHED) {
                                                               this.handshakeSucceeded();
                                                            }

                                                            if (this.isRenegotiating() && !this.allowRenegotiate()) {
                                                               filled = -1;
                                                               var41 = -1;
                                                               break label774;
                                                            }

                                                            if (unwrapResult.bytesProduced() > 0) {
                                                               if (appIn == buffer) {
                                                                  var41 = filled = unwrapResult.bytesProduced();
                                                                  break label775;
                                                               }

                                                               var41 = filled = BufferUtil.append(buffer, SslConnection.this._decryptedInput.getByteBuffer());
                                                               break label776;
                                                            }
                                                            continue;
                                                         default:
                                                            throw new IllegalStateException("Unexpected unwrap result " + unwrap);
                                                      }
                                                   case NEED_TASK:
                                                      SslConnection.this._sslEngine.getDelegatedTask().run();
                                                      break;
                                                   case NEED_WRAP:
                                                      if (SslConnection.this._flushState == SslConnection.FlushState.IDLE
                                                         && this.flush(BufferUtil.EMPTY_BUFFER)) {
                                                         Throwable failure = this._failure;
                                                         if (failure != null) {
                                                            throw IO.rethrow(failure);
                                                         }

                                                         if (SslConnection.this._sslEngine.isInboundDone()) {
                                                            filled = -1;
                                                            appBufferSize = -1;
                                                            break label772;
                                                         }
                                                         break;
                                                      }

                                                      filled = 0;
                                                      var35 = 0;
                                                      break label773;
                                                   default:
                                                      throw new IllegalStateException("Unexpected HandshakeStatus " + status);
                                                }
                                             }
                                          } catch (Throwable var29) {
                                             SslConnection.this.discardInputBuffers();
                                             Throwable f = this.handleException(var29, "fill");
                                             Throwable failurex = this.handshakeFailed(f);
                                             if (SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL) {
                                                SslConnection.this._flushState = SslConnection.FlushState.IDLE;
                                                SslConnection.this.getExecutor()
                                                   .execute(() -> SslConnection.this._sslEndPoint.getWriteFlusher().onFail(failure));
                                             }

                                             throw failurex;
                                          } finally {
                                             SslConnection.this.releaseEmptyInputBuffers();
                                             if (SslConnection.this._flushState == SslConnection.FlushState.WAIT_FOR_FILL) {
                                                SslConnection.this._flushState = SslConnection.FlushState.IDLE;
                                                SslConnection.this.getExecutor()
                                                   .execute(() -> SslConnection.this._sslEndPoint.getWriteFlusher().completeWrite());
                                             }

                                             if (SslConnection.LOG.isDebugEnabled()) {
                                                SslConnection.LOG
                                                   .debug("<fill f={} uf={} {}", new Object[]{filled, SslConnection.this._underflown, SslConnection.this});
                                             }
                                          }
                                       } catch (Throwable var31) {
                                          if (l != null) {
                                             try {
                                                l.close();
                                             } catch (Throwable var27) {
                                                var31.addSuppressed(var27);
                                             }
                                          }

                                          throw var31;
                                       }

                                       if (l != null) {
                                          l.close();
                                       }

                                       return var44;
                                    }

                                    if (l != null) {
                                       l.close();
                                    }

                                    return var41;
                                 }

                                 if (l != null) {
                                    l.close();
                                 }

                                 return handshakeFailure;
                              }

                              if (l != null) {
                                 l.close();
                              }

                              return var41;
                           }

                           if (l != null) {
                              l.close();
                           }

                           return var41;
                        }

                        if (l != null) {
                           l.close();
                        }

                        return var41;
                     }

                     if (l != null) {
                        l.close();
                     }

                     return var35;
                  }

                  if (l != null) {
                     l.close();
                  }

                  return appBufferSize;
               }

               if (l != null) {
                  l.close();
               }

               return var34;
            }

            if (l != null) {
               l.close();
            }

            return var34;
         } catch (Throwable var32) {
            this.close(var32);
            throw IO.rethrow(var32);
         }
      }

      @Override
      protected void needsFillInterest() {
         try {
            ByteBuffer write = null;
            boolean interest = false;
            AutoLock l = SslConnection.this._lock.lock();

            label120: {
               boolean fillable;
               try {
                  if (SslConnection.LOG.isDebugEnabled()) {
                     SslConnection.LOG
                        .debug(
                           ">needFillInterest s={}/{} uf={} ei={} di={} {}",
                           new Object[]{
                              SslConnection.this._flushState,
                              SslConnection.this._fillState,
                              SslConnection.this._underflown,
                              SslConnection.this._encryptedInput,
                              SslConnection.this._decryptedInput,
                              SslConnection.this
                           }
                        );
                  }

                  if (SslConnection.this._fillState != SslConnection.FillState.IDLE) {
                     break label120;
                  }

                  fillable = SslConnection.this._decryptedInput != null && SslConnection.this._decryptedInput.hasRemaining()
                     || SslConnection.this._encryptedInput != null && SslConnection.this._encryptedInput.hasRemaining() && !SslConnection.this._underflown;
                  HandshakeStatus status = SslConnection.this._sslEngine.getHandshakeStatus();
                  switch (status) {
                     case NEED_UNWRAP:
                     case NOT_HANDSHAKING:
                        if (!fillable) {
                           interest = true;
                           SslConnection.this._fillState = SslConnection.FillState.INTERESTED;
                           if (SslConnection.this._flushState == SslConnection.FlushState.IDLE
                              && SslConnection.this._encryptedOutput != null
                              && SslConnection.this._encryptedOutput.hasRemaining()) {
                              SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                              write = SslConnection.this._encryptedOutput.getByteBuffer();
                           }
                        }
                        break;
                     case NEED_TASK:
                        fillable = true;
                        break;
                     case NEED_WRAP:
                        if (fillable) {
                           break;
                        }

                        SslConnection.this._fillState = SslConnection.FillState.WAIT_FOR_FLUSH;
                        if (SslConnection.this._flushState == SslConnection.FlushState.IDLE) {
                           SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                           write = SslConnection.this._encryptedOutput != null && SslConnection.this._encryptedOutput.hasRemaining()
                              ? SslConnection.this._encryptedOutput.getByteBuffer()
                              : BufferUtil.EMPTY_BUFFER;
                        }
                        break;
                     default:
                        throw new IllegalStateException("Unexpected HandshakeStatus " + status);
                  }

                  if (SslConnection.LOG.isDebugEnabled()) {
                     SslConnection.LOG
                        .debug(
                           "<needFillInterest s={}/{} f={} i={} w={}",
                           new Object[]{SslConnection.this._flushState, SslConnection.this._fillState, fillable, interest, BufferUtil.toDetailString(write)}
                        );
                  }
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

               if (write != null) {
                  SslConnection.this.getEndPoint().write(this._incompleteWriteCallback, write);
               } else if (fillable) {
                  SslConnection.this.getExecutor().execute(SslConnection.this._runFillable);
               } else if (interest) {
                  this.ensureFillInterested();
               }

               return;
            }

            if (l != null) {
               l.close();
            }
         } catch (Throwable var9) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug(SslConnection.this.toString(), var9);
            }

            this.close(var9);
            throw var9;
         }
      }

      private void handshakeSucceeded() throws SSLException {
         if (SslConnection.this._handshake.compareAndSet(SslConnection.HandshakeState.HANDSHAKE, SslConnection.HandshakeState.SUCCEEDED)) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG
                  .debug(
                     "handshake succeeded {} {} {}/{}",
                     new Object[]{
                        SslConnection.this,
                        SslConnection.this._sslEngine.getUseClientMode() ? "client" : "resumed server",
                        SslConnection.this._sslEngine.getSession().getProtocol(),
                        SslConnection.this._sslEngine.getSession().getCipherSuite()
                     }
                  );
            }

            this.notifyHandshakeSucceeded(SslConnection.this._sslEngine);
         } else if (SslConnection.this.isHandshakeSucceeded() && SslConnection.this._renegotiationLimit > 0) {
            SslConnection.this._renegotiationLimit--;
         }
      }

      private Throwable handshakeFailed(Throwable failure) {
         if (SslConnection.this._handshake.compareAndSet(SslConnection.HandshakeState.HANDSHAKE, SslConnection.HandshakeState.FAILED)) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("handshake failed {} {}", SslConnection.this, failure);
            }

            if (!(failure instanceof SSLHandshakeException)) {
               failure = new SSLHandshakeException(failure.getMessage()).initCause(failure);
            }

            this.notifyHandshakeFailed(SslConnection.this._sslEngine, failure);
         }

         return failure;
      }

      private void terminateInput() {
         try {
            SslConnection.this._sslEngine.closeInbound();
         } catch (Throwable var2) {
            SslConnection.LOG.trace("IGNORED", var2);
         }
      }

      private Throwable closeInbound() throws SSLException {
         HandshakeStatus handshakeStatus = SslConnection.this._sslEngine.getHandshakeStatus();

         try {
            SslConnection.this._sslEngine.closeInbound();
            return null;
         } catch (SSLException var3) {
            if (handshakeStatus == HandshakeStatus.NOT_HANDSHAKING && SslConnection.this.isRequireCloseMessage()) {
               throw var3;
            } else {
               SslConnection.LOG.trace("IGNORED", var3);
               return var3;
            }
         } catch (Throwable var4) {
            SslConnection.LOG.trace("IGNORED", var4);
            return var4;
         }
      }

      @Override
      public boolean flush(ByteBuffer... appOuts) throws IOException {
         try {
            AutoLock l = SslConnection.this._lock.lock();

            boolean var45;
            label706: {
               boolean flushed;
               label707: {
                  boolean var49;
                  label708: {
                     label709: {
                        label710: {
                           label711: {
                              label712: {
                                 label713: {
                                    label714: {
                                       int written;
                                       try {
                                          if (SslConnection.LOG.isDebugEnabled()) {
                                             SslConnection.LOG.debug(">flush {}", SslConnection.this);
                                             int i = 0;

                                             for (ByteBuffer b : appOuts) {
                                                SslConnection.LOG.debug("flush b[{}]={}", i++, BufferUtil.toDetailString(b));
                                             }
                                          }

                                          Boolean result = null;

                                          try {
                                             if (SslConnection.this._encryptedOutput != null) {
                                                int remaining = SslConnection.this._encryptedOutput.remaining();
                                                if (remaining > 0) {
                                                   flushed = SslConnection.this.networkFlush(SslConnection.this._encryptedOutput.getByteBuffer());
                                                   written = remaining - SslConnection.this._encryptedOutput.remaining();
                                                   if (written > 0) {
                                                      SslConnection.this._bytesOut.addAndGet((long)written);
                                                   }

                                                   if (!flushed) {
                                                      var45 = false;
                                                      break label706;
                                                   }
                                                }
                                             }

                                             boolean isEmpty = BufferUtil.isEmpty(appOuts);
                                             if (SslConnection.this._flushState != SslConnection.FlushState.IDLE) {
                                                flushed = result = false;
                                                break label707;
                                             }

                                             label687:
                                             while (true) {
                                                HandshakeStatus status = SslConnection.this._sslEngine.getHandshakeStatus();
                                                if (SslConnection.LOG.isDebugEnabled()) {
                                                   SslConnection.LOG.debug("flush {}", status);
                                                }

                                                switch (status) {
                                                   case NEED_UNWRAP:
                                                      if (!SslConnection.this.isHandshakeInitial() || !this.isOutboundDone()) {
                                                         if (SslConnection.this._fillState == SslConnection.FillState.IDLE) {
                                                            written = this.fill(BufferUtil.EMPTY_BUFFER);
                                                            if (SslConnection.this._sslEngine.getHandshakeStatus() != status) {
                                                               break;
                                                            }

                                                            if (written < 0) {
                                                               throw new IOException("Broken pipe");
                                                            }
                                                         }

                                                         written = result = isEmpty;
                                                         break label687;
                                                      }
                                                   case NOT_HANDSHAKING:
                                                   case NEED_WRAP:
                                                      written = SslConnection.this.getPacketBufferSize();
                                                      SslConnection.this.acquireEncryptedOutput();
                                                      if (SslConnection.this._handshake
                                                            .compareAndSet(SslConnection.HandshakeState.INITIAL, SslConnection.HandshakeState.HANDSHAKE)
                                                         && SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG.debug("flush starting handshake {}", SslConnection.this);
                                                      }

                                                      ByteBuffer encryptedOutputBuffer = SslConnection.this._encryptedOutput.getByteBuffer();
                                                      BufferUtil.compact(encryptedOutputBuffer);
                                                      int pos = BufferUtil.flipToFill(encryptedOutputBuffer);

                                                      SSLEngineResult wrapResult;
                                                      try {
                                                         wrapResult = SslConnection.this.wrap(SslConnection.this._sslEngine, appOuts, encryptedOutputBuffer);
                                                      } finally {
                                                         BufferUtil.flipToFlush(encryptedOutputBuffer, pos);
                                                      }

                                                      if (SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG
                                                            .debug(
                                                               "wrap {} {} ioDone={}/{}",
                                                               new Object[]{
                                                                  StringUtil.replace(wrapResult.toString(), '\n', ' '),
                                                                  SslConnection.this._encryptedOutput,
                                                                  SslConnection.this._sslEngine.isInboundDone(),
                                                                  SslConnection.this._sslEngine.isOutboundDone()
                                                               }
                                                            );
                                                      }

                                                      isEmpty = BufferUtil.isEmpty(appOuts);
                                                      boolean flushedx = true;
                                                      int remaining = encryptedOutputBuffer.remaining();
                                                      if (remaining > 0) {
                                                         flushedx = SslConnection.this.networkFlush(encryptedOutputBuffer);
                                                         int writtenx = remaining - encryptedOutputBuffer.remaining();
                                                         if (writtenx > 0) {
                                                            SslConnection.this._bytesOut.addAndGet((long)writtenx);
                                                         }
                                                      }

                                                      if (SslConnection.LOG.isDebugEnabled()) {
                                                         SslConnection.LOG.debug("net flushed={}, ac={}", flushedx, isEmpty);
                                                      }

                                                      Status wrap = wrapResult.getStatus();
                                                      switch (wrap) {
                                                         case CLOSED:
                                                            if (!flushedx) {
                                                               var49 = result = false;
                                                               break label713;
                                                            }

                                                            SslConnection.this.getEndPoint().shutdownOutput();
                                                            if (isEmpty) {
                                                               var49 = result = true;
                                                               break label714;
                                                            }

                                                            throw new IOException("Broken pipe");
                                                         case BUFFER_UNDERFLOW:
                                                         default:
                                                            throw new IllegalStateException("Unexpected wrap result " + wrap);
                                                         case BUFFER_OVERFLOW:
                                                            if (!flushedx) {
                                                               var49 = result = false;
                                                               break label712;
                                                            }

                                                            if (written >= SslConnection.this.getPacketBufferSize()) {
                                                               throw new IllegalStateException("Unexpected wrap result " + wrap);
                                                            }

                                                            SslConnection.this.releaseEmptyEncryptedOutputBuffer();
                                                            continue;
                                                         case OK:
                                                            if (wrapResult.getHandshakeStatus() == HandshakeStatus.FINISHED) {
                                                               this.handshakeSucceeded();
                                                            }

                                                            if (this.isRenegotiating() && !this.allowRenegotiate()) {
                                                               SslConnection.this.getEndPoint().shutdownOutput();
                                                               if (!isEmpty
                                                                  || SslConnection.this._encryptedOutput != null
                                                                     && SslConnection.this._encryptedOutput.hasRemaining()) {
                                                                  throw new IOException("Broken pipe");
                                                               }

                                                               var49 = result = true;
                                                               break label708;
                                                            }

                                                            if (!flushedx) {
                                                               var49 = result = false;
                                                               break label709;
                                                            }

                                                            if (!isEmpty
                                                               || wrapResult.getHandshakeStatus() == HandshakeStatus.NEED_WRAP
                                                                  && wrapResult.bytesProduced() != 0) {
                                                               if (SslConnection.this.getEndPoint().isOutputShutdown()) {
                                                                  var49 = false;
                                                                  break label711;
                                                               }
                                                               continue;
                                                            }

                                                            var49 = result = true;
                                                            break label710;
                                                      }
                                                   case NEED_TASK:
                                                      SslConnection.this._sslEngine.getDelegatedTask().run();
                                                      break;
                                                   default:
                                                      throw new IllegalStateException("Unexpected HandshakeStatus " + status);
                                                }
                                             }
                                          } catch (Throwable var29) {
                                             SslConnection.this.discardEncryptedOutputBuffer();
                                             Throwable failure = this.handleException(var29, "flush");
                                             throw this.handshakeFailed(failure);
                                          } finally {
                                             SslConnection.this.releaseEmptyEncryptedOutputBuffer();
                                             if (SslConnection.LOG.isDebugEnabled()) {
                                                SslConnection.LOG.debug("<flush {} {}", result, SslConnection.this);
                                             }
                                          }
                                       } catch (Throwable var31) {
                                          if (l != null) {
                                             try {
                                                l.close();
                                             } catch (Throwable var27) {
                                                var31.addSuppressed(var27);
                                             }
                                          }

                                          throw var31;
                                       }

                                       if (l != null) {
                                          l.close();
                                       }

                                       return (boolean)written;
                                    }

                                    if (l != null) {
                                       l.close();
                                    }

                                    return var49;
                                 }

                                 if (l != null) {
                                    l.close();
                                 }

                                 return var49;
                              }

                              if (l != null) {
                                 l.close();
                              }

                              return var49;
                           }

                           if (l != null) {
                              l.close();
                           }

                           return var49;
                        }

                        if (l != null) {
                           l.close();
                        }

                        return var49;
                     }

                     if (l != null) {
                        l.close();
                     }

                     return var49;
                  }

                  if (l != null) {
                     l.close();
                  }

                  return var49;
               }

               if (l != null) {
                  l.close();
               }

               return flushed;
            }

            if (l != null) {
               l.close();
            }

            return var45;
         } catch (Throwable var32) {
            this.close(var32);
            throw IO.rethrow(var32);
         }
      }

      @Override
      protected void onIncompleteFlush() {
         try {
            boolean fillInterest = false;
            ByteBuffer write = null;
            AutoLock l = SslConnection.this._lock.lock();

            label115: {
               try {
                  if (SslConnection.LOG.isDebugEnabled()) {
                     SslConnection.LOG.debug(">onIncompleteFlush {} {}", SslConnection.this, SslConnection.this._encryptedOutput);
                  }

                  if (SslConnection.this._flushState != SslConnection.FlushState.IDLE) {
                     break label115;
                  }

                  label103:
                  while (true) {
                     HandshakeStatus status = SslConnection.this._sslEngine.getHandshakeStatus();
                     switch (status) {
                        case NEED_UNWRAP:
                           if (SslConnection.this._encryptedOutput != null && SslConnection.this._encryptedOutput.hasRemaining()) {
                              write = SslConnection.this._encryptedOutput.getByteBuffer();
                              SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                              break label103;
                           }

                           if (SslConnection.this._fillState != SslConnection.FillState.IDLE) {
                              SslConnection.this._flushState = SslConnection.FlushState.WAIT_FOR_FILL;
                              break label103;
                           }

                           try {
                              int filled = this.fill(BufferUtil.EMPTY_BUFFER);
                              if (SslConnection.this._sslEngine.getHandshakeStatus() != status) {
                                 break;
                              }

                              if (filled < 0) {
                                 throw new IOException("Broken pipe");
                              }
                           } catch (IOException var7) {
                              SslConnection.LOG.debug("Incomplete flush?", var7);
                              this.close(var7);
                              write = BufferUtil.EMPTY_BUFFER;
                              SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                              break label103;
                           }

                           fillInterest = true;
                           SslConnection.this._fillState = SslConnection.FillState.INTERESTED;
                           SslConnection.this._flushState = SslConnection.FlushState.WAIT_FOR_FILL;
                           break label103;
                        case NOT_HANDSHAKING:
                        case NEED_TASK:
                        case NEED_WRAP:
                           write = SslConnection.this._encryptedOutput != null && SslConnection.this._encryptedOutput.hasRemaining()
                              ? SslConnection.this._encryptedOutput.getByteBuffer()
                              : BufferUtil.EMPTY_BUFFER;
                           SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                           break label103;
                        default:
                           throw new IllegalStateException("Unexpected HandshakeStatus " + status);
                     }
                  }

                  if (SslConnection.LOG.isDebugEnabled()) {
                     SslConnection.LOG
                        .debug(
                           "<onIncompleteFlush s={}/{} fi={} w={}",
                           new Object[]{SslConnection.this._flushState, SslConnection.this._fillState, fillInterest, BufferUtil.toDetailString(write)}
                        );
                  }
               } catch (Throwable var8) {
                  if (l != null) {
                     try {
                        l.close();
                     } catch (Throwable var6) {
                        var8.addSuppressed(var6);
                     }
                  }

                  throw var8;
               }

               if (l != null) {
                  l.close();
               }

               if (write != null) {
                  SslConnection.this.getEndPoint().write(this._incompleteWriteCallback, write);
               } else if (fillInterest) {
                  this.ensureFillInterested();
               }

               return;
            }

            if (l != null) {
               l.close();
            }
         } catch (Throwable var9) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug(SslConnection.this.toString(), var9);
            }

            this.close(var9);
            throw var9;
         }
      }

      @Override
      public void doShutdownOutput() {
         this.doShutdownOutput(false);
      }

      private void doShutdownOutput(boolean close) {
         EndPoint endPoint = SslConnection.this.getEndPoint();

         try {
            boolean flush = false;
            AutoLock l = SslConnection.this._lock.lock();

            try {
               boolean ishut = endPoint.isInputShutdown();
               boolean oshut = endPoint.isOutputShutdown();
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("shutdownOutput: {} oshut={}, ishut={}", new Object[]{SslConnection.this, oshut, ishut});
               }

               this.closeOutbound();
               if (!SslConnection.this._closedOutbound) {
                  SslConnection.this._closedOutbound = true;
                  flush = !oshut;
               }

               if (!close) {
                  close = ishut;
               }
            } catch (Throwable var11) {
               if (l != null) {
                  try {
                     l.close();
                  } catch (Throwable var9) {
                     var11.addSuppressed(var9);
                  }
               }

               throw var11;
            }

            if (l != null) {
               l.close();
            }

            if (flush && !this.flush(BufferUtil.EMPTY_BUFFER) && !close) {
               ByteBuffer write = null;
               AutoLock lx = SslConnection.this._lock.lock();

               try {
                  if (SslConnection.this._encryptedOutput != null && SslConnection.this._encryptedOutput.hasRemaining()) {
                     write = SslConnection.this._encryptedOutput.getByteBuffer();
                     SslConnection.this._flushState = SslConnection.FlushState.WRITING;
                  }
               } catch (Throwable var10) {
                  if (lx != null) {
                     try {
                        lx.close();
                     } catch (Throwable var8) {
                        var10.addSuppressed(var8);
                     }
                  }

                  throw var10;
               }

               if (lx != null) {
                  lx.close();
               }

               if (write != null) {
                  endPoint.write(Callback.from(() -> {
                     AutoLock lxx = SslConnection.this._lock.lock();

                     try {
                        SslConnection.this._flushState = SslConnection.FlushState.IDLE;
                        SslConnection.this.releaseEmptyEncryptedOutputBuffer();
                     } catch (Throwable var5x) {
                        if (lxx != null) {
                           try {
                              lxx.close();
                           } catch (Throwable var4x) {
                              var5x.addSuppressed(var4x);
                           }
                        }

                        throw var5x;
                     }

                     if (lxx != null) {
                        lxx.close();
                     }
                  }, t -> this.disconnect()), write);
               }
            }

            if (close) {
               this.disconnect();
            } else {
               this.ensureFillInterested();
            }
         } catch (Throwable var12) {
            if (SslConnection.LOG.isTraceEnabled()) {
               SslConnection.LOG.trace("IGNORED", var12);
            }

            this.disconnect();
         }
      }

      private void disconnect() {
         AutoLock l = SslConnection.this._lock.lock();

         try {
            SslConnection.this.discardEncryptedOutputBuffer();
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

         SslConnection.this.getEndPoint().close();
      }

      private void closeOutbound() {
         try {
            SslConnection.this._sslEngine.closeOutbound();
         } catch (Throwable var2) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("Unable to close outbound", var2);
            }
         }
      }

      private void ensureFillInterested() {
         if (SslConnection.LOG.isDebugEnabled()) {
            SslConnection.LOG.debug("ensureFillInterested {}", SslConnection.this);
         }

         SslConnection.this.tryFillInterested(SslConnection.this._sslReadCallback);
      }

      @Override
      public boolean isOutputShutdown() {
         return this.isOutboundDone() || SslConnection.this.getEndPoint().isOutputShutdown();
      }

      private boolean isOutboundDone() {
         try {
            return SslConnection.this._sslEngine.isOutboundDone();
         } catch (Throwable var2) {
            SslConnection.LOG.trace("IGNORED", var2);
            return true;
         }
      }

      @Override
      public void doClose() {
         AutoLock l = SslConnection.this._lock.lock();

         try {
            SslConnection.this.discardInputBuffers();
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

         this.doShutdownOutput(true);
         super.doClose();
      }

      @Override
      public Object getTransport() {
         return SslConnection.this.getEndPoint();
      }

      @Override
      public boolean isInputShutdown() {
         return (SslConnection.this._decryptedInput == null || !SslConnection.this._decryptedInput.hasRemaining())
            && (SslConnection.this.getEndPoint().isInputShutdown() || this.isInboundDone());
      }

      private boolean isInboundDone() {
         try {
            return SslConnection.this._sslEngine.isInboundDone();
         } catch (Throwable var2) {
            SslConnection.LOG.trace("IGNORED", var2);
            return true;
         }
      }

      private void notifyHandshakeSucceeded(SSLEngine sslEngine) throws SSLException {
         SslHandshakeListener.Event event = null;

         for (SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
            if (event == null) {
               event = new SslHandshakeListener.Event(sslEngine);
            }

            try {
               listener.handshakeSucceeded(event);
            } catch (SSLException var6) {
               throw var6;
            } catch (Throwable var7) {
               SslConnection.LOG.info("Exception while notifying listener {}", listener, var7);
            }
         }
      }

      private void notifyHandshakeFailed(SSLEngine sslEngine, Throwable failure) {
         SslHandshakeListener.Event event = null;

         for (SslHandshakeListener listener : SslConnection.this.handshakeListeners) {
            if (event == null) {
               event = new SslHandshakeListener.Event(sslEngine);
            }

            try {
               listener.handshakeFailed(event, failure);
            } catch (Throwable var7) {
               SslConnection.LOG.info("Exception while notifying listener {}", listener, var7);
            }
         }
      }

      private boolean isRenegotiating() {
         if (!SslConnection.this.isHandshakeComplete()) {
            return false;
         } else {
            return this.isTLS13() ? false : SslConnection.this._sslEngine.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING;
         }
      }

      private boolean allowRenegotiate() {
         if (!SslConnection.this.isRenegotiationAllowed()) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("Renegotiation denied {}", SslConnection.this);
            }

            this.terminateInput();
            return false;
         } else if (SslConnection.this.getRenegotiationLimit() == 0) {
            if (SslConnection.LOG.isDebugEnabled()) {
               SslConnection.LOG.debug("Renegotiation limit exceeded {}", SslConnection.this);
            }

            this.terminateInput();
            return false;
         } else {
            return true;
         }
      }

      private boolean isTLS13() {
         String protocol = SslConnection.this._sslEngine.getSession().getProtocol();
         return "TLSv1.3".equals(protocol);
      }

      private Throwable handleException(Throwable x, String context) {
         AutoLock l = SslConnection.this._lock.lock();

         Throwable var4;
         try {
            if (this._failure == null) {
               this._failure = x;
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("{} stored {} exception", new Object[]{this, context, x});
               }
            } else {
               ExceptionUtil.addSuppressedIfNotAssociated(this._failure, x);
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("{} suppressed {} exception", new Object[]{this, context, x});
               }
            }

            var4 = this._failure;
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

         return var4;
      }

      @Override
      public String toString() {
         return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.toEndPointString());
      }

      private final class IncompleteWriteCallback implements Callback, Invocable {
         public void succeeded() {
            AutoLock l = SslConnection.this._lock.lock();

            boolean fillable;
            boolean interested;
            try {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("IncompleteWriteCB succeeded {}", SslConnection.this);
               }

               SslConnection.this.releaseEmptyEncryptedOutputBuffer();
               SslConnection.this._flushState = SslConnection.FlushState.IDLE;
               interested = SslConnection.this._fillState == SslConnection.FillState.INTERESTED;
               fillable = SslConnection.this._fillState == SslConnection.FillState.WAIT_FOR_FLUSH;
               if (fillable) {
                  SslConnection.this._fillState = SslConnection.FillState.IDLE;
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

            if (interested) {
               SslEndPoint.this.ensureFillInterested();
            } else if (fillable) {
               SslConnection.this._sslEndPoint.getFillInterest().fillable();
            }

            SslConnection.this._sslEndPoint.getWriteFlusher().completeWrite();
         }

         public void failed(Throwable x) {
            AutoLock l = SslConnection.this._lock.lock();

            boolean failFillInterest;
            try {
               if (SslConnection.LOG.isDebugEnabled()) {
                  SslConnection.LOG.debug("IncompleteWriteCB failed {}", SslConnection.this, x);
               }

               SslConnection.this.discardEncryptedOutputBuffer();
               SslConnection.this._flushState = SslConnection.FlushState.IDLE;
               failFillInterest = SslConnection.this._fillState == SslConnection.FillState.WAIT_FOR_FLUSH
                  || SslConnection.this._fillState == SslConnection.FillState.INTERESTED;
               if (failFillInterest) {
                  SslConnection.this._fillState = SslConnection.FillState.IDLE;
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

            SslConnection.this.getExecutor().execute(() -> {
               if (failFillInterest) {
                  SslConnection.this._sslEndPoint.getFillInterest().onFail(x);
               }

               SslConnection.this._sslEndPoint.getWriteFlusher().onFail(x);
            });
         }

         public InvocationType getInvocationType() {
            return SslConnection.this._sslEndPoint.getWriteFlusher().getCallbackInvocationType();
         }

         @Override
         public String toString() {
            return String.format("SSL@%h.DEP.writeCallback", SslConnection.this);
         }
      }
   }
}
