package retrofit2;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.Call.Factory;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Timeout;

final class OkHttpCall<T> implements Call<T> {
   private final RequestFactory requestFactory;
   private final Object instance;
   private final Object[] args;
   private final Factory callFactory;
   private final Converter<ResponseBody, T> responseConverter;
   private volatile boolean canceled;
   @Nullable
   @GuardedBy("this")
   private okhttp3.Call rawCall;
   @Nullable
   @GuardedBy("this")
   private Throwable creationFailure;
   @GuardedBy("this")
   private boolean executed;

   OkHttpCall(RequestFactory requestFactory, Object instance, Object[] args, Factory callFactory, Converter<ResponseBody, T> responseConverter) {
      this.requestFactory = requestFactory;
      this.instance = instance;
      this.args = args;
      this.callFactory = callFactory;
      this.responseConverter = responseConverter;
   }

   public OkHttpCall<T> clone() {
      return new OkHttpCall<>(this.requestFactory, this.instance, this.args, this.callFactory, this.responseConverter);
   }

   @Override
   public synchronized Request request() {
      try {
         return this.getRawCall().request();
      } catch (IOException var2) {
         throw new RuntimeException("Unable to create request.", var2);
      }
   }

   @Override
   public synchronized Timeout timeout() {
      try {
         return this.getRawCall().timeout();
      } catch (IOException var2) {
         throw new RuntimeException("Unable to create call.", var2);
      }
   }

   @GuardedBy("this")
   private okhttp3.Call getRawCall() throws IOException {
      okhttp3.Call call = this.rawCall;
      if (call != null) {
         return call;
      } else if (this.creationFailure != null) {
         if (this.creationFailure instanceof IOException) {
            throw (IOException)this.creationFailure;
         } else if (this.creationFailure instanceof RuntimeException) {
            throw (RuntimeException)this.creationFailure;
         } else {
            throw (Error)this.creationFailure;
         }
      } else {
         try {
            return this.rawCall = this.createRawCall();
         } catch (Error | IOException | RuntimeException var3) {
            Utils.throwIfFatal(var3);
            this.creationFailure = var3;
            throw var3;
         }
      }
   }

   @Override
   public void enqueue(final Callback<T> callback) {
      Objects.requireNonNull(callback, "callback == null");
      okhttp3.Call call;
      Throwable failure;
      synchronized (this) {
         if (this.executed) {
            throw new IllegalStateException("Already executed.");
         }

         this.executed = true;
         call = this.rawCall;
         failure = this.creationFailure;
         if (call == null && failure == null) {
            try {
               call = this.rawCall = this.createRawCall();
            } catch (Throwable var7) {
               Utils.throwIfFatal(var7);
               failure = this.creationFailure = var7;
            }
         }
      }

      if (failure != null) {
         callback.onFailure(this, failure);
      } else {
         if (this.canceled) {
            call.cancel();
         }

         call.enqueue(new okhttp3.Callback() {
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
               Response<T> response;
               try {
                  response = OkHttpCall.this.parseResponse(rawResponse);
               } catch (Throwable var6) {
                  Utils.throwIfFatal(var6);
                  this.callFailure(var6);
                  return;
               }

               try {
                  callback.onResponse(OkHttpCall.this, response);
               } catch (Throwable var5) {
                  Utils.throwIfFatal(var5);
                  var5.printStackTrace();
               }
            }

            public void onFailure(okhttp3.Call call, IOException e) {
               this.callFailure(e);
            }

            private void callFailure(Throwable e) {
               try {
                  callback.onFailure(OkHttpCall.this, e);
               } catch (Throwable var3) {
                  Utils.throwIfFatal(var3);
                  var3.printStackTrace();
               }
            }
         });
      }
   }

   @Override
   public synchronized boolean isExecuted() {
      return this.executed;
   }

   @Override
   public Response<T> execute() throws IOException {
      okhttp3.Call call;
      synchronized (this) {
         if (this.executed) {
            throw new IllegalStateException("Already executed.");
         }

         this.executed = true;
         call = this.getRawCall();
      }

      if (this.canceled) {
         call.cancel();
      }

      return this.parseResponse(call.execute());
   }

   private okhttp3.Call createRawCall() throws IOException {
      okhttp3.Call call = this.callFactory.newCall(this.requestFactory.create(this.instance, this.args));
      if (call == null) {
         throw new NullPointerException("Call.Factory returned null.");
      } else {
         return call;
      }
   }

   Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
      ResponseBody rawBody = rawResponse.body();
      rawResponse = rawResponse.newBuilder().body(new OkHttpCall.NoContentResponseBody(rawBody.contentType(), rawBody.contentLength())).build();
      int code = rawResponse.code();
      if (code < 200 || code >= 300) {
         Response var13;
         try {
            ResponseBody bufferedBody = Utils.buffer(rawBody);
            var13 = Response.error(bufferedBody, rawResponse);
         } finally {
            rawBody.close();
         }

         return var13;
      } else if (code != 204 && code != 205) {
         OkHttpCall.ExceptionCatchingResponseBody catchingBody = new OkHttpCall.ExceptionCatchingResponseBody(rawBody);

         try {
            T body = this.responseConverter.convert(catchingBody);
            return Response.success(body, rawResponse);
         } catch (RuntimeException var9) {
            catchingBody.throwIfCaught();
            throw var9;
         }
      } else {
         rawBody.close();
         return Response.success(null, rawResponse);
      }
   }

   @Override
   public void cancel() {
      this.canceled = true;
      okhttp3.Call call;
      synchronized (this) {
         call = this.rawCall;
      }

      if (call != null) {
         call.cancel();
      }
   }

   @Override
   public boolean isCanceled() {
      if (this.canceled) {
         return true;
      } else {
         synchronized (this) {
            return this.rawCall != null && this.rawCall.isCanceled();
         }
      }
   }

   static final class ExceptionCatchingResponseBody extends ResponseBody {
      private final ResponseBody delegate;
      private final BufferedSource delegateSource;
      @Nullable
      IOException thrownException;

      ExceptionCatchingResponseBody(ResponseBody delegate) {
         this.delegate = delegate;
         this.delegateSource = Okio.buffer(new ForwardingSource(delegate.source()) {
            public long read(Buffer sink, long byteCount) throws IOException {
               try {
                  return super.read(sink, byteCount);
               } catch (IOException var5) {
                  ExceptionCatchingResponseBody.this.thrownException = var5;
                  throw var5;
               }
            }
         });
      }

      public MediaType contentType() {
         return this.delegate.contentType();
      }

      public long contentLength() {
         return this.delegate.contentLength();
      }

      public BufferedSource source() {
         return this.delegateSource;
      }

      public void close() {
         this.delegate.close();
      }

      void throwIfCaught() throws IOException {
         if (this.thrownException != null) {
            throw this.thrownException;
         }
      }
   }

   static final class NoContentResponseBody extends ResponseBody {
      @Nullable
      private final MediaType contentType;
      private final long contentLength;

      NoContentResponseBody(@Nullable MediaType contentType, long contentLength) {
         this.contentType = contentType;
         this.contentLength = contentLength;
      }

      public MediaType contentType() {
         return this.contentType;
      }

      public long contentLength() {
         return this.contentLength;
      }

      public BufferedSource source() {
         throw new IllegalStateException("Cannot read raw response body of a converted body.");
      }
   }
}
