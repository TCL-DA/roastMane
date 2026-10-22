package org.glassfish.jersey.jetty;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content.Sink;
import org.eclipse.jetty.security.AuthenticationState;
import org.eclipse.jetty.security.AuthenticationState.Succeeded;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Handler.Abstract;
import org.eclipse.jetty.util.Callback;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.internal.util.ExtendedLogger;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.internal.ContainerUtils;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.server.spi.ContainerResponseWriter.TimeoutHandler;

public final class JettyHttpContainer extends Abstract implements Container {
   private static final ExtendedLogger LOGGER = new ExtendedLogger(Logger.getLogger(JettyHttpContainer.class.getName()), Level.FINEST);
   private static final Type REQUEST_TYPE = (new GenericType<Ref<Request>>() {
   }).getType();
   private static final Type RESPONSE_TYPE = (new GenericType<Ref<Response>>() {
   }).getType();
   private static final int INTERNAL_SERVER_ERROR = Status.INTERNAL_SERVER_ERROR.getStatusCode();
   private static final Status BAD_REQUEST_STATUS = Status.BAD_REQUEST;
   private boolean configSetStatusOverSendError;
   private volatile ApplicationHandler appHandler;

   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      JettyHttpContainer.ResponseWriter responseWriter = new JettyHttpContainer.ResponseWriter(request, response, callback, this.configSetStatusOverSendError);

