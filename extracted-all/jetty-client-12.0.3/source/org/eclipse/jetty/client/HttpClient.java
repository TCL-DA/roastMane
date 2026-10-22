package org.eclipse.jetty.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.internal.HttpAuthenticationStore;
import org.eclipse.jetty.client.internal.NotifyingRequestListeners;
import org.eclipse.jetty.client.transport.HttpClientTransportOverHTTP;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpDestination;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.http.HttpCompliance;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpCookieStore;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.SetCookieParser;
import org.eclipse.jetty.http.HttpCookieStore.Default;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.io.ssl.SslClientConnectionFactory;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.ProcessorUtils;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.SocketAddressResolver;
import org.eclipse.jetty.util.Promise.Wrapper;
import org.eclipse.jetty.util.SocketAddressResolver.Async;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.ssl.SslContextFactory.Client;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.Sweeper;
import org.eclipse.jetty.util.thread.ThreadPool.SizedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("The HTTP client")
public class HttpClient extends ContainerLifeCycle {
   public static final String USER_AGENT = "Jetty/" + Jetty.VERSION;
   private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);
   private static final SetCookieParser COOKIE_PARSER = SetCookieParser.newInstance();
   private final ConcurrentMap<Origin, HttpDestination> destinations = new ConcurrentHashMap<>();
   private final ProtocolHandlers handlers = new ProtocolHandlers();
   private final RequestListeners requestListeners = new NotifyingRequestListeners();
   private final ContentDecoder.Factories decoderFactories = new ContentDecoder.Factories();
   private final ProxyConfiguration proxyConfig = new ProxyConfiguration();
   private final HttpClientTransport transport;
   private final ClientConnector connector;
   private AuthenticationStore authenticationStore = new HttpAuthenticationStore();
   private HttpCookieStore cookieStore;
   private SocketAddressResolver resolver;
   private HttpField agentField = new HttpField(HttpHeader.USER_AGENT, USER_AGENT);
   private boolean followRedirects = true;
   private int maxConnectionsPerDestination = 64;
   private int maxRequestsQueuedPerDestination = 1024;
   private int requestBufferSize = 4096;
   private int responseBufferSize = 16384;
   private int maxRedirects = 8;
   private long addressResolutionTimeout = 15000L;
   private boolean strictEventOrdering = false;
   private long destinationIdleTimeout;
   private String name = this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
   private HttpCompliance httpCompliance = HttpCompliance.RFC7230;
   private String defaultRequestContentType = "application/octet-stream";
   private boolean useInputDirectByteBuffers = true;
   private boolean useOutputDirectByteBuffers = true;
   private int maxResponseHeadersSize = -1;
   private Sweeper destinationSweeper;

   public HttpClient() {
      this(new HttpClientTransportOverHTTP());
   }

   public HttpClient(HttpClientTransport transport) {
      this.transport = Objects.requireNonNull(transport);
      this.addBean(transport);
      this.connector = (ClientConnector)((AbstractHttpClientTransport)transport).getContainedBeans(ClientConnector.class).stream().findFirst().orElseThrow();
      this.addBean(this.requestListeners);
      this.addBean(this.handlers);
      this.addBean(this.decoderFactories);
   }

   public HttpClientTransport getTransport() {
      return this.transport;
   }

   public Client getSslContextFactory() {
      return this.connector.getSslContextFactory();
   }

   public void setSslContextFactory(Client sslContextFactory) {
      this.connector.setSslContextFactory(sslContextFactory);
   }

   protected void doStart() throws Exception {
      Executor executor = this.getExecutor();
      if (executor == null) {
         QueuedThreadPool threadPool = new QueuedThreadPool();
         threadPool.setName(this.name);
         executor = threadPool;
         this.setExecutor(threadPool);
      }

      int maxBucketSize = executor instanceof SizedThreadPool ? ((SizedThreadPool)executor).getMaxThreads() / 2 : ProcessorUtils.availableProcessors() * 2;
      ByteBufferPool byteBufferPool = this.getByteBufferPool();
      if (byteBufferPool == null) {
         this.setByteBufferPool(new ArrayByteBufferPool(0, 2048, 65536, maxBucketSize));
      }

      Scheduler scheduler = this.getScheduler();
      if (scheduler == null) {
         scheduler = new ScheduledExecutorScheduler(this.name + "-scheduler", false);
         this.setScheduler(scheduler);
      }

      if (this.resolver == null) {
         this.setSocketAddressResolver(new Async(this.getExecutor(), scheduler, this.getAddressResolutionTimeout()));
      }

      this.handlers.put(new ContinueProtocolHandler());
      this.handlers.put(new ProcessingProtocolHandler());
      this.handlers.put(new EarlyHintsProtocolHandler());
      this.handlers.put(new RedirectProtocolHandler(this));
      this.handlers.put(new WWWAuthenticationProtocolHandler(this));
      this.handlers.put(new ProxyAuthenticationProtocolHandler(this));
      this.handlers.put(new UpgradeProtocolHandler());
      this.decoderFactories.put(new GZIPContentDecoder.Factory(byteBufferPool));
      if (this.cookieStore == null) {
         this.cookieStore = new Default();
      }

      this.transport.setHttpClient(this);
      super.doStart();
      if (this.getDestinationIdleTimeout() > 0L) {
         this.destinationSweeper = new Sweeper(scheduler, 1000L);
         this.destinationSweeper.start();
      }
   }

   protected void doStop() throws Exception {
      if (this.destinationSweeper != null) {
         this.destinationSweeper.stop();
         this.destinationSweeper = null;
      }

      this.decoderFactories.clear();
      this.handlers.clear();
      this.destinations.clear();
      this.requestListeners.clear();
      this.authenticationStore.clearAuthentications();
      this.authenticationStore.clearAuthenticationResults();
      super.doStop();
   }

   public RequestListeners getRequestListeners() {
      return this.requestListeners;
   }

   public HttpCookieStore getHttpCookieStore() {
      return this.cookieStore;
   }

   public void setHttpCookieStore(HttpCookieStore cookieStore) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.cookieStore = Objects.requireNonNull(cookieStore);
      }
   }

   public void putCookie(URI uri, HttpField field) {
      HttpCookie cookie = COOKIE_PARSER.parse(field.getValue());
      if (cookie != null) {
         this.cookieStore.add(uri, cookie);
      }
   }

   public AuthenticationStore getAuthenticationStore() {
      return this.authenticationStore;
   }

   public void setAuthenticationStore(AuthenticationStore authenticationStore) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.authenticationStore = authenticationStore;
      }
   }

   public ContentDecoder.Factories getContentDecoderFactories() {
      return this.decoderFactories;
   }

   public ContentResponse GET(String uri) throws InterruptedException, ExecutionException, TimeoutException {
      return this.GET(URI.create(uri));
   }

   public ContentResponse GET(URI uri) throws InterruptedException, ExecutionException, TimeoutException {
      return this.newRequest(uri).send();
   }

   public ContentResponse FORM(String uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
      return this.FORM(URI.create(uri), fields);
   }

   public ContentResponse FORM(URI uri, Fields fields) throws InterruptedException, ExecutionException, TimeoutException {
      return this.POST(uri).body(new FormRequestContent(fields)).send();
   }

   public Request POST(String uri) {
      return this.POST(URI.create(uri));
   }

   public Request POST(URI uri) {
      return this.newRequest(uri).method(HttpMethod.POST);
   }

   public Request newRequest(String host, int port) {
      return this.newRequest(new Origin("http", host, port).asString());
   }

   public Request newRequest(String uri) {
      return this.newRequest(URI.create(uri));
   }

   public Request newRequest(URI uri) {
      return this.newHttpRequest(this.newConversation(), uri);
   }

   protected Request copyRequest(Request oldRequest, URI newURI) {
      return ((HttpRequest)oldRequest).copy(newURI);
   }

   private HttpRequest newHttpRequest(HttpConversation conversation, URI uri) {
      return new HttpRequest(this, conversation, uri);
   }

   public Destination resolveDestination(Request request) {
      HttpClientTransport transport = this.getTransport();
      Origin origin = transport.newOrigin(request);
      Destination destination = this.resolveDestination(origin);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Resolved {} for {}", destination, request);
      }

      return destination;
   }

   public Origin createOrigin(Request request, Origin.Protocol protocol) {
      String scheme = request.getScheme();
      if (!HttpScheme.HTTP.is(scheme) && !HttpScheme.HTTPS.is(scheme) && !HttpScheme.WS.is(scheme) && !HttpScheme.WSS.is(scheme)) {
         throw new IllegalArgumentException("Invalid protocol " + scheme);
      } else {
         scheme = scheme.toLowerCase(Locale.ENGLISH);
         String host = request.getHost();
         host = host.toLowerCase(Locale.ENGLISH);
         int port = request.getPort();
         port = normalizePort(scheme, port);
         return new Origin(scheme, host, port, request.getTag(), protocol);
      }
   }

   public Destination resolveDestination(Origin origin) {
      return this.destinations.compute(origin, (k, v) -> {
         if (v != null && !v.stale()) {
            return (HttpDestination)v;
         } else {
            HttpDestination newDestination = (HttpDestination)this.getTransport().newDestination(k);
            this.addManaged(newDestination);
            if (this.destinationSweeper != null) {
               this.destinationSweeper.offer(newDestination);
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Created {}; existing: '{}'", newDestination, v);
            }

            return newDestination;
         }
      });
   }

   public boolean removeDestination(Destination destination) {
      HttpDestination httpDestination = (HttpDestination)destination;
      boolean removed = this.destinations.remove(destination.getOrigin(), httpDestination);
      this.removeBean(destination);
      if (this.destinationSweeper != null) {
         this.destinationSweeper.remove(httpDestination);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Removed {}; result: {}", destination, removed);
      }

      return removed;
   }

   public List<Destination> getDestinations() {
      return new ArrayList<>(this.destinations.values());
   }

   public void newConnection(Destination destination, final Promise<Connection> promise) {
      final Map<String, Object> context = new ConcurrentHashMap<>();
      context.put("org.eclipse.jetty.client", this);
      context.put("org.eclipse.jetty.client.destination", destination);
      Origin.Protocol protocol = destination.getOrigin().getProtocol();
      List<String> protocols = protocol != null ? protocol.getProtocols() : List.of("http/1.1");
      context.put("org.eclipse.jetty.client.connector.applicationProtocols", protocols);
      ProxyConfiguration.Proxy proxy = destination.getProxy();
      Origin.Address address = proxy == null ? destination.getOrigin().getAddress() : proxy.getAddress();
      this.getSocketAddressResolver().resolve(address.getHost(), address.getPort(), new Promise<List<InetSocketAddress>>() {
         public void succeeded(List<InetSocketAddress> socketAddresses) {
            this.connect(socketAddresses, 0, context);
         }

         public void failed(Throwable x) {
            promise.failed(x);
         }

         private void connect(final List<InetSocketAddress> socketAddresses, final int index, final Map<String, Object> contextx) {
            context.put("org.eclipse.jetty.client.connection.promise", new Wrapper<Connection>(promise) {
               public void failed(Throwable x) {
                  int nextIndex = index + 1;
                  if (nextIndex == socketAddresses.size()) {
                     super.failed(x);
                  } else {
                     connect(socketAddresses, nextIndex, context);
                  }
               }
            });
            HttpClient.this.transport.connect(socketAddresses.get(index), context);
         }
      });
   }

   private HttpConversation newConversation() {
      return new HttpConversation();
   }

   public ProtocolHandlers getProtocolHandlers() {
      return this.handlers;
   }

   public ProtocolHandler findProtocolHandler(Request request, Response response) {
      return this.handlers.find(request, response);
   }

   public ByteBufferPool getByteBufferPool() {
      return this.connector.getByteBufferPool();
   }

   public void setByteBufferPool(ByteBufferPool byteBufferPool) {
      this.connector.setByteBufferPool(byteBufferPool);
   }

   @ManagedAttribute("The name of this HttpClient")
   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @ManagedAttribute("The timeout, in milliseconds, for connect() operations")
   public long getConnectTimeout() {
      return this.connector.getConnectTimeout().toMillis();
   }

   public void setConnectTimeout(long connectTimeout) {
      this.connector.setConnectTimeout(Duration.ofMillis(connectTimeout));
   }

   public long getAddressResolutionTimeout() {
      return this.addressResolutionTimeout;
   }

   public void setAddressResolutionTimeout(long addressResolutionTimeout) {
      this.addressResolutionTimeout = addressResolutionTimeout;
   }

   @ManagedAttribute("The timeout, in milliseconds, to close idle connections")
   public long getIdleTimeout() {
      return this.connector.getIdleTimeout().toMillis();
   }

   public void setIdleTimeout(long idleTimeout) {
      this.connector.setIdleTimeout(Duration.ofMillis(idleTimeout));
   }

   public SocketAddress getBindAddress() {
      return this.connector.getBindAddress();
   }

   public void setBindAddress(SocketAddress bindAddress) {
      this.connector.setBindAddress(bindAddress);
   }

   public HttpField getUserAgentField() {
      return this.agentField;
   }

   public void setUserAgentField(HttpField agent) {
      if (agent != null && agent.getHeader() != HttpHeader.USER_AGENT) {
         throw new IllegalArgumentException();
      } else {
         this.agentField = agent;
      }
   }

   @ManagedAttribute("Whether HTTP redirects are followed")
   public boolean isFollowRedirects() {
      return this.followRedirects;
   }

   public void setFollowRedirects(boolean follow) {
      this.followRedirects = follow;
   }

   public Executor getExecutor() {
      return this.connector.getExecutor();
   }

   public void setExecutor(Executor executor) {
      this.connector.setExecutor(executor);
   }

   public Scheduler getScheduler() {
      return this.connector.getScheduler();
   }

   public void setScheduler(Scheduler scheduler) {
      this.connector.setScheduler(scheduler);
   }

   public SocketAddressResolver getSocketAddressResolver() {
      return this.resolver;
   }

   public void setSocketAddressResolver(SocketAddressResolver resolver) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.updateBean(this.resolver, resolver);
         this.resolver = resolver;
      }
   }

   @ManagedAttribute("The max number of connections per each destination")
   public int getMaxConnectionsPerDestination() {
      return this.maxConnectionsPerDestination;
   }

   public void setMaxConnectionsPerDestination(int maxConnectionsPerDestination) {
      this.maxConnectionsPerDestination = maxConnectionsPerDestination;
   }

   @ManagedAttribute("The max number of requests queued per each destination")
   public int getMaxRequestsQueuedPerDestination() {
      return this.maxRequestsQueuedPerDestination;
   }

   public void setMaxRequestsQueuedPerDestination(int maxRequestsQueuedPerDestination) {
      this.maxRequestsQueuedPerDestination = maxRequestsQueuedPerDestination;
   }

   @ManagedAttribute("The request buffer size in bytes")
   public int getRequestBufferSize() {
      return this.requestBufferSize;
   }

   public void setRequestBufferSize(int requestBufferSize) {
      this.requestBufferSize = requestBufferSize;
   }

   @ManagedAttribute("The response buffer size in bytes")
   public int getResponseBufferSize() {
      return this.responseBufferSize;
   }

   public void setResponseBufferSize(int responseBufferSize) {
      this.responseBufferSize = responseBufferSize;
   }

   public int getMaxRedirects() {
      return this.maxRedirects;
   }

   public void setMaxRedirects(int maxRedirects) {
      this.maxRedirects = maxRedirects;
   }

   public HttpCompliance getHttpCompliance() {
      return this.httpCompliance;
   }

   public void setHttpCompliance(HttpCompliance httpCompliance) {
      this.httpCompliance = httpCompliance;
   }

   @ManagedAttribute("Whether request/response events must be strictly ordered")
   public boolean isStrictEventOrdering() {
      return this.strictEventOrdering;
   }

   public void setStrictEventOrdering(boolean strictEventOrdering) {
      this.strictEventOrdering = strictEventOrdering;
   }

   @ManagedAttribute("The time in ms after which idle destinations are removed, disabled when zero or negative")
   public long getDestinationIdleTimeout() {
      return this.destinationIdleTimeout;
   }

   public void setDestinationIdleTimeout(long destinationIdleTimeout) {
      if (this.isStarted()) {
         throw new IllegalStateException();
      } else {
         this.destinationIdleTimeout = destinationIdleTimeout;
      }
   }

   @ManagedAttribute("Whether the connect() operation is blocking")
   public boolean isConnectBlocking() {
      return this.connector.isConnectBlocking();
   }

   public void setConnectBlocking(boolean connectBlocking) {
      this.connector.setConnectBlocking(connectBlocking);
   }

   @ManagedAttribute("The default content type for request content")
   public String getDefaultRequestContentType() {
      return this.defaultRequestContentType;
   }

   public void setDefaultRequestContentType(String contentType) {
      this.defaultRequestContentType = contentType;
   }

   @ManagedAttribute("Whether to use direct ByteBuffers for reading")
   public boolean isUseInputDirectByteBuffers() {
      return this.useInputDirectByteBuffers;
   }

   public void setUseInputDirectByteBuffers(boolean useInputDirectByteBuffers) {
      this.useInputDirectByteBuffers = useInputDirectByteBuffers;
   }

   @ManagedAttribute("Whether to use direct ByteBuffers for writing")
   public boolean isUseOutputDirectByteBuffers() {
      return this.useOutputDirectByteBuffers;
   }

   public void setUseOutputDirectByteBuffers(boolean useOutputDirectByteBuffers) {
      this.useOutputDirectByteBuffers = useOutputDirectByteBuffers;
   }

   @ManagedAttribute("The max size in bytes of the response headers")
   public int getMaxResponseHeadersSize() {
      return this.maxResponseHeadersSize;
   }

   public void setMaxResponseHeadersSize(int maxResponseHeadersSize) {
      this.maxResponseHeadersSize = maxResponseHeadersSize;
   }

   public ProxyConfiguration getProxyConfiguration() {
      return this.proxyConfig;
   }

   public static int normalizePort(String scheme, int port) {
      return port > 0 ? port : HttpScheme.getDefaultPort(scheme);
   }

   public ClientConnectionFactory newSslClientConnectionFactory(Client sslContextFactory, ClientConnectionFactory connectionFactory) {
      if (sslContextFactory == null) {
         sslContextFactory = this.getSslContextFactory();
      }

      return new SslClientConnectionFactory(sslContextFactory, this.getByteBufferPool(), this.getExecutor(), connectionFactory);
   }
}
