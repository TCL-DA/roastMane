package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Provider;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.ExtractorException;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataParamException;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.MessageUtils;
import org.glassfish.jersey.message.internal.Utils;
import org.glassfish.jersey.model.Parameter.Source;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.internal.inject.AbstractValueParamProvider;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractor;
import org.glassfish.jersey.server.internal.inject.MultivaluedParameterExtractorProvider;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider.Priority;
import org.glassfish.jersey.server.spi.internal.ValueParamProvider.PriorityType;
import org.jvnet.mimepull.MIMEParsingException;

final class FormDataParamValueParamProvider extends AbstractValueParamProvider {
   private static final Logger LOGGER = Logger.getLogger(FormDataParamValueParamProvider.class.getName());
   private static final Set<Class<?>> TYPES = initializeTypes();

   private static Set<Class<?>> initializeTypes() {
      Set<Class<?>> newSet = new HashSet<>();
      newSet.add(Byte.class);
      newSet.add(byte.class);
      newSet.add(Short.class);
      newSet.add(short.class);
      newSet.add(Integer.class);
      newSet.add(int.class);
      newSet.add(Long.class);
      newSet.add(long.class);
      newSet.add(Float.class);
      newSet.add(float.class);
      newSet.add(Double.class);
      newSet.add(double.class);
      newSet.add(Boolean.class);
      newSet.add(boolean.class);
      newSet.add(Character.class);
      newSet.add(char.class);
      return Collections.unmodifiableSet(newSet);
   }

   private static boolean isPrimitiveType(Class<?> type) {
      return TYPES.contains(type);
   }

   public FormDataParamValueParamProvider(Provider<MultivaluedParameterExtractorProvider> extractorProvider) {
      super(extractorProvider, new Source[]{Source.ENTITY, Source.FORM, Source.UNKNOWN});
   }

   protected Function<ContainerRequest, ?> createValueProvider(Parameter parameter) {
      Class<?> rawType = parameter.getRawType();
      if (Source.ENTITY == parameter.getSource()) {
         return FormDataMultiPart.class.isAssignableFrom(rawType) ? new FormDataParamValueParamProvider.FormDataMultiPartProvider() : null;
      } else if (parameter.getSourceAnnotation().annotationType() == FormDataParam.class) {
         String paramName = parameter.getSourceName();
         if (paramName == null || paramName.isEmpty()) {
            return null;
         } else if (Collection.class == rawType || List.class == rawType) {
            Class clazz = (Class)ReflectionHelper.getGenericTypeArgumentClasses(parameter.getType()).get(0);
            if (FormDataBodyPart.class == clazz) {
               return new FormDataParamValueParamProvider.ListFormDataBodyPartValueProvider(paramName);
            } else {
               return (Function<ContainerRequest, ?>)(FormDataContentDisposition.class == clazz
                  ? new FormDataParamValueParamProvider.ListFormDataContentDispositionProvider(paramName)
                  : new FormDataParamValueParamProvider.FormDataParamValueProvider(parameter, this.get(parameter)));
            }
         } else if (FormDataBodyPart.class == rawType) {
            return new FormDataParamValueParamProvider.FormDataBodyPartProvider(paramName);
         } else if (FormDataContentDisposition.class == rawType) {
            return new FormDataParamValueParamProvider.FormDataContentDispositionProvider(paramName);
         } else {
            return (Function<ContainerRequest, ?>)(File.class == rawType
               ? new FormDataParamValueParamProvider.FileProvider(paramName)
               : new FormDataParamValueParamProvider.FormDataParamValueProvider(parameter, this.get(parameter)));
         }
      } else {
         if (FormParam.class.equals(parameter.getSourceAnnotation().annotationType())) {
            String paramName = parameter.getSourceName();
            if (Collection.class != rawType && List.class != rawType) {
               if (EntityPart.class.equals(rawType)) {
                  return new FormDataParamValueParamProvider.EntityPartValueProvider(paramName);
               }
            } else {
               Class clazz = (Class)ReflectionHelper.getGenericTypeArgumentClasses(parameter.getType()).get(0);
               if (EntityPart.class.equals(clazz)) {
                  return new FormDataParamValueParamProvider.ListEntityPartValueProvider(paramName);
               }
            }
         }

         return null;
      }
   }

   public PriorityType getPriority() {
      return Priority.HIGH;
   }

   private final class EntityPartValueProvider extends FormDataParamValueParamProvider.ValueProvider<EntityPart> {
      private final String name;

      public EntityPartValueProvider(String name) {
         this.name = name;
      }

      public EntityPart apply(ContainerRequest request) {
         List<FormDataBodyPart> bodyParts = this.getEntity(request).getFields(this.name);
         return bodyParts.size() != 0 ? bodyParts.get(0) : null;
      }
   }

   private final class FileProvider extends FormDataParamValueParamProvider.ValueProvider<File> {
      private final String name;

      public FileProvider(String name) {
         this.name = name;
      }

      public File apply(ContainerRequest request) {
         FormDataBodyPart part = this.getEntity(request).getField(this.name);
         BodyPartEntity entity = part != null ? part.getEntityAs(BodyPartEntity.class) : null;
         if (entity != null) {
            try {
               File file = Utils.createTempFile();
               entity.moveTo(file);
               return file;
            } catch (MIMEParsingException | IOException var5) {
               FormDataParamValueParamProvider.LOGGER.log(Level.WARNING, LocalizationMessages.CANNOT_INJECT_FILE(), (Throwable)var5);
            }
         }

         return null;
      }
   }

