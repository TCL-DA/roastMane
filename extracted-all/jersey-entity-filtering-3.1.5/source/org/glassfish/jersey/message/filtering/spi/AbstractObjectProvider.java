package org.glassfish.jersey.message.filtering.spi;

import jakarta.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.glassfish.jersey.internal.guava.Cache;
import org.glassfish.jersey.internal.guava.CacheBuilder;

public abstract class AbstractObjectProvider<T> implements ObjectProvider<T>, ObjectGraphTransformer<T> {
   private static final int PROVIDER_CACHE_SIZE = 1000;
   private final Cache<AbstractObjectProvider.EntityContext, T> filteringObjects = CacheBuilder.newBuilder().maximumSize(1000L).build();
   private ScopeProvider scopeProvider;
   private EntityInspector entityInspector;
   private EntityGraphProvider graphProvider;

   public AbstractObjectProvider(@Context ScopeProvider scopeProvider, @Context EntityInspector entityInspector, @Context EntityGraphProvider graphProvider) {
      this.scopeProvider = scopeProvider;
      this.entityInspector = entityInspector;
      this.graphProvider = graphProvider;
   }

   @Override
   public final T getFilteringObject(Type genericType, boolean forWriter, Annotation... annotations) {
      return this.getFilteringObject(FilteringHelper.getEntityClass(genericType), forWriter, annotations);
   }

   private T getFilteringObject(Class<?> entityClass, boolean forWriter, Annotation... annotations) {
      if (FilteringHelper.filterableEntityClass(entityClass)) {
         this.entityInspector.inspect(entityClass, forWriter);
         Set<String> filteringScope = this.scopeProvider.getFilteringScopes(this.getEntityAnnotations(annotations), true);
         AbstractObjectProvider.EntityContext entityContext = new AbstractObjectProvider.EntityContext(entityClass, filteringScope);
         T filteringObject = (T)this.filteringObjects.getIfPresent(entityContext);
         if (filteringObject == null) {
            filteringObject = this.createFilteringObject(entityClass, filteringScope, forWriter);
            this.filteringObjects.put(entityContext, filteringObject);
         }

         return filteringObject;
      } else {
         return null;
      }
   }

   private Annotation[] getEntityAnnotations(Annotation[] annotations) {
      ArrayList<Annotation> entityAnnotations = new ArrayList<>();

      for (Annotation annotation : annotations) {
         if (!(annotation instanceof Proxy)) {
            entityAnnotations.add(annotation);
         }
      }

      return entityAnnotations.toArray(new Annotation[entityAnnotations.size()]);
   }

   private T createFilteringObject(Class<?> entityClass, Set<String> filteringScopes, boolean forWriter) {
      return this.transform(this.graphProvider.createObjectGraph(entityClass, filteringScopes, forWriter));
   }

   protected Set<String> immutableSetOf(Set<String> set, String item) {
      Set<String> duplicate = new HashSet<>(set);
      duplicate.add(item);
      return Collections.unmodifiableSet(duplicate);
   }

   protected String subgraphIdentifier(Class<?> parent, String field, Class<?> fieldClass) {
      return parent.getName() + "_" + field + "_" + fieldClass.getName();
   }

   private static final class EntityContext {
      private final Class<?> entityClass;
      private final Set<String> filteringContext;

      private EntityContext(Class<?> entityClass, Set<String> filteringScopes) {
         this.entityClass = entityClass;
         this.filteringContext = filteringScopes;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof AbstractObjectProvider.EntityContext)) {
            return false;
         } else {
            AbstractObjectProvider.EntityContext that = (AbstractObjectProvider.EntityContext)o;
            return this.entityClass.equals(that.entityClass) && this.filteringContext.equals(that.filteringContext);
         }
      }

      @Override
      public int hashCode() {
         int result = this.entityClass.hashCode();
         return 47 * result + this.filteringContext.hashCode();
      }
   }
}
