package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import kotlin.coroutines.Continuation;
import okhttp3.ResponseBody;
import okhttp3.Call.Factory;

abstract class HttpServiceMethod<ResponseT, ReturnT> extends ServiceMethod<ReturnT> {
   private final RequestFactory requestFactory;
   private final Factory callFactory;
   private final Converter<ResponseBody, ResponseT> responseConverter;

   static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(Retrofit retrofit, Method method, RequestFactory requestFactory) {
      boolean isKotlinSuspendFunction = requestFactory.isKotlinSuspendFunction;
      boolean continuationWantsResponse = false;
      boolean continuationBodyNullable = false;
      boolean continuationIsUnit = false;
      Annotation[] annotations = method.getAnnotations();
      Type adapterType;
      if (isKotlinSuspendFunction) {
         Type[] parameterTypes = method.getGenericParameterTypes();
         Type responseType = Utils.getParameterLowerBound(0, (ParameterizedType)parameterTypes[parameterTypes.length - 1]);
         if (Utils.getRawType(responseType) == Response.class && responseType instanceof ParameterizedType) {
            responseType = Utils.getParameterUpperBound(0, (ParameterizedType)responseType);
            continuationWantsResponse = true;
         } else {
            if (Utils.getRawType(responseType) == Call.class) {
               throw Utils.methodError(
                  method,
                  "Suspend functions should not return Call, as they already execute asynchronously.\nChange its return type to %s",
                  Utils.getParameterUpperBound(0, (ParameterizedType)responseType)
               );
            }

            continuationIsUnit = Utils.isUnit(responseType);
         }

         adapterType = new Utils.ParameterizedTypeImpl(null, Call.class, responseType);
         annotations = SkipCallbackExecutorImpl.ensurePresent(annotations);
      } else {
         adapterType = method.getGenericReturnType();
      }

      CallAdapter<ResponseT, ReturnT> callAdapter = createCallAdapter(retrofit, method, adapterType, annotations);
      Type responseType = callAdapter.responseType();
      if (responseType == okhttp3.Response.class) {
         throw Utils.methodError(method, "'" + Utils.getRawType(responseType).getName() + "' is not a valid response body type. Did you mean ResponseBody?");
      } else if (responseType == Response.class) {
         throw Utils.methodError(method, "Response must include generic type (e.g., Response<String>)");
      } else if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType) && !Utils.isUnit(responseType)) {
         throw Utils.methodError(method, "HEAD method must use Void or Unit as response type.");
      } else {
         Converter<ResponseBody, ResponseT> responseConverter = createResponseConverter(retrofit, method, responseType);
         Factory callFactory = retrofit.callFactory;
         if (!isKotlinSuspendFunction) {
            return new HttpServiceMethod.CallAdapted<>(requestFactory, callFactory, responseConverter, callAdapter);
         } else {
            return (HttpServiceMethod<ResponseT, ReturnT>)(continuationWantsResponse
               ? new HttpServiceMethod.SuspendForResponse<>(requestFactory, callFactory, responseConverter, callAdapter)
               : new HttpServiceMethod.SuspendForBody<>(
                  requestFactory, callFactory, responseConverter, callAdapter, continuationBodyNullable, continuationIsUnit
               ));
         }
      }
   }

   private static <ResponseT, ReturnT> CallAdapter<ResponseT, ReturnT> createCallAdapter(
      Retrofit retrofit, Method method, Type returnType, Annotation[] annotations
   ) {
      try {
         return (CallAdapter<ResponseT, ReturnT>)retrofit.callAdapter(returnType, annotations);
      } catch (RuntimeException var5) {
         throw Utils.methodError(method, var5, "Unable to create call adapter for %s", returnType);
      }
   }

   private static <ResponseT> Converter<ResponseBody, ResponseT> createResponseConverter(Retrofit retrofit, Method method, Type responseType) {
      Annotation[] annotations = method.getAnnotations();

      try {
         return retrofit.responseBodyConverter(responseType, annotations);
      } catch (RuntimeException var5) {
         throw Utils.methodError(method, var5, "Unable to create converter for %s", responseType);
      }
   }

   HttpServiceMethod(RequestFactory requestFactory, Factory callFactory, Converter<ResponseBody, ResponseT> responseConverter) {
      this.requestFactory = requestFactory;
      this.callFactory = callFactory;
      this.responseConverter = responseConverter;
   }

   @Nullable
   @Override
   final ReturnT invoke(Object instance, Object[] args) {
      Call<ResponseT> call = new OkHttpCall<>(this.requestFactory, instance, args, this.callFactory, this.responseConverter);
      return this.adapt(call, args);
   }

   @Nullable
   protected abstract ReturnT adapt(Call<ResponseT> var1, Object[] var2);

   static final class CallAdapted<ResponseT, ReturnT> extends HttpServiceMethod<ResponseT, ReturnT> {
      private final CallAdapter<ResponseT, ReturnT> callAdapter;

      CallAdapted(
         RequestFactory requestFactory, Factory callFactory, Converter<ResponseBody, ResponseT> responseConverter, CallAdapter<ResponseT, ReturnT> callAdapter
      ) {
         super(requestFactory, callFactory, responseConverter);
         this.callAdapter = callAdapter;
      }

      @Override
      protected ReturnT adapt(Call<ResponseT> call, Object[] args) {
         return this.callAdapter.adapt(call);
      }
   }

   static final class SuspendForBody<ResponseT> extends HttpServiceMethod<ResponseT, Object> {
      private final CallAdapter<ResponseT, Call<ResponseT>> callAdapter;
      private final boolean isNullable;
      private final boolean isUnit;

      SuspendForBody(
         RequestFactory requestFactory,
         Factory callFactory,
         Converter<ResponseBody, ResponseT> responseConverter,
         CallAdapter<ResponseT, Call<ResponseT>> callAdapter,
         boolean isNullable,
         boolean isUnit
      ) {
         super(requestFactory, callFactory, responseConverter);
         this.callAdapter = callAdapter;
         this.isNullable = isNullable;
         this.isUnit = isUnit;
      }

      @Override
      protected Object adapt(Call<ResponseT> call, Object[] args) {
         call = this.callAdapter.adapt(call);
         Continuation<ResponseT> continuation = (Continuation<ResponseT>)args[args.length - 1];

         try {
            if (this.isUnit) {
               return KotlinExtensions.awaitUnit(call, continuation);
            } else {
               return this.isNullable ? KotlinExtensions.awaitNullable(call, continuation) : KotlinExtensions.await(call, continuation);
            }
         } catch (ThreadDeath | LinkageError | VirtualMachineError var5) {
            throw var5;
         } catch (Throwable var6) {
            return KotlinExtensions.suspendAndThrow(var6, continuation);
         }
      }
   }

   static final class SuspendForResponse<ResponseT> extends HttpServiceMethod<ResponseT, Object> {
      private final CallAdapter<ResponseT, Call<ResponseT>> callAdapter;

      SuspendForResponse(
         RequestFactory requestFactory,
         Factory callFactory,
         Converter<ResponseBody, ResponseT> responseConverter,
         CallAdapter<ResponseT, Call<ResponseT>> callAdapter
      ) {
         super(requestFactory, callFactory, responseConverter);
         this.callAdapter = callAdapter;
      }

      @Override
      protected Object adapt(Call<ResponseT> call, Object[] args) {
         call = this.callAdapter.adapt(call);
         Continuation<Response<ResponseT>> continuation = (Continuation<Response<ResponseT>>)args[args.length - 1];

         try {
            return KotlinExtensions.awaitResponse(call, continuation);
         } catch (Exception var5) {
            return KotlinExtensions.suspendAndThrow(var5, continuation);
         }
      }
   }
}
