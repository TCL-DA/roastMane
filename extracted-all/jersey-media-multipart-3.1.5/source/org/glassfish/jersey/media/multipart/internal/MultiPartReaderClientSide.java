package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Providers;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartProperties;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.message.internal.MediaTypes;
import org.jvnet.mimepull.Header;
import org.jvnet.mimepull.MIMEConfig;
import org.jvnet.mimepull.MIMEMessage;
import org.jvnet.mimepull.MIMEParsingException;
import org.jvnet.mimepull.MIMEPart;

@Consumes({"multipart/*"})
@Singleton
@ConstrainedTo(RuntimeType.CLIENT)
public class MultiPartReaderClientSide implements MessageBodyReader<MultiPart> {
   private static final Logger LOGGER = Logger.getLogger(MultiPartReaderClientSide.class.getName());
   private Provider<MessageBodyWorkers> messageBodyWorkers;
   private final MIMEConfig mimeConfig;

   @Inject
   public MultiPartReaderClientSide(@Context Providers providers, @Context Provider<MessageBodyWorkers> messageBodyWorkers) {
      ContextResolver<MultiPartProperties> contextResolver = providers.getContextResolver(MultiPartProperties.class, MediaType.WILDCARD_TYPE);
      MultiPartProperties properties = null;
      if (contextResolver != null) {
         properties = (MultiPartProperties)contextResolver.getContext(this.getClass());
      }

      if (properties == null) {
         properties = new MultiPartProperties();
      }

      this.messageBodyWorkers = messageBodyWorkers;
      this.mimeConfig = this.createMimeConfig(properties);
   }

   private MIMEConfig createMimeConfig(MultiPartProperties properties) {
      MIMEConfig mimeConfig = new MIMEConfig();
      mimeConfig.setMemoryThreshold((long)properties.getBufferThreshold());
      String tempDir = properties.getTempDir();
      if (tempDir != null) {
         mimeConfig.setDir(tempDir);
      }

      if (properties.getBufferThreshold() != -1) {
         try {
            File.createTempFile("MIME", null, tempDir != null ? new File(tempDir) : null).delete();
         } catch (IOException var5) {
            LOGGER.log(Level.WARNING, LocalizationMessages.TEMP_FILE_CANNOT_BE_CREATED(properties.getBufferThreshold()), (Throwable)var5);
         }
      }

      return mimeConfig;
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return MultiPart.class.isAssignableFrom(type);
   }

   public MultiPart readFrom(
      Class<MultiPart> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream stream
   ) throws IOException, WebApplicationException {
      try {
         return this.readMultiPart(type, genericType, annotations, mediaType, headers, stream);
      } catch (MIMEParsingException var8) {
         if (var8.getCause() instanceof IOException) {
            throw (IOException)var8.getCause();
         } else {
            throw new BadRequestException(var8);
         }
      }
   }

   protected MultiPart readMultiPart(
      Class<MultiPart> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream stream
   ) throws IOException, MIMEParsingException {
      mediaType = unquoteMediaTypeParameters(mediaType, "boundary");
      MIMEMessage mimeMessage = new MIMEMessage(stream, (String)mediaType.getParameters().get("boundary"), this.mimeConfig);
      boolean formData = MediaTypes.typeEqual(mediaType, MediaType.MULTIPART_FORM_DATA_TYPE);
      MultiPart multiPart = (MultiPart)(formData ? new FormDataMultiPart() : new MultiPart());
      MessageBodyWorkers workers = (MessageBodyWorkers)this.messageBodyWorkers.get();
      multiPart.setMessageBodyWorkers(workers);
      MultivaluedMap<String, String> multiPartHeaders = multiPart.getHeaders();

      for (Entry<String, List<String>> entry : headers.entrySet()) {
         for (String value : entry.getValue()) {
            multiPartHeaders.add(entry.getKey(), value);
         }
      }

      boolean fileNameFix;
      if (!formData) {
         multiPart.setMediaType(mediaType);
         fileNameFix = false;
      } else {
         String userAgent = (String)headers.getFirst("User-Agent");
         fileNameFix = userAgent != null && userAgent.contains(" MSIE ");
      }

      for (MIMEPart mimePart : this.getMimeParts(mimeMessage)) {
         BodyPart bodyPart = (BodyPart)(formData ? new FormDataBodyPart(fileNameFix) : new BodyPart());
         bodyPart.setMessageBodyWorkers(workers);

         for (Header header : mimePart.getAllHeaders()) {
            bodyPart.getHeaders().add(header.getName(), header.getValue());
         }

         try {
            String contentType = (String)bodyPart.getHeaders().getFirst("Content-Type");
            if (contentType != null) {
               bodyPart.setMediaType(MediaType.valueOf(contentType));
            }

            bodyPart.getContentDisposition();
         } catch (IllegalArgumentException var18) {
            throw new BadRequestException(var18);
         }

         bodyPart.setEntity(new BodyPartEntity(mimePart));
         multiPart.getBodyParts().add(bodyPart);
      }

      return multiPart;
   }

   private List<MIMEPart> getMimeParts(MIMEMessage message) {
      try {
         return message.getAttachments();
      } catch (MIMEParsingException var3) {
         LOGGER.log(Level.FINE, LocalizationMessages.PARSING_ERROR(), (Throwable)var3);
         message.close();
         throw var3;
      }
   }

   private static MediaType unquoteMediaTypeParameters(MediaType mediaType, String... parameters) {
      if (parameters != null && parameters.length != 0) {
         Map<String, String> unquotedParams = new HashMap<>(mediaType.getParameters());

         for (String parameter : parameters) {
            String value = (String)mediaType.getParameters().get(parameter);
            if (value != null && value.startsWith("\"")) {
               value = value.substring(1, value.length() - 1);
               unquotedParams.put(parameter, value);
            }
         }

         return new MediaType(mediaType.getType(), mediaType.getSubtype(), unquotedParams);
      } else {
         return mediaType;
      }
   }
}
