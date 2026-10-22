package org.glassfish.jersey.media.multipart.internal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Providers;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.message.MessageBodyWorkers;
import org.glassfish.jersey.server.CloseableService;
import org.jvnet.mimepull.MIMEParsingException;

@Singleton
@ConstrainedTo(RuntimeType.SERVER)
public class MultiPartReaderServerSide extends MultiPartReaderClientSide {
   private final Provider<CloseableService> closeableServiceProvider;

   @Inject
   public MultiPartReaderServerSide(
      @Context Providers providers, @Context Provider<CloseableService> closeableServiceProvider, @Context Provider<MessageBodyWorkers> messageBodyWorkers
   ) {
      super(providers, messageBodyWorkers);
      this.closeableServiceProvider = closeableServiceProvider;
   }

   @Override
   protected MultiPart readMultiPart(
      Class<MultiPart> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> headers, InputStream stream
   ) throws IOException, MIMEParsingException {
      MultiPart multiPart = super.readMultiPart(type, genericType, annotations, mediaType, headers, stream);
      ((CloseableService)this.closeableServiceProvider.get()).add(multiPart);
      return multiPart;
   }
}
