package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import org.glassfish.jersey.message.filtering.spi.AbstractObjectProvider;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;

final class JacksonObjectProvider extends AbstractObjectProvider<FilterProvider> {
   @Inject
   public JacksonObjectProvider(@Context ScopeProvider scopeProvider, @Context EntityInspector entityInspector, @Context EntityGraphProvider graphProvider) {
      super(scopeProvider, entityInspector, graphProvider);
   }

   public FilterProvider transform(ObjectGraph graph) {
      JacksonObjectProvider.FilteringPropertyFilter root = new JacksonObjectProvider.FilteringPropertyFilter(
         graph.getEntityClass(), graph.getFields(), this.createSubfilters(graph.getEntityClass(), graph.getSubgraphs())
      );
      return new JacksonObjectProvider.FilteringFilterProvider(root);
   }

   private Map<String, JacksonObjectProvider.FilteringPropertyFilter> createSubfilters(Class<?> entityClass, Map<String, ObjectGraph> entitySubgraphs) {
      Map<String, JacksonObjectProvider.FilteringPropertyFilter> subfilters = new HashMap<>();

      for (Entry<String, ObjectGraph> entry : entitySubgraphs.entrySet()) {
         String fieldName = entry.getKey();
         ObjectGraph graph = entry.getValue();
         Map<String, ObjectGraph> subgraphs = graph.getSubgraphs(fieldName);
         Map<String, JacksonObjectProvider.FilteringPropertyFilter> subSubfilters = new HashMap<>();
         if (!subgraphs.isEmpty()) {
            Class<?> subEntityClass = graph.getEntityClass();
            Set<String> processed = Collections.singleton(this.subgraphIdentifier(entityClass, fieldName, subEntityClass));
            subSubfilters = this.createSubfilters(fieldName, subEntityClass, subgraphs, processed);
         }

         JacksonObjectProvider.FilteringPropertyFilter filter = new JacksonObjectProvider.FilteringPropertyFilter(
            graph.getEntityClass(), graph.getFields(fieldName), subSubfilters
         );
         subfilters.put(fieldName, filter);
      }

      return subfilters;
   }

   private Map<String, JacksonObjectProvider.FilteringPropertyFilter> createSubfilters(
      String parent, Class<?> entityClass, Map<String, ObjectGraph> entitySubgraphs, Set<String> processed
   ) {
      Map<String, JacksonObjectProvider.FilteringPropertyFilter> subfilters = new HashMap<>();

      for (Entry<String, ObjectGraph> entry : entitySubgraphs.entrySet()) {
         String fieldName = entry.getKey();
         ObjectGraph graph = entry.getValue();
         String path = parent + "." + fieldName;
         Map<String, ObjectGraph> subgraphs = graph.getSubgraphs(path);
         Class<?> subEntityClass = graph.getEntityClass();
         String processedSubgraph = this.subgraphIdentifier(entityClass, fieldName, subEntityClass);
         Map<String, JacksonObjectProvider.FilteringPropertyFilter> subSubfilters = new HashMap<>();
         if (!subgraphs.isEmpty() && !processed.contains(processedSubgraph)) {
            Set<String> subProcessed = this.immutableSetOf(processed, processedSubgraph);
            subSubfilters = this.createSubfilters(path, subEntityClass, subgraphs, subProcessed);
         }

         subfilters.put(fieldName, new JacksonObjectProvider.FilteringPropertyFilter(graph.getEntityClass(), graph.getFields(path), subSubfilters));
      }

      return subfilters;
   }

   private static class FilteringFilterProvider extends FilterProvider {
      private final JacksonObjectProvider.FilteringPropertyFilter root;
      private final Stack<JacksonObjectProvider.FilteringPropertyFilter> stack = new Stack<>();

      public FilteringFilterProvider(JacksonObjectProvider.FilteringPropertyFilter root) {
         this.root = root;
      }

      public BeanPropertyFilter findFilter(Object filterId) {
         throw new UnsupportedOperationException("Access to deprecated filters not supported");
      }

      public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
         if (filterId instanceof String) {
            String id = (String)filterId;
            if (id.equals(this.root.getEntityClass().getName())) {
               this.stack.clear();
               return this.stack.push(this.root);
            }

            while (!this.stack.isEmpty()) {
               JacksonObjectProvider.FilteringPropertyFilter peek = this.stack.peek();
               JacksonObjectProvider.FilteringPropertyFilter subfilter = peek.findSubfilter(id);
               if (subfilter != null) {
                  this.stack.push(subfilter);
                  if (valueToFilter instanceof Map) {
                     Map<String, ?> map = (Map<String, ?>)valueToFilter;
                     return new JacksonObjectProvider.FilteringPropertyFilter(Map.class, map.keySet(), Collections.emptyMap());
                  }

                  return subfilter;
               }

               this.stack.pop();
            }
         }

         return SimpleBeanPropertyFilter.filterOutAllExcept(new String[0]);
      }
   }

   private static final class FilteringPropertyFilter implements PropertyFilter {
      private final Class<?> entityClass;
      private final Set<String> fields;
      private final Map<String, JacksonObjectProvider.FilteringPropertyFilter> subfilters;

      private FilteringPropertyFilter(Class<?> entityClass, Set<String> fields, Map<String, JacksonObjectProvider.FilteringPropertyFilter> subfilters) {
         this.entityClass = entityClass;
         this.fields = fields;
         this.subfilters = subfilters;
      }

      private boolean include(String fieldName) {
         return this.fields.contains(fieldName) || this.subfilters.containsKey(fieldName);
      }

      public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer) throws Exception {
         if (this.include(writer.getName())) {
            writer.serializeAsField(pojo, jgen, prov);
         }
      }

      public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider prov, PropertyWriter writer) throws Exception {
         if (this.include(writer.getName())) {
            writer.serializeAsElement(elementValue, jgen, prov);
         }
      }

      public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
         if (this.include(writer.getName())) {
            writer.depositSchemaProperty(propertiesNode, provider);
         }
      }

      public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor, SerializerProvider provider) throws JsonMappingException {
         if (this.include(writer.getName())) {
            writer.depositSchemaProperty(objectVisitor, provider);
         }
      }

      public JacksonObjectProvider.FilteringPropertyFilter findSubfilter(String fieldName) {
         return this.subfilters.get(fieldName);
      }

      public Class<?> getEntityClass() {
         return this.entityClass;
      }
   }
}
