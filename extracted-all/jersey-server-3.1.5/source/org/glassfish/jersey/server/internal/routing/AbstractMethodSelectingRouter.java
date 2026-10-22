package org.glassfish.jersey.server.internal.routing;

import jakarta.ws.rs.NotAcceptableException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.guava.Primitives;
import org.glassfish.jersey.internal.routing.CombinedMediaType;
import org.glassfish.jersey.internal.routing.ContentTypeDeterminer;
import org.glassfish.jersey.internal.routing.RequestSpecificConsumesProducesAcceptor;
import org.glassfish.jersey.internal.routing.CombinedMediaType.EffectiveMediaType;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.ReaderModel;
import org.glassfish.jersey.message.WriterModel;
import org.glassfish.jersey.message.internal.AcceptableMediaType;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.ResourceMethod;

abstract class AbstractMethodSelectingRouter extends ContentTypeDeterminer implements Router {
   private static final Logger LOGGER = Logger.getLogger(AbstractMethodSelectingRouter.class.getName());
   private static final Comparator<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> CONSUMES_PRODUCES_ACCEPTOR_COMPARATOR = new Comparator<AbstractMethodSelectingRouter.ConsumesProducesAcceptor>() {
      public int compare(AbstractMethodSelectingRouter.ConsumesProducesAcceptor o1, AbstractMethodSelectingRouter.ConsumesProducesAcceptor o2) {
         ResourceMethod model1 = o1.methodRouting.method;
         ResourceMethod model2 = o2.methodRouting.method;
         int compared = this.compare(model1.getConsumedTypes(), model2.getConsumedTypes());
         if (compared != 0) {
            return compared;
         } else {
            compared = this.compare(model1.getProducedTypes(), model2.getProducedTypes());
            if (compared != 0) {
               return compared;
            } else {
               compared = MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1.consumes.getMediaType(), o2.consumes.getMediaType());
               return compared != 0 ? compared : MediaTypes.PARTIAL_ORDER_COMPARATOR.compare(o1.produces.getMediaType(), o2.produces.getMediaType());
            }
         }
      }

