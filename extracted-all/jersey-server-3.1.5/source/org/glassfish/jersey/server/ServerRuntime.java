package org.glassfish.jersey.server;

import jakarta.inject.Provider;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.CompletionCallback;
import jakarta.ws.rs.container.ConnectionCallback;
import jakarta.ws.rs.container.TimeoutHandler;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.Closure;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.internal.util.collection.Refs;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.message.internal.HeaderValueException;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.glassfish.jersey.message.internal.TracingLogger;
import org.glassfish.jersey.message.internal.HeaderValueException.Context;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse.Builder;
import org.glassfish.jersey.message.internal.OutboundMessageContext.StreamProvider;
import org.glassfish.jersey.message.internal.TracingLogger.Level;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.process.internal.Stage;
import org.glassfish.jersey.process.internal.Stages;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.server.internal.monitoring.EmptyRequestEventBuilder;
import org.glassfish.jersey.server.internal.monitoring.RequestEventBuilder;
import org.glassfish.jersey.server.internal.monitoring.RequestEventImpl;
import org.glassfish.jersey.server.internal.process.Endpoint;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.server.spi.ExternalRequestContext;
import org.glassfish.jersey.server.spi.ExternalRequestScope;
import org.glassfish.jersey.server.spi.ResponseErrorMapper;
import org.glassfish.jersey.spi.ExceptionMappers;

public class ServerRuntime {
   private final Stage<RequestProcessingContext> requestProcessingRoot;
   private final ProcessingProviders processingProviders;
   private final InjectionManager injectionManager;
   private final ScheduledExecutorService backgroundScheduler;
   private final Provider<ExecutorService> managedAsyncExecutor;
   private final RequestScope requestScope;
   private final ExceptionMappers exceptionMappers;
   private final ApplicationEventListener applicationEventListener;
   private final Configuration configuration;
   private final ExternalRequestScope externalRequestScope;
   private final TracingConfig tracingConfig;
   private final Level tracingThreshold;
   private final boolean processResponseErrors;
   private final boolean disableLocationHeaderRelativeUriResolution;
   private final boolean rfc7231LocationHeaderRelativeUriResolution;
   private static final ExceptionMapper<Throwable> DEFAULT_EXCEPTION_MAPPER = new DefaultExceptionMapper();

   static ServerRuntime createServerRuntime(
      InjectionManager injectionManager,
      ServerBootstrapBag bootstrapBag,
      Stage<RequestProcessingContext> processingRoot,
      ApplicationEventListener eventListener,
      ProcessingProviders processingProviders
   ) {
      ScheduledExecutorService scheduledExecutorServiceSupplier = (ScheduledExecutorService)injectionManager.getInstance(
         ScheduledExecutorService.class, new Annotation[]{BackgroundSchedulerLiteral.INSTANCE}
      );
      Provider<ExecutorService> asyncExecutorServiceSupplier = () -> (ExecutorService)injectionManager.getInstance(
            ExecutorService.class, new Annotation[]{ManagedAsyncExecutorLiteral.INSTANCE}
         );
      return new ServerRuntime(
         processingRoot,
         processingProviders,
         injectionManager,
         scheduledExecutorServiceSupplier,
         asyncExecutorServiceSupplier,
         bootstrapBag.getRequestScope(),
         bootstrapBag.getExceptionMappers(),
         eventListener,
         (ExternalRequestScope)injectionManager.getInstance(ExternalRequestScope.class),
         bootstrapBag.getConfiguration()
      );
   }

   private ServerRuntime(
      Stage<RequestProcessingContext> requestProcessingRoot,
      ProcessingProviders processingProviders,
      InjectionManager injectionManager,
      ScheduledExecutorService backgroundScheduler,
      Provider<ExecutorService> managedAsyncExecutorProvider,
      RequestScope requestScope,
      ExceptionMappers exceptionMappers,
      ApplicationEventListener applicationEventListener,
      ExternalRequestScope externalScope,
      Configuration configuration
   ) {
      this.requestProcessingRoot = requestProcessingRoot;
      this.processingProviders = processingProviders;
      this.injectionManager = injectionManager;
      this.backgroundScheduler = backgroundScheduler;
      this.managedAsyncExecutor = managedAsyncExecutorProvider;
      this.requestScope = requestScope;
      this.exceptionMappers = exceptionMappers;
      this.applicationEventListener = applicationEventListener;
      this.externalRequestScope = externalScope;
      this.configuration = configuration;
      this.tracingConfig = TracingUtils.getTracingConfig(configuration);
      this.tracingThreshold = TracingUtils.getTracingThreshold(configuration);
      this.processResponseErrors = PropertiesHelper.isProperty(configuration.getProperty("jersey.config.server.exception.processResponseErrors"));
      this.disableLocationHeaderRelativeUriResolution = ServerProperties.getValue(
         configuration.getProperties(), "jersey.config.server.headers.location.relative.resolution.disabled", Boolean.FALSE, Boolean.class
      );
      this.rfc7231LocationHeaderRelativeUriResolution = ServerProperties.getValue(
         configuration.getProperties(), "jersey.config.server.headers.location.relative.resolution.rfc7231", Boolean.FALSE, Boolean.class
      );
   }

