package org.glassfish.jersey.server.spi.internal;

import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.process.MappableException;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.model.Parameterized;

public final class ParameterValueHelper {
   public static Object[] getParameterValues(List<ParamValueFactoryWithSource<?>> valueProviders, ContainerRequest request) {
      Object[] params = new Object[valueProviders.size()];

      try {
         int entityProviderIndex = -1;
         int index = 0;

         for (ParamValueFactoryWithSource<?> paramValProvider : valueProviders) {
            if (paramValProvider.getSource().equals(Source.ENTITY)) {
               entityProviderIndex = index++;
            } else {
               params[index++] = paramValProvider.apply(request);
            }
         }

         if (entityProviderIndex != -1) {
            params[entityProviderIndex] = valueProviders.get(entityProviderIndex).apply(request);
         }

         return params;
      } catch (WebApplicationException var7) {
         throw var7;
      } catch (MessageBodyProviderNotFoundException var8) {
         throw new NotSupportedException(var8);
      } catch (ProcessingException var9) {
         throw var9;
      } catch (RuntimeException var10) {
         if (var10.getCause() instanceof WebApplicationException) {
            throw (WebApplicationException)var10.getCause();
         } else {
            throw new MappableException("Exception obtaining parameters", var10);
         }
      }
   }

   public static List<ParamValueFactoryWithSource<?>> createValueProviders(Collection<ValueParamProvider> valueSuppliers, Parameterized parameterized) {
      if (null != parameterized.getParameters() && 0 != parameterized.getParameters().size()) {
         List<ValueParamProvider> valueParamProviders = valueSuppliers.stream()
            .sorted((o1, o2) -> o2.getPriority().getWeight() - o1.getPriority().getWeight())
            .collect(Collectors.toList());
         boolean entityParamFound = false;
         List<ParamValueFactoryWithSource<?>> providers = new ArrayList<>(parameterized.getParameters().size());

         for (Parameter parameter : parameterized.getParameters()) {
            Source parameterSource = parameter.getSource();
            entityParamFound = entityParamFound || Source.ENTITY == parameterSource;
            Function<ContainerRequest, ?> valueFunction = getParamValueProvider(valueParamProviders, parameter);
            if (valueFunction != null) {
               providers.add(wrapParamValueProvider(valueFunction, parameterSource));
            } else {
               providers.add(null);
            }
         }

         if (!entityParamFound && Collections.frequency(providers, null) == 1) {
            int entityParamIndex = providers.lastIndexOf(null);
            Parameter parameterx = parameterized.getParameters().get(entityParamIndex);
            if (Source.UNKNOWN == parameterx.getSource() && !parameterx.isQualified()) {
               Parameter overriddenParameter = Parameter.overrideSource(parameterx, Source.ENTITY);
               Function<ContainerRequest, ?> valueFunction = getParamValueProvider(valueParamProviders, overriddenParameter);
               if (valueFunction != null) {
                  providers.set(entityParamIndex, wrapParamValueProvider(valueFunction, overriddenParameter.getSource()));
               } else {
                  providers.set(entityParamIndex, null);
               }
            }
         }

         return providers;
      } else {
         return Collections.emptyList();
      }
   }

   private static <T> ParamValueFactoryWithSource<T> wrapParamValueProvider(Function<ContainerRequest, T> factory, Source paramSource) {
      return new ParamValueFactoryWithSource<>(factory, paramSource);
   }

   private static Function<ContainerRequest, ?> getParamValueProvider(Collection<ValueParamProvider> valueProviders, Parameter parameter) {
      Function<ContainerRequest, ?> valueProvider = null;
      Iterator<ValueParamProvider> vfpIterator = valueProviders.iterator();

      while (valueProvider == null && vfpIterator.hasNext()) {
         valueProvider = vfpIterator.next().getValueProvider(parameter);
      }

      return valueProvider;
   }

   private ParameterValueHelper() {
   }
}
