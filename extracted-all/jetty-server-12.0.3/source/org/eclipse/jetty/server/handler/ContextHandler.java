package org.eclipse.jetty.server.handler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.http.MimeTypes.Mutable;
import org.eclipse.jetty.server.AliasCheck;
import org.eclipse.jetty.server.Context;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SymlinkAllowedResourceAliasChecker;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.DecoratedObjectFactory;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.TypeUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.Attributes.Layer;
import org.eclipse.jetty.util.Attributes.Mapped;
import org.eclipse.jetty.util.Index.Builder;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.component.ClassLoaderDump;
import org.eclipse.jetty.util.component.DumpableAttributes;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;
import org.eclipse.jetty.util.thread.Invocable.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject
public class ContextHandler extends Handler.Wrapper implements Attributes, AliasCheck {
   private static final Logger LOG = LoggerFactory.getLogger(ContextHandler.class);
   private static final ThreadLocal<Context> __context = new ThreadLocal<>();
   public static final String MANAGED_ATTRIBUTES = "org.eclipse.jetty.server.context.ManagedAttributes";
   private final ContextHandler.ScopedContext _context;
   private final Attributes _persistentAttributes = new Mapped();
   private final org.eclipse.jetty.http.MimeTypes.Wrapper _mimeTypes = new org.eclipse.jetty.http.MimeTypes.Wrapper();
   private final List<ContextHandler.ContextScopeListener> _contextListeners = new CopyOnWriteArrayList<>();
   private final List<ContextHandler.VHost> _vhosts = new ArrayList<>();
   private String _displayName;
   private String _contextPath = "/";
   private boolean _rootContext = true;
   private Resource _baseResource;
   private ClassLoader _classLoader;
   private Request.Handler _errorHandler;
   private boolean _allowNullPathInContext;
   private Index<ContextHandler.ProtectedTargetType> _protectedTargets = Index.empty(false);
   private final List<AliasCheck> _aliasChecks = new CopyOnWriteArrayList<>();
   private File _tempDirectory;
   private boolean _tempDirectoryPersisted = false;
   private boolean _tempDirectoryCreated = false;
   private final AtomicReference<ContextHandler.Availability> _availability = new AtomicReference<>(ContextHandler.Availability.STOPPED);

   public static Context getCurrentContext() {
      return __context.get();
   }

   public static Context getCurrentContext(Server server) {
      Context context = __context.get();
      return context == null ? (server == null ? null : server.getContext()) : context;
   }

   public static ContextHandler getCurrentContextHandler() {
      return getCurrentContext() instanceof ContextHandler.ScopedContext scopedContext ? scopedContext.getContextHandler() : null;
   }

   public static ContextHandler getContextHandler(Request request) {
      ContextRequest contextRequest = Request.as(request, ContextRequest.class);
      if (contextRequest == null) {
         return null;
      } else {
         return contextRequest.getContext() instanceof ContextHandler.ScopedContext scoped ? scoped.getContextHandler() : null;
      }
   }

   public ContextHandler() {
      this(null, null);
   }

   public ContextHandler(Handler handler) {
      this(handler, null);
   }

   public ContextHandler(String contextPath) {
      this(null, contextPath);
   }

   public ContextHandler(Handler handler, String contextPath) {
      super(handler);
      this._context = this.newContext();
      if (contextPath != null) {
         this.setContextPath(contextPath);
      }

      if (File.separatorChar == '/') {
         this.addAliasCheck(new SymlinkAllowedResourceAliasChecker(this));
      }

      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      if (classLoader == null) {
         classLoader = this.getClass().getClassLoader();
      }

      if (classLoader != Server.class.getClassLoader()) {
         this._classLoader = classLoader;
      }
   }

   @Override
   public void setServer(Server server) {
      super.setServer(server);
      this._mimeTypes.setWrapped(server.getMimeTypes());
   }

   protected ContextHandler.ScopedContext newContext() {
      return new ContextHandler.ScopedContext();
   }

