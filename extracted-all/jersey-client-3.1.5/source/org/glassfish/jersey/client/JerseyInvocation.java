package org.glassfish.jersey.client;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.RedirectionException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.CompletionStageRxInvoker;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.InvocationCallback;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.client.RxInvoker;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.Status.Family;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import org.glassfish.jersey.client.internal.ClientResponseProcessingException;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.inject.ServiceHolder;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.ReflectionHelper.DeclaringClassInterfacePair;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.spi.ExecutorServiceProvider;

public class JerseyInvocation implements Invocation {
   private static final Logger LOGGER = Logger.getLogger(JerseyInvocation.class.getName());
   private final ClientRequest requestContext;
   private final boolean copyRequestContext;
   private boolean ignoreResponseException;
   private static final Map<String, JerseyInvocation.EntityPresence> METHODS = initializeMap();

   private JerseyInvocation(JerseyInvocation.Builder builder) {
      this(builder, false);
   }

   private JerseyInvocation(JerseyInvocation.Builder builder, boolean copyRequestContext) {
      this.validateHttpMethodAndEntity(builder.requestContext);
      this.requestContext = new ClientRequest(builder.requestContext);
      this.copyRequestContext = copyRequestContext;
      Object value = builder.requestContext.getConfiguration().getProperty("jersey.config.client.ignoreExceptionResponse");
      if (value != null) {
         Boolean booleanValue = (Boolean)PropertiesHelper.convertValue(value, Boolean.class);
         if (booleanValue != null) {
            this.ignoreResponseException = booleanValue;
         }
      }
   }

   private static Map<String, JerseyInvocation.EntityPresence> initializeMap() {
      Map<String, JerseyInvocation.EntityPresence> map = new HashMap<>();
      map.put("DELETE", JerseyInvocation.EntityPresence.MUST_BE_NULL);
      map.put("GET", JerseyInvocation.EntityPresence.MUST_BE_NULL);
      map.put("HEAD", JerseyInvocation.EntityPresence.MUST_BE_NULL);
      map.put("OPTIONS", JerseyInvocation.EntityPresence.OPTIONAL);
      map.put("PATCH", JerseyInvocation.EntityPresence.MUST_BE_PRESENT);
      map.put("POST", JerseyInvocation.EntityPresence.OPTIONAL);
      map.put("PUT", JerseyInvocation.EntityPresence.MUST_BE_PRESENT);
      map.put("TRACE", JerseyInvocation.EntityPresence.MUST_BE_NULL);
      return map;
   }

   private void validateHttpMethodAndEntity(ClientRequest request) {
      boolean suppressExceptions = PropertiesHelper.isProperty(request.getConfiguration().getProperty("jersey.config.client.suppressHttpComplianceValidation"));
      Object shcvProperty = request.getProperty("jersey.config.client.suppressHttpComplianceValidation");
      if (shcvProperty != null) {
         suppressExceptions = PropertiesHelper.isProperty(shcvProperty);
      }

      String method = request.getMethod();
      JerseyInvocation.EntityPresence entityPresence = METHODS.get(method.toUpperCase(Locale.ROOT));
      if (entityPresence == JerseyInvocation.EntityPresence.MUST_BE_NULL && request.hasEntity()) {
         if (!suppressExceptions) {
            throw new IllegalStateException(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NOT_NULL(method));
         }

         LOGGER.warning(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NOT_NULL(method));
      } else if (entityPresence == JerseyInvocation.EntityPresence.MUST_BE_PRESENT && !request.hasEntity()) {
         if (!suppressExceptions) {
            throw new IllegalStateException(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NULL(method));
         }

         LOGGER.warning(LocalizationMessages.ERROR_HTTP_METHOD_ENTITY_NULL(method));
      }
   }

   private ClientRequest requestForCall(ClientRequest requestContext) {
      return this.copyRequestContext ? new ClientRequest(requestContext) : requestContext;
   }

