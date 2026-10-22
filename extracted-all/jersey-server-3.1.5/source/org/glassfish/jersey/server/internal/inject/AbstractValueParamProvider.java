package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

public abstract class AbstractValueParamProvider implements ValueParamProvider {
   private final Provider<MultivaluedParameterExtractorProvider> mpep;
   private final Set<Source> compatibleSources;

   protected AbstractValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep, Source... compatibleSources) {
      this.mpep = mpep;
      this.compatibleSources = new HashSet<>(Arrays.asList(compatibleSources));
   }

   protected final MultivaluedParameterExtractor<?> get(Parameter parameter) {
      return ((MultivaluedParameterExtractorProvider)this.mpep.get()).get(parameter);
   }

   protected abstract Function<ContainerRequest, ?> createValueProvider(Parameter var1);

   @Override
   public final Function<ContainerRequest, ?> getValueProvider(Parameter parameter) {
      return !this.compatibleSources.contains(parameter.getSource()) ? null : this.createValueProvider(parameter);
   }

   @Override
   public ValueParamProvider.PriorityType getPriority() {
      return ValueParamProvider.Priority.NORMAL;
   }
}
