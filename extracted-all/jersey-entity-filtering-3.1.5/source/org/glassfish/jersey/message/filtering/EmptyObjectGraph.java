package org.glassfish.jersey.message.filtering;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;

final class EmptyObjectGraph implements ObjectGraph {
   private final Class<?> entityClass;

   EmptyObjectGraph(Class<?> entityClass) {
      this.entityClass = entityClass;
   }

   @Override
   public Class<?> getEntityClass() {
      return this.entityClass;
   }

   @Override
   public Set<String> getFields() {
      return Collections.emptySet();
   }

   @Override
   public Set<String> getFields(String parent) {
      return Collections.emptySet();
   }

   @Override
   public Map<String, ObjectGraph> getSubgraphs() {
      return Collections.emptyMap();
   }

   @Override
   public Map<String, ObjectGraph> getSubgraphs(String parent) {
      return Collections.emptyMap();
   }
}
