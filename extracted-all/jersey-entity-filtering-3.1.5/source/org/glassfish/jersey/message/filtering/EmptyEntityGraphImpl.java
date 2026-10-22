package org.glassfish.jersey.message.filtering;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;

final class EmptyEntityGraphImpl implements EntityGraph {
   private final Class<?> clazz;

   EmptyEntityGraphImpl(Class<?> clazz) {
      this.clazz = clazz;
   }

   @Override
   public EntityGraph addField(String fieldName) {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityGraph addField(String fieldName, String... filteringScopes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityGraph addField(String fieldName, Set<String> filteringScopes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityGraph addSubgraph(String fieldName, Class<?> fieldClass) {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityGraph addSubgraph(String fieldName, Class<?> fieldClass, String... filteringScopes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public EntityGraph addSubgraph(String fieldName, Class<?> fieldClass, Set<String> filteringScopes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Class<?> getEntityClass() {
      return this.clazz;
   }

   @Override
   public Set<String> getFields(String filteringScope) {
      return Collections.emptySet();
   }

   @Override
   public Set<String> getFields(String... filteringScopes) {
      return Collections.emptySet();
   }

   @Override
   public Set<String> getFields(Set<String> filteringScopes) {
      return Collections.emptySet();
   }

   @Override
   public Map<String, Class<?>> getSubgraphs(String filteringScope) {
      return Collections.emptyMap();
   }

   @Override
   public Map<String, Class<?>> getSubgraphs(String... filteringScopes) {
      return Collections.emptyMap();
   }

   @Override
   public Map<String, Class<?>> getSubgraphs(Set<String> filteringScopes) {
      return Collections.emptyMap();
   }

   @Override
   public boolean presentInScopes(String field) {
      return false;
   }

   @Override
   public boolean presentInScope(String field, String filteringScope) {
      return false;
   }

   @Override
   public EntityGraph remove(String name) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<String> getFilteringScopes() {
      return Collections.emptySet();
   }

   @Override
   public Set<String> getClassFilteringScopes() {
      return Collections.emptySet();
   }

   @Override
   public EntityGraph addFilteringScopes(Set<String> filteringScopes) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         EmptyEntityGraphImpl that = (EmptyEntityGraphImpl)o;
         return this.clazz.equals(that.clazz);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.clazz.hashCode();
   }
}
