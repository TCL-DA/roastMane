package retrofit2.mock;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.concurrent.ExecutorService;
import javax.annotation.Nullable;
import kotlin.coroutines.Continuation;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.KotlinExtensions;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class BehaviorDelegate<T> {
   final Retrofit retrofit;
   private final NetworkBehavior behavior;
   private final ExecutorService executor;
   private final Class<T> service;

   BehaviorDelegate(Retrofit retrofit, NetworkBehavior behavior, ExecutorService executor, Class<T> service) {
      this.retrofit = retrofit;
      this.behavior = behavior;
      this.executor = executor;
      this.service = service;
   }

   public T returningResponse(@Nullable Object response) {
      return this.returning(Calls.response(response));
   }

   public <R> T returning(Call<R> call) {
      Call<R> behaviorCall = new BehaviorCall(this.behavior, this.executor, (Call<T>)call);
      return (T)Proxy.newProxyInstance(this.service.getClassLoader(), new Class[]{this.service}, (proxy, method, args) -> {
         BehaviorDelegate.ServiceMethodAdapterInfo adapterInfo = parseServiceMethodAdapterInfo(method);
         Annotation[] methodAnnotations = method.getAnnotations();
         CallAdapter<R, T> callAdapter = this.retrofit.callAdapter(adapterInfo.responseType, methodAnnotations);
         T adapted = (T)callAdapter.adapt(behaviorCall);
         if (!adapterInfo.isSuspend) {
            return adapted;
         } else {
            Call<Object> adaptedCall = (Call<Object>)adapted;
            Continuation<Object> continuation = (Continuation<Object>)args[args.length - 1];

            try {
               return adapterInfo.wantsResponse ? KotlinExtensions.awaitResponse(adaptedCall, continuation) : KotlinExtensions.await(adaptedCall, continuation);
            } catch (Exception var12) {
               return KotlinExtensions.suspendAndThrow(var12, continuation);
            }
         }
      });
   }

   private static BehaviorDelegate.ServiceMethodAdapterInfo parseServiceMethodAdapterInfo(Method method) {
      Type[] genericParameterTypes = method.getGenericParameterTypes();
      if (genericParameterTypes.length != 0) {
         Type lastParameterType = genericParameterTypes[genericParameterTypes.length - 1];
         if (lastParameterType instanceof ParameterizedType) {
            ParameterizedType parameterizedLastParameterType = (ParameterizedType)lastParameterType;

            try {
               if (parameterizedLastParameterType.getRawType() == Continuation.class) {
                  Type resultType = parameterizedLastParameterType.getActualTypeArguments()[0];
                  if (resultType instanceof WildcardType) {
                     resultType = ((WildcardType)resultType).getLowerBounds()[0];
                  }

                  if (resultType instanceof ParameterizedType) {
                     ParameterizedType parameterizedResultType = (ParameterizedType)resultType;
                     if (parameterizedResultType.getRawType() == Response.class) {
                        Type bodyType = parameterizedResultType.getActualTypeArguments()[0];
                        Type callType = new BehaviorDelegate.CallParameterizedTypeImpl(bodyType);
                        return new BehaviorDelegate.ServiceMethodAdapterInfo(true, true, callType);
                     }
                  }

                  Type callType = new BehaviorDelegate.CallParameterizedTypeImpl(resultType);
                  return new BehaviorDelegate.ServiceMethodAdapterInfo(true, false, callType);
               }
            } catch (NoClassDefFoundError var8) {
            }
         }
      }

      return new BehaviorDelegate.ServiceMethodAdapterInfo(false, false, method.getGenericReturnType());
   }

   static final class CallParameterizedTypeImpl implements ParameterizedType {
      private final Type bodyType;

      CallParameterizedTypeImpl(Type bodyType) {
         this.bodyType = bodyType;
      }

      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{this.bodyType};
      }

      @Override
      public Type getRawType() {
         return Call.class;
      }

      @Nullable
      @Override
      public Type getOwnerType() {
         return null;
      }
   }

   static class ServiceMethodAdapterInfo {
      final boolean isSuspend;
      final boolean wantsResponse;
      final Type responseType;

      ServiceMethodAdapterInfo(boolean isSuspend, boolean wantsResponse, Type responseType) {
         this.isSuspend = isSuspend;
         this.wantsResponse = wantsResponse;
         this.responseType = responseType;
      }
   }
}
