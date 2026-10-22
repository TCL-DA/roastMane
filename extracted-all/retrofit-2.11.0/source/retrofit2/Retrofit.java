package retrofit2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.Call.Factory;

public final class Retrofit {
   private final ConcurrentHashMap<Method, Object> serviceMethodCache = new ConcurrentHashMap<>();
   final Factory callFactory;
   final HttpUrl baseUrl;
   final List<Converter.Factory> converterFactories;
   final int defaultConverterFactoriesSize;
   final List<CallAdapter.Factory> callAdapterFactories;
   final int defaultCallAdapterFactoriesSize;
   @Nullable
   final Executor callbackExecutor;
   final boolean validateEagerly;

   Retrofit(
      Factory callFactory,
      HttpUrl baseUrl,
      List<Converter.Factory> converterFactories,
      int defaultConverterFactoriesSize,
      List<CallAdapter.Factory> callAdapterFactories,
      int defaultCallAdapterFactoriesSize,
      @Nullable Executor callbackExecutor,
      boolean validateEagerly
   ) {
      this.callFactory = callFactory;
      this.baseUrl = baseUrl;
      this.converterFactories = converterFactories;
      this.defaultConverterFactoriesSize = defaultConverterFactoriesSize;
      this.callAdapterFactories = callAdapterFactories;
      this.defaultCallAdapterFactoriesSize = defaultCallAdapterFactoriesSize;
      this.callbackExecutor = callbackExecutor;
      this.validateEagerly = validateEagerly;
   }

   public <T> T create(final Class<T> service) {
      this.validateServiceInterface(service);
      return (T)Proxy.newProxyInstance(
         service.getClassLoader(),
         new Class[]{service},
         new InvocationHandler() {
            private final Object[] emptyArgs = new Object[0];

            @Nullable
            @Override
            public Object invoke(Object proxy, Method method, @Nullable Object[] args) throws Throwable {
               if (method.getDeclaringClass() == Object.class) {
                  return method.invoke(this, args);
               } else {
                  args = args != null ? args : this.emptyArgs;
                  Reflection reflection = Platform.reflection;
                  return reflection.isDefaultMethod(method)
                     ? reflection.invokeDefaultMethod(method, service, proxy, args)
                     : Retrofit.this.loadServiceMethod(service, method).invoke(proxy, args);
               }
            }
         }
      );
   }

   private void validateServiceInterface(Class<?> service) {
      if (!service.isInterface()) {
         throw new IllegalArgumentException("API declarations must be interfaces.");
      } else {
         Deque<Class<?>> check = new ArrayDeque<>(1);
         check.add(service);

         while (!check.isEmpty()) {
            Class<?> candidate = check.removeFirst();
            if (candidate.getTypeParameters().length != 0) {
               StringBuilder message = new StringBuilder("Type parameters are unsupported on ").append(candidate.getName());
               if (candidate != service) {
                  message.append(" which is an interface of ").append(service.getName());
               }

               throw new IllegalArgumentException(message.toString());
            }

            Collections.addAll(check, candidate.getInterfaces());
         }

         if (this.validateEagerly) {
            Reflection reflection = Platform.reflection;

            for (Method method : service.getDeclaredMethods()) {
               if (!reflection.isDefaultMethod(method) && !Modifier.isStatic(method.getModifiers()) && !method.isSynthetic()) {
                  this.loadServiceMethod(service, method);
               }
            }
         }
      }
   }

   ServiceMethod<?> loadServiceMethod(Class<?> service, Method method) {
      while (true) {
         Object lookup = this.serviceMethodCache.get(method);
         if (lookup instanceof ServiceMethod) {
            return (ServiceMethod<?>)lookup;
         }

         if (lookup == null) {
            Object lock = new Object();
            synchronized (lock) {
               lookup = this.serviceMethodCache.putIfAbsent(method, lock);
               if (lookup == null) {
                  ServiceMethod<Object> result;
                  try {
                     result = ServiceMethod.parseAnnotations(this, service, method);
                  } catch (Throwable var10) {
                     this.serviceMethodCache.remove(method);
                     throw var10;
                  }

                  this.serviceMethodCache.put(method, result);
                  return result;
               }
            }
         }

         synchronized (lookup) {
            Object result = this.serviceMethodCache.get(method);
            if (result != null) {
               return (ServiceMethod<?>)result;
            }
         }
      }
   }

   public Factory callFactory() {
      return this.callFactory;
   }

   public HttpUrl baseUrl() {
      return this.baseUrl;
   }

   public List<CallAdapter.Factory> callAdapterFactories() {
      return this.callAdapterFactories;
   }

