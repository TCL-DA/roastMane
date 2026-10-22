package org.eclipse.jetty.server.internal;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritePendingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.http.ComplianceViolation;
import org.eclipse.jetty.http.HostPortHttpField;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpGenerator;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpHeaderValue;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpParser;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.Trailers;
import org.eclipse.jetty.http.UriCompliance;
import org.eclipse.jetty.http.ComplianceViolation.Mode;
import org.eclipse.jetty.http.HttpCompliance.Violation;
import org.eclipse.jetty.http.HttpException.RuntimeException;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.http.HttpGenerator.Result;
import org.eclipse.jetty.http.MetaData.Response;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.io.RetainableByteBuffer;
import org.eclipse.jetty.io.RuntimeIOException;
import org.eclipse.jetty.io.Connection.UpgradeFrom;
import org.eclipse.jetty.io.Connection.UpgradeTo;
import org.eclipse.jetty.io.Content.Chunk;
import org.eclipse.jetty.io.WriteFlusher.Listener;
import org.eclipse.jetty.io.ssl.SslConnection.SslEndPoint;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.ConnectionMetaData;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.TunnelSupport;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.HostPort;
import org.eclipse.jetty.util.IteratingCallback;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.Attributes.Lazy;
import org.eclipse.jetty.util.IteratingCallback.Action;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnection extends AbstractConnection implements Runnable, Listener, UpgradeFrom, UpgradeTo, ConnectionMetaData {
   private static final Logger LOG = LoggerFactory.getLogger(HttpConnection.class);
   private static final HttpField PREAMBLE_UPGRADE_H2C = new HttpField(HttpHeader.UPGRADE, "h2c");
   private static final ThreadLocal<HttpConnection> __currentConnection = new ThreadLocal<>();
   private static final AtomicLong __connectionIdGenerator = new AtomicLong();
   private final TunnelSupport _tunnelSupport = new HttpConnection.TunnelSupportOverHTTP1();
   private final AtomicLong _streamIdGenerator = new AtomicLong();
   private final long _id;
   private final HttpConfiguration _configuration;
   private final Connector _connector;
   private final HttpChannel _httpChannel;
   private final HttpConnection.RequestHandler _requestHandler;
   private final HttpParser _parser;
   private final HttpGenerator _generator;
   private final ByteBufferPool _bufferPool;
   private final AtomicReference<HttpConnection.HttpStreamOverHTTP1> _stream = new AtomicReference<>();
   private final Lazy _attributes = new Lazy();
   private final HttpConnection.DemandContentCallback _demandContentCallback = new HttpConnection.DemandContentCallback();
   private final HttpConnection.SendCallback _sendCallback = new HttpConnection.SendCallback();
   private final boolean _recordHttpComplianceViolations;
   private final LongAdder bytesIn = new LongAdder();
   private final LongAdder bytesOut = new LongAdder();
   private final AtomicBoolean _handling = new AtomicBoolean(false);
   private final Mutable _headerBuilder = HttpFields.build();
   private volatile RetainableByteBuffer _retainableByteBuffer;
   private Mutable _trailers;
   private Runnable _onRequest;
   private long _requests;
   private boolean _useInputDirectByteBuffers;
   private boolean _useOutputDirectByteBuffers;

   public static HttpConnection getCurrentConnection() {
      return __currentConnection.get();
   }

   protected static HttpConnection setCurrentConnection(HttpConnection connection) {
      HttpConnection last = __currentConnection.get();
      __currentConnection.set(connection);
      return last;
   }

   public HttpConnection(HttpConfiguration configuration, Connector connector, EndPoint endPoint, boolean recordComplianceViolations) {
      super(endPoint, connector.getExecutor());
      this._id = __connectionIdGenerator.getAndIncrement();
      this._configuration = configuration;
      this._connector = connector;
      this._bufferPool = this._connector.getByteBufferPool();
      this._generator = this.newHttpGenerator();
      this._httpChannel = this.newHttpChannel(connector.getServer(), configuration);
      this._requestHandler = this.newRequestHandler();
      this._parser = this.newHttpParser(configuration.getHttpCompliance());
      this._recordHttpComplianceViolations = recordComplianceViolations;
      if (LOG.isDebugEnabled()) {
         LOG.debug("New HTTP Connection {}", this);
      }
   }

   public InvocationType getInvocationType() {
      return this.getServer().getInvocationType();
   }

   public boolean isRecordHttpComplianceViolations() {
      return this._recordHttpComplianceViolations;
   }

   protected HttpGenerator newHttpGenerator() {
      return new HttpGenerator();
   }

   protected HttpParser newHttpParser(HttpCompliance compliance) {
      HttpParser parser = new HttpParser(this._requestHandler, this.getHttpConfiguration().getRequestHeaderSize(), compliance);
      parser.setHeaderCacheSize(this.getHttpConfiguration().getHeaderCacheSize());
      parser.setHeaderCacheCaseSensitive(this.getHttpConfiguration().isHeaderCacheCaseSensitive());
      return parser;
   }

   protected HttpChannel newHttpChannel(Server server, HttpConfiguration configuration) {
      return new HttpChannelState(this);
   }

   protected HttpConnection.HttpStreamOverHTTP1 newHttpStream(String method, String uri, HttpVersion version) {
      return new HttpConnection.HttpStreamOverHTTP1(method, uri, version);
   }

   protected HttpConnection.RequestHandler newRequestHandler() {
      return new HttpConnection.RequestHandler();
   }

   public Server getServer() {
      return this._connector.getServer();
   }

   @Override
   public Connector getConnector() {
      return this._connector;
   }

   public HttpChannel getHttpChannel() {
      return this._httpChannel;
   }

   public HttpParser getParser() {
      return this._parser;
   }

   public HttpGenerator getGenerator() {
      return this._generator;
   }

   @Override
   public String getId() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getEndPoint().getRemoteSocketAddress()).append('@');

      try {
         TypeUtil.toHex(this.hashCode(), builder);
      } catch (IOException var3) {
      }

      builder.append('#').append(this._id);
      return builder.toString();
   }

   @Override
   public HttpConfiguration getHttpConfiguration() {
      return this._configuration;
   }

   @Override
   public HttpVersion getHttpVersion() {
      HttpConnection.HttpStreamOverHTTP1 stream = this._stream.get();
      return stream != null ? stream._version : HttpVersion.HTTP_1_1;
   }

   @Override
   public String getProtocol() {
      return this.getHttpVersion().asString();
   }

   @Override
   public Connection getConnection() {
      return this;
   }

   @Override
   public boolean isPersistent() {
      return this._generator.isPersistent(this.getHttpVersion());
   }

   @Override
   public boolean isSecure() {
      return this.getEndPoint() instanceof SslEndPoint;
   }

   @Override
   public SocketAddress getRemoteSocketAddress() {
      return this.getEndPoint().getRemoteSocketAddress();
   }

   @Override
   public SocketAddress getLocalSocketAddress() {
      HttpConfiguration config = this.getHttpConfiguration();
      if (config != null) {
         SocketAddress override = config.getLocalAddress();
         if (override != null) {
            return override;
         }
      }

      return this.getEndPoint().getLocalSocketAddress();
   }

   public Object removeAttribute(String name) {
      return this._attributes.removeAttribute(name);
   }

   public Object setAttribute(String name, Object attribute) {
      return this._attributes.setAttribute(name, attribute);
   }

   public Object getAttribute(String name) {
      return this._attributes.getAttribute(name);
   }

   public Set<String> getAttributeNameSet() {
      return this._attributes.getAttributeNameSet();
   }

   public void clearAttributes() {
      this._attributes.clearAttributes();
   }

   public long getMessagesIn() {
      return this._requests;
   }

   public long getMessagesOut() {
      return this._requests;
   }

   public boolean isUseInputDirectByteBuffers() {
      return this._useInputDirectByteBuffers;
   }

   public void setUseInputDirectByteBuffers(boolean useInputDirectByteBuffers) {
      this._useInputDirectByteBuffers = useInputDirectByteBuffers;
   }

   public boolean isUseOutputDirectByteBuffers() {
      return this._useOutputDirectByteBuffers;
   }

   public void setUseOutputDirectByteBuffers(boolean useOutputDirectByteBuffers) {
      this._useOutputDirectByteBuffers = useOutputDirectByteBuffers;
   }

   protected void onComplianceViolation(Mode mode, ComplianceViolation violation, String details) {
      if (this.isRecordHttpComplianceViolations()) {
         HttpConnection.HttpStreamOverHTTP1 stream = this._stream.get();
         if (stream != null) {
            if (stream._complianceViolations == null) {
               stream._complianceViolations = new ArrayList<>();
            }

            String record = String.format("%s (see %s) in mode %s for %s in %s", violation.getDescription(), violation.getURL(), mode, details, this);
            stream._complianceViolations.add(record);
            if (LOG.isDebugEnabled()) {
               LOG.debug(record);
            }
         }
      }
   }

   public ByteBuffer onUpgradeFrom() {
      if (!this.isRequestBufferEmpty()) {
         ByteBuffer unconsumed = ByteBuffer.allocateDirect(this._retainableByteBuffer.remaining());
         unconsumed.put(this._retainableByteBuffer.getByteBuffer());
         unconsumed.flip();
         this.releaseRequestBuffer();
         return unconsumed;
      } else {
         return null;
      }
   }

   public void onUpgradeTo(ByteBuffer buffer) {
      BufferUtil.append(this.getRequestBuffer(), buffer);
   }

   public void onFlushed(long bytes) throws IOException {
   }

   void releaseRequestBuffer() {
      if (this._retainableByteBuffer != null && !this._retainableByteBuffer.hasRemaining()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("releaseRequestBuffer {}", this);
         }

         if (!this._retainableByteBuffer.release()) {
            throw new IllegalStateException("unreleased buffer " + this._retainableByteBuffer);
         }

         this._retainableByteBuffer = null;
      }
   }

   private ByteBuffer getRequestBuffer() {
      if (this._retainableByteBuffer == null) {
         this._retainableByteBuffer = this._bufferPool.acquire(this.getInputBufferSize(), this.isUseInputDirectByteBuffers());
      }

      return this._retainableByteBuffer.getByteBuffer();
   }

   public boolean isRequestBufferEmpty() {
      return this._retainableByteBuffer == null || !this._retainableByteBuffer.hasRemaining();
   }

   public void onFillable() {
      if (LOG.isDebugEnabled()) {
         LOG.debug(">>onFillable enter {} {} {}", new Object[]{this, this._httpChannel, this._retainableByteBuffer});
      }

      HttpConnection last = setCurrentConnection(this);

      try {
         while (this.getEndPoint().isOpen()) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("onFillable fill and parse {} {} {}", new Object[]{this, this._httpChannel, this._retainableByteBuffer});
            }

            int filled = this.fillRequestBuffer();
            if (filled < 0 && this.getEndPoint().isOutputShutdown()) {
               this.close();
            }

            boolean handle = this.parseRequestBuffer();
            if (this.getEndPoint().getConnection() != this) {
               break;
            }

            if (handle) {
               Request request = this._httpChannel.getRequest();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("HANDLE {} {}", request, this);
               }

               this._handling.set(true);
               Runnable onRequest = this._onRequest;
               this._onRequest = null;
               onRequest.run();
               if (this._handling.compareAndSet(true, false)) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("request !complete {} {}", request, this);
                  }
                  break;
               }

               if (this.getEndPoint().getConnection() != this) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("upgraded {} -> {}", this, this.getEndPoint().getConnection());
                  }
                  break;
               }
            } else {
               if (filled < 0) {
                  this.getEndPoint().shutdownOutput();
                  break;
               }

               if (this._requestHandler._failure != null) {
                  break;
               }

               if (filled == 0) {
                  this.fillInterested();
                  break;
               }
            }
         }
      } catch (Throwable var15) {
         Throwable x = var15;

         try {
            if (LOG.isDebugEnabled()) {
               LOG.debug("caught exception {} {}", new Object[]{this, this._httpChannel, x});
            }

            if (this._retainableByteBuffer != null) {
               this._retainableByteBuffer.clear();
               this.releaseRequestBuffer();
            }
         } finally {
            this.getEndPoint().close(var15);
         }
      } finally {
         setCurrentConnection(last);
         if (LOG.isDebugEnabled()) {
            LOG.debug("<<onFillable exit {} {} {}", new Object[]{this, this._httpChannel, this._retainableByteBuffer});
         }
      }
   }

   void parseAndFillForContent() {
      if (this._parser.isTerminated()) {
         this._requestHandler.messageComplete();
      } else {
         while (this._parser.inContentState() && !this.parseRequestBuffer() && (!this._parser.inContentState() || this.fillRequestBuffer() > 0)) {
         }
      }
   }

   private int fillRequestBuffer() {
      if (this._retainableByteBuffer != null && this._retainableByteBuffer.isRetained()) {
         RetainableByteBuffer newBuffer = this._bufferPool.acquire(this.getInputBufferSize(), this.isUseInputDirectByteBuffers());
         if (LOG.isDebugEnabled()) {
            LOG.debug("replace buffer {} <- {} in {}", new Object[]{this._retainableByteBuffer, newBuffer, this});
         }

         this._retainableByteBuffer.release();
         this._retainableByteBuffer = newBuffer;
      }

      if (!this.isRequestBufferEmpty()) {
         return this._retainableByteBuffer.remaining();
      } else {
         ByteBuffer requestBuffer = this.getRequestBuffer();

         try {
            int filled = this.getEndPoint().fill(requestBuffer);
            if (filled == 0) {
               filled = this.getEndPoint().fill(requestBuffer);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("{} filled {} {}", new Object[]{this, filled, this._retainableByteBuffer});
            }

            if (filled > 0) {
               this.bytesIn.add((long)filled);
            } else {
               if (filled < 0) {
                  this._parser.atEOF();
               }

               this.releaseRequestBuffer();
            }

            return filled;
         } catch (Throwable var3) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Unable to fill from endpoint {}", this.getEndPoint(), var3);
            }

            this._parser.atEOF();
            if (this._retainableByteBuffer != null) {
               this._retainableByteBuffer.clear();
               this.releaseRequestBuffer();
            }

            return -1;
         }
      }
   }

   private boolean parseRequestBuffer() {
      if (LOG.isDebugEnabled()) {
         LOG.debug("{} parse {}", this, this._retainableByteBuffer);
      }

      if (this._parser.isTerminated()) {
         throw new RuntimeIOException("Parser is terminated");
      } else {
         boolean handle = this._parser.parseNext(this._retainableByteBuffer == null ? BufferUtil.EMPTY_BUFFER : this._retainableByteBuffer.getByteBuffer());
         if (LOG.isDebugEnabled()) {
            LOG.debug("{} parsed {} {}", new Object[]{this, handle, this._parser});
         }

         if (this._retainableByteBuffer != null && !this._retainableByteBuffer.isRetained()) {
            this.releaseRequestBuffer();
         }

         return handle;
      }
   }

   private boolean upgrade(HttpConnection.HttpStreamOverHTTP1 stream) {
      if (stream.upgrade()) {
         this._httpChannel.recycle();
         this._parser.close();
         this._generator.reset();
         return true;
      } else {
         return false;
      }
   }

   protected void onFillInterestedFailed(Throwable cause) {
      this._parser.close();
      super.onFillInterestedFailed(cause);
   }

   public boolean onIdleExpired(TimeoutException timeout) {
      if (this._httpChannel.getRequest() == null) {
         return true;
      } else {
         Runnable task = this._httpChannel.onIdleTimeout(timeout);
         if (task != null) {
            this.getExecutor().execute(task);
         }

         return false;
      }
   }

   public void onOpen() {
      super.onOpen();
      if (this.isRequestBufferEmpty()) {
         this.fillInterested();
      } else {
         this.getExecutor().execute(this);
      }
   }

   public void onClose(Throwable cause) {
      if (cause == null) {
         this._sendCallback.close();
      } else {
         this._sendCallback.failed(cause);
      }

      super.onClose(cause);
   }

   @Override
   public void run() {
      this.onFillable();
   }

   public void asyncReadFillInterested() {
      this.getEndPoint().tryFillInterested(this._demandContentCallback);
   }

   public long getBytesIn() {
      return this.bytesIn.longValue();
   }

   public long getBytesOut() {
      return this.bytesOut.longValue();
   }

   public String toConnectionString() {
      return String.format("%s@%x[p=%s,g=%s]=>%s", this.getClass().getSimpleName(), this.hashCode(), this._parser, this._generator, this._httpChannel);
   }

   private class DemandContentCallback implements Callback {
      public void succeeded() {
         Runnable task = HttpConnection.this._httpChannel.onContentAvailable();
         if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("demand succeeded {}", task);
         }

         if (task != null) {
            task.run();
         }
      }

      public void failed(Throwable x) {
         Runnable task = HttpConnection.this._httpChannel.onFailure(x);
         if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("demand failed {}", task, x);
         }

         if (task != null) {
            HttpConnection.this.getConnector().getExecutor().execute(task);
         }
      }

      public InvocationType getInvocationType() {
         return Invocable.getInvocationType(HttpConnection.this._httpChannel);
      }
   }

   protected class HttpStreamOverHTTP1 implements HttpStream {
      private final long _id;
      private final String _method;
      private final org.eclipse.jetty.http.HttpURI.Mutable _uri;
      private final HttpVersion _version;
      private long _contentLength = -1L;
      private HostPortHttpField _hostField;
      private org.eclipse.jetty.http.MetaData.Request _request;
      private HttpField _upgrade = null;
      private Chunk _chunk;
      private boolean _connectionClose = false;
      private boolean _connectionKeepAlive = false;
      private boolean _connectionUpgrade = false;
      private boolean _unknownExpectation = false;
      private boolean _expects100Continue = false;
      private List<String> _complianceViolations;

      protected HttpStreamOverHTTP1(String method, String uri, HttpVersion version) {
         this._id = HttpConnection.this._streamIdGenerator.getAndIncrement();
         this._method = method;
         this._uri = uri == null ? null : HttpURI.build(method, uri);
         this._version = Objects.requireNonNull(version);
         if (this._uri != null && this._uri.getPath() == null && this._uri.getScheme() != null && this._uri.hasAuthority()) {
            this._uri.path("/");
         }
      }

      @Override
      public Throwable consumeAvailable() {
         Throwable result = HttpStream.consumeAvailable(this, HttpConnection.this.getHttpConfiguration());
         if (result != null) {
            HttpConnection.this._generator.setPersistent(false);
            if (this._chunk != null) {
               this._chunk.release();
            }

            this._chunk = Chunk.from(result, true);
         }

         return result;
      }

      public void parsedHeader(HttpField field) {
         HttpHeader header = field.getHeader();
         String value = field.getValue();
         if (header != null) {
            switch (header) {
               case CONNECTION:
                  this._connectionClose = this._connectionClose | field.contains(HttpHeaderValue.CLOSE.asString());
                  if (HttpVersion.HTTP_1_0.equals(this._version)) {
                     this._connectionKeepAlive = this._connectionKeepAlive | field.contains(HttpHeader.KEEP_ALIVE.asString());
                  }

                  this._connectionUpgrade = this._connectionUpgrade | field.contains(HttpHeaderValue.UPGRADE.asString());
                  break;
               case HOST:
                  if (value == null) {
                     value = "";
                  }

                  if (field instanceof HostPortHttpField) {
                     this._hostField = (HostPortHttpField)field;
                  } else {
                     field = this._hostField = new HostPortHttpField(value);
                  }
                  break;
               case EXPECT:
                  if (!HttpHeaderValue.parseCsvIndex(value, t -> {
                     if (t == HttpHeaderValue.CONTINUE) {
                        this._expects100Continue = true;
                        return true;
                     } else {
                        return false;
                     }
                  }, s -> false)) {
                     this._unknownExpectation = true;
                     this._expects100Continue = false;
                  }
                  break;
               case UPGRADE:
                  this._upgrade = field;
                  break;
               case CONTENT_LENGTH:
                  this._contentLength = field.getLongValue();
            }
         }

         HttpConnection.this._headerBuilder.add(field);
      }

      public Runnable headerComplete() {
         if (this._uri.hasViolations()) {
            UriCompliance compliance = HttpConnection.this._configuration.getUriCompliance();
            String badMessage = UriCompliance.checkUriCompliance(compliance, this._uri);
            if (badMessage != null) {
               throw new BadMessageException(badMessage);
            }
         }

         if (this._hostField != null) {
            if (this._uri.isAbsolute()) {
               if (!this._hostField.getValue().equals(this._uri.getAuthority())) {
                  HttpCompliance httpCompliance = HttpConnection.this.getHttpConfiguration().getHttpCompliance();
                  if (!httpCompliance.allows(Violation.MISMATCHED_AUTHORITY)) {
                     throw new BadMessageException("Authority!=Host");
                  }

                  HttpConnection.this.onComplianceViolation(httpCompliance, Violation.MISMATCHED_AUTHORITY, this._uri.asString());
               }
            } else if (StringUtil.isBlank(this._hostField.getHostPort().getHost())) {
               throw new BadMessageException("Blank Host");
            }
         }

         if (!this._uri.isAbsolute()) {
            this._uri.scheme(HttpConnection.this.getEndPoint() instanceof SslEndPoint ? HttpScheme.HTTPS : HttpScheme.HTTP);
         }

         if (this._uri.getAuthority() == null && !HttpMethod.CONNECT.is(this._method)) {
            HostPort hostPort = this._hostField == null ? HttpConnection.this.getServerAuthority() : this._hostField.getHostPort();
            int port = hostPort.getPort();
            if (port == HttpScheme.getDefaultPort(this._uri.getScheme())) {
               port = -1;
            }

            this._uri.authority(hostPort.getHost(), port);
         }

         if (this._uri.getPath() == null) {
            this._uri.path("/");
         }

         this._request = new org.eclipse.jetty.http.MetaData.Request(
            HttpConnection.this._parser.getBeginNanoTime(),
            this._method,
            this._uri.asImmutable(),
            this._version,
            HttpConnection.this._headerBuilder,
            this._contentLength
         );
         Runnable handle = HttpConnection.this._httpChannel.onRequest(this._request);
         HttpConnection.this._requests++;
         if (this._complianceViolations != null && !this._complianceViolations.isEmpty()) {
            HttpConnection.this._httpChannel.getRequest().setAttribute("org.eclipse.jetty.http.compliance.violations", this._complianceViolations);
            this._complianceViolations = null;
         }

         boolean persistent;
         switch (this._request.getHttpVersion()) {
            case HTTP_0_9:
               persistent = false;
               break;
            case HTTP_1_0:
               persistent = HttpConnection.this.getHttpConfiguration().isPersistentConnectionsEnabled() && this._connectionKeepAlive && !this._connectionClose
                  || HttpMethod.CONNECT.is(this._method);
               HttpConnection.this._generator.setPersistent(persistent);
               if (!persistent) {
                  this._connectionKeepAlive = false;
               }
               break;
            case HTTP_1_1:
               if (this._unknownExpectation) {
                  HttpConnection.this._requestHandler.badMessage(new BadMessageException(417));
                  return null;
               }

               persistent = HttpConnection.this.getHttpConfiguration().isPersistentConnectionsEnabled() && !this._connectionClose
                  || HttpMethod.CONNECT.is(this._method);
               HttpConnection.this._generator.setPersistent(persistent);
               if (this._upgrade != null && HttpConnection.this.upgrade(HttpConnection.this._stream.get())) {
                  return null;
               }
               break;
            case HTTP_2:
               this._upgrade = HttpConnection.PREAMBLE_UPGRADE_H2C;
               if (HttpMethod.PRI.is(this._method)
                  && "*".equals(this._uri.getPath())
                  && HttpConnection.this._headerBuilder.size() == 0
                  && HttpConnection.this.upgrade(HttpConnection.this._stream.get())) {
                  return null;
               }

               HttpConnection.this._parser.close();
               throw new BadMessageException(426, "Upgrade Required");
            default:
               throw new IllegalStateException("unsupported version " + this._version);
         }

         if (!persistent) {
            HttpConnection.this._generator.setPersistent(false);
         }

         return handle;
      }

      @Override
      public String getId() {
         return Long.toString(this._id);
      }

      @Override
      public Chunk read() {
         if (this._chunk == null) {
            if (HttpConnection.this._parser.isTerminated()) {
               this._chunk = Chunk.EOF;
            } else {
               HttpConnection.this.parseAndFillForContent();
            }
         }

         Chunk content = this._chunk;
         this._chunk = Chunk.next(content);
         if (content != null && this._expects100Continue && content.hasRemaining()) {
            this._expects100Continue = false;
         }

         return content;
      }

      @Override
      public void demand() {
         if (this._chunk != null) {
            Runnable onContentAvailable = HttpConnection.this._httpChannel.onContentAvailable();
            if (onContentAvailable != null) {
               onContentAvailable.run();
            }
         } else {
            HttpConnection.this.parseAndFillForContent();
            if (this._chunk != null) {
               Runnable onContentAvailable = HttpConnection.this._httpChannel.onContentAvailable();
               if (onContentAvailable != null) {
                  onContentAvailable.run();
               }
            } else {
               if (this._expects100Continue) {
                  this._expects100Continue = false;
                  this.send(this._request, HttpGenerator.CONTINUE_100_INFO, false, null, Callback.NOOP);
               }

               HttpConnection.this.tryFillInterested(HttpConnection.this._demandContentCallback);
            }
         }
      }

      @Override
      public void prepareResponse(Mutable headers) {
         if (this._connectionKeepAlive && this._version == HttpVersion.HTTP_1_0 && !headers.contains(HttpHeader.CONNECTION, HttpHeaderValue.CLOSE.asString())) {
            headers.add(HttpFields.CONNECTION_KEEPALIVE);
         }
      }

      @Override
      public void send(org.eclipse.jetty.http.MetaData.Request request, Response response, boolean last, ByteBuffer content, Callback callback) {
         if (response == null) {
            if (!last && BufferUtil.isEmpty(content)) {
               callback.succeeded();
               return;
            }
         } else if (HttpConnection.this._generator.isCommitted()) {
            callback.failed(new IllegalStateException("Committed"));
         } else if (this._expects100Continue) {
            if (response.getStatus() == 100) {
               this._expects100Continue = false;
            } else {
               HttpConnection.this._generator.setPersistent(false);
            }
         }

         if (HttpConnection.this._sendCallback.reset(this._request, response, content, last, callback)) {
            HttpConnection.this._sendCallback.iterate();
         }
      }

      @Override
      public long getIdleTimeout() {
         return HttpConnection.this.getEndPoint().getIdleTimeout();
      }

      @Override
      public void setIdleTimeout(long idleTimeoutMs) {
         HttpConnection.this.getEndPoint().setIdleTimeout(idleTimeoutMs);
      }

      @Override
      public boolean isCommitted() {
         return HttpConnection.this._stream.get() != this || HttpConnection.this._generator.isCommitted();
      }

      private boolean upgrade() {
         if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("upgrade {} {}", this, this._upgrade);
         }

         if (!this._connectionUpgrade && this._upgrade == null) {
            return false;
         } else {
            boolean isPriorKnowledgeH2C = this._upgrade == HttpConnection.PREAMBLE_UPGRADE_H2C;
            if (!isPriorKnowledgeH2C && !this._connectionUpgrade) {
               throw new BadMessageException(400);
            } else {
               ConnectionFactory.Upgrading factory = HttpConnection.this.getConnector()
                  .getConnectionFactories()
                  .stream()
                  .filter(f -> f instanceof ConnectionFactory.Upgrading)
                  .map(ConnectionFactory.Upgrading.class::cast)
                  .filter(f -> f.getProtocols().contains(this._upgrade.getValue()))
                  .findAny()
                  .orElse(null);
               if (factory == null) {
                  if (HttpConnection.LOG.isDebugEnabled()) {
                     HttpConnection.LOG.debug("No factory for {} in {}", this._upgrade, HttpConnection.this.getConnector());
                  }

                  return false;
               } else {
                  Mutable response101 = HttpFields.build();
                  Connection upgradeConnection = factory.upgradeConnection(
                     HttpConnection.this.getConnector(), HttpConnection.this.getEndPoint(), this._request, response101
                  );
                  if (upgradeConnection == null) {
                     if (HttpConnection.LOG.isDebugEnabled()) {
                        HttpConnection.LOG.debug("Upgrade ignored for {} by {}", this._upgrade, factory);
                     }

                     return false;
                  } else {
                     if (!isPriorKnowledgeH2C) {
                        this.send(this._request, new Response(101, null, HttpVersion.HTTP_1_1, response101, 0L), false, null, Callback.NOOP);
                     }

                     if (HttpConnection.LOG.isDebugEnabled()) {
                        HttpConnection.LOG.debug("Upgrading from {} to {}", HttpConnection.this.getEndPoint().getConnection(), upgradeConnection);
                     }

                     HttpConnection.this.getEndPoint().upgrade(upgradeConnection);
                     return true;
                  }
               }
            }
         }
      }

      @Override
      public TunnelSupport getTunnelSupport() {
         return HttpConnection.this._tunnelSupport;
      }

      public void succeeded() {
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.getAndSet(null);
         if (stream != null) {
            if (HttpConnection.LOG.isDebugEnabled()) {
               HttpConnection.LOG.debug("succeeded {}", HttpConnection.this);
            }

            if (HttpConnection.this.isFillInterested()) {
               HttpConnection.LOG.warn("Read pending {} {}", this, HttpConnection.this.getEndPoint());
               this.abort(new IOException("Pending read in onCompleted"));
            } else {
               Connection upgradeConnection = (Connection)HttpConnection.this._httpChannel.getRequest().getAttribute(HttpStream.UPGRADE_CONNECTION_ATTRIBUTE);
               if (upgradeConnection != null) {
                  HttpConnection.this.getEndPoint().upgrade(upgradeConnection);
                  HttpConnection.this._httpChannel.recycle();
                  HttpConnection.this._parser.close();
                  HttpConnection.this._generator.reset();
               } else {
                  HttpConnection.this._httpChannel.recycle();
                  if (this._expects100Continue) {
                     this._expects100Continue = false;
                     HttpConnection.this._parser.close();
                  }

                  if (!HttpConnection.this._parser.isClosed()) {
                     if (HttpConnection.this._generator.isPersistent()) {
                        HttpConnection.this._parser.reset();
                     } else {
                        HttpConnection.this._parser.close();
                     }
                  }

                  HttpConnection.this._generator.reset();
                  if (!HttpConnection.this._handling.compareAndSet(true, false)) {
                     if (HttpConnection.LOG.isDebugEnabled()) {
                        HttpConnection.LOG.debug("non-current completion {}", this);
                     }

                     if (HttpConnection.this._sendCallback._shutdownOut) {
                        HttpConnection.this.getEndPoint().shutdownOutput();
                     }

                     if (HttpConnection.this._parser.isStart()) {
                        if (HttpConnection.this.isRequestBufferEmpty()) {
                           HttpConnection.this.fillInterested();
                        } else if (HttpConnection.this.getConnector().isRunning()) {
                           try {
                              HttpConnection.this.getExecutor().execute(HttpConnection.this);
                           } catch (RejectedExecutionException var4) {
                              if (HttpConnection.this.getConnector().isRunning()) {
                                 HttpConnection.LOG.warn("Failed dispatch of {}", this, var4);
                              } else {
                                 HttpConnection.LOG.trace("IGNORED", var4);
                              }

                              HttpConnection.this.getEndPoint().close();
                           }
                        } else {
                           HttpConnection.this.getEndPoint().close();
                        }
                     } else if (HttpConnection.this.getEndPoint().isOpen()) {
                        HttpConnection.this.fillInterested();
                     }
                  }
               }
            }
         }
      }

      public void failed(Throwable x) {
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.getAndSet(null);
         if (stream == null) {
            if (HttpConnection.LOG.isDebugEnabled()) {
               HttpConnection.LOG.debug("ignored", x);
            }
         } else {
            if (HttpConnection.LOG.isDebugEnabled()) {
               HttpConnection.LOG.debug("aborting", x);
            }

            this.abort(x);
         }
      }

      private void abort(Throwable failure) {
         HttpConnection.this.getEndPoint().close(failure);
      }

      public InvocationType getInvocationType() {
         return HttpStream.super.getInvocationType();
      }
   }

   protected class RequestHandler implements org.eclipse.jetty.http.HttpParser.RequestHandler, org.eclipse.jetty.http.ComplianceViolation.Listener {
      private Throwable _failure;

      public void startRequest(String method, String uri, HttpVersion version) {
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this.newHttpStream(method, uri, version);
         if (!HttpConnection.this._stream.compareAndSet(null, stream)) {
            throw new IllegalStateException("Stream pending");
         } else {
            HttpConnection.this._headerBuilder.clear();
            HttpConnection.this._httpChannel.setHttpStream(stream);
         }
      }

      public void parsedHeader(HttpField field) {
         HttpConnection.this._stream.get().parsedHeader(field);
      }

      public boolean headerComplete() {
         HttpConnection.this._onRequest = HttpConnection.this._stream.get().headerComplete();
         return true;
      }

      public boolean content(ByteBuffer buffer) {
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.get();
         if (stream != null && stream._chunk == null && HttpConnection.this._retainableByteBuffer != null) {
            if (HttpConnection.LOG.isDebugEnabled()) {
               HttpConnection.LOG
                  .debug(
                     "content {}/{} for {}", new Object[]{BufferUtil.toDetailString(buffer), HttpConnection.this._retainableByteBuffer, HttpConnection.this}
                  );
            }

            HttpConnection.this._retainableByteBuffer.retain();
            stream._chunk = Chunk.asChunk(buffer, false, HttpConnection.this._retainableByteBuffer);
            return true;
         } else {
            throw new IllegalStateException();
         }
      }

      public boolean contentComplete() {
         return false;
      }

      public void parsedTrailer(HttpField field) {
         if (HttpConnection.this._trailers == null) {
            HttpConnection.this._trailers = HttpFields.build();
         }

         HttpConnection.this._trailers.add(field);
      }

      public boolean messageComplete() {
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.get();
         if (stream._chunk != null) {
            throw new IllegalStateException();
         } else {
            if (HttpConnection.this._trailers != null) {
               stream._chunk = new Trailers(HttpConnection.this._trailers.asImmutable());
            } else {
               stream._chunk = Chunk.EOF;
            }

            return false;
         }
      }

      public void badMessage(HttpException failure) {
         if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("badMessage {} {}", HttpConnection.this, failure);
         }

         this._failure = (Throwable)failure;
         HttpConnection.this._generator.setPersistent(false);
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.get();
         if (stream == null) {
            stream = HttpConnection.this.newHttpStream("GET", "/badMessage", HttpVersion.HTTP_1_0);
            HttpConnection.this._stream.set(stream);
            HttpConnection.this._httpChannel.setHttpStream(stream);
         }

         if (HttpConnection.this._httpChannel.getRequest() == null) {
            HttpURI uri = stream._uri;
            if (uri.hasViolations()) {
               uri = HttpURI.from("/badURI");
            }

            HttpConnection.this._httpChannel
               .onRequest(
                  new org.eclipse.jetty.http.MetaData.Request(
                     HttpConnection.this._parser.getBeginNanoTime(), stream._method, uri, stream._version, HttpFields.EMPTY
                  )
               );
         }

         Runnable task = HttpConnection.this._httpChannel.onFailure(this._failure);
         if (task != null) {
            HttpConnection.this.getServer().getThreadPool().execute(task);
         }
      }

      public void earlyEOF() {
         if (HttpConnection.LOG.isDebugEnabled()) {
            HttpConnection.LOG.debug("early EOF {}", HttpConnection.this);
         }

         HttpConnection.this._generator.setPersistent(false);
         HttpConnection.HttpStreamOverHTTP1 stream = HttpConnection.this._stream.get();
         if (stream != null) {
            BadMessageException bad = new BadMessageException("Early EOF");
            if (Chunk.isFailure(stream._chunk)) {
               stream._chunk.getFailure().addSuppressed(bad);
            } else {
               if (stream._chunk != null) {
                  stream._chunk.release();
               }

               stream._chunk = Chunk.from(bad);
            }

            Runnable todo = HttpConnection.this._httpChannel.onFailure(bad);
            if (todo != null) {
               HttpConnection.this.getServer().getThreadPool().execute(todo);
            }
         }
      }

      public void onComplianceViolation(Mode mode, ComplianceViolation violation, String details) {
         HttpConnection.this.onComplianceViolation(mode, violation, details);
      }
   }

   private class SendCallback extends IteratingCallback {
      private Response _info;
      private boolean _head;
      private ByteBuffer _content;
      private boolean _lastContent;
      private Callback _callback;
      private RetainableByteBuffer _header;
      private RetainableByteBuffer _chunk;
      private boolean _shutdownOut;

      private SendCallback() {
         super(true);
      }

      public InvocationType getInvocationType() {
         return this._callback.getInvocationType();
      }

      private boolean reset(org.eclipse.jetty.http.MetaData.Request request, Response response, ByteBuffer content, boolean last, Callback callback) {
         if (!this.reset()) {
            if (this.isClosed() && response == null && last && content == null) {
               callback.succeeded();
               return false;
            } else {
               HttpConnection.LOG.warn("reset failed {}", this);
               if (this.isClosed()) {
                  callback.failed(new EofException());
               } else {
                  callback.failed(new WritePendingException());
               }

               return false;
            }
         } else {
            this._info = response;
            this._head = request != null && HttpMethod.HEAD.is(request.getMethod());
            this._content = content;
            this._lastContent = last;
            this._callback = callback;
            this._header = null;
            if (HttpConnection.this.getConnector().isShutdown()) {
               HttpConnection.this._generator.setPersistent(false);
            }

            return true;
         }
      }

      public Action process() throws Exception {
         if (this._callback == null) {
            throw new IllegalStateException();
         } else {
            boolean useDirectByteBuffers = HttpConnection.this.isUseOutputDirectByteBuffers();

            while (true) {
               ByteBuffer headerByteBuffer = this._header == null ? null : this._header.getByteBuffer();
               ByteBuffer chunkByteBuffer = this._chunk == null ? null : this._chunk.getByteBuffer();
               Result result = HttpConnection.this._generator
                  .generateResponse(this._info, this._head, headerByteBuffer, chunkByteBuffer, this._content, this._lastContent);
               if (HttpConnection.LOG.isDebugEnabled()) {
                  HttpConnection.LOG
                     .debug(
                        "generate: {} for {} ({},{},{})@{}",
                        new Object[]{
                           result,
                           this,
                           BufferUtil.toSummaryString(headerByteBuffer),
                           BufferUtil.toSummaryString(this._content),
                           this._lastContent,
                           HttpConnection.this._generator.getState()
                        }
                     );
               }

               switch (result) {
                  case NEED_INFO:
                     throw new EofException("request lifecycle violation");
                  case NEED_HEADER:
                     this._header = HttpConnection.this._bufferPool
                        .acquire(
                           Math.min(HttpConnection.this._configuration.getResponseHeaderSize(), HttpConnection.this._configuration.getOutputBufferSize()),
                           useDirectByteBuffers
                        );
                     break;
                  case HEADER_OVERFLOW:
                     if (this._header.capacity() >= HttpConnection.this._configuration.getResponseHeaderSize()) {
                        throw new RuntimeException(500, "Response header too large");
                     }

                     this.releaseHeader();
                     this._header = HttpConnection.this._bufferPool.acquire(HttpConnection.this._configuration.getResponseHeaderSize(), useDirectByteBuffers);
                     break;
                  case NEED_CHUNK:
                     this._chunk = HttpConnection.this._bufferPool.acquire(12, useDirectByteBuffers);
                     break;
                  case NEED_CHUNK_TRAILER:
                     this.releaseChunk();
                     this._chunk = HttpConnection.this._bufferPool.acquire(HttpConnection.this._configuration.getResponseHeaderSize(), useDirectByteBuffers);
                     break;
                  case FLUSH:
                     if (this._head || HttpConnection.this._generator.isNoContent()) {
                        if (this._chunk != null) {
                           this._chunk.clear();
                        }

                        BufferUtil.clear(this._content);
                     }

                     int gatherWrite = 0;
                     long bytes = 0L;
                     if (BufferUtil.hasContent(headerByteBuffer)) {
                        gatherWrite += 4;
                        bytes += (long)this._header.remaining();
                     }

                     if (BufferUtil.hasContent(chunkByteBuffer)) {
                        gatherWrite += 2;
                        bytes += (long)this._chunk.remaining();
                     }

                     if (BufferUtil.hasContent(this._content)) {
                        gatherWrite++;
                        bytes += (long)this._content.remaining();
                     }

                     HttpConnection.this.bytesOut.add(bytes);
                     switch (gatherWrite) {
                        case 1:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{this._content});
                           break;
                        case 2:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{chunkByteBuffer});
                           break;
                        case 3:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{chunkByteBuffer, this._content});
                           break;
                        case 4:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{headerByteBuffer});
                           break;
                        case 5:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{headerByteBuffer, this._content});
                           break;
                        case 6:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{headerByteBuffer, chunkByteBuffer});
                           break;
                        case 7:
                           HttpConnection.this.getEndPoint().write(this, new ByteBuffer[]{headerByteBuffer, chunkByteBuffer, this._content});
                           break;
                        default:
                           this.succeeded();
                     }

                     return Action.SCHEDULED;
                  case SHUTDOWN_OUT:
                     this._shutdownOut = true;
                     break;
                  case DONE:
                     if (HttpConnection.this.getConnector().isShutdown()
                        && HttpConnection.this._generator.isEnd()
                        && HttpConnection.this._generator.isPersistent()) {
                        this._shutdownOut = true;
                     }

                     return Action.SUCCEEDED;
                  case CONTINUE:
                     break;
                  default:
                     throw new IllegalStateException("generateResponse=" + result);
               }
            }
         }
      }

      private Callback release() {
         Callback complete = this._callback;
         this._callback = null;
         this._info = null;
         this._content = null;
         this.releaseHeader();
         this.releaseChunk();
         return complete;
      }

      private void releaseHeader() {
         if (this._header != null) {
            this._header.release();
         }

         this._header = null;
      }

      private void releaseChunk() {
         if (this._chunk != null) {
            this._chunk.release();
         }

         this._chunk = null;
      }

      protected void onCompleteSuccess() {
         this.release().succeeded();
      }

      public void onCompleteFailure(Throwable x) {
         HttpConnection.this.failedCallback(this.release(), x);
      }

      public String toString() {
         return String.format("%s[i=%s,cb=%s]", super.toString(), this._info, this._callback);
      }
   }

   private class TunnelSupportOverHTTP1 implements TunnelSupport {
      @Override
      public String getProtocol() {
         return null;
      }

      @Override
      public EndPoint getEndPoint() {
         return HttpConnection.this.getEndPoint();
      }
   }
}