   private final class FormDataBodyPartProvider extends FormDataParamValueParamProvider.ValueProvider<FormDataBodyPart> {
      private final String name;

      public FormDataBodyPartProvider(String name) {
         this.name = name;
      }

      public FormDataBodyPart apply(ContainerRequest request) {
         return this.getEntity(request).getField(this.name);
      }
   }

   private final class FormDataContentDispositionProvider extends FormDataParamValueParamProvider.ValueProvider<FormDataContentDisposition> {
      private final String name;

      public FormDataContentDispositionProvider(String name) {
         this.name = name;
      }

      public FormDataContentDisposition apply(ContainerRequest request) {
         FormDataBodyPart part = this.getEntity(request).getField(this.name);
         return part == null ? null : part.getFormDataContentDisposition();
      }
   }

   private final class FormDataMultiPartProvider extends FormDataParamValueParamProvider.ValueProvider<FormDataMultiPart> {
      private FormDataMultiPartProvider() {
      }

      public FormDataMultiPart apply(ContainerRequest request) {
         return this.getEntity(request);
      }
   }

   private final class FormDataParamValueProvider extends FormDataParamValueParamProvider.ValueProvider<Object> {
      private final MultivaluedParameterExtractor<?> extractor;
      private final Parameter parameter;

      public FormDataParamValueProvider(Parameter parameter, MultivaluedParameterExtractor<?> extractor) {
         this.parameter = parameter;
         this.extractor = extractor;
      }

      public Object apply(ContainerRequest request) {
         List<FormDataBodyPart> parts = this.getEntity(request).getFields(this.parameter.getSourceName());
         FormDataBodyPart part = parts != null ? parts.get(0) : null;
         MediaType mediaType = part != null ? part.getMediaType() : MediaType.TEXT_PLAIN_TYPE;
         MessageBodyWorkers messageBodyWorkers = request.getWorkers();
         MessageBodyReader reader = messageBodyWorkers.getMessageBodyReader(
            this.parameter.getRawType(), this.parameter.getType(), this.parameter.getAnnotations(), mediaType
         );
         if (reader != null && !FormDataParamValueParamProvider.isPrimitiveType(this.parameter.getRawType())) {
            InputStream stream;
            if (part == null) {
               if (this.parameter.getDefaultValue() == null) {
                  return null;
               }

               stream = new ByteArrayInputStream(this.parameter.getDefaultValue().getBytes(MessageUtils.getCharset(mediaType)));
            } else {
               stream = part.getEntityAs(BodyPartEntity.class).getInputStream();
            }

            try {
               return reader.readFrom(
                  this.parameter.getRawType(), this.parameter.getType(), this.parameter.getAnnotations(), mediaType, request.getHeaders(), stream
               );
            } catch (IOException var11) {
               throw new FormDataParamException(var11, this.parameter.getSourceName(), this.parameter.getDefaultValue());
            }
         } else if (this.extractor != null) {
            MultivaluedMap<String, String> map = new MultivaluedStringMap();

            try {
               if (part != null) {
                  for (FormDataBodyPart p : parts) {
                     reader = messageBodyWorkers.getMessageBodyReader(String.class, String.class, this.parameter.getAnnotations(), p.getMediaType());
                     String value = (String)reader.readFrom(
                        String.class,
                        String.class,
                        this.parameter.getAnnotations(),
                        mediaType,
                        request.getHeaders(),
                        ((BodyPartEntity)p.getEntity()).getInputStream()
                     );
                     map.add(this.parameter.getSourceName(), value);
                  }
               }

               return this.extractor.extract(map);
            } catch (ExtractorException | IOException var12) {
               throw new FormDataParamException(var12, this.extractor.getName(), this.extractor.getDefaultValueString());
            }
         } else {
            return null;
         }
      }
   }

   private final class ListEntityPartValueProvider extends FormDataParamValueParamProvider.ValueProvider<List<EntityPart>> {
      private final String name;

      public ListEntityPartValueProvider(String name) {
         this.name = name;
      }

      public List<EntityPart> apply(ContainerRequest request) {
         return this.getEntity(request).getFields(this.name);
      }
   }

   private final class ListFormDataBodyPartValueProvider extends FormDataParamValueParamProvider.ValueProvider<List<FormDataBodyPart>> {
      private final String name;

      public ListFormDataBodyPartValueProvider(String name) {
         this.name = name;
      }

      public List<FormDataBodyPart> apply(ContainerRequest request) {
         return this.getEntity(request).getFields(this.name);
      }
   }

   private final class ListFormDataContentDispositionProvider extends FormDataParamValueParamProvider.ValueProvider<List<FormDataContentDisposition>> {
      private final String name;

      public ListFormDataContentDispositionProvider(String name) {
         this.name = name;
      }

      public List<FormDataContentDisposition> apply(ContainerRequest request) {
         List<FormDataBodyPart> parts = this.getEntity(request).getFields(this.name);
         return parts == null ? null : parts.stream().map(FormDataBodyPart::getFormDataContentDisposition).collect(Collectors.toList());
      }
   }

   private abstract class ValueProvider<T> implements Function<ContainerRequest, T> {
      private ValueProvider() {
      }

      FormDataMultiPart getEntity(ContainerRequest request) {
         String requestPropertyName = FormDataMultiPart.class.getName();
         Object entity = request.getProperty(requestPropertyName);
         if (entity == null) {
            entity = request.readEntity(FormDataMultiPart.class);
            if (entity == null) {
               throw new BadRequestException(LocalizationMessages.ENTITY_IS_EMPTY());
            }

            request.setProperty(requestPropertyName, entity);
         }

         return (FormDataMultiPart)entity;
      }
   }
}
