package org.glassfish.jersey.spi;

import jakarta.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Priority(4000)
@Contract
public abstract class ContentEncoder implements ReaderInterceptor, WriterInterceptor {
   private final Set<String> supportedEncodings;

   protected ContentEncoder(String... supportedEncodings) {
      if (supportedEncodings.length == 0) {
         throw new IllegalArgumentException();
      } else {
         this.supportedEncodings = Collections.unmodifiableSet(Arrays.stream(supportedEncodings).collect(Collectors.toSet()));
      }
   }

   public final Set<String> getSupportedEncodings() {
      return this.supportedEncodings;
   }

   public abstract InputStream decode(String var1, InputStream var2) throws IOException;

   public abstract OutputStream encode(String var1, OutputStream var2) throws IOException;

   public final Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      String contentEncoding = (String)context.getHeaders().getFirst("Content-Encoding");
      if (contentEncoding != null && this.getSupportedEncodings().contains(contentEncoding)) {
         context.setInputStream(this.decode(contentEncoding, context.getInputStream()));
      }

      return context.proceed();
   }

   public final void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
      String contentEncoding = (String)context.getHeaders().getFirst("Content-Encoding");
      if (contentEncoding != null && this.getSupportedEncodings().contains(contentEncoding)) {
         context.setOutputStream(this.encode(contentEncoding, context.getOutputStream()));
      }

      context.proceed();
   }
}
