package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.PathSegment;
import java.util.List;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.model.Parameter;

@Singleton
final class MatrixParamValueParamProvider extends AbstractValueParamProvider {
   public MatrixParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep) {
      super(mpep, Source.MATRIX);
   }

   @Override
   public Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
      String parameterName = parameter.getSourceName();
      if (parameterName != null && parameterName.length() != 0) {
         MultivaluedParameterExtractor e = this.get(parameter);
         return e == null ? null : new MatrixParamValueParamProvider.MatrixParamValueProvider(e, !parameter.isEncoded());
      } else {
         return null;
      }
   }

   private static final class MatrixParamValueProvider implements Function<ContainerRequest, Object> {
      private final MultivaluedParameterExtractor<?> extractor;
      private final boolean decode;

      MatrixParamValueProvider(MultivaluedParameterExtractor<?> extractor, boolean decode) {
         this.extractor = extractor;
         this.decode = decode;
      }

      public Object apply(ContainerRequest containerRequest) {
         List<PathSegment> l = containerRequest.getUriInfo().getPathSegments(this.decode);
         PathSegment p = l.get(l.size() - 1);

         try {
            return this.extractor.extract(p.getMatrixParameters());
         } catch (ExtractorException var5) {
            throw new ParamException.MatrixParamException(var5.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
         }
      }
   }
}
