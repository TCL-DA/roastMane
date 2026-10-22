package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.glassfish.jersey.internal.routing.CombinedMediaType.EffectiveMediaType;
import org.glassfish.jersey.message.MessageBodyWorkers;

final class WildcardMethodSelectingRouter extends AbstractMethodSelectingRouter implements Router {
   WildcardMethodSelectingRouter(MessageBodyWorkers workers, List<MethodRouting> methodRoutings) {
      super(workers, methodRoutings);
   }

   @Override
   protected AbstractMethodSelectingRouter.ConsumesProducesAcceptor createConsumesProducesAcceptor(
      EffectiveMediaType consumes, EffectiveMediaType produces, MethodRouting methodRouting
   ) {
      return new WildcardMethodSelectingRouter.ConsumesProducesAcceptor(consumes, produces, methodRouting);
   }

   private static class ConsumesProducesAcceptor extends AbstractMethodSelectingRouter.ConsumesProducesAcceptor {
      private ConsumesProducesAcceptor(EffectiveMediaType consumes, EffectiveMediaType produces, MethodRouting methodRouting) {
         super(consumes, produces, methodRouting);
      }

      @Override
      boolean isConsumable(MediaType contentType) {
         return contentType == null || this.consumes.getMediaType().isCompatible(contentType);
      }
   }
}