   public void process(final ContainerRequest request) {
      TracingUtils.initTracingSupport(this.tracingConfig, this.tracingThreshold, request);
      TracingUtils.logStart(request);
      UriRoutingContext routingContext = request.getUriRoutingContext();
      RequestEventBuilder monitoringEventBuilder = EmptyRequestEventBuilder.INSTANCE;
      RequestEventListener monitoringEventListener = null;
      if (this.applicationEventListener != null) {
         monitoringEventBuilder = new RequestEventImpl.Builder().setContainerRequest(request).setExtendedUriInfo(routingContext);
         monitoringEventListener = this.applicationEventListener.onRequest(monitoringEventBuilder.build(RequestEvent.Type.START));
      }

      request.setProcessingProviders(this.processingProviders);
      final RequestProcessingContext context = new RequestProcessingContext(
         this.injectionManager, request, routingContext, monitoringEventBuilder, monitoringEventListener
      );
      request.checkState();
      final ServerRuntime.Responder responder = new ServerRuntime.Responder(context, this);
      RequestContext requestScopeInstance = this.requestScope.createContext();
      final ServerRuntime.AsyncResponderHolder asyncResponderHolder = new ServerRuntime.AsyncResponderHolder(
         responder, this.externalRequestScope, requestScopeInstance, this.externalRequestScope.open(this.injectionManager)
      );
      context.initAsyncContext(asyncResponderHolder);

      try {
         this.requestScope.runInScope(requestScopeInstance, new Runnable() {
            @Override
            public void run() {
               try {
                  if (!ServerRuntime.this.disableLocationHeaderRelativeUriResolution) {
                     URI uriToUse = ServerRuntime.this.rfc7231LocationHeaderRelativeUriResolution ? request.getRequestUri() : request.getBaseUri();
                     Builder.setBaseUri(uriToUse);
                  }

                  Ref<Endpoint> endpointRef = Refs.emptyRef();
                  RequestProcessingContext data = (RequestProcessingContext)Stages.process(context, ServerRuntime.this.requestProcessingRoot, endpointRef);
                  Endpoint endpoint = (Endpoint)endpointRef.get();
                  if (endpoint == null) {
                     throw new NotFoundException();
                  }

                  ContainerResponse response = (ContainerResponse)endpoint.apply(data);
                  if (!asyncResponderHolder.isAsync()) {
                     responder.process(response);
                  } else {
                     ServerRuntime.this.externalRequestScope.suspend(asyncResponderHolder.externalContext, ServerRuntime.this.injectionManager);
                  }
               } catch (Throwable var8) {
                  responder.process(var8);
               } finally {
                  asyncResponderHolder.release();
                  Builder.clearBaseUri();
               }
            }
         });
      } catch (RuntimeException var10) {
         if (!IllegalStateException.class.isInstance(var10.getCause()) || !this.injectionManager.isShutdown()) {
            throw var10;
         }
      }
   }

   ScheduledExecutorService getBackgroundScheduler() {
      return this.backgroundScheduler;
   }

   private static void ensureAbsolute(URI location, MultivaluedMap<String, Object> headers, ContainerRequest request, boolean incompatible) {
      if (location != null && !location.isAbsolute()) {
         URI uri = incompatible ? request.getRequestUri() : request.getBaseUri();
         headers.putSingle("Location", uri.resolve(location));
      }
   }

   abstract static class AbstractCallbackRunner<T> {
      private final Queue<T> callbacks = new ConcurrentLinkedQueue<>();
      private final Logger logger;

      protected AbstractCallbackRunner(Logger logger) {
         this.logger = logger;
      }

      public final boolean supports(Class<?> callbackClass) {
         return this.getCallbackContract().isAssignableFrom(callbackClass);
      }

      public abstract Class<?> getCallbackContract();

      public boolean register(Object callback) {
         return this.callbacks.offer((T)callback);
      }

      protected final void executeCallbacks(Closure<T> invoker) {
         for (T callback : this.callbacks) {
            try {
               invoker.invoke(callback);
            } catch (Throwable var5) {
               this.logger.log(java.util.logging.Level.WARNING, LocalizationMessages.ERROR_ASYNC_CALLBACK_FAILED(callback.getClass().getName()), var5);
            }
         }
      }
   }

