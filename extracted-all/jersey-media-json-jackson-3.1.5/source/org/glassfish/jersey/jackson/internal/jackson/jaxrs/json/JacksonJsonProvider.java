package org.glassfish.jersey.jackson.internal.jackson.jaxrs.json;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.base.ProviderBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.Annotations;

@Provider
@Consumes({"*/*"})
@Produces({"*/*"})
public class JacksonJsonProvider extends ProviderBase<JacksonJsonProvider, ObjectMapper, JsonEndpointConfig, JsonMapperConfigurator> {
   public static final String MIME_JAVASCRIPT = "application/javascript";
   public static final String MIME_JAVASCRIPT_MS = "application/x-javascript";
   public static final Annotations[] BASIC_ANNOTATIONS = new Annotations[]{Annotations.JACKSON};
   protected String _jsonpFunctionName;
   @Context
   protected Providers _providers;

   public JacksonJsonProvider() {
      this(null, BASIC_ANNOTATIONS);
   }

   public JacksonJsonProvider(Annotations... annotationsToUse) {
      this(null, annotationsToUse);
   }

   public JacksonJsonProvider(ObjectMapper mapper) {
      this(mapper, BASIC_ANNOTATIONS);
   }

   public JacksonJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
      super(new JsonMapperConfigurator(mapper, annotationsToUse));
   }

   protected JacksonJsonProvider(JsonMapperConfigurator configurator) {
      super(configurator);
   }

   public Version version() {
      return PackageVersion.VERSION;
   }

   public void setJSONPFunctionName(String fname) {
      this._jsonpFunctionName = fname;
   }

   @Override
   protected boolean hasMatchingMediaType(MediaType mediaType) {
      if (mediaType == null) {
         return true;
      } else {
         String subtype = mediaType.getSubtype();
         return "json".equalsIgnoreCase(subtype)
            || subtype.endsWith("+json")
            || "javascript".equals(subtype)
            || "x-javascript".equals(subtype)
            || "x-json".equals(subtype);
      }
   }

   @Override
   protected ObjectMapper _locateMapperViaProvider(Class<?> type, MediaType mediaType) {
      if (this._providers != null) {
         ContextResolver<ObjectMapper> resolver = this._providers.getContextResolver(ObjectMapper.class, mediaType);
         if (resolver == null) {
            resolver = this._providers.getContextResolver(ObjectMapper.class, null);
         }

         if (resolver != null) {
            return (ObjectMapper)resolver.getContext(type);
         }
      }

      return null;
   }

   protected JsonEndpointConfig _configForReading(ObjectReader reader, Annotation[] annotations) {
      return JsonEndpointConfig.forReading(reader, annotations);
   }

   protected JsonEndpointConfig _configForWriting(ObjectWriter writer, Annotation[] annotations) {
      return JsonEndpointConfig.forWriting(writer, annotations, this._jsonpFunctionName);
   }
}
