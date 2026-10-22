package org.glassfish.jersey.message.filtering;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.glassfish.jersey.message.filtering.spi.EntityGraph;
import org.glassfish.jersey.message.filtering.spi.EntityProcessorContext;

final class EntityProcessorContextImpl implements EntityProcessorContext {
   private final EntityProcessorContext.Type type;
   private final Class<?> clazz;
   private final Field field;
   private final Method method;
   private final EntityGraph graph;

   public EntityProcessorContextImpl(EntityProcessorContext.Type type, Class<?> clazz, EntityGraph graph) {
      this(type, clazz, null, null, graph);
   }

   public EntityProcessorContextImpl(EntityProcessorContext.Type type, Field field, Method method, EntityGraph graph) {
      this(type, null, field, method, graph);
   }

   public EntityProcessorContextImpl(EntityProcessorContext.Type type, Method method, EntityGraph graph) {
      this(type, null, null, method, graph);
   }

   public EntityProcessorContextImpl(EntityProcessorContext.Type type, Class<?> clazz, Field field, Method method, EntityGraph graph) {
      this.type = type;
      this.clazz = clazz;
      this.field = field;
      this.method = method;
      this.graph = graph;
   }

   @Override
   public EntityProcessorContext.Type getType() {
      return this.type;
   }

   @Override
   public Class<?> getEntityClass() {
      return this.clazz;
   }

   @Override
   public Field getField() {
      return this.field;
   }

   @Override
   public Method getMethod() {
      return this.method;
   }

   @Override
   public EntityGraph getEntityGraph() {
      return this.graph;
   }
}
