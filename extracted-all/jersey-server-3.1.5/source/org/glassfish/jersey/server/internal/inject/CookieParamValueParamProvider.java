package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MultivaluedMap;
import java.util.Map.Entry;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.model.Parameter;

@Singleton
final class CookieParamValueParamProvider extends AbstractValueParamProvider {
   public CookieParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep) {
      super(mpep, Source.COOKIE);
   }

   @Override
   public Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
      String parameterName = parameter.getSourceName();
      if (parameterName != null && parameterName.length() != 0) {
         if (parameter.getRawType() == Cookie.class) {
            return new CookieParamValueParamProvider.CookieTypeParamValueProvider(parameterName);
         } else {
            MultivaluedParameterExtractor e = this.get(parameter);
            return e == null ? null : new CookieParamValueParamProvider.CookieParamValueProvider(e);
         }
      } else {
         return null;
      }
   }

   private static final class CookieParamValueProvider implements Function<ContainerRequest, Object> {
      private final MultivaluedParameterExtractor<?> extractor;

      CookieParamValueProvider(MultivaluedParameterExtractor<?> extractor) {
         this.extractor = extractor;
      }

      public Object apply(ContainerRequest containerRequest) {
         MultivaluedMap<String, String> cookies = new MultivaluedStringMap();

         for (Entry<String, Cookie> e : containerRequest.getCookies().entrySet()) {
            cookies.putSingle(e.getKey(), e.getValue().getValue());
         }

         try {
            return this.extractor.extract(cookies);
         } catch (ExtractorException var5) {
            throw new ParamException.CookieParamException(var5.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
         }
      }
   }

   private static final class CookieTypeParamValueProvider implements Function<ContainerRequest, Cookie> {
      private final String name;

      CookieTypeParamValueProvider(String name) {
         this.name = name;
      }

      public Cookie apply(ContainerRequest containerRequest) {
         return containerRequest.getCookies().get(this.name);
      }
   }
}
