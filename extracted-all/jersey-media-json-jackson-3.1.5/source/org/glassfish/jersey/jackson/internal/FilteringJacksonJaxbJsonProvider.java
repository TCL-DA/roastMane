package org.glassfish.jersey.jackson.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.ser.BeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.EndpointConfigBase;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterInjector;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg.ObjectWriterModifier;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JsonEndpointConfig;
import org.glassfish.jersey.message.filtering.spi.ObjectProvider;

@Singleton
public final class FilteringJacksonJaxbJsonProvider extends DefaultJacksonJaxbJsonProvider {
   private final Provider<ObjectProvider<FilterProvider>> provider;

   @Inject
   public FilteringJacksonJaxbJsonProvider(
      @Context Provider<ObjectProvider<FilterProvider>> provider, @Context Providers providers, @Context Configuration config
   ) {
      super(providers, config);
      this.provider = provider;
   }

   protected JsonEndpointConfig _configForWriting(ObjectMapper mapper, Annotation[] annotations, Class<?> defaultView) {
      AnnotationIntrospector customIntrospector = mapper.getSerializationConfig().getAnnotationIntrospector();
      ObjectMapper filteringMapper = mapper.setAnnotationIntrospector(AnnotationIntrospector.pair(customIntrospector, new JacksonAnnotationIntrospector() {
         public Object findFilterId(Annotated a) {
            Object filterId = super.findFilterId(a);
            if (filterId != null) {
               return filterId;
            } else {
               if (a instanceof AnnotatedMethod) {
                  Method method = ((AnnotatedMethod)a).getAnnotated();
                  if (ReflectionHelper.isGetter(method)) {
                     return ReflectionHelper.getPropertyName(method);
                  }
               }

               return !(a instanceof AnnotatedField) && !(a instanceof AnnotatedClass) ? null : a.getName();
            }
         }
      }));
      return super._configForWriting(filteringMapper, annotations, defaultView);
   }

   @Override
   public void writeTo(
      Object value,
      Class<?> type,
      Type genericType,
      Annotation[] annotations,
      MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders,
      OutputStream entityStream
   ) throws IOException {
      FilterProvider filterProvider = (FilterProvider)((ObjectProvider)this.provider.get()).getFilteringObject(genericType, true, annotations);
      if (filterProvider != null) {
         ObjectWriterInjector.set(new FilteringJacksonJaxbJsonProvider.FilteringObjectWriterModifier(filterProvider, ObjectWriterInjector.getAndClear()));
      }

      super.writeTo(value, type, genericType, annotations, mediaType, httpHeaders, entityStream);
   }

   private static final class FilteringObjectWriterModifier extends ObjectWriterModifier {
      private final ObjectWriterModifier original;
      private final FilterProvider filterProvider;

      private FilteringObjectWriterModifier(FilterProvider filterProvider, ObjectWriterModifier original) {
         this.original = original;
         this.filterProvider = filterProvider;
      }

      @Override
      public ObjectWriter modify(
         EndpointConfigBase<?> endpoint, MultivaluedMap<String, Object> responseHeaders, Object valueToWrite, ObjectWriter w, JsonGenerator g
      ) throws IOException {
         ObjectWriter writer = this.original == null ? w : this.original.modify(endpoint, responseHeaders, valueToWrite, w, g);
         final FilterProvider customFilterProvider = writer.getConfig().getFilterProvider();
         return customFilterProvider == null ? writer.with(this.filterProvider) : writer.with(new FilterProvider() {
            public BeanPropertyFilter findFilter(Object filterId) {
               return customFilterProvider.findFilter(filterId);
            }

            public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
               PropertyFilter filter = customFilterProvider.findPropertyFilter(filterId, valueToFilter);
               return filter != null ? filter : FilteringObjectWriterModifier.this.filterProvider.findPropertyFilter(filterId, valueToFilter);
            }
         });
      }
   }
}