   @ManagedAttribute(
      value = "temporary directory location",
      readonly = true
   )
   public File getTempDirectory() {
      return this._tempDirectory;
   }

   public void setTempDirectory(File tempDirectory) {
      if (this.isStarted()) {
         throw new IllegalStateException("Started");
      } else {
         if (tempDirectory != null) {
            try {
               tempDirectory = new File(tempDirectory.getCanonicalPath());
            } catch (IOException var3) {
               LOG.warn("Unable to find canonical path for {}", tempDirectory, var3);
            }
         }

         this._tempDirectory = tempDirectory;
      }
   }

   public void setTempDirectoryPersistent(boolean persist) {
      this._tempDirectoryPersisted = persist;
   }

   public boolean isTempDirectoryPersistent() {
      return this._tempDirectoryPersisted;
   }

   public Mutable getMimeTypes() {
      return this._mimeTypes;
   }

   public void dump(Appendable out, String indent) throws IOException {
      this.dumpObjects(
         out,
         indent,
         new Object[]{
            new ClassLoaderDump(this.getClassLoader()),
            new DumpableAttributes("handler attributes", this._persistentAttributes),
            new DumpableAttributes("attributes", this._context)
         }
      );
   }

   @ManagedAttribute("Context")
   public ContextHandler.ScopedContext getContext() {
      return this._context;
   }

   @ManagedAttribute("Checks if the /context is not redirected to /context/")
   public boolean getAllowNullPathInContext() {
      return this._allowNullPathInContext;
   }

   public void setAllowNullPathInContext(boolean allowNullPathInContext) {
      this._allowNullPathInContext = allowNullPathInContext;
   }

