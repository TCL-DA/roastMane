package org.glassfish.jersey.server.internal.inject;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.AccessController;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.NullableMultivaluedHashMap;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ParamException;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider;

@Singleton
final class FormParamValueParamProvider extends AbstractValueParamProvider {
   private final FormParamValueParamProvider.MultipartFormParamValueProvider multipartProvider;

   public FormParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> mpep, InjectionManager injectionManager) {
      super(mpep, Source.FORM);
      this.multipartProvider = new FormParamValueParamProvider.MultipartFormParamValueProvider(injectionManager);
   }

   @Override
   public Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
      String parameterName = parameter.getSourceName();
      if (parameterName == null || parameterName.isEmpty()) {
         return null;
      } else if (EntityPart.class == parameter.getType()) {
         return new Function<ContainerRequest, Object>() {
            public Object apply(ContainerRequest containerRequest) {
               return FormParamValueParamProvider.this.multipartProvider.getEntityPart(containerRequest, parameter);
            }
         };
      } else {
         MultivaluedParameterExtractor e = this.get(parameter);
         return e == null ? null : new FormParamValueParamProvider.FormParamValueProvider(e, this.multipartProvider, !parameter.isEncoded(), parameter);
      }
   }

   private static final class FormParamValueProvider implements Function<ContainerRequest, Object> {
      private static final Annotation encodedAnnotation = getEncodedAnnotation();
      private final MultivaluedParameterExtractor<?> extractor;
      private final FormParamValueParamProvider.MultipartFormParamValueProvider multipartProvider;
      private final boolean decode;
      private final Parameter parameter;

      FormParamValueProvider(
         MultivaluedParameterExtractor<?> extractor,
         FormParamValueParamProvider.MultipartFormParamValueProvider multipartProvider,
         boolean decode,
         Parameter parameter
      ) {
         this.extractor = extractor;
         this.multipartProvider = multipartProvider;
         this.decode = decode;
         this.parameter = parameter;
      }

      private static Form getCachedForm(ContainerRequest request, boolean decode) {
         return (Form)request.getProperty(decode ? "jersey.config.server.representation.decoded.form" : "jersey.config.server.representation.form");
      }

      private static ContainerRequest ensureValidRequest(ContainerRequest request) throws IllegalStateException {
         if (request.getMethod().equals("GET")) {
            throw new IllegalStateException(LocalizationMessages.FORM_PARAM_METHOD_ERROR());
         } else if (!MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
            throw new IllegalStateException(LocalizationMessages.FORM_PARAM_CONTENT_TYPE_ERROR());
         } else {
            return request;
         }
      }

      private static Annotation getEncodedAnnotation() {
         @Encoded
         final class EncodedAnnotationTemp {
         }

         return EncodedAnnotationTemp.class.getAnnotation(Encoded.class);
      }

      public Object apply(ContainerRequest request) {
         if (MediaTypes.typeEqual(MediaType.MULTIPART_FORM_DATA_TYPE, request.getMediaType())) {
            return this.multipartProvider.apply(request, this.parameter);
         } else {
            Form form = getCachedForm(request, this.decode);
            if (form == null) {
               Form otherForm = getCachedForm(request, !this.decode);
               if (otherForm != null) {
                  form = this.switchUrlEncoding(request, otherForm);
               } else {
                  form = this.getForm(request);
               }

               this.cacheForm(request, form);
            }

            try {
               return this.extractor.extract(form.asMap());
            } catch (ExtractorException var4) {
               throw new ParamException.FormParamException(var4.getCause(), this.extractor.getName(), this.extractor.getDefaultValueString());
            }
         }
      }

      private Form switchUrlEncoding(ContainerRequest request, Form otherForm) {
         Set<Entry<String, List<String>>> entries = otherForm.asMap().entrySet();
         MultivaluedMap<String, String> formMap = new NullableMultivaluedHashMap();

         for (Entry<String, List<String>> entry : entries) {
            String charsetName = ReaderWriter.getCharset(MediaType.valueOf(request.getHeaderString("Content-Type"))).name();

            try {
               String key = this.decode ? URLDecoder.decode(entry.getKey(), charsetName) : URLEncoder.encode(entry.getKey(), charsetName);

               for (String value : entry.getValue()) {
                  if (value != null) {
                     formMap.add(key, this.decode ? URLDecoder.decode(value, charsetName) : URLEncoder.encode(value, charsetName));
                  } else {
                     formMap.add(key, null);
                  }
               }
            } catch (UnsupportedEncodingException var11) {
               throw new ProcessingException(LocalizationMessages.ERROR_UNSUPPORTED_ENCODING(charsetName, this.extractor.getName()), var11);
            }
         }

         return new Form(formMap);
      }

      private void cacheForm(ContainerRequest request, Form form) {
         request.setProperty(this.decode ? "jersey.config.server.representation.decoded.form" : "jersey.config.server.representation.form", form);
      }

      private Form getForm(ContainerRequest request) {
         return this.getFormParameters(ensureValidRequest(request));
      }

      private Form getFormParameters(ContainerRequest request) {
         if (MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, request.getMediaType())) {
            request.bufferEntity();
            Form form;
            if (this.decode) {
               form = request.readEntity(Form.class);
            } else {
               Annotation[] annotations = new Annotation[]{encodedAnnotation};
               form = request.readEntity(Form.class, annotations);
            }

            return form == null ? new Form() : form;
         } else {
            return new Form();
         }
      }
   }

   @Singleton
   private static class MultipartFormParamValueProvider implements BiFunction<ContainerRequest, Parameter, Object> {
      private static Parameter entityPartParameter = Parameter.create(
         EntityPart.class,
         EntityPart.class,
         false,
         EntityPart.class,
         EntityPart.class,
         ((Field[])AccessController.doPrivileged(
               ReflectionHelper.getDeclaredFieldsPA(FormParamValueParamProvider.MultipartFormParamValueProvider.FormParamHolder.class)
            ))[0]
            .getAnnotations()
      );
      private final InjectionManager injectionManager;
      private final LazyValue<ValueParamProvider> entityPartProvider;

      private MultipartFormParamValueProvider(InjectionManager injectionManager) {
         this.injectionManager = injectionManager;
         this.entityPartProvider = Values.lazy(() -> {
            for (ValueParamProvider vfp : Providers.getProviders(injectionManager, ValueParamProvider.class)) {
               Function<ContainerRequest, ?> paramValueSupplier = vfp.getValueProvider(entityPartParameter);
               if (paramValueSupplier != null && !FormParamValueParamProvider.class.isInstance(vfp)) {
                  return vfp;
               }
            }

            return null;
         });
      }

      public Object apply(ContainerRequest containerRequest, Parameter parameter) {
         Object entity = null;
         EntityPart entityPart = this.getEntityPart(containerRequest, parameter);
         if (entityPart != null) {
            try {
               entity = parameter.getType() != parameter.getRawType()
                  ? entityPart.getContent(this.genericType(parameter.getRawType(), parameter.getType()))
                  : entityPart.getContent(parameter.getRawType());
            } catch (IOException var6) {
               throw new ProcessingException(var6);
            }
         }

         return entity;
      }

      private EntityPart getEntityPart(ContainerRequest containerRequest, Parameter parameter) {
         ValueParamProvider valueParamProvider = (ValueParamProvider)this.entityPartProvider.get();
         if (valueParamProvider != null) {
            Function<ContainerRequest, ?> valueSupplier = valueParamProvider.getValueProvider(
               new FormParamValueParamProvider.MultipartFormParamValueProvider.WrappingFormParamParameter(entityPartParameter, parameter)
            );
            return (EntityPart)valueSupplier.apply(containerRequest);
         } else {
            return null;
         }
      }

      private GenericType genericType(final Type rawType, final Type genericType) {
         return new GenericType(new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
               return new Type[]{genericType};
            }

            @Override
            public Type getRawType() {
               return rawType;
            }

            @Override
            public Type getOwnerType() {
               return null;
            }
         });
      }

      private static final class FormParamHolder {
         @FormParam("name")
         public static final Void dummy = null;
      }

      private static class WrappingFormParamParameter extends Parameter {
         protected WrappingFormParamParameter(Parameter entityPartDataParam, Parameter realDataParam) {
            super(
               realDataParam.getAnnotations(),
               realDataParam.getSourceAnnotation(),
               realDataParam.getSource(),
               realDataParam.getSourceName(),
               entityPartDataParam.getRawType(),
               entityPartDataParam.getType(),
               realDataParam.isEncoded(),
               realDataParam.getDefaultValue()
            );
         }
      }
   }
}
