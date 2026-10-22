package org.glassfish.jersey.message;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import org.glassfish.jersey.message.internal.MessageBodyFactory;

public final class WriterModel extends AbstractEntityProviderModel<MessageBodyWriter> {
   public WriterModel(MessageBodyWriter provider, List<MediaType> types, Boolean custom) {
      super(provider, types, custom, MessageBodyWriter.class);
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return MessageBodyFactory.isWriteable((MessageBodyWriter<?>)super.provider(), type, genericType, annotations, mediaType);
   }
}
