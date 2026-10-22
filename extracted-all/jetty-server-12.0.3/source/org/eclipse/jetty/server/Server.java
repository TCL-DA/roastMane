package org.eclipse.jetty.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.DateGenerator;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Mutable;
import org.eclipse.jetty.io.ArrayByteBufferPool;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.internal.ResponseHttpFields;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Jetty;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.Uptime;
import org.eclipse.jetty.util.ExceptionUtil.MultiException;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.component.AttributeContainerMap;
import org.eclipse.jetty.util.component.ClassLoaderDump;
import org.eclipse.jetty.util.component.DumpableAttributes;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.component.Environment;
import org.eclipse.jetty.util.component.Graceful;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.AbstractLifeCycle.StopException;
import org.eclipse.jetty.util.resource.FileSystemPool;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.ShutdownThread;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class Server extends Handler.Wrapper implements Attributes {
   private static final Logger LOG = LoggerFactory.getLogger(Server.class);
   private static final String __serverInfo = "jetty/" + getVersion();
   private final AttributeContainerMap _attributes = new AttributeContainerMap();
   private final ThreadPool _threadPool;
   private final Scheduler _scheduler;
   private final ByteBufferPool _bufferPool;
   private final List<Connector> _connectors = new CopyOnWriteArrayList<>();
   private final Context _serverContext = new Server.ServerContext();
   private final AutoLock _dateLock = new AutoLock();
   private final Mutable _mimeTypes = new Mutable();
   private String _serverInfo = __serverInfo;
   private boolean _stopAtShutdown;
   private boolean _dumpAfterStart;
   private boolean _dumpBeforeStop;
   private Handler _defaultHandler;
   private Request.Handler _errorHandler;
   private RequestLog _requestLog;
   private boolean _dryRun;
   private volatile Server.DateField _dateField;
   private long _stopTimeout;
   private InvocationType _invocationType = InvocationType.NON_BLOCKING;
   private File _tempDirectory;

   public Server() {
      this((ThreadPool)null);
   }

   public Server(@Name("port") int port) {
      this((ThreadPool)null);
      ServerConnector connector = new ServerConnector(this);
      connector.setPort(port);
      this.addConnector(connector);
      this.addBean(this._attributes);
   }

   public Server(@Name("address") InetSocketAddress addr) {
      this((ThreadPool)null);
      ServerConnector connector = new ServerConnector(this);
      connector.setHost(addr.getHostName());
      connector.setPort(addr.getPort());
      this.addConnector(connector);
   }

   public Server(@Name("threadPool") ThreadPool pool) {
      this(pool, null, null);
   }

   public Server(@Name("threadPool") ThreadPool threadPool, @Name("scheduler") Scheduler scheduler, @Name("bufferPool") ByteBufferPool bufferPool) {
      this._threadPool = (ThreadPool)(threadPool != null ? threadPool : new QueuedThreadPool());
      this.addBean(this._threadPool);
      this._scheduler = (Scheduler)(scheduler != null ? scheduler : new ScheduledExecutorScheduler());
      this.addBean(this._scheduler);
      this._bufferPool = (ByteBufferPool)(bufferPool != null ? bufferPool : new ArrayByteBufferPool());
      this.addBean(this._bufferPool);
      this.setServer(this);
      this.addBean(FileSystemPool.INSTANCE, false);
   }

   public Handler getDefaultHandler() {
      return this._defaultHandler;
   }

   public void setDefaultHandler(Handler defaultHandler) {
      if (!this.isDynamic() && this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         if (defaultHandler != null) {
            defaultHandler.setServer(this);
         }

         Handler old = this._defaultHandler;
         this._defaultHandler = defaultHandler;
         this.updateBean(old, defaultHandler);
      }
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      return next != null && next.handle(request, response, callback)
         || this._defaultHandler != null && this._defaultHandler.handle(request, response, callback);
   }

   public String getServerInfo() {
      return this._serverInfo;
   }

   public void setTempDirectory(String temp) {
      this.setTempDirectory(new File(temp));
   }

   public void setTempDirectory(File temp) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else if (temp != null && !temp.exists()) {
         throw new IllegalArgumentException("Does not exist: " + temp);
      } else if (temp != null && !temp.canWrite()) {
         throw new IllegalArgumentException("Cannot write: " + temp);
      } else {
         this._tempDirectory = temp;
      }
   }

   @ManagedAttribute(
      value = "The server temporary directory",
      readonly = true
   )
   public File getTempDirectory() {
      return this._tempDirectory;
   }

   public void setServerInfo(String serverInfo) {
      this._serverInfo = serverInfo;
   }

   public Context getContext() {
      return this._serverContext;
   }

   public Mutable getMimeTypes() {
      return this._mimeTypes;
   }

   @Override
   public InvocationType getInvocationType() {
      if (this.isDynamic()) {
         return InvocationType.BLOCKING;
      } else if (this.isStarted()) {
         return this._invocationType;
      } else {
         InvocationType type = InvocationType.NON_BLOCKING;
         Handler handler = this.getHandler();
         if (handler != null) {
            type = Invocable.combine(type, handler.getInvocationType());
         }

         handler = this.getDefaultHandler();
         if (handler != null) {
            type = Invocable.combine(type, handler.getInvocationType());
         }

         return type;
      }
   }

   public boolean isDryRun() {
      return this._dryRun;
   }

   public void setDryRun(boolean dryRun) {
      this._dryRun = dryRun;
   }

   public RequestLog getRequestLog() {
      return this._requestLog;
   }

   public Request.Handler getErrorHandler() {
      return this._errorHandler;
   }

   public void setRequestLog(RequestLog requestLog) {
      this.updateBean(this._requestLog, requestLog);
      this._requestLog = requestLog;
   }

   public void setErrorHandler(Request.Handler errorHandler) {
      this.updateBean(this._errorHandler, errorHandler);
      this._errorHandler = errorHandler;
   }

   @ManagedAttribute("The version of this server")
   public static String getVersion() {
      return Jetty.VERSION;
   }

   public void setStopTimeout(long stopTimeout) {
      this._stopTimeout = stopTimeout;
   }

   public long getStopTimeout() {
      return this._stopTimeout;
   }

   public boolean getStopAtShutdown() {
      return this._stopAtShutdown;
   }

   public void setStopAtShutdown(boolean stop) {
      if (stop) {
         if (!this._stopAtShutdown && this.isStarted()) {
            ShutdownThread.register(new LifeCycle[]{this});
         }
      } else {
         ShutdownThread.deregister(this);
      }

      this._stopAtShutdown = stop;
   }

   @ManagedAttribute(
      value = "connectors for this server",
      readonly = true
   )
   public Connector[] getConnectors() {
      List<Connector> connectors = new ArrayList<>(this._connectors);
      return connectors.toArray(new Connector[0]);
   }

   public void addConnector(Connector connector) {
      if (connector.getServer() != this) {
         throw new IllegalArgumentException("Connector " + connector + " cannot be shared among server " + connector.getServer() + " and server " + this);
      } else {
         this._connectors.add(connector);
         this.addBean(connector);
      }
   }

   public void removeConnector(Connector connector) {
      if (this._connectors.remove(connector)) {
         this.removeBean(connector);
      }
   }

   public void setConnectors(Connector[] connectors) {
      if (connectors != null) {
         for (Connector connector : connectors) {
            if (connector.getServer() != this) {
               throw new IllegalArgumentException("Connector " + connector + " cannot be shared among server " + connector.getServer() + " and server " + this);
            }
         }
      }

      Connector[] oldConnectors = this.getConnectors();
      this.updateBeans(oldConnectors, connectors);
      this._connectors.removeAll(Arrays.asList(oldConnectors));
      if (connectors != null) {
         this._connectors.addAll(Arrays.asList(connectors));
      }
   }

   public void addBeanToAllConnectors(Object bean) {
      for (Connector connector : this.getConnectors()) {
         connector.addBean(bean);
      }
   }

   @ManagedAttribute("The server Thread pool")
   public ThreadPool getThreadPool() {
      return this._threadPool;
   }

   @ManagedAttribute("The server Scheduler")
   public Scheduler getScheduler() {
      return this._scheduler;
   }

   @ManagedAttribute("The server ByteBuffer pool")
   public ByteBufferPool getByteBufferPool() {
      return this._bufferPool;
   }

   @ManagedAttribute("Whether to dump the server to stderr after start")
   public boolean isDumpAfterStart() {
      return this._dumpAfterStart;
   }

   public void setDumpAfterStart(boolean dumpAfterStart) {
      this._dumpAfterStart = dumpAfterStart;
   }

   @ManagedAttribute("Whether to dump the server to stderr before stop")
   public boolean isDumpBeforeStop() {
      return this._dumpBeforeStop;
   }

   public void setDumpBeforeStop(boolean dumpBeforeStop) {
      this._dumpBeforeStop = dumpBeforeStop;
   }

   public HttpField getDateField() {
      long now = System.currentTimeMillis();
      long seconds = now / 1000L;
      Server.DateField df = this._dateField;
      if (df == null || df._seconds != seconds) {
         AutoLock ignore = this._dateLock.lock();

         label49: {
            Object var8;
            try {
               df = this._dateField;
               if (df != null && df._seconds == seconds) {
                  break label49;
               }

               HttpField field = new ResponseHttpFields.PersistentPreEncodedHttpField(HttpHeader.DATE, DateGenerator.formatDate(now));
               this._dateField = new Server.DateField(seconds, field);
               var8 = field;
            } catch (Throwable var10) {
               if (ignore != null) {
                  try {
                     ignore.close();
                  } catch (Throwable var9) {
                     var10.addSuppressed(var9);
                  }
               }

               throw var10;
            }

            if (ignore != null) {
               ignore.close();
            }

            return (HttpField)var8;
         }

         if (ignore != null) {
            ignore.close();
         }
      }

      return df._dateField;
   }

   @Override
   protected void doStart() throws Exception {
      try {
         if (this.getStopAtShutdown()) {
            ShutdownThread.register(new LifeCycle[]{this});
         }

         ShutdownMonitor.register(this);
         ShutdownMonitor.getInstance().start();
         if (this._errorHandler == null) {
            this.setErrorHandler(new Server.DynamicErrorHandler());
         }

         String gitHash = Jetty.GIT_HASH;
         String timestamp = Jetty.BUILD_TIMESTAMP;
         LOG.info(
            "jetty-{}; built: {}; git: {}; jvm {}",
            new Object[]{getVersion(), timestamp, gitHash, System.getProperty("java.runtime.version", System.getProperty("java.version"))}
         );
         if (!Jetty.STABLE) {
            LOG.warn("THIS IS NOT A STABLE RELEASE! DO NOT USE IN PRODUCTION!");
            LOG.warn("Download a stable release from https://download.eclipse.org/jetty/");
         }

         MultiException multiException = new MultiException();
         if (!this._dryRun) {
            this._connectors.stream().filter(NetworkConnector.class::isInstance).map(NetworkConnector.class::cast).forEach(connectorx -> {
               try {
                  connectorx.open();
               } catch (Throwable var3x) {
                  multiException.add(var3x);
               }
            });
            multiException.ifExceptionThrow();
         }

         super.doStart();
         this._invocationType = this.getInvocationType();
         if (this._dryRun) {
            LOG.info(String.format("Started(dry run) %s @%dms", this, Uptime.getUptime()));
            throw new StopException(this);
         }

         for (Connector connector : this._connectors) {
            try {
               connector.start();
            } catch (Throwable var11) {
               multiException.add(var11);
               this._connectors.stream().filter(LifeCycle::isRunning).map(Object.class::cast).forEach(LifeCycle::stop);
            }
         }

         multiException.ifExceptionThrow();
         LOG.info(String.format("Started %s @%dms", this, Uptime.getUptime()));
      } catch (Throwable var12) {
         this._connectors.stream().filter(NetworkConnector.class::isInstance).map(NetworkConnector.class::cast).forEach(nc -> {
            try {
               nc.close();
            } catch (Throwable var3x) {
               if (var12 != var3x) {
                  var12.addSuppressed(var3x);
               }
            }
         });
         throw var12;
      } finally {
         if (this.isDumpAfterStart() && (!this._dryRun || !this.isDumpBeforeStop())) {
            this.dumpStdErr();
         }
      }
   }

   protected void start(LifeCycle l) throws Exception {
      if (!(l instanceof Connector)) {
         super.start(l);
      }
   }

   @Override
   protected void doStop() throws Exception {
      if (this.isDumpBeforeStop()) {
         this.dumpStdErr();
      }

      LOG.info(String.format("Stopped %s", this));
      if (LOG.isDebugEnabled()) {
         LOG.debug("doStop {}", this);
      }

      Throwable multiException = null;
      if (this.getStopTimeout() > 0L) {
         long end = NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(this.getStopTimeout());

         try {
            Graceful.shutdown(this).get(this.getStopTimeout(), TimeUnit.MILLISECONDS);
         } catch (Throwable var7) {
            multiException = ExceptionUtil.combine(multiException, var7);
         }

         QueuedThreadPool qtp = (QueuedThreadPool)this.getBean(QueuedThreadPool.class);
         if (qtp != null) {
            qtp.setStopTimeout(Math.max(1000L, NanoTime.millisUntil(end)));
         }
      }

      for (Connector connector : this._connectors) {
         try {
            connector.stop();
         } catch (Throwable var6) {
            multiException = ExceptionUtil.combine(multiException, var6);
         }
      }

      try {
         super.doStop();
      } catch (Throwable var5) {
         multiException = ExceptionUtil.combine(multiException, var5);
      }

      if (this.getErrorHandler() instanceof Server.DynamicErrorHandler) {
         this.setErrorHandler(null);
      }

      if (this.getStopAtShutdown()) {
         ShutdownThread.deregister(this);
      }

      ShutdownMonitor.deregister(this);
      ExceptionUtil.ifExceptionThrow(multiException);
   }

   public void join() throws InterruptedException {
      this.getThreadPool().join();
   }

   public Object setAttribute(String name, Object attribute) {
      return this._attributes.setAttribute(name, attribute);
   }

   public Object removeAttribute(String name) {
      return this._attributes.removeAttribute(name);
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

   public URI getURI() {
      NetworkConnector connector = null;

      for (Connector c : this._connectors) {
         if (c instanceof NetworkConnector) {
            connector = (NetworkConnector)c;
            break;
         }
      }

      if (connector == null) {
         return null;
      } else {
         ContextHandler context = this.getDescendant(ContextHandler.class);

         try {
            String protocol = connector.getDefaultConnectionFactory().getProtocol();
            String scheme = "http";
            if (protocol.startsWith("SSL-") || protocol.equals("SSL")) {
               scheme = "https";
            }

            String host = connector.getHost();
            if (host == null) {
               host = InetAddress.getLocalHost().getHostAddress();
            }

            int port = connector.getLocalPort();
            String path = "/";
            if (context != null) {
               Optional<String> vhost = context.getVirtualHosts().stream().filter(h -> !h.startsWith("*.") && !h.startsWith("@")).findFirst();
               if (vhost.isPresent()) {
                  host = vhost.get();
                  int at = host.indexOf(64);
                  if (at > 0) {
                     host = host.substring(0, at);
                  }
               }

               path = context.getContextPath();
            }

            return new URI(scheme, null, host, port, path, null, null);
         } catch (Exception var10) {
            LOG.warn("Unable to build server URI", var10);
            return null;
         }
      }
   }

   @Override
   public Server getServer() {
      return this;
   }

   public Resource getDefaultStyleSheet() {
      return this.newResource("jetty-dir.css");
   }

   public Resource getDefaultFavicon() {
      return this.newResource("favicon.ico");
   }

   private Resource newResource(String name) {
      URL url = this.getClass().getResource(name);
      if (url == null) {
         throw new IllegalStateException("Missing server resource: " + name);
      } else {
         return ResourceFactory.root().newMemoryResource(url);
      }
   }

   @Override
   public String toString() {
      return String.format("%s[%s,sto=%d]", super.toString(), getVersion(), this.getStopTimeout());
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(
         out,
         indent,
         new Object[]{
            new ClassLoaderDump(this.getClass().getClassLoader()),
            new DumpableCollection("environments", Environment.getAll()),
            new DumpableAttributes("attributes", this._attributes),
            FileSystemPool.INSTANCE
         }
      );
   }

   public static void main(String... args) {
      System.err.println(getVersion());
   }

   private static class DateField {
      final long _seconds;
      final HttpField _dateField;

      public DateField(long seconds, HttpField dateField) {
         this._seconds = seconds;
         this._dateField = dateField;
      }
   }

   private static class DynamicErrorHandler extends ErrorHandler {
   }

   class ServerContext extends org.eclipse.jetty.util.Attributes.Wrapper implements Context {
      private final File jettyBase = IO.asFile(System.getProperty("jetty.base"));
      private final File workDir = this.jettyBase != null && this.jettyBase.isDirectory() && this.jettyBase.canWrite()
         ? new File(this.jettyBase, "work")
         : null;
      private final File tempDir = this.workDir != null && this.workDir.isDirectory() && this.workDir.canWrite()
         ? this.workDir
         : IO.asFile(System.getProperty("java.io.tmpdir"));

      private ServerContext() {
         super(Server.this);
      }

      @Override
      public String getContextPath() {
         return null;
      }

      @Override
      public MimeTypes getMimeTypes() {
         return Server.this._mimeTypes;
      }

      @Override
      public ClassLoader getClassLoader() {
         return Server.class.getClassLoader();
      }

      @Override
      public Resource getBaseResource() {
         return null;
      }

      @Override
      public List<String> getVirtualHosts() {
         return Collections.emptyList();
      }

      @Override
      public void run(Runnable runnable) {
         runnable.run();
      }

      @Override
      public void run(Runnable runnable, Request request) {
         runnable.run();
      }

      @Override
      public void execute(Runnable runnable) {
         Server.this.getThreadPool().execute(runnable);
      }

      @Override
      public Request.Handler getErrorHandler() {
         return Server.this.getErrorHandler();
      }

      public <T> T decorate(T o) {
         DecoratedObjectFactory factory = (DecoratedObjectFactory)Server.this.getBean(DecoratedObjectFactory.class);
         return (T)(factory != null ? factory.decorate(o) : o);
      }

      public void destroy(Object o) {
         DecoratedObjectFactory factory = (DecoratedObjectFactory)Server.this.getBean(DecoratedObjectFactory.class);
         if (factory != null) {
            factory.destroy(o);
         }
      }

      @Override
      public String getPathInContext(String canonicallyEncodedPath) {
         return canonicallyEncodedPath;
      }

      @Override
      public File getTempDirectory() {
         return Objects.requireNonNullElse(Server.this.getTempDirectory(), this.tempDir);
      }

      @Override
      public String toString() {
         return "ServerContext@%x".formatted(Server.this.hashCode());
      }
   }
}