   public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
      return this.nextCallAdapter(null, returnType, annotations);
   }

   public CallAdapter<?, ?> nextCallAdapter(@Nullable CallAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {
      Objects.requireNonNull(returnType, "returnType == null");
      Objects.requireNonNull(annotations, "annotations == null");
      int start = this.callAdapterFactories.indexOf(skipPast) + 1;
      int i = start;

      for (int count = this.callAdapterFactories.size(); i < count; i++) {
         CallAdapter<?, ?> adapter = this.callAdapterFactories.get(i).get(returnType, annotations, this);
         if (adapter != null) {
            return adapter;
         }
      }

      StringBuilder builder = new StringBuilder("Could not locate call adapter for ").append(returnType).append(".\n");
      if (skipPast != null) {
         builder.append("  Skipped:");

         for (int ix = 0; ix < start; ix++) {
            builder.append("\n   * ").append(this.callAdapterFactories.get(ix).getClass().getName());
         }

         builder.append('\n');
      }

      builder.append("  Tried:");
      int ix = start;

      for (int countx = this.callAdapterFactories.size(); ix < countx; ix++) {
         builder.append("\n   * ").append(this.callAdapterFactories.get(ix).getClass().getName());
      }

      throw new IllegalArgumentException(builder.toString());
   }

   public List<Converter.Factory> converterFactories() {
      return this.converterFactories;
   }

   public <T> Converter<T, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
      return this.nextRequestBodyConverter(null, type, parameterAnnotations, methodAnnotations);
   }

   public <T> Converter<T, RequestBody> nextRequestBodyConverter(
      @Nullable Converter.Factory skipPast, Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations
   ) {
      Objects.requireNonNull(type, "type == null");
      Objects.requireNonNull(parameterAnnotations, "parameterAnnotations == null");
      Objects.requireNonNull(methodAnnotations, "methodAnnotations == null");
      int start = this.converterFactories.indexOf(skipPast) + 1;
      int i = start;

      for (int count = this.converterFactories.size(); i < count; i++) {
         Converter.Factory factory = this.converterFactories.get(i);
         Converter<?, RequestBody> converter = factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, this);
         if (converter != null) {
            return (Converter<T, RequestBody>)converter;
         }
      }

      StringBuilder builder = new StringBuilder("Could not locate RequestBody converter for ").append(type).append(".\n");
      if (skipPast != null) {
         builder.append("  Skipped:");

         for (int ix = 0; ix < start; ix++) {
            builder.append("\n   * ").append(this.converterFactories.get(ix).getClass().getName());
         }

         builder.append('\n');
      }

      builder.append("  Tried:");
      int ix = start;

      for (int countx = this.converterFactories.size(); ix < countx; ix++) {
         builder.append("\n   * ").append(this.converterFactories.get(ix).getClass().getName());
      }

      throw new IllegalArgumentException(builder.toString());
   }

   public <T> Converter<ResponseBody, T> responseBodyConverter(Type type, Annotation[] annotations) {
      return this.nextResponseBodyConverter(null, type, annotations);
   }

   public <T> Converter<ResponseBody, T> nextResponseBodyConverter(@Nullable Converter.Factory skipPast, Type type, Annotation[] annotations) {
      Objects.requireNonNull(type, "type == null");
      Objects.requireNonNull(annotations, "annotations == null");
      int start = this.converterFactories.indexOf(skipPast) + 1;
      int i = start;

      for (int count = this.converterFactories.size(); i < count; i++) {
         Converter<ResponseBody, ?> converter = this.converterFactories.get(i).responseBodyConverter(type, annotations, this);
         if (converter != null) {
            return (Converter<ResponseBody, T>)converter;
         }
      }

      StringBuilder builder = new StringBuilder("Could not locate ResponseBody converter for ").append(type).append(".\n");
      if (skipPast != null) {
         builder.append("  Skipped:");

         for (int ix = 0; ix < start; ix++) {
            builder.append("\n   * ").append(this.converterFactories.get(ix).getClass().getName());
         }

         builder.append('\n');
      }

      builder.append("  Tried:");
      int ix = start;

      for (int countx = this.converterFactories.size(); ix < countx; ix++) {
         builder.append("\n   * ").append(this.converterFactories.get(ix).getClass().getName());
      }

      throw new IllegalArgumentException(builder.toString());
   }

   public <T> Converter<T, String> stringConverter(Type type, Annotation[] annotations) {
      Objects.requireNonNull(type, "type == null");
      Objects.requireNonNull(annotations, "annotations == null");
      int i = 0;

      for (int count = this.converterFactories.size(); i < count; i++) {
         Converter<?, String> converter = this.converterFactories.get(i).stringConverter(type, annotations, this);
         if (converter != null) {
            return (Converter<T, String>)converter;
         }
      }

      return BuiltInConverters.ToStringConverter.INSTANCE;
   }

   @Nullable
   public Executor callbackExecutor() {
      return this.callbackExecutor;
   }

   public Retrofit.Builder newBuilder() {
      return new Retrofit.Builder(this);
   }

   public static final class Builder {
      @Nullable
      private Factory callFactory;
      @Nullable
      private HttpUrl baseUrl;
      private final List<Converter.Factory> converterFactories = new ArrayList<>();
      private final List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();
      @Nullable
      private Executor callbackExecutor;
      private boolean validateEagerly;

      public Builder() {
      }

      Builder(Retrofit retrofit) {
         this.callFactory = retrofit.callFactory;
         this.baseUrl = retrofit.baseUrl;
         int i = 1;

         for (int size = retrofit.converterFactories.size() - retrofit.defaultConverterFactoriesSize; i < size; i++) {
            this.converterFactories.add(retrofit.converterFactories.get(i));
         }

         i = 0;

         for (int size = retrofit.callAdapterFactories.size() - retrofit.defaultCallAdapterFactoriesSize; i < size; i++) {
            this.callAdapterFactories.add(retrofit.callAdapterFactories.get(i));
         }

         this.callbackExecutor = retrofit.callbackExecutor;
         this.validateEagerly = retrofit.validateEagerly;
      }

      public Retrofit.Builder client(OkHttpClient client) {
         return this.callFactory(Objects.requireNonNull(client, "client == null"));
      }

      public Retrofit.Builder callFactory(Factory factory) {
         this.callFactory = Objects.requireNonNull(factory, "factory == null");
         return this;
      }

      public Retrofit.Builder baseUrl(URL baseUrl) {
         Objects.requireNonNull(baseUrl, "baseUrl == null");
         return this.baseUrl(HttpUrl.get(baseUrl.toString()));
      }

      public Retrofit.Builder baseUrl(String baseUrl) {
         Objects.requireNonNull(baseUrl, "baseUrl == null");
         return this.baseUrl(HttpUrl.get(baseUrl));
      }

      public Retrofit.Builder baseUrl(HttpUrl baseUrl) {
         Objects.requireNonNull(baseUrl, "baseUrl == null");
         List<String> pathSegments = baseUrl.pathSegments();
         if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
            throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
         } else {
            this.baseUrl = baseUrl;
            return this;
         }
      }

      public Retrofit.Builder addConverterFactory(Converter.Factory factory) {
         this.converterFactories.add(Objects.requireNonNull(factory, "factory == null"));
         return this;
      }

      public Retrofit.Builder addCallAdapterFactory(CallAdapter.Factory factory) {
         this.callAdapterFactories.add(Objects.requireNonNull(factory, "factory == null"));
         return this;
      }

      public Retrofit.Builder callbackExecutor(Executor executor) {
         this.callbackExecutor = Objects.requireNonNull(executor, "executor == null");
         return this;
      }

      public List<CallAdapter.Factory> callAdapterFactories() {
         return this.callAdapterFactories;
      }

      public List<Converter.Factory> converterFactories() {
         return this.converterFactories;
      }

      public Retrofit.Builder validateEagerly(boolean validateEagerly) {
         this.validateEagerly = validateEagerly;
         return this;
      }

      public Retrofit build() {
         if (this.baseUrl == null) {
            throw new IllegalStateException("Base URL required.");
         } else {
            Factory callFactory = this.callFactory;
            if (callFactory == null) {
               callFactory = new OkHttpClient();
            }

            Executor callbackExecutor = this.callbackExecutor;
            if (callbackExecutor == null) {
               callbackExecutor = Platform.callbackExecutor;
            }

            BuiltInFactories builtInFactories = Platform.builtInFactories;
            List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>(this.callAdapterFactories);
            List<? extends CallAdapter.Factory> defaultCallAdapterFactories = builtInFactories.createDefaultCallAdapterFactories(callbackExecutor);
            callAdapterFactories.addAll(defaultCallAdapterFactories);
            List<? extends Converter.Factory> defaultConverterFactories = builtInFactories.createDefaultConverterFactories();
            int defaultConverterFactoriesSize = defaultConverterFactories.size();
            List<Converter.Factory> converterFactories = new ArrayList<>(1 + this.converterFactories.size() + defaultConverterFactoriesSize);
            converterFactories.add(new BuiltInConverters());
            converterFactories.addAll(this.converterFactories);
            converterFactories.addAll(defaultConverterFactories);
            return new Retrofit(
               callFactory,
               this.baseUrl,
               Collections.unmodifiableList(converterFactories),
               defaultConverterFactoriesSize,
               Collections.unmodifiableList(callAdapterFactories),
               defaultCallAdapterFactories.size(),
               callbackExecutor,
               this.validateEagerly
            );
         }
      }
   }
}