      try {
         URI baseUri = this.getBaseUri(request);
         URI requestUri = this.getRequestUri(request, baseUri);
         ContainerRequest requestContext = new ContainerRequest(
            baseUri, requestUri, request.getMethod(), this.getSecurityContext(request), new MapPropertiesDelegate(), this.appHandler.getConfiguration()
         );
         requestContext.setEntityStream(Request.asInputStream(request));
         request.getHeaders()
            .forEach(httpField -> requestContext.headers(httpField.getName(), new Object[]{httpField.getValue() == null ? "" : httpField.getValue()}));
         requestContext.setWriter(responseWriter);
         requestContext.setRequestScopedInitializer(injectionManager -> {
            ((Ref)injectionManager.getInstance(REQUEST_TYPE)).set(request);
            ((Ref)injectionManager.getInstance(RESPONSE_TYPE)).set(response);
         });
         this.appHandler.handle(requestContext);
         return true;
      } catch (URISyntaxException var8) {
         this.setResponseForInvalidUri(request, response, callback, var8);
         return true;
      } catch (Exception var9) {
         callback.failed(var9);
         throw new RuntimeException(var9);
      }
   }

   private URI getRequestUri(Request request, URI baseUri) throws URISyntaxException {
      String serverAddress = this.getServerAddress(baseUri);
      String uri = request.getHttpURI().getPath();
      String queryString = request.getHttpURI().getQuery();
      if (queryString != null) {
         uri = uri + "?" + ContainerUtils.encodeUnsafeCharacters(queryString);
      }

      return new URI(serverAddress + uri);
   }

   private void setResponseForInvalidUri(Request request, Response response, Callback callback, Throwable throwable) {
      LOGGER.log(Level.FINER, "Error while processing request.", throwable);
      if (this.configSetStatusOverSendError) {
         response.reset();
         response.setStatus(BAD_REQUEST_STATUS.getStatusCode());
         callback.failed(throwable);
      } else {
         Response.writeError(request, response, callback, BAD_REQUEST_STATUS.getStatusCode(), BAD_REQUEST_STATUS.getReasonPhrase(), throwable);
      }
   }

   private String getServerAddress(URI baseUri) {
      String serverAddress = baseUri.toString();
      return serverAddress.charAt(serverAddress.length() - 1) == '/' ? serverAddress.substring(0, serverAddress.length() - 1) : serverAddress;
   }

   private SecurityContext getSecurityContext(final Request request) {
      final Succeeded authenticationState = AuthenticationState.authenticate(request);
      return new SecurityContext() {
         public boolean isUserInRole(String role) {
            return authenticationState != null && authenticationState.isUserInRole(role);
         }

         public boolean isSecure() {
            return request.isSecure();
         }

         public Principal getUserPrincipal() {
            return authenticationState != null ? authenticationState.getUserIdentity().getUserPrincipal() : null;
         }

         public String getAuthenticationScheme() {
            return authenticationState != null ? authenticationState.getAuthenticationType() : null;
         }
      };
   }

   private URI getBaseUri(Request request) throws URISyntaxException {
      return new URI(
         request.getHttpURI().getScheme(), null, Request.getServerName(request), Request.getServerPort(request), this.getBasePath(request), null, null
      );
   }

   private String getBasePath(Request request) {
      String contextPath = Request.getContextPath(request);
      if (contextPath != null && !contextPath.isEmpty()) {
         return contextPath.charAt(contextPath.length() - 1) != '/' ? contextPath + "/" : contextPath;
      } else {
         return "/";
      }
   }

   public ResourceConfig getConfiguration() {
      return this.appHandler.getConfiguration();
   }

   public void reload() {
      this.reload(new ResourceConfig(this.getConfiguration()));
   }

   public void reload(ResourceConfig configuration) {
      this.appHandler.onShutdown(this);
      this.appHandler = new ApplicationHandler(configuration.register(new JettyHttpContainer.JettyBinder()));
      this.appHandler.onReload(this);
      this.appHandler.onStartup(this);
      this.cacheConfigSetStatusOverSendError();
   }

   public ApplicationHandler getApplicationHandler() {
      return this.appHandler;
   }

   protected void doStart() throws Exception {
      super.doStart();
      this.appHandler.onStartup(this);
   }

   public void doStop() throws Exception {
      super.doStop();
      this.appHandler.onShutdown(this);
      this.appHandler = null;
   }

   JettyHttpContainer(Application application, Object parentContext) {
      this.appHandler = new ApplicationHandler(application, new JettyHttpContainer.JettyBinder(), parentContext);
   }

   JettyHttpContainer(Application application) {
      this.appHandler = new ApplicationHandler(application, new JettyHttpContainer.JettyBinder());
      this.cacheConfigSetStatusOverSendError();
   }

   JettyHttpContainer(Class<? extends Application> applicationClass) {
      this.appHandler = new ApplicationHandler(applicationClass, new JettyHttpContainer.JettyBinder());
      this.cacheConfigSetStatusOverSendError();
   }

   private void cacheConfigSetStatusOverSendError() {
      this.configSetStatusOverSendError = (Boolean)ServerProperties.getValue(
         this.getConfiguration().getProperties(), "jersey.config.server.response.setStatusOverSendError", false, Boolean.class
      );
   }

   private static class JettyBinder extends AbstractBinder {
      private JettyBinder() {
      }

      protected void configure() {
         ((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(JettyHttpContainer.JettyRequestReferencingFactory.class).to(Request.class))
               .proxy(false))
            .in(RequestScoped.class);
         ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<Request>>() {
         })).in(RequestScoped.class);
         ((SupplierClassBinding)((SupplierClassBinding)this.bindFactory(JettyHttpContainer.JettyResponseReferencingFactory.class).to(Response.class))
               .proxy(false))
            .in(RequestScoped.class);
         ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to(new GenericType<Ref<Response>>() {
         })).in(RequestScoped.class);
      }
   }

   private static class JettyRequestReferencingFactory extends ReferencingFactory<Request> {
      @Inject
      public JettyRequestReferencingFactory(Provider<Ref<Request>> referenceFactory) {
         super(referenceFactory);
      }
   }

   private static class JettyResponseReferencingFactory extends ReferencingFactory<Response> {
      @Inject
      public JettyResponseReferencingFactory(Provider<Ref<Response>> referenceFactory) {
         super(referenceFactory);
      }
   }

   private static final class ResponseWriter implements ContainerResponseWriter {
      private final Request request;
      private final Response response;
      private final Callback callback;
      private final boolean configSetStatusOverSendError;
      private final Timer timer = new Timer();
      private final long asyncStartTimeMillis;
      private final ConcurrentLinkedQueue<TimeoutHandler> timeoutHandlerQueue = new ConcurrentLinkedQueue<>();
      private TimerTask currentTimerTask;

      ResponseWriter(Request request, Response response, Callback callback, boolean configSetStatusOverSendError) {
         this.request = request;
         this.response = response;
         this.callback = callback;
         this.asyncStartTimeMillis = System.currentTimeMillis();
         this.configSetStatusOverSendError = configSetStatusOverSendError;
      }

      private synchronized void setNewTimeout(long timeOut, TimeUnit timeUnit) {
         long timeOutMillis = timeUnit.toMillis(timeOut);
         if (this.currentTimerTask != null) {
            this.currentTimerTask.cancel();
         }

         this.currentTimerTask = new TimerTask() {
            @Override
            public void run() {
               ResponseWriter.this.timeoutHandlerQueue.forEach(timeoutHandler -> timeoutHandler.onTimeout(ResponseWriter.this));
            }
         };
         this.timer.schedule(this.currentTimerTask, Math.max(0L, timeOutMillis + this.asyncStartTimeMillis - System.currentTimeMillis()));
      }

      public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse context) throws ContainerException {
         StatusType statusInfo = context.getStatusInfo();
         int code = statusInfo.getStatusCode();
         this.response.setStatus(code);
         if (contentLength != -1L && contentLength < 2147483647L && !"HEAD".equals(this.request.getMethod())) {
            this.response.getHeaders().add(new HttpField(HttpHeader.CONTENT_LENGTH, String.valueOf((int)contentLength)));
         }

         for (Entry<String, List<String>> e : context.getStringHeaders().entrySet()) {
            for (String value : e.getValue()) {
               this.response.getHeaders().add(new HttpField(e.getKey(), value));
            }
         }

         return Sink.asOutputStream(this.response);
      }

      public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
         if (timeOut > 0L) {
            this.setNewTimeout(timeOut, timeUnit);
         }

         if (timeoutHandler != null) {
            this.timeoutHandlerQueue.add(timeoutHandler);
         }

         return true;
      }

      public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
         if (timeOut > 0L) {
            this.setNewTimeout(timeOut, timeUnit);
         }
      }

      public void commit() {
         this.callback.succeeded();
         JettyHttpContainer.LOGGER.log(Level.FINEST, "commit() called");
      }

      public void failure(Throwable error) {
         try {
            if (!this.response.isCommitted()) {
               try {
                  if (this.configSetStatusOverSendError) {
                     this.response.reset();
                     this.response.setStatus(JettyHttpContainer.INTERNAL_SERVER_ERROR);
                     this.callback.failed(error);
                  } else {
                     Response.writeError(this.request, this.response, this.callback, JettyHttpContainer.INTERNAL_SERVER_ERROR, "Request failed.", error);
                  }
               } catch (IllegalStateException var6) {
                  JettyHttpContainer.LOGGER.log(Level.FINER, "Unable to reset failed response.", var6);
               }
            }
         } finally {
            JettyHttpContainer.LOGGER.log(Level.FINEST, "failure(...) called");
            this.rethrow(error);
         }
      }

      public boolean enableResponseBuffering() {
         return false;
      }

      private void rethrow(Throwable error) {
         if (error instanceof RuntimeException) {
            throw (RuntimeException)error;
         } else {
            throw new ContainerException(error);
         }
      }
   }
}
