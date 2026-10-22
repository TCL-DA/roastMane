package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.model.Parameter;

@Singleton
final class QueryParamValueParamProvider extends AbstractValueParamProvider {
   public QueryParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep) {
      super(mpep, Source.QUERY);
   }

   @Override
   public Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
      String parameterName = parameter.getSourceName();
      if (parameterName != null && parameterName.length() != 0) {
         MultivaluedParameterExtractor e = this.get(parameter);
         return e == null ? null : new QueryParamValueParamProvider.QueryParamValueProvider(e, !parameter.isEncoded());
      } else {
         return null;
      }
   }

   private static final class QueryParamValueProvider implements Function<ContainerRequest, Object> {
      private final MultivaluedParameterExtractor<?> extractor;
      private final boolean decode;

      QueryParamValueProvider(MultivaluedParameterExtractor<?> extractor, boolean decode) {
         this.extractor = extractor;
         this.decode = decode;
      }

      public Object apply(ContainerRequest containerRequest) {
         try {
            return this.extractor.extract(containerRequest.getUriInfo().getQueryParameters(this.decode));
         } catch (ExtractorException var3) {
            throw new ParamException.QueryParamException(var3.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
         }
      }
   }
}