   private static class AsyncResponder implements AsyncContext, ContainerResponseWriter.TimeoutHandler, CompletionCallback {
      private static final Logger LOGGER = Logger.getLogger(ServerRuntime.AsyncResponder.class.getName());
      private static final TimeoutHandler DEFAULT_TIMEOUT_HANDLER = new TimeoutHandler() {
         public void handleTimeout(AsyncResponse asyncResponse) {
            throw new ServiceUnavailableException();
         }
      };
      private final Object stateLock = new Object();
      private AsyncContext.State state = AsyncContext.State.RUNNING;
      private boolean cancelled = false;
      private final ServerRuntime.Responder responder;
      private final RequestContext requestContext;
      private final ExternalRequestContext<?> foreignScopeInstance;
      private final ExternalRequestScope requestScopeListener;
      private volatile TimeoutHandler timeoutHandler = DEFAULT_TIMEOUT_HANDLER;
      private final List<ServerRuntime.AbstractCallbackRunner<?>> callbackRunners;

      public AsyncResponder(
         ServerRuntime.Responder responder,
         RequestContext requestContext,
         ExternalRequestScope requestScopeListener,
         ExternalRequestContext<?> foreignScopeInstance
      ) {
         this.responder = responder;
         this.requestContext = requestContext;
         this.foreignScopeInstance = foreignScopeInstance;
         this.requestScopeListener = requestScopeListener;
         this.callbackRunners = Collections.unmodifiableList(Arrays.asList(responder.completionCallbackRunner, responder.connectionCallbackRunner));
         responder.completionCallbackRunner.register(this);
      }

      @Override
      public void onTimeout(ContainerResponseWriter responseWriter) {
         TimeoutHandler handler = this.timeoutHandler;

         try {
            synchronized (this.stateLock) {
               if (this.state == AsyncContext.State.SUSPENDED) {
                  handler.handleTimeout(this);
               }
            }
         } catch (Throwable var6) {
            this.resume(var6);
         }
      }

      public void onComplete(Throwable throwable) {
         synchronized (this.stateLock) {
            this.state = AsyncContext.State.COMPLETED;
         }
      }

      @Override
      public void invokeManaged(final Producer<Response> producer) {
         ((ExecutorService)this.responder.runtime.managedAsyncExecutor.get())
            .submit(
               new Runnable() {
                  @Override
                  public void run() {
                     AsyncResponder.this.responder
                        .runtime
                        .requestScope
                        .runInScope(
                           AsyncResponder.this.requestContext,
                           new Runnable() {
                              @Override
                              public void run() {
                                 try {
                                    AsyncResponder.this.requestScopeListener
                                       .resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                                    Response response = (Response)producer.call();
                                    if (response != null) {
                                       AsyncResponder.this.resume(response);
                                    }
                                 } catch (Throwable var2) {
                                    AsyncResponder.this.resume(var2);
                                 }
                              }
                           }
                        );
                  }
               }
            );
      }

      @Override
      public boolean suspend() {
         synchronized (this.stateLock) {
            if (this.state == AsyncContext.State.RUNNING && this.responder.processingContext.request().getResponseWriter().suspend(0L, TimeUnit.SECONDS, this)) {
               this.state = AsyncContext.State.SUSPENDED;
               return true;
            } else {
               return false;
            }
         }
      }

