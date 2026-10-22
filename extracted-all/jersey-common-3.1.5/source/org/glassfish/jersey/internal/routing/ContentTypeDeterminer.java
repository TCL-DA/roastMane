package org.glassfish.jersey.internal.routing;

import jakarta.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.WriterModel;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import org.glassfish.jersey.message.internal.MediaTypes;

public class ContentTypeDeterminer {
   protected MessageBodyWorkers workers;

   protected ContentTypeDeterminer(MessageBodyWorkers workers) {
      this.workers = workers;
   }

   protected MediaType determineResponseMediaType(
      Class<?> entityClass,
      Type entityType,
      RequestSpecificConsumesProducesAcceptor<?> selectedMethod,
      List<AcceptableMediaType> acceptableMediaTypes,
      List<MediaType> methodProducesTypes,
      Annotation[] handlingMethodAnnotations
   ) {
      Class<?> responseEntityClass = entityClass;
      List<WriterModel> writersForEntityType = this.workers.getWritersModelsForType(entityClass);
      CombinedMediaType selected = null;

      for (MediaType acceptableMediaType : acceptableMediaTypes) {
         for (MediaType methodProducesType : methodProducesTypes) {
            if (acceptableMediaType.isCompatible(methodProducesType)) {
               for (WriterModel model : writersForEntityType) {
                  for (MediaType writerProduces : model.declaredTypes()) {
                     if (writerProduces.isCompatible(acceptableMediaType) && methodProducesType.isCompatible(writerProduces)) {
                        CombinedMediaType.EffectiveMediaType effectiveProduces = new CombinedMediaType.EffectiveMediaType(
                           MediaTypes.mostSpecific(methodProducesType, writerProduces), false
                        );
                        CombinedMediaType candidate = CombinedMediaType.create(acceptableMediaType, effectiveProduces);
                        if (candidate != CombinedMediaType.NO_MATCH
                           && (selected == null || CombinedMediaType.COMPARATOR.compare(candidate, selected) < 0)
                           && model.isWriteable(responseEntityClass, entityType, handlingMethodAnnotations, candidate.getCombinedType())) {
                           selected = candidate;
                        }
                     }
                  }
               }
            }
         }
      }

      return selected != null ? selected.getCombinedType() : selectedMethod.getProduces().getCombinedType();
   }
}
