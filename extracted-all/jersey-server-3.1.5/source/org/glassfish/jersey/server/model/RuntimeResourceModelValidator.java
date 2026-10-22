package org.glassfish.jersey.server.model;

import jakarta.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.glassfish.jersey.Severity;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.internal.LocalizationMessages;

public class RuntimeResourceModelValidator extends AbstractResourceModelVisitor {
   private final MessageBodyWorkers workers;
   private static final List<MediaType> StarTypeList = Arrays.asList(new MediaType("*", "*"));

   public RuntimeResourceModelValidator(MessageBodyWorkers workers) {
      this.workers = workers;
   }

   @Override
   public void visitRuntimeResource(RuntimeResource runtimeResource) {
      this.checkMethods(runtimeResource);
   }

   private void checkMethods(RuntimeResource resource) {
      List<ResourceMethod> resourceMethods = new ArrayList<>(resource.getResourceMethods());
      resourceMethods.addAll(resource.getResourceLocators());
      if (resourceMethods.size() >= 2) {
         for (ResourceMethod m1 : resourceMethods.subList(0, resourceMethods.size() - 1)) {
            for (ResourceMethod m2 : resourceMethods.subList(resourceMethods.indexOf(m1) + 1, resourceMethods.size())) {
               if (m1.getHttpMethod() == null && m2.getHttpMethod() == null) {
                  Errors.error(this, LocalizationMessages.AMBIGUOUS_SRLS_PATH_PATTERN(resource.getFullPathRegex()), Severity.FATAL);
               } else if (m1.getHttpMethod() != null && m2.getHttpMethod() != null && this.sameHttpMethod(m1, m2)) {
                  this.checkIntersectingMediaTypes(resource, m1.getHttpMethod(), m1, m2);
               }
            }
         }
      }
   }

   private void checkIntersectingMediaTypes(RuntimeResource runtimeResource, String httpMethod, ResourceMethod m1, ResourceMethod m2) {
      List<MediaType> inputTypes1 = this.getEffectiveInputTypes(m1);
      List<MediaType> inputTypes2 = this.getEffectiveInputTypes(m2);
      List<MediaType> outputTypes1 = this.getEffectiveOutputTypes(m1);
      List<MediaType> outputTypes2 = this.getEffectiveOutputTypes(m2);
      boolean consumesOnlyIntersects = false;
      boolean consumesFails;
      if (!m1.getConsumedTypes().isEmpty() && !m2.getConsumedTypes().isEmpty()) {
         consumesFails = MediaTypes.intersect(inputTypes1, inputTypes2);
      } else {
         consumesFails = inputTypes1.equals(inputTypes2);
         if (!consumesFails) {
            consumesOnlyIntersects = MediaTypes.intersect(inputTypes1, inputTypes2);
         }
      }

      boolean producesOnlyIntersects = false;
      boolean producesFails;
      if (!m1.getProducedTypes().isEmpty() && !m2.getProducedTypes().isEmpty()) {
         producesFails = MediaTypes.intersect(outputTypes1, outputTypes2);
      } else {
         producesFails = outputTypes1.equals(outputTypes2);
         if (!producesFails) {
            producesOnlyIntersects = MediaTypes.intersect(outputTypes1, outputTypes2);
         }
      }

      if (consumesFails && producesFails) {
         Errors.fatal(
            runtimeResource,
            LocalizationMessages.AMBIGUOUS_FATAL_RMS(
               httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()
            )
         );
      } else if (producesFails && consumesOnlyIntersects || consumesFails && producesOnlyIntersects || consumesOnlyIntersects && producesOnlyIntersects) {
         if (m1.getInvocable().requiresEntity()) {
            Errors.hint(
               runtimeResource,
               LocalizationMessages.AMBIGUOUS_RMS_IN(
                  httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()
               )
            );
         } else {
            Errors.hint(
               runtimeResource,
               LocalizationMessages.AMBIGUOUS_RMS_OUT(
                  httpMethod, m1.getInvocable().getHandlingMethod(), m2.getInvocable().getHandlingMethod(), runtimeResource.getRegex()
               )
            );
         }
      }
   }

   private List<MediaType> getEffectiveInputTypes(ResourceMethod resourceMethod) {
      if (!resourceMethod.getConsumedTypes().isEmpty()) {
         return resourceMethod.getConsumedTypes();
      } else {
         List<MediaType> result = new LinkedList<>();
         if (this.workers != null) {
            for (Parameter p : resourceMethod.getInvocable().getParameters()) {
               if (p.getSource() == Source.ENTITY) {
                  result.addAll(this.workers.getMessageBodyReaderMediaTypes(p.getRawType(), p.getType(), p.getDeclaredAnnotations()));
               }
            }
         }

         return result.isEmpty() ? StarTypeList : result;
      }
   }

   private List<MediaType> getEffectiveOutputTypes(ResourceMethod resourceMethod) {
      if (!resourceMethod.getProducedTypes().isEmpty()) {
         return resourceMethod.getProducedTypes();
      } else {
         List<MediaType> result = new LinkedList<>();
         if (this.workers != null) {
            Invocable invocable = resourceMethod.getInvocable();
            result.addAll(
               this.workers
                  .getMessageBodyWriterMediaTypes(invocable.getRawResponseType(), invocable.getResponseType(), invocable.getHandlingMethod().getAnnotations())
            );
         }

         return result.isEmpty() ? StarTypeList : result;
      }
   }

   private boolean sameHttpMethod(ResourceMethod m1, ResourceMethod m2) {
      return m1.getHttpMethod().equals(m2.getHttpMethod());
   }
}
