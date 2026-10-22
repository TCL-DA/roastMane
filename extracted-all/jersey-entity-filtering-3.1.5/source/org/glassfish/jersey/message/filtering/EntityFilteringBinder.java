package org.glassfish.jersey.message.filtering;

import jakarta.inject.Singleton;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.message.filtering.spi.EntityGraphProvider;
import org.glassfish.jersey.message.filtering.spi.EntityInspector;
import org.glassfish.jersey.message.filtering.spi.ObjectGraph;
import org.glassfish.jersey.message.filtering.spi.ObjectGraphTransformer;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;

final class EntityFilteringBinder extends AbstractBinder {
   protected void configure() {
      ((ClassBinding)this.bind(EntityInspectorImpl.class).to(EntityInspector.class)).in(Singleton.class);
      ((ClassBinding)this.bind(EntityGraphProviderImpl.class).to(EntityGraphProvider.class)).in(Singleton.class);
      ((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)((ClassBinding)this.bindAsContract(ObjectGraphProvider.class)
                           .to(ObjectProvider.class))
                        .to(new GenericType<ObjectProvider<Object>>() {
                        }))
                     .to(new GenericType<ObjectProvider<ObjectGraph>>() {
                     }))
                  .to(ObjectGraphTransformer.class))
               .to(new GenericType<ObjectGraphTransformer<Object>>() {
               }))
            .to(new GenericType<ObjectGraphTransformer<ObjectGraph>>() {
            }))
         .in(Singleton.class);
   }
}
