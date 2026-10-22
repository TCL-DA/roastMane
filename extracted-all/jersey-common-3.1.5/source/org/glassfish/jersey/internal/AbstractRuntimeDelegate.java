package org.glassfish.jersey.internal;

import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Variant.VariantListBuilder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.glassfish.jersey.innate.spi.EntityPartBuilderProvider;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.internal.JerseyLink;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.glassfish.jersey.spi.HeaderDelegateProvider;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

public abstract class AbstractRuntimeDelegate extends RuntimeDelegate {
   private final Set<HeaderDelegateProvider> hps;
   private final Map<Class<?>, HeaderDelegate<?>> map;
   private LazyValue<EntityPartBuilderProvider> entityPartBuilderProvider = Values.lazy(
      (Value<EntityPartBuilderProvider>)(() -> findEntityPartBuilderProvider())
   );

   protected AbstractRuntimeDelegate(Set<HeaderDelegateProvider> hps) {
      this.hps = hps;
      this.map = new WeakHashMap<>();
      this.map.put(EntityTag.class, this._createHeaderDelegate(EntityTag.class));
      this.map.put(MediaType.class, this._createHeaderDelegate(MediaType.class));
      this.map.put(CacheControl.class, this._createHeaderDelegate(CacheControl.class));
      this.map.put(NewCookie.class, this._createHeaderDelegate(NewCookie.class));
      this.map.put(Cookie.class, this._createHeaderDelegate(Cookie.class));
      this.map.put(URI.class, this._createHeaderDelegate(URI.class));
      this.map.put(Date.class, this._createHeaderDelegate(Date.class));
      this.map.put(String.class, this._createHeaderDelegate(String.class));
   }

   public VariantListBuilder createVariantListBuilder() {
      return new org.glassfish.jersey.message.internal.VariantListBuilder();
   }

   public ResponseBuilder createResponseBuilder() {
      return new OutboundJaxrsResponse.Builder(new OutboundMessageContext((Configuration)null));
   }

   public UriBuilder createUriBuilder() {
      return new JerseyUriBuilder();
   }

   public Builder createLinkBuilder() {
      return new JerseyLink.Builder();
   }

   public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
      if (type == null) {
         throw new IllegalArgumentException("type parameter cannot be null");
      } else {
         HeaderDelegate<T> delegate = (HeaderDelegate<T>)this.map.get(type);
         return delegate != null ? delegate : this._createHeaderDelegate(type);
      }
   }

   private <T> HeaderDelegate<T> _createHeaderDelegate(Class<T> type) {
      for (HeaderDelegateProvider hp : this.hps) {
         if (hp.supports(type)) {
            return hp;
         }
      }

      return null;
   }

   public jakarta.ws.rs.core.EntityPart.Builder createEntityPartBuilder(String partName) throws IllegalArgumentException {
      return this.entityPartBuilderProvider.get().withName(partName);
   }

   private static EntityPartBuilderProvider findEntityPartBuilderProvider() {
      for (EntityPartBuilderProvider entityPartBuilder : ServiceFinder.find(EntityPartBuilderProvider.class)) {
         if (entityPartBuilder != null) {
            return entityPartBuilder;
         }
      }

      throw new IllegalArgumentException(LocalizationMessages.NO_ENTITYPART_BUILDER_FOUND());
   }
}
