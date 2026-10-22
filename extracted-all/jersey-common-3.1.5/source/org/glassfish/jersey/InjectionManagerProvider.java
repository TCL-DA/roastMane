package org.glassfish.jersey;

import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.ext.ReaderInterceptorContext;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;

public class InjectionManagerProvider {
   public static InjectionManager getInjectionManager(WriterInterceptorContext writerInterceptorContext) {
      if (!(writerInterceptorContext instanceof InjectionManagerSupplier)) {
         throw new IllegalArgumentException(
            LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_WRITER_INTERCEPTOR_CONTEXT(writerInterceptorContext.getClass().getName())
         );
      } else {
         return ((InjectionManagerSupplier)writerInterceptorContext).getInjectionManager();
      }
   }

   public static InjectionManager getInjectionManager(ReaderInterceptorContext readerInterceptorContext) {
      if (!(readerInterceptorContext instanceof InjectionManagerSupplier)) {
         throw new IllegalArgumentException(
            LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_READER_INTERCEPTOR_CONTEXT(readerInterceptorContext.getClass().getName())
         );
      } else {
         return ((InjectionManagerSupplier)readerInterceptorContext).getInjectionManager();
      }
   }

   public static InjectionManager getInjectionManager(FeatureContext featureContext) {
      if (!(featureContext instanceof InjectionManagerSupplier)) {
         throw new IllegalArgumentException(LocalizationMessages.ERROR_SERVICE_LOCATOR_PROVIDER_INSTANCE_FEATURE_CONTEXT(featureContext.getClass().getName()));
      } else {
         return ((InjectionManagerSupplier)featureContext).getInjectionManager();
      }
   }
}