   public void setVirtualHosts(List<String> vhosts) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._vhosts.clear();
         if (vhosts != null && !vhosts.isEmpty()) {
            for (String vhost : vhosts) {
               if (vhost != null) {
                  boolean wild = false;
                  String connector = null;
                  int at = vhost.indexOf(64);
                  if (at >= 0) {
                     connector = vhost.substring(at + 1);
                     vhost = vhost.substring(0, at);
                  }

                  if (StringUtil.isBlank(vhost)) {
                     vhost = null;
                  } else if (vhost.startsWith("*.")) {
                     vhost = vhost.substring(1);
                     wild = true;
                  }

                  this._vhosts.add(new ContextHandler.VHost(this.normalizeHostname(vhost), wild, connector));
               }
            }
         }
      }
   }

   public void addVirtualHosts(String... virtualHosts) {
      if (virtualHosts != null && virtualHosts.length != 0) {
         List<String> vhosts = new ArrayList<>(this.getVirtualHosts());
         vhosts.addAll(Arrays.asList(virtualHosts));
         this.setVirtualHosts(vhosts);
      }
   }

   public void removeVirtualHosts(String... virtualHosts) {
      List<String> vhosts = new ArrayList<>(this.getVirtualHosts());
      if (virtualHosts != null && virtualHosts.length != 0 && !vhosts.isEmpty()) {
         for (String vh : virtualHosts) {
            vhosts.remove(this.normalizeHostname(vh));
         }
      }
   }

   @ManagedAttribute(
      value = "Virtual hosts accepted by the context",
      readonly = true
   )
   public List<String> getVirtualHosts() {
      return this._vhosts.stream().map(ContextHandler.VHost::getName).collect(Collectors.toList());
   }

   public Object getAttribute(String name) {
      return this._persistentAttributes.getAttribute(name);
   }

   public Set<String> getAttributeNameSet() {
      return this._persistentAttributes.getAttributeNameSet();
   }

   public Object setAttribute(String name, Object attribute) {
      return this._persistentAttributes.setAttribute(name, attribute);
   }

   public Object removeAttribute(String name) {
      return this._persistentAttributes.removeAttribute(name);
   }

   public ClassLoader getClassLoader() {
      return this._classLoader;
   }

   public void setClassLoader(ClassLoader contextLoader) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._classLoader = contextLoader;
      }
   }

   @ManagedAttribute("The file classpath")
   public String getClassPath() {
      if (this._classLoader != null && this._classLoader instanceof URLClassLoader loader) {
         String classpath = URIUtil.streamOf(loader).map(URI::toASCIIString).collect(Collectors.joining(File.pathSeparator));
         return StringUtil.isBlank(classpath) ? null : classpath;
      } else {
         return null;
      }
   }

   @ManagedAttribute("Context path of the Context")
   public String getContextPath() {
      return this._contextPath;
   }

   @ManagedAttribute("Display name of the Context")
   public String getDisplayName() {
      if (this._displayName != null) {
         return this._displayName;
      } else {
         return "/".equals(this._contextPath) ? "ROOT" : this._contextPath;
      }
   }

   public boolean addEventListener(EventListener listener) {
      if (super.addEventListener(listener)) {
         if (listener instanceof ContextHandler.ContextScopeListener) {
            this._contextListeners.add((ContextHandler.ContextScopeListener)listener);
            if (__context.get() != null) {
               ((ContextHandler.ContextScopeListener)listener).enterScope(__context.get(), null);
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean removeEventListener(EventListener listener) {
      if (super.removeEventListener(listener)) {
         if (listener instanceof ContextHandler.ContextScopeListener) {
            this._contextListeners.remove(listener);
         }

         return true;
      } else {
         return false;
      }
   }

   protected ClassLoader enterScope(Request contextRequest) {
      ClassLoader lastLoader = Thread.currentThread().getContextClassLoader();
      __context.set(this._context);
      if (this._classLoader != null) {
         Thread.currentThread().setContextClassLoader(this._classLoader);
      }

      this.notifyEnterScope(contextRequest);
      return lastLoader;
   }

   protected void notifyEnterScope(Request request) {
      for (ContextHandler.ContextScopeListener listener : this._contextListeners) {
         try {
            listener.enterScope(this._context, request);
         } catch (Throwable var5) {
            LOG.warn("Unable to enter scope", var5);
         }
      }
   }

   protected void exitScope(Request request, Context lastContext, ClassLoader lastLoader) {
      this.notifyExitScope(request);
      __context.set(lastContext);
      Thread.currentThread().setContextClassLoader(lastLoader);
   }

   protected void notifyExitScope(Request request) {
      int i = this._contextListeners.size();

      while (i-- > 0) {
         try {
            this._contextListeners.get(i).exitScope(this._context, request);
         } catch (Throwable var4) {
            LOG.warn("Unable to exit scope", var4);
         }
      }
   }

   public boolean isAvailable() {
      return this._availability.get() == ContextHandler.Availability.AVAILABLE && this.isStarted();
   }

   public void setAvailable(boolean available) {
      if (!available) {
         label20:
         while (true) {
            ContextHandler.Availability availability = this._availability.get();
            switch (availability) {
               case STARTING:
               case AVAILABLE:
                  if (this._availability.compareAndSet(availability, ContextHandler.Availability.UNAVAILABLE)) {
                     break label20;
                  }
                  break;
               default:
                  return;
            }
         }
      } else {
         while (true) {
            ContextHandler.Availability availability = this._availability.get();
            switch (availability) {
               case UNAVAILABLE:
                  if (!this._availability.compareAndSet(availability, ContextHandler.Availability.AVAILABLE)) {
                     break;
                  }
               case AVAILABLE:
                  return;
               default:
                  throw new IllegalStateException(availability.toString());
            }
         }
      }
   }

   @Override
   protected void doStart() throws Exception {
      if (this.getContextPath() == null) {
         throw new IllegalStateException("Null contextPath");
      } else {
         Resource baseResource = this.getBaseResource();
         if (baseResource != null && !Resources.isReadable(baseResource)) {
            throw new IllegalArgumentException("Base Resource is not valid: " + baseResource);
         } else {
            this._availability.set(ContextHandler.Availability.STARTING);

            try {
               this.createTempDirectory();
               this._context.call(() -> super.doStart(), null);
               this._availability.compareAndSet(ContextHandler.Availability.STARTING, ContextHandler.Availability.AVAILABLE);
               LOG.info("Started {}", this);
            } finally {
               this._availability.compareAndSet(ContextHandler.Availability.STARTING, ContextHandler.Availability.UNAVAILABLE);
            }
         }
      }
   }

   protected void createTempDirectory() {
      File tempDirectory = this.getTempDirectory();
      if (tempDirectory != null && !this._tempDirectoryCreated) {
         this._tempDirectoryCreated = true;
         if (this.isTempDirectoryPersistent()) {
            if (!tempDirectory.exists() && !tempDirectory.mkdirs()) {
               throw new IllegalArgumentException("Unable to create temp dir: " + tempDirectory);
            }
         } else {
            if (tempDirectory.exists() && !IO.delete(tempDirectory)) {
               throw new IllegalArgumentException("Failed to delete temp dir: " + tempDirectory);
            }

            if (!tempDirectory.mkdirs()) {
               throw new IllegalArgumentException("Unable to create temp dir: " + tempDirectory);
            }

            tempDirectory.deleteOnExit();
         }

         if (!tempDirectory.canWrite() || !tempDirectory.isDirectory()) {
            throw new IllegalArgumentException(
               "Temp dir " + tempDirectory + " not useable: writeable=" + tempDirectory.canWrite() + ", dir=" + tempDirectory.isDirectory()
            );
         }
      }
   }

   @Override
   protected void doStop() throws Exception {
      this._context.call(() -> super.doStop(), null);
      File tempDirectory = this.getTempDirectory();
      if (tempDirectory != null && tempDirectory.exists() && !this.isTempDirectoryPersistent()) {
         IO.delete(tempDirectory);
      }

      this._tempDirectoryCreated = false;
   }

   public boolean checkVirtualHost(Request request) {
      if (this._vhosts.isEmpty()) {
         return true;
      } else {
         String host = this.normalizeHostname(request.getHttpURI().getHost());
         String connectorName = request.getConnectionMetaData().getConnector().getName();

         for (ContextHandler.VHost vhost : this._vhosts) {
            String contextVhost = vhost._vHost;
            String contextVConnector = vhost._vConnector;
            if (contextVConnector != null) {
               if (!contextVConnector.equalsIgnoreCase(connectorName)) {
                  continue;
               }

               if (contextVhost == null) {
                  return true;
               }
            }

            if (contextVhost != null) {
               if (vhost._wild) {
                  int index = host.indexOf(".");
                  if (index >= 0 && host.substring(index).equalsIgnoreCase(contextVhost)) {
                     return true;
                  }
               } else if (host.equalsIgnoreCase(contextVhost)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   @Override
   public void destroy() {
      this._context.run(() -> super.destroy());
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler handler = this.getHandler();
      if (handler == null || !this.isStarted()) {
         return false;
      } else if (!this.checkVirtualHost(request)) {
         return false;
      } else {
         String path = request.getHttpURI().getCanonicalPath();
         String pathInContext = this._context.getPathInContext(path);
         if (pathInContext == null) {
            return false;
         } else if (!this.isAvailable()) {
            this.handleUnavailable(request, response, callback);
            return true;
         } else if (pathInContext.length() == 0 && !this.getAllowNullPathInContext()) {
            this.handleMovedPermanently(request, response, callback);
            return true;
         } else {
            ContextRequest contextRequest = this.wrapRequest(request, response);
            if (contextRequest == null) {
               return false;
            } else if (this.handleByContextHandler(pathInContext, contextRequest, response, callback)) {
               return true;
            } else {
               ClassLoader lastLoader = this.enterScope(contextRequest);
               ContextResponse contextResponse = this.wrapResponse(contextRequest, response);

               boolean var11;
               try {
                  return handler.handle(contextRequest, contextResponse, callback);
               } catch (Throwable var15) {
                  Response.writeError(contextRequest, contextResponse, callback, var15);
                  var11 = true;
               } finally {
                  this.exitScope(contextRequest, request.getContext(), lastLoader);
               }

               return var11;
            }
         }
      }
   }

   protected boolean handleByContextHandler(String pathInContext, ContextRequest request, Response response, Callback callback) {
      return false;
   }

   protected void handleMovedPermanently(Request request, Response response, Callback callback) {
      String location = this._contextPath + "/";
      if (request.getHttpURI().getParam() != null) {
         location = location + ";" + request.getHttpURI().getParam();
      }

      if (request.getHttpURI().getQuery() != null) {
         location = location + "?" + request.getHttpURI().getQuery();
      }

      response.setStatus(301);
      response.getHeaders().add(new HttpField(HttpHeader.LOCATION, location));
      callback.succeeded();
   }

   protected void handleUnavailable(Request request, Response response, Callback callback) {
      Response.writeError(request, response, callback, 503, null);
   }

   public void setContextPath(String contextPath) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._contextPath = URIUtil.canonicalPath(Objects.requireNonNull(contextPath));
         this._rootContext = "/".equals(contextPath);
      }
   }

   public void setDisplayName(String servletContextName) {
      this._displayName = servletContextName;
   }

   @ManagedAttribute(
      value = "document root for context",
      readonly = true
   )
   public Resource getBaseResource() {
      return this._baseResource;
   }

   public void setBaseResource(Resource resourceBase) {
      if (this.isStarted()) {
         throw new IllegalStateException(this.getState());
      } else {
         this._baseResource = resourceBase;
      }
   }

   public void setBaseResourceAsPath(Path path) {
      this.setBaseResource(path == null ? null : ResourceFactory.of(this).newResource(path));
   }

   public void setBaseResourceAsString(String base) {
      this.setBaseResource(base == null ? null : ResourceFactory.of(this).newResource(base));
   }

   @ManagedAttribute("The error handler to use for the context")
   public Request.Handler getErrorHandler() {
      return this._errorHandler;
   }

   public void setErrorHandler(Request.Handler errorHandler) {
      this.updateBean(this._errorHandler, errorHandler, true);
      this._errorHandler = errorHandler;
   }

   protected ContextRequest wrapRequest(Request request, Response response) {
      return new ContextRequest(this._context, request);
   }

   protected ContextResponse wrapResponse(ContextRequest request, Response response) {
      return new ContextResponse(this._context, request, response);
   }

   public void clearAttributes() {
      this._persistentAttributes.clearAttributes();
   }

   public boolean isProtectedTarget(String target) {
      if (target != null && !this._protectedTargets.isEmpty()) {
         if (target.startsWith("//")) {
            target = URIUtil.compactPath(target);
         }

         ContextHandler.ProtectedTargetType type = (ContextHandler.ProtectedTargetType)this._protectedTargets.getBest(target);
         return type == ContextHandler.ProtectedTargetType.PREFIX
            || type == ContextHandler.ProtectedTargetType.EXACT && this._protectedTargets.get(target) == ContextHandler.ProtectedTargetType.EXACT;
      } else {
         return false;
      }
   }

   public void setProtectedTargets(String[] targets) {
      Builder<ContextHandler.ProtectedTargetType> builder = new Builder();
      if (targets != null) {
         for (String t : targets) {
            if (!t.startsWith("/")) {
               throw new IllegalArgumentException("Bad protected target: " + t);
            }

            builder.with(t, ContextHandler.ProtectedTargetType.EXACT);
            builder.with(t + "/", ContextHandler.ProtectedTargetType.PREFIX);
            builder.with(t + "?", ContextHandler.ProtectedTargetType.PREFIX);
            builder.with(t + "#", ContextHandler.ProtectedTargetType.PREFIX);
            builder.with(t + ";", ContextHandler.ProtectedTargetType.PREFIX);
         }
      }

      this._protectedTargets = builder.caseSensitive(false).build();
   }

   public String[] getProtectedTargets() {
      return this._protectedTargets == null
         ? null
         : this._protectedTargets
            .keySet()
            .stream()
            .filter(s -> this._protectedTargets.get(s) == ContextHandler.ProtectedTargetType.EXACT)
            .toArray(String[]::new);
   }

   public void addAliasCheck(AliasCheck check) {
      this._aliasChecks.add(check);
      if (check instanceof LifeCycle) {
         this.addManaged((LifeCycle)check);
      } else {
         this.addBean(check);
      }
   }

   public List<AliasCheck> getAliasChecks() {
      return Collections.unmodifiableList(this._aliasChecks);
   }

   public void setAliasChecks(List<AliasCheck> checks) {
      this.clearAliasChecks();
      checks.forEach(this::addAliasCheck);
   }

   public void clearAliasChecks() {
      this._aliasChecks.forEach(this::removeBean);
      this._aliasChecks.clear();
   }

   @Override
   public boolean checkAlias(String pathInContext, Resource resource) {
      if (resource.isAlias()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Aliased resource: {} -> {}", resource, resource.getRealURI());
         }

         for (AliasCheck check : this._aliasChecks) {
            if (check.checkAlias(pathInContext, resource)) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Aliased resource: {} approved by {}", resource, check);
               }

               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public String toString() {
      List<String> vhosts = this.getVirtualHosts();
      StringBuilder b = new StringBuilder();
      b.append(TypeUtil.toShortName(this.getClass())).append('@').append(Integer.toString(this.hashCode(), 16));
      b.append('{');
      if (this.getDisplayName() != null) {
         b.append(this.getDisplayName()).append(',');
      }

      b.append(this.getContextPath());
      b.append(",b=").append(this.getBaseResource());
      b.append(",a=").append(this._availability.get());
      if (!vhosts.isEmpty()) {
         b.append(",vh=[");
         b.append(String.join(",", vhosts));
         b.append(']');
      }

      Handler nestedHandler = this.getHandler();
      if (nestedHandler != null) {
         b.append(",h=");
         b.append(nestedHandler);
      }

      b.append('}');
      return b.toString();
   }

   private String normalizeHostname(String host) {
      if (host == null) {
         return null;
      } else {
         int connectorIndex = host.indexOf(64);
         String connector = null;
         if (connectorIndex > 0) {
            host = host.substring(0, connectorIndex);
            connector = host.substring(connectorIndex);
         }

         if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
         }

         if (connector != null) {
            host = host + connector;
         }

         return host;
      }
   }

   public static enum Availability {
      STOPPED,
      STARTING,
      AVAILABLE,
      UNAVAILABLE;
   }

   public interface ContextScopeListener extends EventListener {
      default void enterScope(Context context, Request request) {
      }

      default void exitScope(Context context, Request request) {
      }
   }

   private static enum ProtectedTargetType {
      EXACT,
      PREFIX;
   }

   public class ScopedContext extends Layer implements Context {
      public ScopedContext() {
         super(ContextHandler.this._persistentAttributes);
      }

      public <H extends ContextHandler> H getContextHandler() {
         return (H)ContextHandler.this;
      }

      @Override
      public Request.Handler getErrorHandler() {
         Request.Handler handler = ContextHandler.this.getErrorHandler();
         if (handler == null) {
            handler = ContextHandler.this.getServer().getErrorHandler();
         }

         return handler;
      }

      @Override
      public String getContextPath() {
         return ContextHandler.this._contextPath;
      }

      @Override
      public MimeTypes getMimeTypes() {
         return ContextHandler.this._mimeTypes;
      }

      @Override
      public String toString() {
         return "%s@%x".formatted(this.getClass().getSimpleName(), ContextHandler.this.hashCode());
      }

      @Override
      public ClassLoader getClassLoader() {
         return ContextHandler.this._classLoader;
      }

      @Override
      public Resource getBaseResource() {
         return ContextHandler.this._baseResource;
      }

      @Override
      public File getTempDirectory() {
         File tempDirectory = ContextHandler.this.getTempDirectory();
         if (tempDirectory == null) {
            tempDirectory = ContextHandler.this.getServer().getTempDirectory();
         }

         return tempDirectory;
      }

      @Override
      public List<String> getVirtualHosts() {
         return ContextHandler.this.getVirtualHosts();
      }

      public void call(Callable callable, Request request) throws Exception {
         Context lastContext = ContextHandler.__context.get();
         if (lastContext == this) {
            callable.call();
         } else {
            ClassLoader lastLoader = ContextHandler.this.enterScope(request);

            try {
               callable.call();
            } finally {
               ContextHandler.this.exitScope(request, lastContext, lastLoader);
            }
         }
      }

      public <T> boolean test(Predicate<T> predicate, T t, Request request) {
         Context lastContext = ContextHandler.__context.get();
         if (lastContext == this) {
            return predicate.test(t);
         } else {
            ClassLoader lastLoader = ContextHandler.this.enterScope(request);

            boolean var6;
            try {
               var6 = predicate.test(t);
            } finally {
               ContextHandler.this.exitScope(request, lastContext, lastLoader);
            }

            return var6;
         }
      }

      public void accept(Consumer<Throwable> consumer, Throwable t, Request request) {
         Context lastContext = ContextHandler.__context.get();
         if (lastContext == this) {
            consumer.accept(t);
         } else {
            ClassLoader lastLoader = ContextHandler.this.enterScope(request);

            try {
               consumer.accept(t);
            } finally {
               ContextHandler.this.exitScope(request, lastContext, lastLoader);
            }
         }
      }

      @Override
      public void run(Runnable runnable) {
         this.run(runnable, null);
      }

      @Override
      public void run(Runnable runnable, Request request) {
         Context lastContext = ContextHandler.__context.get();
         if (lastContext == this) {
            runnable.run();
         } else {
            ClassLoader lastLoader = ContextHandler.this.enterScope(request);

            try {
               runnable.run();
            } finally {
               ContextHandler.this.exitScope(request, lastContext, lastLoader);
            }
         }
      }

      @Override
      public void execute(Runnable runnable) {
         ContextHandler.this.getServer().getContext().execute(() -> this.run(runnable));
      }

      protected DecoratedObjectFactory getDecoratedObjectFactory() {
         DecoratedObjectFactory factory = (DecoratedObjectFactory)ContextHandler.this.getBean(DecoratedObjectFactory.class);
         return factory != null ? factory : (DecoratedObjectFactory)ContextHandler.this.getServer().getBean(DecoratedObjectFactory.class);
      }

      public <T> T decorate(T o) {
         DecoratedObjectFactory factory = this.getDecoratedObjectFactory();
         return (T)(factory != null ? factory.decorate(o) : o);
      }

      public void destroy(Object o) {
         DecoratedObjectFactory factory = this.getDecoratedObjectFactory();
         if (factory != null) {
            factory.destroy(o);
         }
      }

      @Override
      public String getPathInContext(String canonicallyEncodedPath) {
         return ContextHandler.this._rootContext ? canonicallyEncodedPath : Context.getPathInContext(ContextHandler.this._contextPath, canonicallyEncodedPath);
      }
   }

   private static class VHost {
      private final String _vHost;
      private final boolean _wild;
      private final String _vConnector;

      private VHost(String vHost, boolean wild, String vConnector) {
         this._vHost = vHost;
         this._wild = wild;
         this._vConnector = vConnector;
      }

      String getVHost() {
         return this._vHost;
      }

      String getName() {
         return this._vConnector != null ? "@" + this._vConnector : this._vHost;
      }

      @Override
      public String toString() {
         return "VHost{_vHost='" + this._vHost + "', _wild=" + this._wild + ", _vConnector='" + this._vConnector + "'}";
      }
   }
}
