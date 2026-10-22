package org.glassfish.jersey.message.filtering;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.message.filtering.spi.AbstractObjectProvider;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import org.glassfish.jersey.message.filtering.spi.ScopeProvider;

final class ObjectGraphProvider extends AbstractObjectProvider<ObjectGraph> {
   @Inject
   public ObjectGraphProvider(@Context ScopeProvider scopeProvider, @Context EntityInspector entityInspector, @Context EntityGraphProvider graphProvider) {
      super(scopeProvider, entityInspector, graphProvider);
   }

   public ObjectGraph transform(ObjectGraph graph) {
      return graph;
   }
}
