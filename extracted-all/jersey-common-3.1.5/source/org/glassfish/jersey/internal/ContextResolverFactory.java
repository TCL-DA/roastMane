package org.glassfish.jersey.internal;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.KeyComparatorHashMap;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.glassfish.jersey.spi.ContextResolvers;

public class ContextResolverFactory implements ContextResolvers {
   private final Map<Type, Map<MediaType, ContextResolver>> resolver = new HashMap<>(3);
   private final Map<Type, ConcurrentHashMap<MediaType, ContextResolver>> cache = new HashMap<>(3);
   private static final ContextResolverFactory.NullContextResolverAdapter NULL_CONTEXT_RESOLVER = new ContextResolverFactory.NullContextResolverAdapter();

   private ContextResolverFactory() {
   }

   private void initialize(List<ContextResolver> contextResolvers) {
      Map<Type, Map<MediaType, List<ContextResolver>>> rs = new HashMap<>();

      for (ContextResolver provider : contextResolvers) {
         List<MediaType> ms = MediaTypes.createFrom(provider.getClass().getAnnotation(Produces.class));
         Type type = this.getParameterizedType(provider.getClass());
         Map<MediaType, List<ContextResolver>> mr = rs.get(type);
         if (mr == null) {
            mr = new HashMap<>();
            rs.put(type, mr);
         }

         for (MediaType m : ms) {
            List<ContextResolver> crl = mr.get(m);
            if (crl == null) {
               crl = new ArrayList<>();
               mr.put(m, crl);
            }

            crl.add(provider);
         }
      }

      for (Entry<Type, Map<MediaType, List<ContextResolver>>> e : rs.entrySet()) {
         Map<MediaType, ContextResolver> mr = new KeyComparatorHashMap<>(4, MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
         this.resolver.put(e.getKey(), mr);
         this.cache.put(e.getKey(), new ConcurrentHashMap<>(4));

         for (Entry<MediaType, List<ContextResolver>> f : e.getValue().entrySet()) {
            mr.put(f.getKey(), this.reduce(f.getValue()));
         }
      }
   }

   private Type getParameterizedType(Class<?> c) {
      ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(c, ContextResolver.class);
      Type[] as = ReflectionHelper.getParameterizedTypeArguments(p);
      return (Type)(as != null ? as[0] : Object.class);
   }

   private ContextResolver reduce(List<ContextResolver> r) {
      return (ContextResolver)(r.size() == 1 ? r.iterator().next() : new ContextResolverFactory.ContextResolverAdapter(r));
   }

   @Override
   public <T> ContextResolver<T> resolve(Type t, MediaType m) {
      ConcurrentHashMap<MediaType, ContextResolver> crMapCache = this.cache.get(t);
      if (crMapCache == null) {
         return null;
      } else {
         if (m == null) {
            m = MediaType.WILDCARD_TYPE;
         }

         ContextResolver<T> cr = crMapCache.get(m);
         if (cr == null) {
            Map<MediaType, ContextResolver> crMap = this.resolver.get(t);
            if (m.isWildcardType()) {
               cr = crMap.get(MediaType.WILDCARD_TYPE);
               if (cr == null) {
                  cr = NULL_CONTEXT_RESOLVER;
               }
            } else if (m.isWildcardSubtype()) {
               ContextResolver<T> subTypeWildCard = crMap.get(m);
               ContextResolver<T> wildCard = crMap.get(MediaType.WILDCARD_TYPE);
               cr = new ContextResolverFactory.ContextResolverAdapter(subTypeWildCard, wildCard).reduce();
            } else {
               ContextResolver<T> type = crMap.get(m);
               ContextResolver<T> subTypeWildCard = crMap.get(new MediaType(m.getType(), "*"));
               ContextResolver<T> wildCard = crMap.get(MediaType.WILDCARD_TYPE);
               cr = new ContextResolverFactory.ContextResolverAdapter(type, subTypeWildCard, wildCard).reduce();
            }

            ContextResolver<T> _cr = crMapCache.putIfAbsent(m, cr);
            if (_cr != null) {
               cr = _cr;
            }
         }

         return cr != NULL_CONTEXT_RESOLVER ? cr : null;
      }
   }

   private static final class ContextResolverAdapter implements ContextResolver {
      private final ContextResolver[] cra;

      ContextResolverAdapter(ContextResolver... cra) {
         this(removeNull(cra));
      }

      ContextResolverAdapter(List<ContextResolver> crl) {
         this.cra = crl.toArray(new ContextResolver[crl.size()]);
      }

      public Object getContext(Class objectType) {
         for (ContextResolver cr : this.cra) {
            Object c = cr.getContext(objectType);
            if (c != null) {
               return c;
            }
         }

         return null;
      }

      ContextResolver reduce() {
         if (this.cra.length == 0) {
            return ContextResolverFactory.NULL_CONTEXT_RESOLVER;
         } else {
            return (ContextResolver)(this.cra.length == 1 ? this.cra[0] : this);
         }
      }

      private static List<ContextResolver> removeNull(ContextResolver... cra) {
         List<ContextResolver> crl = new ArrayList<>(cra.length);

         for (ContextResolver cr : cra) {
            if (cr != null) {
               crl.add(cr);
            }
         }

         return crl;
      }
   }

   public static class ContextResolversConfigurator implements BootstrapConfigurator {
      private ContextResolverFactory contextResolverFactory;

      @Override
      public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         this.contextResolverFactory = new ContextResolverFactory();
         InstanceBinding<ContextResolverFactory> binding = Bindings.service(this.contextResolverFactory).to(ContextResolvers.class);
         injectionManager.register((Binding)binding);
      }

      @Override
      public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         this.contextResolverFactory.initialize(injectionManager.getAllInstances(ContextResolver.class));
         bootstrapBag.setContextResolvers(this.contextResolverFactory);
      }
   }

   private static final class NullContextResolverAdapter implements ContextResolver {
      private NullContextResolverAdapter() {
      }

      public Object getContext(Class type) {
         throw new UnsupportedOperationException("Not supported yet.");
      }
   }
}
