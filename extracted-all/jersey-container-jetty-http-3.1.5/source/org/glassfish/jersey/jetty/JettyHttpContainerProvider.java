package org.glassfish.jersey.jetty;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.internal.util.JdkVersion;
import org.glassfish.jersey.jetty.internal.LocalizationMessages;
import org.glassfish.jersey.server.spi.ContainerProvider;

public final class JettyHttpContainerProvider implements ContainerProvider {
   public static final String HANDLER_NAME = "org.eclipse.jetty.server.Handler";

   public <T> T createContainer(Class<T> type, Application application) throws ProcessingException {
      if (JdkVersion.getJdkVersion().getMajor() < 17) {
         throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
      } else {
         return type == null || !"org.eclipse.jetty.server.Handler".equalsIgnoreCase(type.getCanonicalName()) && JettyHttpContainer.class != type
            ? null
            : type.cast(new JettyHttpContainer(application));
      }
   }

   public <T> T createContainer(Class<T> type, Application application, Object parentContext) throws ProcessingException {
      if (JdkVersion.getJdkVersion().getMajor() < 17) {
         throw new ProcessingException(LocalizationMessages.NOT_SUPPORTED());
      } else {
         return type == null || !"org.eclipse.jetty.server.Handler".equalsIgnoreCase(type.getCanonicalName()) && JettyHttpContainer.class != type
            ? null
            : type.cast(new JettyHttpContainer(application, parentContext));
      }
   }
}
