package org.glassfish.jersey.server;

import jakarta.inject.Singleton;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.WriterInterceptor;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.server.internal.JsonWithPaddingInterceptor;
import org.glassfish.jersey.server.internal.MappableExceptionWrapperInterceptor;
import org.glassfish.jersey.server.internal.monitoring.MonitoringContainerListener;

class ServerBinder extends AbstractBinder {
   protected void configure() {
      this.install(new AbstractBinder[]{new MappableExceptionWrapperInterceptor.Binder(), new MonitoringContainerListener.Binder()});
      ((ClassBinding)this.bind(ChunkedResponseWriter.class).to(MessageBodyWriter.class)).in(Singleton.class);
      ((ClassBinding)this.bind(JsonWithPaddingInterceptor.class).to(WriterInterceptor.class)).in(Singleton.class);
      ((ClassBinding)this.bind(DefaultExceptionMapper.class).to(ExceptionMapper.class)).in(Singleton.class);
   }
}
