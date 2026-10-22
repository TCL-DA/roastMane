package org.glassfish.jersey.server.spi;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerResponse;

public interface ContainerResponseWriter {
   OutputStream writeResponseStatusAndHeaders(long var1, ContainerResponse var3) throws ContainerException;

   boolean suspend(long var1, TimeUnit var3, ContainerResponseWriter.TimeoutHandler var4);

   void setSuspendTimeout(long var1, TimeUnit var3) throws IllegalStateException;

   void commit();

   void failure(Throwable var1);

   boolean enableResponseBuffering();

   public interface TimeoutHandler {
      void onTimeout(ContainerResponseWriter var1);
   }
}