      private int compare(List<MediaType> mediaTypeList1, List<MediaType> mediaTypeList2) {
         mediaTypeList1 = mediaTypeList1.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList1;
         mediaTypeList2 = mediaTypeList2.isEmpty() ? MediaTypes.WILDCARD_TYPE_SINGLETON_LIST : mediaTypeList2;
         return MediaTypes.MEDIA_TYPE_LIST_COMPARATOR.compare(mediaTypeList1, mediaTypeList2);
      }
   };
   private final Map<String, List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor>> consumesProducesAcceptors = new HashMap<>();
   private final Router router;

   AbstractMethodSelectingRouter(MessageBodyWorkers workers, List<MethodRouting> methodRoutings) {
      super(workers);
      Set<String> httpMethods = new HashSet<>();

      for (MethodRouting methodRouting : methodRoutings) {
         String httpMethod = methodRouting.method.getHttpMethod();
         httpMethods.add(httpMethod);
         List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> httpMethodBoundAcceptors = this.consumesProducesAcceptors.get(httpMethod);
         if (httpMethodBoundAcceptors == null) {
            httpMethodBoundAcceptors = new LinkedList<>();
            this.consumesProducesAcceptors.put(httpMethod, httpMethodBoundAcceptors);
         }

         this.addAllConsumesProducesCombinations(httpMethodBoundAcceptors, methodRouting);
      }

      for (String httpMethod : httpMethods) {
         Collections.sort(this.consumesProducesAcceptors.get(httpMethod), CONSUMES_PRODUCES_ACCEPTOR_COMPARATOR);
      }

      if (!this.consumesProducesAcceptors.containsKey("HEAD")) {
         this.router = this.createHeadEnrichedRouter();
      } else {
         this.router = this.createInternalRouter();
      }
   }

   Set<String> getHttpMethods() {
      return this.consumesProducesAcceptors.keySet();
   }

   protected abstract AbstractMethodSelectingRouter.ConsumesProducesAcceptor createConsumesProducesAcceptor(
      EffectiveMediaType var1, EffectiveMediaType var2, MethodRouting var3
   );

   private Router createInternalRouter() {
      return new Router() {
         @Override
         public Router.Continuation apply(RequestProcessingContext requestContext) {
            return Router.Continuation.of(requestContext, AbstractMethodSelectingRouter.this.getMethodRouter(requestContext));
         }
      };
   }

   @Override
   public Router.Continuation apply(RequestProcessingContext requestContext) {
      return this.router.apply(requestContext);
   }

   private void addAllConsumesProducesCombinations(List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> acceptors, MethodRouting methodRouting) {
      ResourceMethod resourceMethod = methodRouting.method;
      Set<MediaType> effectiveInputTypes = new LinkedHashSet<>();
      boolean consumesFromWorkers = this.fillMediaTypes(effectiveInputTypes, resourceMethod, resourceMethod.getConsumedTypes(), true);
      Set<MediaType> effectiveOutputTypes = new LinkedHashSet<>();
      boolean producesFromWorkers = this.fillMediaTypes(effectiveOutputTypes, resourceMethod, resourceMethod.getProducedTypes(), false);
      Set<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> acceptorSet = new HashSet<>();

      for (MediaType consumes : effectiveInputTypes) {
         for (MediaType produces : effectiveOutputTypes) {
            acceptorSet.add(
               this.createConsumesProducesAcceptor(
                  new EffectiveMediaType(consumes, consumesFromWorkers), new EffectiveMediaType(produces, producesFromWorkers), methodRouting
               )
            );
         }
      }

      acceptors.addAll(acceptorSet);
   }

   private boolean fillMediaTypes(Set<MediaType> effectiveTypes, ResourceMethod resourceMethod, List<MediaType> methodTypes, boolean inputTypes) {
      if (methodTypes.size() > 1 || !methodTypes.contains(MediaType.WILDCARD_TYPE)) {
         effectiveTypes.addAll(methodTypes);
      }

      boolean mediaTypesFromWorkers = effectiveTypes.isEmpty();
      if (mediaTypesFromWorkers) {
         Invocable invocableMethod = resourceMethod.getInvocable();
         if (inputTypes) {
            this.fillInputTypesFromWorkers(effectiveTypes, invocableMethod);
         } else {
            this.fillOutputTypesFromWorkers(effectiveTypes, invocableMethod.getRawResponseType());
         }

         mediaTypesFromWorkers = !effectiveTypes.isEmpty();
         if (!mediaTypesFromWorkers) {
            if (inputTypes) {
               effectiveTypes.addAll(this.workers.getMessageBodyReaderMediaTypesByType(Object.class));
            } else {
               effectiveTypes.addAll(this.workers.getMessageBodyWriterMediaTypesByType(Object.class));
            }

            mediaTypesFromWorkers = true;
         }

         boolean noEntityArgInResourceMethod = inputTypes && this.getEntityParam(invocableMethod) == null;
         boolean voidReturnType = !inputTypes && invocableMethod.getRawResponseType() == void.class;
         if (noEntityArgInResourceMethod || voidReturnType) {
            effectiveTypes.add(MediaType.WILDCARD_TYPE);
         }
      }

      return mediaTypesFromWorkers;
   }

   private void fillOutputTypesFromWorkers(Set<MediaType> effectiveOutputTypes, Class<?> returnEntityType) {
      effectiveOutputTypes.addAll(this.workers.getMessageBodyWriterMediaTypesByType(returnEntityType));
   }

   private void fillInputTypesFromWorkers(Set<MediaType> effectiveInputTypes, Invocable invocableMethod) {
      for (Parameter p : invocableMethod.getParameters()) {
         if (p.getSource() == Source.ENTITY) {
            effectiveInputTypes.addAll(this.workers.getMessageBodyReaderMediaTypesByType(p.getRawType()));
            break;
         }
      }
   }

   private Parameter getEntityParam(Invocable invocable) {
      for (Parameter parameter : invocable.getParameters()) {
         if (parameter.getSource() == Source.ENTITY && !ContainerRequestContext.class.isAssignableFrom(parameter.getRawType())) {
            return parameter;
         }
      }

      return null;
   }

   private List<Router> getMethodRouter(RequestProcessingContext context) {
      final ContainerRequest request = context.request();
      List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> acceptors = this.consumesProducesAcceptors.get(request.getMethod());
      if (acceptors == null) {
         throw new NotAllowedException(Response.status(Status.METHOD_NOT_ALLOWED).allow(this.consumesProducesAcceptors.keySet()).build());
      } else {
         List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> satisfyingAcceptors = new LinkedList<>();
         Set<ResourceMethod> differentInvokableMethods = Collections.newSetFromMap(new IdentityHashMap<>());
         MediaType requestContentType = request.getMediaType();

         for (AbstractMethodSelectingRouter.ConsumesProducesAcceptor cpi : acceptors) {
            if (cpi.isConsumable(requestContentType)) {
               satisfyingAcceptors.add(cpi);
               differentInvokableMethods.add(cpi.methodRouting.method);
            }
         }

         if (satisfyingAcceptors.isEmpty()) {
            throw new NotSupportedException();
         } else {
            final List<AcceptableMediaType> acceptableMediaTypes = request.getQualifiedAcceptableMediaTypes();
            MediaType effectiveContentType = requestContentType == null ? MediaType.WILDCARD_TYPE : requestContentType;
            final AbstractMethodSelectingRouter.MethodSelector methodSelector = this.selectMethod(
               acceptableMediaTypes, satisfyingAcceptors, effectiveContentType, differentInvokableMethods.size() == 1
            );
            if (methodSelector.selected != null) {
               RequestSpecificConsumesProducesAcceptor<MethodRouting> selected = methodSelector.selected;
               if (methodSelector.sameFitnessAcceptors != null) {
                  this.reportMethodSelectionAmbiguity(acceptableMediaTypes, methodSelector.selected, methodSelector.sameFitnessAcceptors);
               }

               context.push(
                  new Function<ContainerResponse, ContainerResponse>() {
                     public ContainerResponse apply(ContainerResponse responseContext) {
                        if (responseContext.getMediaType() == null && (responseContext.hasEntity() || "HEAD".equals(request.getMethod()))) {
                           MediaType effectiveResponseType = AbstractMethodSelectingRouter.this.determineResponseMediaType(
                              responseContext.getEntityClass(), responseContext.getEntityType(), methodSelector.selected, acceptableMediaTypes
                           );
                           if (MediaTypes.isWildcard(effectiveResponseType)) {
                              if (!effectiveResponseType.isWildcardType() && !"application".equalsIgnoreCase(effectiveResponseType.getType())) {
                                 throw new NotAcceptableException();
                              }

                              effectiveResponseType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
                           }

                           responseContext.setMediaType(effectiveResponseType);
                        }

                        return responseContext;
                     }
                  }
               );
               return ((MethodRouting)selected.getMethodRouting()).routers;
            } else {
               throw new NotAcceptableException();
            }
         }
      }
   }

   private MediaType determineResponseMediaType(
      Class<?> entityClass,
      Type entityType,
      RequestSpecificConsumesProducesAcceptor<MethodRouting> selectedMethod,
      List<AcceptableMediaType> acceptableMediaTypes
   ) {
      if (usePreSelectedMediaType(selectedMethod, acceptableMediaTypes)) {
         return selectedMethod.getProduces().getCombinedType();
      } else {
         ResourceMethod resourceMethod = ((MethodRouting)selectedMethod.getMethodRouting()).method;
         Invocable invocable = resourceMethod.getInvocable();
         Class<?> responseEntityClass = entityClass == null ? invocable.getRawRoutingResponseType() : entityClass;
         Method handlingMethod = invocable.getHandlingMethod();
         List<MediaType> methodProducesTypes = !resourceMethod.getProducedTypes().isEmpty()
            ? resourceMethod.getProducedTypes()
            : Collections.singletonList(MediaType.WILDCARD_TYPE);
         return super.determineResponseMediaType(
            responseEntityClass, entityType, selectedMethod, acceptableMediaTypes, methodProducesTypes, handlingMethod.getDeclaredAnnotations()
         );
      }
   }

   private static boolean usePreSelectedMediaType(
      RequestSpecificConsumesProducesAcceptor<MethodRouting> selectedMethod, List<AcceptableMediaType> acceptableMediaTypes
   ) {
      return !selectedMethod.producesFromProviders() && ((MethodRouting)selectedMethod.getMethodRouting()).method.getProducedTypes().size() == 1
         ? true
         : acceptableMediaTypes.size() == 1 && !MediaTypes.isWildcard((MediaType)acceptableMediaTypes.get(0));
   }

   private boolean isWriteable(RequestSpecificConsumesProducesAcceptor<MethodRouting> candidate) {
      Invocable invocable = ((MethodRouting)candidate.getMethodRouting()).method.getInvocable();
      Class<?> responseType = Primitives.wrap(invocable.getRawRoutingResponseType());
      if (!Response.class.isAssignableFrom(responseType) && !Void.class.isAssignableFrom(responseType)) {
         Type genericType = invocable.getRoutingResponseType();
         Type genericReturnType = genericType instanceof GenericType ? ((GenericType)genericType).getType() : genericType;

         for (WriterModel model : this.workers.getWritersModelsForType(responseType)) {
            if (model.isWriteable(
               responseType, genericReturnType, invocable.getHandlingMethod().getDeclaredAnnotations(), candidate.getProduces().getCombinedType()
            )) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean isReadable(RequestSpecificConsumesProducesAcceptor<MethodRouting> candidate) {
      Invocable invocable = ((MethodRouting)candidate.getMethodRouting()).method.getInvocable();
      Method handlingMethod = invocable.getHandlingMethod();
      Parameter entityParam = this.getEntityParam(invocable);
      if (entityParam == null) {
         return true;
      } else {
         Class<?> entityType = entityParam.getRawType();

         for (ReaderModel model : this.workers.getReaderModelsForType(entityType)) {
            if (model.isReadable(entityType, entityParam.getType(), handlingMethod.getDeclaredAnnotations(), candidate.getConsumes().getCombinedType())) {
               return true;
            }
         }

         return false;
      }
   }

   private AbstractMethodSelectingRouter.MethodSelector selectMethod(
      List<AcceptableMediaType> acceptableMediaTypes,
      List<AbstractMethodSelectingRouter.ConsumesProducesAcceptor> satisfyingAcceptors,
      MediaType effectiveContentType,
      boolean singleInvokableMethod
   ) {
      AbstractMethodSelectingRouter.MethodSelector method = new AbstractMethodSelectingRouter.MethodSelector(null);
      AbstractMethodSelectingRouter.MethodSelector alternative = new AbstractMethodSelectingRouter.MethodSelector(null);

      for (MediaType acceptableMediaType : acceptableMediaTypes) {
         for (AbstractMethodSelectingRouter.ConsumesProducesAcceptor satisfiable : satisfyingAcceptors) {
            CombinedMediaType produces = CombinedMediaType.create(acceptableMediaType, satisfiable.produces);
            if (produces != CombinedMediaType.NO_MATCH) {
               CombinedMediaType consumes = CombinedMediaType.create(effectiveContentType, satisfiable.consumes);
               RequestSpecificConsumesProducesAcceptor<MethodRouting> candidate = new RequestSpecificConsumesProducesAcceptor(
                  consumes, produces, satisfiable.produces.isDerived(), satisfiable.methodRouting
               );
               if (singleInvokableMethod) {
                  return new AbstractMethodSelectingRouter.MethodSelector(candidate);
               }

               if (candidate.compareTo(method.selected) < 0) {
                  if (method.selected != null
                     && ((MethodRouting)candidate.getMethodRouting()).method == ((MethodRouting)method.selected.getMethodRouting()).method) {
                     method.consider(candidate);
                  } else if (this.isReadable(candidate) && this.isWriteable(candidate)) {
                     method.consider(candidate);
                  } else {
                     alternative.consider(candidate);
                  }
               }
            }
         }
      }

      return method.selected != null ? method : alternative;
   }

   private void reportMethodSelectionAmbiguity(
      List<AcceptableMediaType> acceptableTypes,
      RequestSpecificConsumesProducesAcceptor<MethodRouting> selected,
      List<RequestSpecificConsumesProducesAcceptor> sameFitnessAcceptors
   ) {
      if (LOGGER.isLoggable(Level.WARNING)) {
         StringBuilder msgBuilder = new StringBuilder(LocalizationMessages.AMBIGUOUS_RESOURCE_METHOD(acceptableTypes)).append('\n');
         msgBuilder.append('\t').append(((MethodRouting)selected.getMethodRouting()).method).append('\n');
         Set<ResourceMethod> reportedMethods = new HashSet<>();
         reportedMethods.add(((MethodRouting)selected.getMethodRouting()).method);

         for (RequestSpecificConsumesProducesAcceptor<MethodRouting> i : sameFitnessAcceptors) {
            if (!reportedMethods.contains(((MethodRouting)i.getMethodRouting()).method)) {
               msgBuilder.append('\t').append(((MethodRouting)i.getMethodRouting()).method).append('\n');
            }

            reportedMethods.add(((MethodRouting)i.getMethodRouting()).method);
         }

         LOGGER.log(Level.WARNING, msgBuilder.toString());
      }
   }

   private Router createHeadEnrichedRouter() {
      return new Router() {
         @Override
         public Router.Continuation apply(RequestProcessingContext context) {
            ContainerRequest request = context.request();
            if ("HEAD".equals(request.getMethod())) {
               request.setMethodWithoutException("GET");
               context.push(new Function<ContainerResponse, ContainerResponse>() {
                  public ContainerResponse apply(ContainerResponse responseContext) {
                     responseContext.getRequestContext().setMethodWithoutException("HEAD");
                     return responseContext;
                  }
               });
            }

            return Router.Continuation.of(context, AbstractMethodSelectingRouter.this.getMethodRouter(context));
         }
      };
   }

   protected abstract static class ConsumesProducesAcceptor {
      final EffectiveMediaType consumes;
      final EffectiveMediaType produces;
      final MethodRouting methodRouting;

      protected ConsumesProducesAcceptor(EffectiveMediaType consumes, EffectiveMediaType produces, MethodRouting methodRouting) {
         this.methodRouting = methodRouting;
         this.consumes = consumes;
         this.produces = produces;
      }

      abstract boolean isConsumable(MediaType var1);

      @Override
      public String toString() {
         return String.format("%s->%s:%s", this.consumes.getMediaType(), this.produces.getMediaType(), this.methodRouting);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (!(o instanceof AbstractMethodSelectingRouter.ConsumesProducesAcceptor)) {
            return false;
         } else {
            AbstractMethodSelectingRouter.ConsumesProducesAcceptor that = (AbstractMethodSelectingRouter.ConsumesProducesAcceptor)o;
            if (this.consumes != null ? this.consumes.equals(that.consumes) : that.consumes == null) {
               if (this.methodRouting != null ? this.methodRouting.equals(that.methodRouting) : that.methodRouting == null) {
                  return this.produces != null ? this.produces.equals(that.produces) : that.produces == null;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public int hashCode() {
         int result = this.consumes != null ? this.consumes.hashCode() : 0;
         result = 31 * result + (this.produces != null ? this.produces.hashCode() : 0);
         return 31 * result + (this.methodRouting != null ? this.methodRouting.hashCode() : 0);
      }
   }

   private static class MethodSelector {
      RequestSpecificConsumesProducesAcceptor<MethodRouting> selected;
      List<RequestSpecificConsumesProducesAcceptor> sameFitnessAcceptors;

      MethodSelector(RequestSpecificConsumesProducesAcceptor<MethodRouting> i) {
         this.selected = i;
         this.sameFitnessAcceptors = null;
      }

      void consider(RequestSpecificConsumesProducesAcceptor<MethodRouting> i) {
         int theLessTheBetter = i.compareTo(this.selected);
         if (theLessTheBetter < 0) {
            this.selected = i;
            this.sameFitnessAcceptors = null;
         } else if (theLessTheBetter == 0 && this.selected.getMethodRouting() != i.getMethodRouting()) {
            this.getSameFitnessList().add(i);
         }
      }

      List<RequestSpecificConsumesProducesAcceptor> getSameFitnessList() {
         if (this.sameFitnessAcceptors == null) {
            this.sameFitnessAcceptors = new LinkedList<>();
         }

         return this.sameFitnessAcceptors;
      }
   }
}
