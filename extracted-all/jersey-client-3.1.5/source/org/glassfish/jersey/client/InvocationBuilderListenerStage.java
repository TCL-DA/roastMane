package org.glassfish.jersey.client;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.glassfish.jersey.client.spi.InvocationBuilderListener;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.internal.RankedComparator.Order;

class InvocationBuilderListenerStage {
   final Iterator<InvocationBuilderListener> invocationBuilderListenerIterator;

   InvocationBuilderListenerStage(InjectionManager injectionManager) {
      RankedComparator<InvocationBuilderListener> comparator = new RankedComparator(Order.ASCENDING);
      this.invocationBuilderListenerIterator = Providers.getAllProviders(injectionManager, InvocationBuilderListener.class, comparator).iterator();
   }

   void invokeListener(JerseyInvocation.Builder builder) {
      while (this.invocationBuilderListenerIterator.hasNext()) {
         this.invocationBuilderListenerIterator.next().onNewBuilder(new InvocationBuilderListenerStage.InvocationBuilderContextImpl(builder));
      }
   }

   private static class InvocationBuilderContextImpl implements InvocationBuilderListener.InvocationBuilderContext {
      private final JerseyInvocation.Builder builder;

      private InvocationBuilderContextImpl(JerseyInvocation.Builder builder) {
         this.builder = builder;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext accept(String... mediaTypes) {
         this.builder.accept(mediaTypes);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext accept(MediaType... mediaTypes) {
         this.builder.accept(mediaTypes);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext acceptLanguage(Locale... locales) {
         this.builder.acceptLanguage(locales);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext acceptLanguage(String... locales) {
         this.builder.acceptLanguage(locales);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext acceptEncoding(String... encodings) {
         this.builder.acceptEncoding(encodings);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext cookie(Cookie cookie) {
         this.builder.cookie(cookie);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext cookie(String name, String value) {
         this.builder.cookie(name, value);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext cacheControl(CacheControl cacheControl) {
         this.builder.cacheControl(cacheControl);
         return this;
      }

      @Override
      public List<String> getAccepted() {
         return this.getHeader("Accept");
      }

      @Override
      public List<String> getAcceptedLanguages() {
         return this.getHeader("Accept-Language");
      }

      @Override
      public List<CacheControl> getCacheControls() {
         return (List<CacheControl>)this.builder.request().getHeaders().get("Cache-Control");
      }

      @Override
      public Configuration getConfiguration() {
         return this.builder.request().getConfiguration();
      }

      @Override
      public Map<String, Cookie> getCookies() {
         return this.builder.request().getCookies();
      }

      @Override
      public List<String> getEncodings() {
         return this.getHeader("Accept-Encoding");
      }

      @Override
      public List<String> getHeader(String name) {
         return this.builder.request().getRequestHeader(name);
      }

      @Override
      public MultivaluedMap<String, Object> getHeaders() {
         return this.builder.request().getHeaders();
      }

      @Override
      public Object getProperty(String name) {
         return this.builder.request().getProperty(name);
      }

      @Override
      public Collection<String> getPropertyNames() {
         return this.builder.request().getPropertyNames();
      }

      @Override
      public URI getUri() {
         return this.builder.request().getUri();
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext header(String name, Object value) {
         this.builder.header(name, value);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext headers(MultivaluedMap<String, Object> headers) {
         this.builder.headers(headers);
         return this;
      }

      @Override
      public InvocationBuilderListener.InvocationBuilderContext property(String name, Object value) {
         this.builder.property(name, value);
         return this;
      }

      @Override
      public void removeProperty(String name) {
         this.builder.request().removeProperty(name);
      }
   }
}
