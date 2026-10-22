package org.glassfish.jersey.server.internal;

import jakarta.annotation.Priority;
import jakarta.inject.Singleton;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.message.internal.MessageBodyProviderNotFoundException;
import org.glassfish.jersey.server.internal.process.MappableException;

@Priority(10)
@Singleton
public class MappableExceptionWrapperInterceptor implements ReaderInterceptor, WriterInterceptor {
   public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
      try {
         return context.proceed();
      } catch (MappableException | MessageBodyProviderNotFoundException | WebApplicationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new MappableException(var4);
      }
   }

   public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
      try {
         context.proceed();
      } catch (MappableException | WebApplicationException var3) {
         throw var3;
      } catch (MessageBodyProviderNotFoundException var4) {
         throw new InternalServerErrorException(var4);
      } catch (Exception var5) {
         throw new MappableException(var5);
      }
   }

   public static class Binder extends AbstractBinder {
      protected void configure() {
         ((ClassBinding)((ClassBinding)this.bind(MappableExceptionWrapperInterceptor.class).to(ReaderInterceptor.class)).to(WriterInterceptor.class))
            .in(Singleton.class);
      }
   }
}
