package org.glassfish.jersey.client;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.ResponseProcessingException;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import org.glassfish.jersey.client.internal.routing.ClientResponseMediaTypeDeterminer;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import org.glassfish.jersey.model.internal.RankedComparator.Order;
import org.glassfish.jersey.process.internal.AbstractChainableStage;
import org.glassfish.jersey.process.internal.ChainableStage;
import org.glassfish.jersey.process.internal.Stage.Continuation;

class ClientFilteringStages {
   private ClientFilteringStages() {
   }

   static ChainableStage<ClientRequest> createRequestFilteringStage(InjectionManager injectionManager) {
      RankedComparator<ClientRequestFilter> comparator = new RankedComparator(Order.ASCENDING);
      Iterable<ClientRequestFilter> requestFilters = Providers.getAllProviders(injectionManager, ClientRequestFilter.class, comparator);
      return requestFilters.iterator().hasNext() ? new ClientFilteringStages.RequestFilteringStage(requestFilters) : null;
   }

   static ChainableStage<ClientRequest> createRequestFilteringStage(ClientRequestFilter firstFilter, InjectionManager injectionManager) {
      RankedComparator<ClientRequestFilter> comparator = new RankedComparator(Order.ASCENDING);
      Iterable<ClientRequestFilter> requestFilters = Providers.getAllProviders(injectionManager, ClientRequestFilter.class, comparator);
      if (firstFilter != null && !requestFilters.iterator().hasNext()) {
         return new ClientFilteringStages.RequestFilteringStage(Collections.singletonList(firstFilter));
      } else {
         return firstFilter != null && requestFilters.iterator().hasNext()
            ? new ClientFilteringStages.RequestFilteringStage(prependFilter(firstFilter, requestFilters))
            : null;
      }
   }

   static ChainableStage<ClientResponse> createResponseFilteringStage(InjectionManager injectionManager) {
      RankedComparator<ClientResponseFilter> comparator = new RankedComparator(Order.DESCENDING);
      Iterable<ClientResponseFilter> responseFilters = Providers.getAllProviders(injectionManager, ClientResponseFilter.class, comparator);
      return responseFilters.iterator().hasNext() ? new ClientFilteringStages.ResponseFilterStage(responseFilters) : null;
   }

   private static <T> Iterable<T> prependFilter(final T filter, final Iterable<T> filters) {
      return new Iterable<T>() {
         @Override
         public Iterator<T> iterator() {
            return new Iterator<T>() {
               final Iterator<T> filterIterator = filters.iterator();
               boolean wasInterceptorFilterNext = false;

               @Override
               public boolean hasNext() {
                  return !this.wasInterceptorFilterNext || this.filterIterator.hasNext();
               }

               @Override
               public T next() {
                  if (this.wasInterceptorFilterNext) {
                     return this.filterIterator.next();
                  } else {
                     this.wasInterceptorFilterNext = true;
                     return filter;
                  }
               }
            };
         }
      };
   }

   private static final class RequestFilteringStage extends AbstractChainableStage<ClientRequest> {
      private final Iterable<ClientRequestFilter> requestFilters;

      private RequestFilteringStage(Iterable<ClientRequestFilter> requestFilters) {
         this.requestFilters = requestFilters;
      }

      public Continuation<ClientRequest> apply(ClientRequest requestContext) {
         for (ClientRequestFilter filter : this.requestFilters) {
            try {
               filter.filter(requestContext);
               Response abortResponse = requestContext.getAbortResponse();
               if (abortResponse != null) {
                  ClientResponseMediaTypeDeterminer determiner = new ClientResponseMediaTypeDeterminer(requestContext.getWorkers());
                  determiner.setResponseMediaTypeIfNotSet(abortResponse, requestContext.getConfiguration());
                  throw new AbortException(new ClientResponse(requestContext, abortResponse));
               }
            } catch (IOException var6) {
               throw new ProcessingException(var6);
            }
         }

         return Continuation.of(requestContext, this.getDefaultNext());
      }
   }

   private static class ResponseFilterStage extends AbstractChainableStage<ClientResponse> {
      private final Iterable<ClientResponseFilter> filters;

      private ResponseFilterStage(Iterable<ClientResponseFilter> filters) {
         this.filters = filters;
      }

      public Continuation<ClientResponse> apply(ClientResponse responseContext) {
         try {
            for (ClientResponseFilter filter : this.filters) {
               filter.filter(responseContext.getRequestContext(), responseContext);
            }
         } catch (IOException var4) {
            InboundJaxrsResponse response = new InboundJaxrsResponse(responseContext, null);
            throw new ResponseProcessingException(response, var4);
         }

         return Continuation.of(responseContext, this.getDefaultNext());
      }
   }
}