   public Response invoke() throws ProcessingException, WebApplicationException {
      ClientRuntime runtime = this.request().getClientRuntime();
      RequestScope requestScope = runtime.getRequestScope();
      return this.runInScope(() -> new InboundJaxrsResponse(runtime.invoke(this.requestForCall(this.requestContext)), requestScope), requestScope);
   }

   public <T> T invoke(Class<T> responseType) throws ProcessingException, WebApplicationException {
      if (responseType == null) {
         throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
      } else {
         ClientRuntime runtime = this.request().getClientRuntime();
         RequestScope requestScope = runtime.getRequestScope();
         return this.runInScope(() -> this.translate(runtime.invoke(this.requestForCall(this.requestContext)), requestScope, responseType), requestScope);
      }
   }

   public <T> T invoke(GenericType<T> responseType) throws ProcessingException, WebApplicationException {
      if (responseType == null) {
         throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
      } else {
         ClientRuntime runtime = this.request().getClientRuntime();
         RequestScope requestScope = runtime.getRequestScope();
         return this.runInScope(() -> this.translate(runtime.invoke(this.requestForCall(this.requestContext)), requestScope, responseType), requestScope);
      }
   }

   private <T> T runInScope(Producer<T> producer, RequestScope scope) throws ProcessingException, WebApplicationException {
      return (T)scope.runInScope(() -> this.call(producer, scope));
   }

   private <T> T call(Producer<T> producer, RequestScope scope) throws ProcessingException, WebApplicationException {
      try {
         return (T)producer.call();
      } catch (ClientResponseProcessingException var4) {
         throw new ResponseProcessingException(this.translate(var4.getClientResponse(), scope, Response.class), var4.getCause());
      } catch (ProcessingException var5) {
         if (WebApplicationException.class.isInstance(var5.getCause())) {
            throw (WebApplicationException)var5.getCause();
         } else {
            throw var5;
         }
      }
   }

   public Future<Response> submit() {
      CompletableFuture<Response> responseFuture = new CompletableFuture<>();
      ClientRuntime runtime = this.request().getClientRuntime();
      runtime.submit(
         runtime.createRunnableForAsyncProcessing(
            this.requestForCall(this.requestContext),
            new JerseyInvocation.InvocationResponseCallback(responseFuture, (request, scope) -> this.translate(request, scope, Response.class))
         )
      );
      return responseFuture;
   }

   public <T> Future<T> submit(Class<T> responseType) {
      if (responseType == null) {
         throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
      } else {
         CompletableFuture<T> responseFuture = new CompletableFuture<>();
         ClientRuntime runtime = this.request().getClientRuntime();
         runtime.submit(
            runtime.createRunnableForAsyncProcessing(
               this.requestForCall(this.requestContext),
               new JerseyInvocation.InvocationResponseCallback(responseFuture, (request, scope) -> this.translate(request, scope, responseType))
            )
         );
         return responseFuture;
      }
   }

