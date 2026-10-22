package org.glassfish.jersey.server.spi.internal;

import java.util.function.Function;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;

public final class ParamValueFactoryWithSource<T> implements Function<ContainerRequest, T> {
   private final Function<ContainerRequest, T> parameterFunction;
   private final Source parameterSource;

   public ParamValueFactoryWithSource(Function<ContainerRequest, T> paramFunction, Source parameterSource) {
      this.parameterFunction = paramFunction;
      this.parameterSource = parameterSource;
   }

   public T apply(ContainerRequest request) {
      return this.parameterFunction.apply(request);
   }

   public Source getSource() {
      return this.parameterSource;
   }
}
