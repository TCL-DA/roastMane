package org.eclipse.jetty.client.transport.internal;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.transport.HttpClientTransportOverHTTP;
import org.eclipse.jetty.client.transport.HttpExchange;
import org.eclipse.jetty.client.transport.HttpReceiver;
import org.eclipse.jetty.client.transport.HttpResponse;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpParser.ResponseHandler;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpReceiverOverHTTP extends HttpReceiver implements ResponseHandler {
   private static final Logger LOG = LoggerFactory.getLogger(HttpReceiverOverHTTP.class);
   private final LongAdder inMessages = new LongAdder();
   private final HttpParser parser;
   private final ByteBufferPool byteBufferPool;
   private RetainableByteBuffer networkBuffer;
   private boolean shutdown;
   private boolean complete;
   private boolean unsolicited;
   private String method;
   private int status;
   private Chunk chunk;
   private Runnable action;

   public HttpReceiverOverHTTP(HttpChannelOverHTTP channel) {
      super(channel);
      HttpClient httpClient = channel.getHttpDestination().getHttpClient();
      this.parser = new HttpParser(this, httpClient.getMaxResponseHeadersSize(), httpClient.getHttpCompliance());
      if (httpClient.getTransport() instanceof HttpClientTransportOverHTTP httpTransport) {
         this.parser.setHeaderCacheSize(httpTransport.getHeaderCacheSize());
         this.parser.setHeaderCacheCaseSensitive(httpTransport.isHeaderCacheCaseSensitive());
      }

      this.byteBufferPool = httpClient.getByteBufferPool();
   }

   void receive() {
      if (!this.hasContent()) {
         boolean setFillInterest = this.parseAndFill();
         if (!this.hasContent() && setFillInterest) {
            this.fillInterested();
         }
      } else {
         this.responseContentAvailable();
      }
   }

   @Override
   protected void onInterim() {
      this.receive();
   }

   @Override
   protected void reset() {
      super.reset();
      this.parser.reset();
      if (this.chunk != null) {
         this.chunk.release();
         this.chunk = null;
      }
   }

   @Override
   protected void dispose() {
      super.dispose();
      this.parser.close();
      if (this.chunk != null) {
         this.chunk.release();
         this.chunk = null;
      }
   }

   @Override
   public Chunk read(boolean fillInterestIfNeeded) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Reading, fillInterestIfNeeded={} in {}", fillInterestIfNeeded, this);
      }

      Chunk chunk = this.consumeChunk();
      if (chunk != null) {
         return chunk;
      } else {
         boolean needFillInterest = this.parseAndFill();
         if (LOG.isDebugEnabled()) {
            LOG.debug("ParseAndFill needFillInterest {} in {}", needFillInterest, this);
         }

         chunk = this.consumeChunk();
         if (chunk != null) {
            return chunk;
         } else {
            if (needFillInterest && fillInterestIfNeeded) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Read null, filled 0, fill interest requested -> call fillInterested in {}", this);
               }

               this.fillInterested();
            }

            return null;
         }
      }
   }

   private Chunk consumeChunk() {
      Chunk chunk = this.chunk;
      this.chunk = null;
      if (LOG.isDebugEnabled()) {
         LOG.debug("Receiver consuming chunk {} in {}", chunk, this);
      }

      return chunk;
   }

   @Override
   public void failAndClose(Throwable failure) {
      this.responseFailure(failure, Promise.from(failed -> {
         if (failed) {
            this.getHttpConnection().close(failure);
         }
      }, x -> this.getHttpConnection().close(failure)));
   }

   public HttpChannelOverHTTP getHttpChannel() {
      return (HttpChannelOverHTTP)super.getHttpChannel();
   }

   private HttpConnectionOverHTTP getHttpConnection() {
      return this.getHttpChannel().getHttpConnection();
   }

   protected ByteBuffer getResponseBuffer() {
      return this.networkBuffer == null ? null : this.networkBuffer.getByteBuffer();
   }

   private void acquireNetworkBuffer() {
      this.networkBuffer = this.newNetworkBuffer();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Acquired {} in {}", this.networkBuffer, this);
      }
   }

   private void reacquireNetworkBuffer() {
      RetainableByteBuffer currentBuffer = this.networkBuffer;
      if (currentBuffer == null) {
         throw new IllegalStateException();
      } else if (currentBuffer.hasRemaining()) {
         throw new IllegalStateException();
      } else {
         currentBuffer.release();
         this.networkBuffer = this.newNetworkBuffer();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Reacquired {} <- {} in {}", new Object[]{currentBuffer, this.networkBuffer, this});
         }
      }
   }

   private RetainableByteBuffer newNetworkBuffer() {
      HttpClient client = this.getHttpDestination().getHttpClient();
      boolean direct = client.isUseInputDirectByteBuffers();
      return this.byteBufferPool.acquire(client.getResponseBufferSize(), direct);
   }

   private void releaseNetworkBuffer() {
      if (this.networkBuffer != null) {
         this.networkBuffer.release();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Released {} in {}", this.networkBuffer, this);
         }

         this.networkBuffer = null;
      }
   }

   protected ByteBuffer onUpgradeFrom() {
      RetainableByteBuffer networkBuffer = this.networkBuffer;
      if (networkBuffer == null) {
         return null;
      } else {
         ByteBuffer upgradeBuffer = null;
         if (networkBuffer.hasRemaining()) {
            HttpClient client = this.getHttpDestination().getHttpClient();
            upgradeBuffer = BufferUtil.allocate(networkBuffer.remaining(), client.isUseInputDirectByteBuffers());
            BufferUtil.clearToFill(upgradeBuffer);
            BufferUtil.put(networkBuffer.getByteBuffer(), upgradeBuffer);
            BufferUtil.flipToFlush(upgradeBuffer, 0);
         }

         this.releaseNetworkBuffer();
         return upgradeBuffer;
      }
   }

   private boolean parseAndFill() {
      HttpConnectionOverHTTP connection = this.getHttpConnection();
      EndPoint endPoint = connection.getEndPoint();

      try {
         if (this.networkBuffer == null) {
            this.acquireNetworkBuffer();
         }

         while (true) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Parsing {} in {}", BufferUtil.toDetailString(this.networkBuffer.getByteBuffer()), this);
            }

            if (this.parse()) {
               return false;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Parser willing to advance in {}", this);
            }

            if (connection.isClosed()) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Closed {} in {}", connection, this);
               }

               this.releaseNetworkBuffer();
               return false;
            }

            if (this.networkBuffer.isRetained()) {
               this.reacquireNetworkBuffer();
            }

            int read = endPoint.fill(this.networkBuffer.getByteBuffer());
            if (LOG.isDebugEnabled()) {
               LOG.debug("Read {} bytes in {} from {} in {}", new Object[]{read, this.networkBuffer, endPoint, this});
            }

            if (read <= 0) {
               if (read == 0) {
                  this.releaseNetworkBuffer();
                  return true;
               }

               this.releaseNetworkBuffer();
               this.shutdown();
               return false;
            }

            connection.addBytesIn((long)read);
         }
      } catch (Throwable var4) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Error processing {} in {}", new Object[]{endPoint, this, var4});
         }

         this.releaseNetworkBuffer();
         this.failAndClose(var4);
         return false;
      }
   }

   private boolean parse() {
      do {
         boolean handle = this.parser.parseNext(this.networkBuffer.getByteBuffer());
         if (LOG.isDebugEnabled()) {
            LOG.debug("Parse result={} on {}", handle, this);
         }

         Runnable action = this.getAndSetAction(null);
         if (action != null) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Executing action after parser returned: {} on {}", action, this);
            }

            action.run();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Action executed after Parse result={} on {}", handle, this);
            }
         }

         if (handle) {
            return !this.parser.isClose();
         }

         boolean complete = this.complete;
         this.complete = false;
         if (LOG.isDebugEnabled()) {
            LOG.debug("Parse complete={}, {} {} in {}", new Object[]{complete, this.networkBuffer, this.parser, this});
         }

         if (complete) {
            int status = this.status;
            this.status = 0;
            if (status == 101) {
               return true;
            }

            String method = this.method;
            this.method = null;
            if (this.getHttpChannel().isTunnel(method, status)) {
               return true;
            }

            if (!this.networkBuffer.hasRemaining()) {
               return false;
            }

            if (!HttpStatus.isInformational(status)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Discarding unexpected content after response {}: {} in {}", new Object[]{status, this.networkBuffer, this});
               }

               this.networkBuffer.clear();
            }

            return false;
         }
      } while (this.networkBuffer.hasRemaining());

      return false;
   }

   protected void fillInterested() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Registering as fill interested in {}", this);
      }

      this.getHttpConnection().fillInterested();
   }

   private void shutdown() {
      this.shutdown = true;
      this.parser.atEOF();
      this.parser.parseNext(BufferUtil.EMPTY_BUFFER);
   }

   protected boolean isShutdown() {
      return this.shutdown;
   }

   public void startResponse(HttpVersion version, int status, String reason) {
      HttpExchange exchange = this.getHttpExchange();
      this.unsolicited = exchange == null;
      if (exchange != null) {
         this.method = exchange.getRequest().getMethod();
         this.status = status;
         this.parser.setHeadResponse(HttpMethod.HEAD.is(this.method) || this.getHttpChannel().isTunnel(this.method, status));
         exchange.getResponse().version(version).status(status).reason(reason);
         this.responseBegin(exchange);
      }
   }

   public void parsedHeader(HttpField field) {
      HttpExchange exchange = this.getHttpExchange();
      this.unsolicited |= exchange == null;
      if (!this.unsolicited) {
         this.responseHeader(exchange, field);
      }
   }

   public boolean headerComplete() {
      HttpExchange exchange = this.getHttpExchange();
      this.unsolicited |= exchange == null;
      if (this.unsolicited) {
         return false;
      } else {
         exchange.getRequest().getConversation().setAttribute(EndPoint.class.getName(), this.getHttpConnection().getEndPoint());
         this.getHttpConnection().onResponseHeaders(exchange);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Setting action to responseHeaders(exchange, boolean) on {}", this);
         }

         if (this.getAndSetAction(() -> this.responseHeaders(exchange)) != null) {
            throw new IllegalStateException();
         } else {
            return true;
         }
      }
   }

   public boolean content(ByteBuffer buffer) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Parser generated content {} in {}", BufferUtil.toDetailString(buffer), this);
      }

      HttpExchange exchange = this.getHttpExchange();
      this.unsolicited |= exchange == null;
      if (this.unsolicited) {
         return false;
      } else if (this.chunk != null) {
         throw new IllegalStateException("Content generated with unconsumed content left");
      } else {
         this.networkBuffer.retain();
         this.chunk = Chunk.asChunk(buffer, false, this.networkBuffer);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Setting action to responseContentAvailable on {}", this);
         }

         if (this.getAndSetAction(() -> this.responseContentAvailable()) != null) {
            throw new IllegalStateException();
         } else if (this.getHttpConnection().isFillInterested()) {
            throw new IllegalStateException();
         } else {
            return true;
         }
      }
   }

   public boolean contentComplete() {
      return false;
   }

   public void parsedTrailer(HttpField trailer) {
      HttpExchange exchange = this.getHttpExchange();
      this.unsolicited |= exchange == null;
      if (!this.unsolicited) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Appending trailer '{}' to response in {}", trailer, this);
         }

         exchange.getResponse().trailer(trailer);
      }
   }

   public boolean messageComplete() {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null && !this.unsolicited) {
         int status = exchange.getResponse().getStatus();
         if (!HttpStatus.isInterim(status)) {
            this.inMessages.increment();
            this.complete = true;
         }

         if (this.chunk != null) {
            throw new IllegalStateException();
         } else {
            this.chunk = Chunk.EOF;
            boolean isUpgrade = status == 101;
            boolean isTunnel = this.getHttpChannel().isTunnel(this.method, status);
            Runnable task = !isUpgrade && !isTunnel ? this::receiveNext : null;
            if (LOG.isDebugEnabled()) {
               LOG.debug("Message complete, calling response success with task {} in {}", task, this);
            }

            this.responseSuccess(exchange, task);
            return false;
         }
      } else {
         this.getHttpConnection().close();
         return false;
      }
   }

   private void receiveNext() {
      if (this.hasContent()) {
         throw new IllegalStateException();
      } else if (this.chunk != null) {
         throw new IllegalStateException();
      } else {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Receiving next request in {}", this);
         }

         boolean setFillInterest = this.parseAndFill();
         if (!this.hasContent() && setFillInterest) {
            this.fillInterested();
         }
      }
   }

   public void earlyEOF() {
      HttpExchange exchange = this.getHttpExchange();
      HttpConnectionOverHTTP connection = this.getHttpConnection();
      if (exchange != null && !this.unsolicited) {
         this.failAndClose(new EOFException(String.valueOf(connection)));
      } else {
         connection.close();
      }
   }

   public void badMessage(HttpException failure) {
      HttpExchange exchange = this.getHttpExchange();
      if (exchange != null && !this.unsolicited) {
         HttpResponse response = exchange.getResponse();
         response.status(failure.getCode()).reason(failure.getReason());
         this.failAndClose(new HttpResponseException("HTTP protocol violation: bad response on " + this.getHttpConnection(), response, (Throwable)failure));
      } else {
         this.getHttpConnection().close();
      }
   }

   private Runnable getAndSetAction(Runnable action) {
      Runnable r = this.action;
      this.action = action;
      return r;
   }

   long getMessagesIn() {
      return this.inMessages.longValue();
   }

   @Override
   public String toString() {
      return String.format("%s[%s]", super.toString(), this.parser);
   }
}
