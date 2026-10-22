package org.glassfish.jersey.message.filtering;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.glassfish.jersey.internal.util.collection.Views;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;

final class ObjectGraphImpl implements ObjectGraph {
   private final Set<String> filteringScopes;
   private final Map<Class<?>, EntityGraph> classToGraph;
   private final EntityGraph graph;
   private Set<String> fields;
   private Map<String, ObjectGraph> subgraphs;

   ObjectGraphImpl(Map<Class<?>, EntityGraph> classToGraph, EntityGraph graph, Set<String> filteringScopes) {
      this.filteringScopes = filteringScopes;
      this.classToGraph = classToGraph;
      this.graph = graph;
   }

   @Override
   public Class<?> getEntityClass() {
      return this.graph.getEntityClass();
   }

   @Override
   public Set<String> getFields() {
      return this.getFields(null);
   }

   @Override
   public Set<String> getFields(String parent) {
      Set<String> childFilteringScopes = this.getFilteringScopes(parent);
      if (this.fields == null) {
         this.fields = this.graph.getFields(Views.setUnionView(childFilteringScopes, Collections.singleton(ScopeProvider.DEFAULT_SCOPE)));
      }

      return this.fields;
   }

   @Override
   public Map<String, ObjectGraph> getSubgraphs() {
      return this.getSubgraphs(null);
   }

   @Override
   public Map<String, ObjectGraph> getSubgraphs(String parent) {
      Set<String> childFilteringScopes = this.getFilteringScopes(parent);
      if (this.subgraphs == null) {
         Map<String, Class<?>> contextSubgraphs = this.graph.getSubgraphs(childFilteringScopes);
         contextSubgraphs.putAll(this.graph.getSubgraphs(ScopeProvider.DEFAULT_SCOPE));
         this.subgraphs = Views.mapView(
            contextSubgraphs,
            new Function<Class<?>, ObjectGraph>() {
               public ObjectGraph apply(Class<?> clazz) {
                  EntityGraph entityGraph = ObjectGraphImpl.this.classToGraph.get(clazz);
                  return (ObjectGraph)(entityGraph == null
                     ? new EmptyObjectGraph(clazz)
                     : new ObjectGraphImpl(ObjectGraphImpl.this.classToGraph, entityGraph, ObjectGraphImpl.this.filteringScopes));
               }
            }
         );
      }

      return this.subgraphs;
   }

   private Set<String> getFilteringScopes(String parent) {
      Set<String> childFilteringScopes = new HashSet<>();
      if (!this.filteringScopes.contains(SelectableScopeResolver.DEFAULT_SCOPE) && parent != null) {
         for (String filteringScope : this.filteringScopes) {
            Pattern p = Pattern.compile(SelectableScopeResolver.PREFIX + parent + "\\.(\\w+)(\\.\\w+)*$");
            Matcher m = p.matcher(filteringScope);
            if (m.matches()) {
               childFilteringScopes.add(SelectableScopeResolver.PREFIX + m.group(1));
            } else {
               childFilteringScopes.add(filteringScope);
            }
         }
      } else {
         childFilteringScopes = this.filteringScopes;
      }

      return childFilteringScopes;
   }
}