      public boolean resume(final Object response) {
         return this.resume(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     AsyncResponder.this.requestScopeListener
                        .resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                     Response jaxrsResponse = response instanceof Response ? (Response)response : Response.ok(response).build();
                     if (!AsyncResponder.this.responder.runtime.disableLocationHeaderRelativeUriResolution) {
                        ServerRuntime.ensureAbsolute(
                           jaxrsResponse.getLocation(),
                           jaxrsResponse.getHeaders(),
                           AsyncResponder.this.responder.processingContext.request(),
                           AsyncResponder.this.responder.runtime.rfc7231LocationHeaderRelativeUriResolution
                        );
                     }

                     AsyncResponder.this.responder.process(new ContainerResponse(AsyncResponder.this.responder.processingContext.request(), jaxrsResponse));
                  } catch (Throwable var2) {
                     AsyncResponder.this.responder.process(var2);
                  }
               }
            }
         );
      }

      public boolean resume(final Throwable error) {
         return this.resume(
            new Runnable() {
               @Override
               public void run() {
                  try {
                     AsyncResponder.this.requestScopeListener
                        .resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                     AsyncResponder.this.responder.process(new MappableException(error));
                  } catch (Throwable var2) {
                  }
               }
            }
         );
      }

      private boolean resume(Runnable handler) {
         synchronized (this.stateLock) {
            if (this.state != AsyncContext.State.SUSPENDED) {
               return false;
            }

            this.state = AsyncContext.State.RESUMED;
         }

         try {
            this.responder.runtime.requestScope.runInScope(this.requestContext, handler);
         } finally {
            this.requestContext.release();
         }

         return true;
      }

      public boolean cancel() {
         return this.cancel(new Value<Response>() {
            public Response get() {
               return Response.status(Status.SERVICE_UNAVAILABLE).build();
            }
         });
      }

      public boolean cancel(final int retryAfter) {
         return this.cancel(new Value<Response>() {
            public Response get() {
               return Response.status(Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build();
            }
         });
      }

      public boolean cancel(final Date retryAfter) {
         return this.cancel(new Value<Response>() {
            public Response get() {
               return Response.status(Status.SERVICE_UNAVAILABLE).header("Retry-After", retryAfter).build();
            }
         });
      }

      private boolean cancel(final Value<Response> responseValue) {
         synchronized (this.stateLock) {
            if (this.cancelled) {
               return true;
            }

            if (this.state != AsyncContext.State.SUSPENDED) {
               return false;
            }

            this.state = AsyncContext.State.RESUMED;
            this.cancelled = true;
         }

         this.responder
            .runtime
            .requestScope
            .runInScope(
               this.requestContext,
               new Runnable() {
                  @Override
                  public void run() {
                     try {
                        AsyncResponder.this.requestScopeListener
                           .resume(AsyncResponder.this.foreignScopeInstance, AsyncResponder.this.responder.runtime.injectionManager);
                        Response response = (Response)responseValue.get();
                        AsyncResponder.this.responder.process(new ContainerResponse(AsyncResponder.this.responder.processingContext.request(), response));
                     } catch (Throwable var2) {
                        AsyncResponder.this.responder.process(var2);
                     }
                  }
               }
            );
         return true;
      }

      public boolean isRunning() {
         synchronized (this.stateLock) {
            return this.state == AsyncContext.State.RUNNING;
         }
      }

      public boolean isSuspended() {
         synchronized (this.stateLock) {
            return this.state == AsyncContext.State.SUSPENDED;
         }
      }

      public boolean isCancelled() {
         synchronized (this.stateLock) {
            return this.cancelled;
         }
      }

      public boolean isDone() {
         synchronized (this.stateLock) {
            return this.state == AsyncContext.State.COMPLETED;
         }
      }

      public boolean setTimeout(long time, TimeUnit unit) {
         try {
            this.responder.processingContext.request().getResponseWriter().setSuspendTimeout(time, unit);
            return true;
         } catch (IllegalStateException var5) {
            LOGGER.log(java.util.logging.Level.FINER, "Unable to set timeout on the AsyncResponse.", (Throwable)var5);
            return false;
         }
      }

      public void setTimeoutHandler(TimeoutHandler handler) {
         this.timeoutHandler = handler;
      }

      public Collection<Class<?>> register(Class<?> callback) {
         Preconditions.checkNotNull(callback, LocalizationMessages.PARAM_NULL("callback"));
         return this.register(Injections.getOrCreate(this.responder.runtime.injectionManager, callback));
      }

      public Map<Class<?>, Collection<Class<?>>> register(Class<?> callback, Class<?>... callbacks) {
         Preconditions.checkNotNull(callback, LocalizationMessages.PARAM_NULL("callback"));
         Preconditions.checkNotNull(callbacks, LocalizationMessages.CALLBACK_ARRAY_NULL());

         for (Class<?> additionalCallback : callbacks) {
            Preconditions.checkNotNull(additionalCallback, LocalizationMessages.CALLBACK_ARRAY_ELEMENT_NULL());
         }

         Map<Class<?>, Collection<Class<?>>> results = new HashMap<>();
         results.put(callback, this.register(callback));

         for (Class<?> c : callbacks) {
            results.put(c, this.register(c));
         }

         return results;
      }

      public Collection<Class<?>> register(Object callback) {
         Preconditions.checkNotNull(callback, LocalizationMessages.PARAM_NULL("callback"));
         Collection<Class<?>> result = new LinkedList<>();

         for (ServerRuntime.AbstractCallbackRunner<?> runner : this.callbackRunners) {
            if (runner.supports(callback.getClass()) && runner.register(callback)) {
               result.add(runner.getCallbackContract());
            }
         }

         return result;
      }

      public Map<Class<?>, Collection<Class<?>>> register(Object callback, Object... callbacks) {
         Preconditions.checkNotNull(callback, LocalizationMessages.PARAM_NULL("callback"));
         Preconditions.checkNotNull(callbacks, LocalizationMessages.CALLBACK_ARRAY_NULL());

         for (Object additionalCallback : callbacks) {
            Preconditions.checkNotNull(additionalCallback, LocalizationMessages.CALLBACK_ARRAY_ELEMENT_NULL());
         }

         Map<Class<?>, Collection<Class<?>>> results = new HashMap<>();
         results.put(callback.getClass(), this.register(callback));

         for (Object c : callbacks) {
            results.put(c.getClass(), this.register(c));
         }

         return results;
      }
   }

   private static class AsyncResponderHolder implements Value<AsyncContext> {
      private final ServerRuntime.Responder responder;
      private final ExternalRequestScope externalScope;
      private final RequestContext requestContext;
      private final ExternalRequestContext<?> externalContext;
      private volatile ServerRuntime.AsyncResponder asyncResponder;

      private AsyncResponderHolder(
         ServerRuntime.Responder responder, ExternalRequestScope externalRequestScope, RequestContext requestContext, ExternalRequestContext<?> externalContext
      ) {
         this.responder = responder;
         this.externalScope = externalRequestScope;
         this.requestContext = requestContext;
         this.externalContext = externalContext;
      }

      public AsyncContext get() {
         ServerRuntime.AsyncResponder ar = new ServerRuntime.AsyncResponder(this.responder, this.requestContext, this.externalScope, this.externalContext);
         this.asyncResponder = ar;
         return ar;
      }

      public boolean isAsync() {
         ServerRuntime.AsyncResponder ar = this.asyncResponder;
         return ar != null && !ar.isRunning();
      }

      public void release() {
         if (this.asyncResponder == null) {
            this.requestContext.release();
         }
      }
   }

   private static class CompletionCallbackRunner extends ServerRuntime.AbstractCallbackRunner<CompletionCallback> implements CompletionCallback {
      private static final Logger LOGGER = Logger.getLogger(ServerRuntime.CompletionCallbackRunner.class.getName());

      private CompletionCallbackRunner() {
         super(LOGGER);
      }

      @Override
      public Class<?> getCallbackContract() {
         return CompletionCallback.class;
      }

      public void onComplete(final Throwable throwable) {
         this.executeCallbacks(new Closure<CompletionCallback>() {
            public void invoke(CompletionCallback callback) {
               callback.onComplete(throwable);
            }
         });
      }
   }

   private static class ConnectionCallbackRunner extends ServerRuntime.AbstractCallbackRunner<ConnectionCallback> implements ConnectionCallback {
      private static final Logger LOGGER = Logger.getLogger(ServerRuntime.ConnectionCallbackRunner.class.getName());

      private ConnectionCallbackRunner() {
         super(LOGGER);
      }

      @Override
      public Class<?> getCallbackContract() {
         return ConnectionCallback.class;
      }

      public void onDisconnect(final AsyncResponse disconnected) {
         this.executeCallbacks(new Closure<ConnectionCallback>() {
            public void invoke(ConnectionCallback callback) {
               callback.onDisconnect(disconnected);
            }
         });
      }
   }

   private static class Responder {
      private static final Logger LOGGER = Logger.getLogger(ServerRuntime.Responder.class.getName());
      private final RequestProcessingContext processingContext;
      private final ServerRuntime runtime;
      private final ServerRuntime.CompletionCallbackRunner completionCallbackRunner = new ServerRuntime.CompletionCallbackRunner();
      private final ServerRuntime.ConnectionCallbackRunner connectionCallbackRunner = new ServerRuntime.ConnectionCallbackRunner();
      private final TracingLogger tracingLogger;

      public Responder(RequestProcessingContext processingContext, ServerRuntime runtime) {
         this.processingContext = processingContext;
         this.runtime = runtime;
         this.tracingLogger = TracingLogger.getInstance(processingContext.request());
      }

      public void process(ContainerResponse response) {
         this.processingContext.monitoringEventBuilder().setContainerResponse(response);
         response = this.processResponse(response, null);
         this.release(response);
      }

      private ContainerResponse processResponse(ContainerResponse response, Throwable unmappedThrowable) {
         Stage<ContainerResponse> respondingRoot = this.processingContext.createRespondingRoot();
         if (respondingRoot != null) {
            response = (ContainerResponse)Stages.process(response, respondingRoot);
         }

         this.writeResponse(response);
         this.completionCallbackRunner.onComplete(unmappedThrowable);
         return response;
      }

      private ContainerResponse preProcessResponse(Response exceptionResponse, ContainerRequest request) {
         ContainerResponse response;
         try {
            response = this.convertResponse(exceptionResponse);
            if (!this.runtime.disableLocationHeaderRelativeUriResolution) {
               ServerRuntime.ensureAbsolute(response.getLocation(), response.getHeaders(), request, this.runtime.rfc7231LocationHeaderRelativeUriResolution);
            }

            this.processingContext.monitoringEventBuilder().setContainerResponse(response).setResponseSuccessfullyMapped(true);
         } finally {
            this.processingContext.triggerEvent(RequestEvent.Type.EXCEPTION_MAPPING_FINISHED);
         }

         return response;
      }

      public void process(Throwable throwable) {
         ContainerRequest request = this.processingContext.request();
         this.processingContext.monitoringEventBuilder().setException(throwable, RequestEvent.ExceptionCause.ORIGINAL);
         this.processingContext.triggerEvent(RequestEvent.Type.ON_EXCEPTION);
         ContainerResponse response = null;
         ContainerResponse defaultMapperResponse = null;

         try {
            Response exceptionResponse = this.mapException(throwable);

            try {
               response = this.preProcessResponse(exceptionResponse, request);
               this.processResponse(response, null);
            } catch (Throwable var18) {
               LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.ERROR_PROCESSING_RESPONSE_FROM_ALREADY_MAPPED_EXCEPTION());
               this.processingContext.monitoringEventBuilder().setException(var18, RequestEvent.ExceptionCause.MAPPED_RESPONSE);
               this.processingContext.triggerEvent(RequestEvent.Type.ON_EXCEPTION);
               throw var18;
            }
         } catch (Throwable var19) {
            Throwable responseError = var19;
            if (throwable != var19 && (!(throwable instanceof MappableException) || throwable.getCause() != var19)) {
               LOGGER.log(java.util.logging.Level.FINE, LocalizationMessages.ERROR_EXCEPTION_MAPPING_ORIGINAL_EXCEPTION(), throwable);
            }

            if (!this.processResponseError(var19)) {
               LOGGER.log(java.util.logging.Level.WARNING, LocalizationMessages.ERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER(), var19);

               try {
                  request.getResponseWriter().failure(responseError);
               } finally {
                  defaultMapperResponse = this.processResponseWithDefaultExceptionMapper(var19, request);
               }
            }
         } finally {
            this.release(response, defaultMapperResponse);
         }
      }

      private boolean processResponseError(Throwable responseError) {
         boolean processed = false;
         if (this.runtime.processResponseErrors) {
            Iterable<ResponseErrorMapper> mappers = Providers.getAllProviders(this.runtime.injectionManager, ResponseErrorMapper.class);
            ContainerResponse processedResponse = null;

            try {
               Response processedError = null;

               for (ResponseErrorMapper mapper : mappers) {
                  processedError = mapper.toResponse(responseError);
                  if (processedError != null) {
                     break;
                  }
               }

               if (processedError != null) {
                  processedResponse = this.processResponse(new ContainerResponse(this.processingContext.request(), processedError), null);
                  processed = true;
               }
            } catch (Throwable var11) {
               LOGGER.log(java.util.logging.Level.FINE, LocalizationMessages.ERROR_EXCEPTION_MAPPING_PROCESSED_RESPONSE_ERROR(), var11);
            } finally {
               if (processedResponse != null) {
                  this.release(processedResponse);
               }
            }
         }

         return processed;
      }

      private ContainerResponse convertResponse(Response exceptionResponse) {
         ContainerResponse containerResponse = new ContainerResponse(this.processingContext.request(), exceptionResponse);
         containerResponse.setMappedFromException(true);
         return containerResponse;
      }

      private Response mapException(Throwable originalThrowable) throws Throwable {
         LOGGER.log(java.util.logging.Level.FINER, LocalizationMessages.EXCEPTION_MAPPING_START(), originalThrowable);
         ServerRuntime.ThrowableWrap wrap = new ServerRuntime.ThrowableWrap(originalThrowable);
         wrap.tryMappableException();

         do {
            Throwable throwable = wrap.getCurrent();
            if (wrap.isInMappable() || throwable instanceof WebApplicationException) {
               if (this.runtime.processResponseErrors
                  && throwable instanceof InternalServerErrorException
                  && throwable.getCause() instanceof MessageBodyProviderNotFoundException) {
                  throw throwable;
               }

               Response waeResponse = null;
               if (throwable instanceof WebApplicationException) {
                  WebApplicationException webApplicationException = (WebApplicationException)throwable;
                  this.processingContext.routingContext().setMappedThrowable(throwable);
                  waeResponse = webApplicationException.getResponse();
                  if (waeResponse != null && waeResponse.hasEntity()) {
                     LOGGER.log(java.util.logging.Level.FINE, LocalizationMessages.EXCEPTION_MAPPING_WAE_ENTITY(waeResponse.getStatus()), throwable);
                     return waeResponse;
                  }
               }

               long timestamp = this.tracingLogger.timestamp(ServerTraceEvent.EXCEPTION_MAPPING);
               ExceptionMapper mapper = this.runtime.exceptionMappers.findMapping(throwable);
               if (mapper != null && !DefaultExceptionMapper.class.isInstance(mapper)) {
                  return this.processExceptionWithMapper(mapper, throwable, timestamp);
               }

               if (waeResponse != null) {
                  LOGGER.log(java.util.logging.Level.FINE, LocalizationMessages.EXCEPTION_MAPPING_WAE_NO_ENTITY(waeResponse.getStatus()), throwable);
                  return waeResponse;
               }
            }

            if (throwable instanceof HeaderValueException && ((HeaderValueException)throwable).getContext() == Context.INBOUND) {
               return Response.status(Status.BAD_REQUEST).build();
            }

            if (!wrap.isInMappable() || !wrap.isWrapped()) {
               throw wrap.getWrappedOrCurrent();
            }
         } while (wrap.unwrap() != null);

         throw originalThrowable;
      }

      private Response processExceptionWithMapper(ExceptionMapper mapper, Throwable throwable, long timestamp) {
         this.processingContext.monitoringEventBuilder().setExceptionMapper(mapper);
         this.processingContext.triggerEvent(RequestEvent.Type.EXCEPTION_MAPPER_FOUND);

         try {
            Response mappedResponse = mapper.toResponse(throwable);
            if (isTracingLoggingEnabled(mapper, throwable, this.tracingLogger)) {
               this.tracingLogger
                  .logDuration(
                     ServerTraceEvent.EXCEPTION_MAPPING,
                     timestamp,
                     new Object[]{
                        mapper, throwable, throwable.getLocalizedMessage(), mappedResponse != null ? mappedResponse.getStatusInfo() : "-no-response-"
                     }
                  );
            }

            this.processingContext.routingContext().setMappedThrowable(throwable);
            if (mappedResponse != null) {
               if (LOGGER.isLoggable(java.util.logging.Level.FINER)) {
                  String message = String.format(
                     "Exception '%s' has been mapped by '%s' to response '%s' (%s:%s).",
                     throwable.getLocalizedMessage(),
                     mapper.getClass().getName(),
                     mappedResponse.getStatusInfo().getReasonPhrase(),
                     mappedResponse.getStatusInfo().getStatusCode(),
                     mappedResponse.getStatusInfo().getFamily()
                  );
                  LOGGER.log(java.util.logging.Level.FINER, message);
               }

               return mappedResponse;
            } else {
               return Response.noContent().build();
            }
         } catch (Throwable var7) {
            LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.EXCEPTION_MAPPER_THROWS_EXCEPTION(mapper.getClass()), var7);
            LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.EXCEPTION_MAPPER_FAILED_FOR_EXCEPTION(), throwable);
            return Response.serverError().build();
         }
      }

      private ContainerResponse processResponseWithDefaultExceptionMapper(Throwable exception, ContainerRequest request) {
         long timestamp = this.tracingLogger.timestamp(ServerTraceEvent.EXCEPTION_MAPPING);
         Response response = this.processExceptionWithMapper(ServerRuntime.DEFAULT_EXCEPTION_MAPPER, exception, timestamp);
         return this.processResponse(this.preProcessResponse(response, request), exception);
      }

      private ContainerResponse writeResponse(final ContainerResponse response) {
         ContainerRequest request = this.processingContext.request();
         final ContainerResponseWriter writer = request.getResponseWriter();
         if (!this.runtime.disableLocationHeaderRelativeUriResolution) {
            ServerRuntime.ensureAbsolute(
               response.getLocation(), response.getHeaders(), response.getRequestContext(), this.runtime.rfc7231LocationHeaderRelativeUriResolution
            );
         }

         if (!response.hasEntity()) {
            this.tracingLogger.log(ServerTraceEvent.FINISHED, new Object[]{response.getStatusInfo()});
            this.tracingLogger.flush(response.getHeaders());
            writer.writeResponseStatusAndHeaders(0L, response);
            this.setWrittenResponse(response);
            return response;
         } else {
            Object entity = response.getEntity();
            boolean skipFinally = false;
            final boolean isHead = request.getMethod().equals("HEAD");

            try {
               response.setStreamProvider(
                  new StreamProvider() {
                     public OutputStream getOutputStream(int contentLength) throws IOException {
                        if (!Responder.this.runtime.disableLocationHeaderRelativeUriResolution) {
                           ServerRuntime.ensureAbsolute(
                              response.getLocation(),
                              response.getHeaders(),
                              response.getRequestContext(),
                              Responder.this.runtime.rfc7231LocationHeaderRelativeUriResolution
                           );
                        }

                        OutputStream outputStream = writer.writeResponseStatusAndHeaders((long)contentLength, response);
                        return isHead ? null : outputStream;
                     }
                  }
               );
               if ((writer.enableResponseBuffering() || isHead) && !response.isChunked()) {
                  response.enableBuffering(this.runtime.configuration);
               }

               try {
                  response.setEntityStream(
                     request.getWorkers()
                        .writeTo(
                           entity,
                           entity.getClass(),
                           response.getEntityType(),
                           response.getEntityAnnotations(),
                           response.getMediaType(),
                           response.getHeaders(),
                           request.getPropertiesDelegate(),
                           response.getEntityStream(),
                           request.getWriterInterceptors()
                        )
                  );
               } catch (MappableException var29) {
                  if (var29.getCause() instanceof IOException) {
                     this.connectionCallbackRunner.onDisconnect(this.processingContext.asyncContext());
                  }

                  throw var29;
               }

               this.tracingLogger.log(ServerTraceEvent.FINISHED, new Object[]{response.getStatusInfo()});
               this.tracingLogger.flush(response.getHeaders());
               this.setWrittenResponse(response);
            } catch (Throwable var30) {
               if (!response.isCommitted()) {
                  skipFinally = true;
                  if (var30 instanceof RuntimeException) {
                     throw (RuntimeException)var30;
                  }

                  throw new MappableException(var30);
               }

               LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.ERROR_WRITING_RESPONSE_ENTITY(), var30);
            } finally {
               if (!skipFinally) {
                  boolean close = !response.isChunked();
                  if (response.isChunked()) {
                     try {
                        response.commitStream();
                     } catch (Exception var28) {
                        LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.ERROR_COMMITTING_OUTPUT_STREAM(), (Throwable)var28);
                        close = true;
                     }

                     ChunkedOutput chunked = (ChunkedOutput)entity;

                     try {
                        chunked.setContext(
                           this.runtime.requestScope, this.runtime.requestScope.referenceCurrent(), request, response, this.connectionCallbackRunner
                        );
                     } catch (IOException var27) {
                        LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.ERROR_WRITING_RESPONSE_ENTITY_CHUNK(), (Throwable)var27);
                        close = true;
                     }

                     if (!chunked.isClosed() && !writer.suspend(0L, TimeUnit.SECONDS, null)) {
                        LOGGER.fine(LocalizationMessages.ERROR_SUSPENDING_CHUNKED_OUTPUT_RESPONSE());
                     }
                  }

                  if (close) {
                     try {
                        response.close();
                     } catch (Exception var26) {
                        LOGGER.log(java.util.logging.Level.SEVERE, LocalizationMessages.ERROR_CLOSING_COMMIT_OUTPUT_STREAM(), (Throwable)var26);
                     }
                  }
               }
            }

            return response;
         }
      }

      private void setWrittenResponse(ContainerResponse response) {
         this.processingContext
            .monitoringEventBuilder()
            .setContainerResponse(response)
            .setSuccess(response.getStatus() < Status.BAD_REQUEST.getStatusCode())
            .setResponseWritten(true);
      }

      private void release(ContainerResponse... responseContexts) {
         try {
            this.processingContext.closeableService().close();
            Arrays.stream(responseContexts)
               .filter(responseContext -> responseContext != null && !responseContext.isChunked())
               .forEach(ContainerResponse::close);
         } catch (Throwable var6) {
            LOGGER.log(java.util.logging.Level.WARNING, LocalizationMessages.RELEASING_REQUEST_PROCESSING_RESOURCES_FAILED(), var6);
         } finally {
            this.runtime.externalRequestScope.close();
            this.processingContext.triggerEvent(RequestEvent.Type.FINISHED);
         }
      }

      private static boolean isTracingLoggingEnabled(ExceptionMapper mapper, Throwable throwable, TracingLogger tracingLogger) {
         boolean defaultLoggingState = mapper instanceof DefaultExceptionMapper && throwable instanceof WebApplicationException;
         return !defaultLoggingState && tracingLogger.isLogEnabled(ServerTraceEvent.EXCEPTION_MAPPING);
      }
   }

   private static class ThrowableWrap {
      private final Throwable original;
      private Throwable wrapped = null;
      private Throwable current;
      private boolean inMappable = false;

      private ThrowableWrap(Throwable original) {
         this.original = original;
         this.current = original;
      }

      private Throwable getOriginal() {
         return this.original;
      }

      private Throwable getWrappedOrCurrent() {
         return this.wrapped != null ? this.wrapped : this.current;
      }

      private Throwable getCurrent() {
         return this.current;
      }

      private boolean isWrapped() {
         return CompletionException.class.isInstance(this.current) || ExecutionException.class.isInstance(this.current);
      }

      private Throwable unwrap() {
         if (this.wrapped == null) {
            this.wrapped = this.current;
         }

         this.current = this.current.getCause();
         return this.current;
      }

      private boolean tryMappableException() {
         if (MappableException.class.isInstance(this.original)) {
            this.inMappable = true;
            this.current = this.original.getCause();
            return true;
         } else {
            return false;
         }
      }

      private boolean isInMappable() {
         return this.inMappable;
      }
   }
}
