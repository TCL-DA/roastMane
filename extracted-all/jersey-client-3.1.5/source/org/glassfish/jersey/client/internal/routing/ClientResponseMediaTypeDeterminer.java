package org.glassfish.jersey.client.internal.routing;

import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ReaderInterceptor;
import java.util.Collections;
import java.util.List;
import org.glassfish.jersey.internal.routing.CombinedMediaType;
import org.glassfish.jersey.internal.routing.ContentTypeDeterminer;
import org.glassfish.jersey.internal.routing.RequestSpecificConsumesProducesAcceptor;
import org.glassfish.jersey.internal.routing.CombinedMediaType.EffectiveMediaType;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import org.glassfish.jersey.message.internal.HeaderUtils;
import org.glassfish.jersey.message.internal.InboundMessageContext;

public class ClientResponseMediaTypeDeterminer extends ContentTypeDeterminer {
   private static final ClientResponseMediaTypeDeterminer.AbortedRouting ABORTED_ROUTING = new ClientResponseMediaTypeDeterminer.AbortedRouting();

   public ClientResponseMediaTypeDeterminer(MessageBodyWorkers workers) {
      super(workers);
   }

   public void setResponseMediaTypeIfNotSet(Response response, Configuration configuration) {
      if (response.hasEntity() && response.getMediaType() == null) {
         InboundMessageContext headerContext = new InboundMessageContext(configuration) {
            protected Iterable<ReaderInterceptor> getReaderInterceptors() {
               return null;
            }
         };
         headerContext.headers(HeaderUtils.asStringHeaders(response.getHeaders(), configuration));
         MediaType mediaType = this.determineResponseMediaType(response.getEntity(), headerContext.getQualifiedAcceptableMediaTypes());
         response.getHeaders().add("Content-Type", mediaType);
      }
   }

   private MediaType determineResponseMediaType(Object entity, List<AcceptableMediaType> acceptableMediaTypes) {
      GenericType type = ReflectionHelper.genericTypeFor(entity);
      CombinedMediaType wildcardType = CombinedMediaType.create(MediaType.WILDCARD_TYPE, new EffectiveMediaType(MediaType.WILDCARD_TYPE));
      RequestSpecificConsumesProducesAcceptor<ClientResponseMediaTypeDeterminer.AbortedRouting> selectedMethod = new RequestSpecificConsumesProducesAcceptor(
         wildcardType, wildcardType, true, ABORTED_ROUTING
      );
      List<MediaType> methodProducesTypes = Collections.singletonList(MediaType.WILDCARD_TYPE);
      return this.determineResponseMediaType(type.getRawType(), type.getType(), selectedMethod, acceptableMediaTypes, methodProducesTypes, null);
   }

   private static final class AbortedRouting {
      private AbortedRouting() {
      }

      @Override
      public String toString() {
         return "{Aborted by ClientRequestFilter}";
      }
   }
}
