package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.glassfish.jersey.Beta;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.CLIENT)
@Beta
public interface InvocationBuilderListener {
   void onNewBuilder(InvocationBuilderListener.InvocationBuilderContext var1);

   public interface InvocationBuilderContext {
      InvocationBuilderListener.InvocationBuilderContext accept(String... var1);

      InvocationBuilderListener.InvocationBuilderContext accept(MediaType... var1);

      InvocationBuilderListener.InvocationBuilderContext acceptLanguage(Locale... var1);

      InvocationBuilderListener.InvocationBuilderContext acceptLanguage(String... var1);

      InvocationBuilderListener.InvocationBuilderContext acceptEncoding(String... var1);

      InvocationBuilderListener.InvocationBuilderContext cookie(Cookie var1);

      InvocationBuilderListener.InvocationBuilderContext cookie(String var1, String var2);

      InvocationBuilderListener.InvocationBuilderContext cacheControl(CacheControl var1);

      List<String> getAccepted();

      List<String> getAcceptedLanguages();

      List<CacheControl> getCacheControls();

      Configuration getConfiguration();

      Map<String, Cookie> getCookies();

      List<String> getEncodings();

      List<String> getHeader(String var1);

      MultivaluedMap<String, Object> getHeaders();

      Object getProperty(String var1);

      Collection<String> getPropertyNames();

      URI getUri();

      InvocationBuilderListener.InvocationBuilderContext header(String var1, Object var2);

      InvocationBuilderListener.InvocationBuilderContext headers(MultivaluedMap<String, Object> var1);

      InvocationBuilderListener.InvocationBuilderContext property(String var1, Object var2);

      void removeProperty(String var1);
   }
}
