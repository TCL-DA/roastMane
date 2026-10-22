package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.function.Function;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;

@Singleton
class EntityParamValueParamProvider extends AbstractValueParamProvider {
   EntityParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep) {
      super(mpep, Source.ENTITY);
   }

   @Override
   protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
      return new EntityParamValueParamProvider.EntityValueSupplier(parameter);
   }

   private static class EntityValueSupplier implements Function<ContainerRequest, Object> {
      private final Parameter parameter;

      public EntityValueSupplier(Parameter parameter) {
         this.parameter = parameter;
      }

      public Object apply(ContainerRequest containerRequest) {
         Class<?> rawType = this.parameter.getRawType();
         Object value;
         if ((Request.class.isAssignableFrom(rawType) || ContainerRequestContext.class.isAssignableFrom(rawType)) && rawType.isInstance(containerRequest)) {
            value = containerRequest;
         } else {
            value = containerRequest.readEntity(rawType, this.parameter.getType(), this.parameter.getAnnotations());
            if (rawType.isPrimitive() && value == null) {
               throw new BadRequestException(Response.status(Status.BAD_REQUEST).entity(LocalizationMessages.ERROR_PRIMITIVE_TYPE_NULL()).build());
            }
         }

         return value;
      }
   }
}
