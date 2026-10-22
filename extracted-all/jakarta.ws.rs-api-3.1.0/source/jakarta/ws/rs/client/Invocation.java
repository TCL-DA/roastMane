package jakarta.ws.rs.client;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import java.util.Locale;
import java.util.concurrent.Future;

public interface Invocation {
   Invocation property(String var1, Object var2);

   Response invoke();

   <T> T invoke(Class<T> var1);

   <T> T invoke(GenericType<T> var1);

   Future<Response> submit();

   <T> Future<T> submit(Class<T> var1);

   <T> Future<T> submit(GenericType<T> var1);

   <T> Future<T> submit(InvocationCallback<T> var1);

   public interface Builder extends SyncInvoker {
      Invocation build(String var1);

      Invocation build(String var1, Entity<?> var2);

      Invocation buildGet();

      Invocation buildDelete();

      Invocation buildPost(Entity<?> var1);

      Invocation buildPut(Entity<?> var1);

      AsyncInvoker async();

      Invocation.Builder accept(String... var1);

      Invocation.Builder accept(MediaType... var1);

      Invocation.Builder acceptLanguage(Locale... var1);

      Invocation.Builder acceptLanguage(String... var1);

      Invocation.Builder acceptEncoding(String... var1);

      Invocation.Builder cookie(Cookie var1);

      Invocation.Builder cookie(String var1, String var2);

      Invocation.Builder cacheControl(CacheControl var1);

      Invocation.Builder header(String var1, Object var2);

      Invocation.Builder headers(MultivaluedMap<String, Object> var1);

      Invocation.Builder property(String var1, Object var2);

      CompletionStageRxInvoker rx();

      <T extends RxInvoker> T rx(Class<T> var1);
   }
}
