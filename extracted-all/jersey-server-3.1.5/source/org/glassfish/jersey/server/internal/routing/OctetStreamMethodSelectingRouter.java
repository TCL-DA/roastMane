package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.internal.routing.CombinedMediaType.EffectiveMediaType;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.model.ResourceMethod;

final class OctetStreamMethodSelectingRouter extends AbstractMethodSelectingRouter implements Router {
   OctetStreamMethodSelectingRouter(MessageBodyWorkers workers, List<MethodRouting> methodRoutings) {
      super(workers, methodRoutings);
   }

   @Override
   protected AbstractMethodSelectingRouter.ConsumesProducesAcceptor createConsumesProducesAcceptor(
      EffectiveMediaType consumes, EffectiveMediaType produces, MethodRouting methodRouting
   ) {
      return new OctetStreamMethodSelectingRouter.ConsumesProducesAcceptor(consumes, produces, methodRouting);
   }

   private static class ConsumesProducesAcceptor extends AbstractMethodSelectingRouter.ConsumesProducesAcceptor {
      private ConsumesProducesAcceptor(EffectiveMediaType consumes, EffectiveMediaType produces, MethodRouting methodRouting) {
         super(consumes, produces, methodRouting);
      }

      @Override
      boolean isConsumable(MediaType contentType) {
         if (contentType == null
            && this.methodRouting.method.getType() != ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR
            && this.methodRouting.method.getInvocable().requiresEntity()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
         }

         return contentType == null || this.consumes.getMediaType().isCompatible(contentType);
      }
   }
}