   private <T> T translate(ClientResponse response, RequestScope scope, Class<T> responseType) throws ProcessingException {
      if (responseType == Response.class) {
         return responseType.cast(new InboundJaxrsResponse(response, scope));
      } else if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
         try {
            return response.readEntity(responseType);
         } catch (ProcessingException var5) {
            if (var5.getClass() == ProcessingException.class) {
               throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), var5.getCause());
            } else {
               throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), var5);
            }
         } catch (WebApplicationException var6) {
            throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), var6);
         } catch (Exception var7) {
            throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), LocalizationMessages.UNEXPECTED_ERROR_RESPONSE_PROCESSING(), var7);
         }
      } else {
         throw this.convertToException(new InboundJaxrsResponse(response, scope));
      }
   }

   public <T> Future<T> submit(GenericType<T> responseType) {
      if (responseType == null) {
         throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
      } else {
         CompletableFuture<T> responseFuture = new CompletableFuture<>();
         ClientRuntime runtime = this.request().getClientRuntime();
         runtime.submit(
            runtime.createRunnableForAsyncProcessing(
               this.requestForCall(this.requestContext),
               new JerseyInvocation.InvocationResponseCallback(responseFuture, (request, scope) -> this.translate(request, scope, responseType))
            )
         );
         return responseFuture;
      }
   }

   private <T> T translate(ClientResponse response, RequestScope scope, GenericType<T> responseType) throws ProcessingException {
      if (responseType.getRawType() == Response.class) {
         return (T)(new InboundJaxrsResponse(response, scope));
      } else if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
         try {
            return response.readEntity(responseType);
         } catch (ProcessingException var5) {
            throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), (Throwable)(var5.getCause() != null ? var5.getCause() : var5));
         } catch (WebApplicationException var6) {
            throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), var6);
         } catch (Exception var7) {
            throw new ResponseProcessingException(new InboundJaxrsResponse(response, scope), LocalizationMessages.UNEXPECTED_ERROR_RESPONSE_PROCESSING(), var7);
         }
      } else {
         throw this.convertToException(new InboundJaxrsResponse(response, scope));
      }
   }

   public <T> Future<T> submit(InvocationCallback<T> callback) {
      return this.submit(null, callback);
   }

   public <T> Future<T> submit(GenericType<T> responseType, final InvocationCallback<T> callback) {
      final CompletableFuture<T> responseFuture = new CompletableFuture<>();

      try {
         DeclaringClassInterfacePair pair = ReflectionHelper.getClass(callback.getClass(), InvocationCallback.class);
         final Class<T> callbackParamClass;
         final Type callbackParamType;
         if (responseType != null) {
            callbackParamType = responseType.getType();
            callbackParamClass = ReflectionHelper.erasure(responseType.getRawType());
         } else {
            Type[] typeArguments = ReflectionHelper.getParameterizedTypeArguments(pair);
            if (typeArguments != null && typeArguments.length != 0) {
               callbackParamType = typeArguments[0];
            } else {
               callbackParamType = Object.class;
            }

            callbackParamClass = ReflectionHelper.erasure(callbackParamType);
         }

         ResponseCallback responseCallback = new ResponseCallback() {
            @Override
            public void completed(ClientResponse response, RequestScope scope) {
               if (responseFuture.isCancelled()) {
                  response.close();
                  this.failed(new ProcessingException(new CancellationException(LocalizationMessages.ERROR_REQUEST_CANCELLED())));
               } else {
                  if (callbackParamClass == Response.class) {
                     T result = callbackParamClass.cast(new InboundJaxrsResponse(response, scope));
                     responseFuture.complete(result);
                     callback.completed(result);
                  } else if (response.getStatusInfo().getFamily() == Family.SUCCESSFUL) {
                     T result = response.readEntity(new GenericType(callbackParamType));
                     responseFuture.complete(result);
                     callback.completed(result);
                  } else {
                     this.failed(JerseyInvocation.this.convertToException(new InboundJaxrsResponse(response, scope)));
                  }
               }
            }

            @Override
            public void failed(ProcessingException error) {
               Exception called = null;

               try {
                  if (error.getCause() instanceof WebApplicationException) {
                     responseFuture.completeExceptionally(error.getCause());
                  } else if (!responseFuture.isCancelled()) {
                     try {
                        JerseyInvocation.this.call(() -> {
                           throw error;
                        }, null);
                     } catch (Exception var7) {
                        called = var7;
                        responseFuture.completeExceptionally(var7);
                     }
                  }
               } finally {
                  callback.failed((Throwable)(error.getCause() instanceof CancellationException ? error.getCause() : (called != null ? called : error)));
               }
            }
         };
         ClientRuntime runtime = this.request().getClientRuntime();
         runtime.submit(runtime.createRunnableForAsyncProcessing(this.requestForCall(this.requestContext), responseCallback));
      } catch (Throwable var9) {
         ProcessingException ce;
         if (var9 instanceof ClientResponseProcessingException) {
            ce = new ProcessingException(var9.getCause());
            responseFuture.completeExceptionally(ce);
         } else if (var9 instanceof ProcessingException) {
            ce = (ProcessingException)var9;
            responseFuture.completeExceptionally(ce);
         } else if (var9 instanceof WebApplicationException) {
            ce = new ProcessingException(var9);
            responseFuture.completeExceptionally(var9);
         } else {
            ce = new ProcessingException(var9);
            responseFuture.completeExceptionally(ce);
         }

         callback.failed(ce);
      }

      return responseFuture;
   }

   public JerseyInvocation property(String name, Object value) {
      this.requestContext.setProperty(name, value);
      return this;
   }

   private ProcessingException convertToException(Response response) {
      int statusCode = response.getStatus();
      Response finalResponse = this.ignoreResponseException ? Response.status(statusCode).build() : response;

      try {
         response.bufferEntity();
         Status status = Status.fromStatusCode(statusCode);
         WebApplicationException webAppException;
         if (status == null) {
            Family statusFamily = finalResponse.getStatusInfo().getFamily();
            webAppException = this.createExceptionForFamily(finalResponse, statusFamily);
         } else {
            switch (status) {
               case BAD_REQUEST:
                  webAppException = new BadRequestException(finalResponse);
                  break;
               case UNAUTHORIZED:
                  webAppException = new NotAuthorizedException(finalResponse);
                  break;
               case FORBIDDEN:
                  webAppException = new ForbiddenException(finalResponse);
                  break;
               case NOT_FOUND:
                  webAppException = new NotFoundException(finalResponse);
                  break;
               case METHOD_NOT_ALLOWED:
                  webAppException = new NotAllowedException(finalResponse);
                  break;
               case NOT_ACCEPTABLE:
                  webAppException = new NotAcceptableException(finalResponse);
                  break;
               case UNSUPPORTED_MEDIA_TYPE:
                  webAppException = new NotSupportedException(finalResponse);
                  break;
               case INTERNAL_SERVER_ERROR:
                  webAppException = new InternalServerErrorException(finalResponse);
                  break;
               case SERVICE_UNAVAILABLE:
                  webAppException = new ServiceUnavailableException(finalResponse);
                  break;
               default:
                  Family statusFamily = finalResponse.getStatusInfo().getFamily();
                  webAppException = this.createExceptionForFamily(finalResponse, statusFamily);
            }
         }

         return new ResponseProcessingException(finalResponse, webAppException);
      } catch (Throwable var7) {
         return new ResponseProcessingException(finalResponse, LocalizationMessages.RESPONSE_TO_EXCEPTION_CONVERSION_FAILED(), var7);
      }
   }

   private WebApplicationException createExceptionForFamily(Response response, Family statusFamily) {
      WebApplicationException webAppException;
      switch (statusFamily) {
         case REDIRECTION:
            webAppException = new RedirectionException(response);
            break;
         case CLIENT_ERROR:
            webAppException = new ClientErrorException(response);
            break;
         case SERVER_ERROR:
            webAppException = new ServerErrorException(response);
            break;
         default:
            webAppException = new WebApplicationException(response);
      }

      return webAppException;
   }

   ClientRequest request() {
      return this.requestContext;
   }

   @Override
   public String toString() {
      return "JerseyInvocation [" + this.request().getMethod() + ' ' + this.request().getUri() + "]";
   }

   static class AsyncInvoker extends CompletableFutureAsyncInvoker implements jakarta.ws.rs.client.AsyncInvoker {
      private final JerseyInvocation.Builder builder;

      AsyncInvoker(JerseyInvocation.Builder request) {
         this.builder = request;
         this.builder.requestContext.setAsynchronous(true);
      }

      public CompletableFuture<Response> method(String name) {
         this.builder.requestContext.setMethod(name);
         return (CompletableFuture<Response>)new JerseyInvocation(this.builder).submit();
      }

      public <T> CompletableFuture<T> method(String name, Class<T> responseType) {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.builder.requestContext.setMethod(name);
            return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(responseType);
         }
      }

      public <T> CompletableFuture<T> method(String name, GenericType<T> responseType) {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.builder.requestContext.setMethod(name);
            return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(responseType);
         }
      }

      @Override
      public <T> CompletableFuture<T> method(String name, InvocationCallback<T> callback) {
         this.builder.requestContext.setMethod(name);
         return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(callback);
      }

      public CompletableFuture<Response> method(String name, Entity<?> entity) {
         this.builder.requestContext.setMethod(name);
         this.builder.storeEntity(entity);
         return (CompletableFuture<Response>)new JerseyInvocation(this.builder).submit();
      }

      @Override
      public <T> CompletableFuture<T> method(String name, Entity<?> entity, Class<T> responseType) {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(responseType);
         }
      }

      @Override
      public <T> CompletableFuture<T> method(String name, Entity<?> entity, GenericType<T> responseType) {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.builder.requestContext.setMethod(name);
            this.builder.storeEntity(entity);
            return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(responseType);
         }
      }

      @Override
      public <T> CompletableFuture<T> method(String name, Entity<?> entity, InvocationCallback<T> callback) {
         this.builder.requestContext.setMethod(name);
         this.builder.storeEntity(entity);
         return (CompletableFuture<T>)new JerseyInvocation(this.builder).<T>submit(callback);
      }
   }

   public static class Builder implements jakarta.ws.rs.client.Invocation.Builder {
      private final ClientRequest requestContext;

      protected Builder(URI uri, ClientConfig configuration) {
         this.requestContext = new ClientRequest(uri, configuration, new MapPropertiesDelegate());
      }

      ClientRequest request() {
         return this.requestContext;
      }

      private void storeEntity(Entity<?> entity) {
         if (entity != null) {
            this.requestContext.variant(entity.getVariant());
            this.requestContext.setEntity(entity.getEntity());
            this.requestContext.setEntityAnnotations(entity.getAnnotations());
         }
      }

      public JerseyInvocation build(String method) {
         this.requestContext.setMethod(method);
         return new JerseyInvocation(this, true);
      }

      public JerseyInvocation build(String method, Entity<?> entity) {
         this.requestContext.setMethod(method);
         this.storeEntity(entity);
         return new JerseyInvocation(this, true);
      }

      public JerseyInvocation buildGet() {
         this.requestContext.setMethod("GET");
         return new JerseyInvocation(this, true);
      }

      public JerseyInvocation buildDelete() {
         this.requestContext.setMethod("DELETE");
         return new JerseyInvocation(this, true);
      }

      public JerseyInvocation buildPost(Entity<?> entity) {
         this.requestContext.setMethod("POST");
         this.storeEntity(entity);
         return new JerseyInvocation(this, true);
      }

      public JerseyInvocation buildPut(Entity<?> entity) {
         this.requestContext.setMethod("PUT");
         this.storeEntity(entity);
         return new JerseyInvocation(this, true);
      }

      public jakarta.ws.rs.client.AsyncInvoker async() {
         return new JerseyInvocation.AsyncInvoker(this);
      }

      public JerseyInvocation.Builder accept(String... mediaTypes) {
         this.requestContext.accept(mediaTypes);
         return this;
      }

      public JerseyInvocation.Builder accept(MediaType... mediaTypes) {
         this.requestContext.accept(mediaTypes);
         return this;
      }

      public jakarta.ws.rs.client.Invocation.Builder acceptEncoding(String... encodings) {
         this.requestContext.getHeaders().addAll("Accept-Encoding", encodings);
         return this;
      }

      public JerseyInvocation.Builder acceptLanguage(Locale... locales) {
         this.requestContext.acceptLanguage(locales);
         return this;
      }

      public JerseyInvocation.Builder acceptLanguage(String... locales) {
         this.requestContext.acceptLanguage(locales);
         return this;
      }

      public JerseyInvocation.Builder cookie(Cookie cookie) {
         this.requestContext.cookie(cookie);
         return this;
      }

      public JerseyInvocation.Builder cookie(String name, String value) {
         this.requestContext.cookie(new Cookie(name, value));
         return this;
      }

      public JerseyInvocation.Builder cacheControl(CacheControl cacheControl) {
         this.requestContext.cacheControl(cacheControl);
         return this;
      }

      public JerseyInvocation.Builder header(String name, Object value) {
         MultivaluedMap<String, Object> headers = this.requestContext.getHeaders();
         if (value == null) {
            headers.remove(name);
         } else {
            headers.add(name, value);
         }

         if ("User-Agent".equalsIgnoreCase(name)) {
            this.requestContext.ignoreUserAgent(value == null);
         }

         return this;
      }

      public JerseyInvocation.Builder headers(MultivaluedMap<String, Object> headers) {
         this.requestContext.replaceHeaders(headers);
         return this;
      }

      public Response get() throws ProcessingException {
         return this.method("GET");
      }

      public <T> T get(Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("GET", responseType);
      }

      public <T> T get(GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("GET", responseType);
      }

      public Response put(Entity<?> entity) throws ProcessingException {
         return this.method("PUT", entity);
      }

      public <T> T put(Entity<?> entity, Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("PUT", entity, responseType);
      }

      public <T> T put(Entity<?> entity, GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("PUT", entity, responseType);
      }

      public Response post(Entity<?> entity) throws ProcessingException {
         return this.method("POST", entity);
      }

      public <T> T post(Entity<?> entity, Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("POST", entity, responseType);
      }

      public <T> T post(Entity<?> entity, GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("POST", entity, responseType);
      }

      public Response delete() throws ProcessingException {
         return this.method("DELETE");
      }

      public <T> T delete(Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("DELETE", responseType);
      }

      public <T> T delete(GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("DELETE", responseType);
      }

      public Response head() throws ProcessingException {
         return this.method("HEAD");
      }

      public Response options() throws ProcessingException {
         return this.method("OPTIONS");
      }

      public <T> T options(Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("OPTIONS", responseType);
      }

      public <T> T options(GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("OPTIONS", responseType);
      }

      public Response trace() throws ProcessingException {
         return this.method("TRACE");
      }

      public <T> T trace(Class<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("TRACE", responseType);
      }

      public <T> T trace(GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         return this.method("TRACE", responseType);
      }

      public Response method(String name) throws ProcessingException {
         this.requestContext.setMethod(name);
         return new JerseyInvocation(this).invoke();
      }

      public <T> T method(String name, Class<T> responseType) throws ProcessingException, WebApplicationException {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.requestContext.setMethod(name);
            return new JerseyInvocation(this).invoke(responseType);
         }
      }

      public <T> T method(String name, GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.requestContext.setMethod(name);
            return new JerseyInvocation(this).invoke(responseType);
         }
      }

      public Response method(String name, Entity<?> entity) throws ProcessingException {
         this.requestContext.setMethod(name);
         this.storeEntity(entity);
         return new JerseyInvocation(this).invoke();
      }

      public <T> T method(String name, Entity<?> entity, Class<T> responseType) throws ProcessingException, WebApplicationException {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.requestContext.setMethod(name);
            this.storeEntity(entity);
            return new JerseyInvocation(this).invoke(responseType);
         }
      }

      public <T> T method(String name, Entity<?> entity, GenericType<T> responseType) throws ProcessingException, WebApplicationException {
         if (responseType == null) {
            throw new IllegalArgumentException(LocalizationMessages.RESPONSE_TYPE_IS_NULL());
         } else {
            this.requestContext.setMethod(name);
            this.storeEntity(entity);
            return new JerseyInvocation(this).invoke(responseType);
         }
      }

      public JerseyInvocation.Builder property(String name, Object value) {
         this.requestContext.setProperty(name, value);
         return this;
      }

      public CompletionStageRxInvoker rx() {
         return this.rx(JerseyCompletionStageRxInvoker.class);
      }

      public <T extends RxInvoker> T rx(Class<T> clazz) {
         if (clazz == JerseyCompletionStageRxInvoker.class) {
            ExecutorService configured = this.request().getClientConfig().getExecutorService();
            if (configured == null) {
               ExecutorService provided = this.executorService();
               if (provided != null) {
                  ((ClientConfig)this.request().getConfiguration()).executorService(provided);
               }
            }

            return (T)(new JerseyCompletionStageRxInvoker(this));
         } else {
            return this.createRxInvoker(clazz, this.executorService());
         }
      }

      private <T extends RxInvoker> T rx(Class<T> clazz, ExecutorService executorService) {
         if (executorService == null) {
            throw new IllegalArgumentException(LocalizationMessages.NULL_INPUT_PARAMETER("executorService"));
         } else {
            return this.createRxInvoker(clazz, executorService);
         }
      }

      private ExecutorService executorService() {
         ExecutorService result = this.request().getClientConfig().getExecutorService();
         if (result != null) {
            return result;
         } else {
            List<ServiceHolder<ExecutorServiceProvider>> serviceHolders = this.requestContext
               .getInjectionManager()
               .getAllServiceHolders(ExecutorServiceProvider.class, new Annotation[0]);
            JerseyInvocation.Builder.BestServiceHolder best = serviceHolders.stream()
               .map(x$0 -> new JerseyInvocation.Builder.BestServiceHolder(x$0))
               .sorted((a, b) -> a.isBetterThen(b) ? -1 : 1)
               .findFirst()
               .get();
            return best.provider.getExecutorService();
         }
      }

      private <T extends RxInvoker> T createRxInvoker(Class<? extends RxInvoker> clazz, ExecutorService executorService) {
         if (clazz == null) {
            throw new IllegalArgumentException(LocalizationMessages.NULL_INPUT_PARAMETER("clazz"));
         } else {
            for (RxInvokerProvider invokerProvider : Providers.getAllProviders(this.requestContext.getInjectionManager(), RxInvokerProvider.class)) {
               if (invokerProvider.isProviderFor(clazz)) {
                  RxInvoker rxInvoker = invokerProvider.getRxInvoker(this, executorService);
                  if (rxInvoker == null) {
                     throw new IllegalStateException(LocalizationMessages.CLIENT_RX_PROVIDER_NULL());
                  }

                  return (T)rxInvoker;
               }
            }

            throw new IllegalStateException(LocalizationMessages.CLIENT_RX_PROVIDER_NOT_REGISTERED(clazz.getSimpleName()));
         }
      }

      private static final class BestServiceHolder {
         private final ExecutorServiceProvider provider;
         private final int value;

         private BestServiceHolder(ServiceHolder<ExecutorServiceProvider> holder) {
            this.provider = (ExecutorServiceProvider)holder.getInstance();
            boolean isDefault = DefaultClientAsyncExecutorProvider.class.equals(holder.getImplementationClass())
               || ClientExecutorProvidersConfigurator.ClientExecutorServiceProvider.class.equals(holder.getImplementationClass());
            boolean isAsync = holder.getImplementationClass().getAnnotation(ClientAsyncExecutor.class) != null;
            this.value = 10 * (isDefault ? 0 : 1) + (isAsync ? 1 : 0);
         }

         public boolean isBetterThen(JerseyInvocation.Builder.BestServiceHolder other) {
            return this.value > other.value;
         }
      }
   }

   private static enum EntityPresence {
      MUST_BE_NULL,
      MUST_BE_PRESENT,
      OPTIONAL;
   }

   private class InvocationResponseCallback<R> implements ResponseCallback {
      private final CompletableFuture<R> responseFuture;
      private final BiFunction<ClientResponse, RequestScope, R> producer;

      private InvocationResponseCallback(CompletableFuture<R> responseFuture, BiFunction<ClientResponse, RequestScope, R> producer) {
         this.responseFuture = responseFuture;
         this.producer = producer;
      }

      @Override
      public void completed(ClientResponse response, RequestScope scope) {
         if (this.responseFuture.isCancelled()) {
            response.close();
         } else {
            try {
               this.responseFuture.complete(this.producer.apply(response, scope));
            } catch (ProcessingException var4) {
               this.failed(var4);
            }
         }
      }

      @Override
      public void failed(ProcessingException error) {
         if (!this.responseFuture.isCancelled()) {
            try {
               JerseyInvocation.this.call(() -> {
                  throw error;
               }, null);
            } catch (Exception var3) {
               this.responseFuture.completeExceptionally(var3);
            }
         }
      }
   }
}
