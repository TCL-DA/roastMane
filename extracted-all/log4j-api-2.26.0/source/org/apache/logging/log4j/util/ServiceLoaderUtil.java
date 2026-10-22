package org.apache.logging.log4j.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.Logger;

@InternalApi
public final class ServiceLoaderUtil {
   private static final int MAX_BROKEN_SERVICES = 8;

   private ServiceLoaderUtil() {
   }

   public static <S> Stream<S> safeStream(final Class<S> serviceType, final ServiceLoader<? extends S> serviceLoader, final Logger logger) {
      Objects.requireNonNull(serviceLoader, "serviceLoader");
      Collection<Class<?>> classes = new HashSet<>();
      Stream<S> services = StreamSupport.stream(new ServiceLoaderUtil.ServiceLoaderSpliterator<>(serviceType, serviceLoader, logger), false);
      Class<?> callerClass = StackLocatorUtil.getCallerClass(2);
      Stream<S> allServices = OsgiServiceLocator.isAvailable() && callerClass != null
         ? Stream.concat(services, OsgiServiceLocator.loadServices(serviceType, callerClass, logger))
         : services;
      return allServices.filter(service -> classes.add(service.getClass()));
   }

   private static final class ServiceLoaderSpliterator<S> extends AbstractSpliterator<S> {
      private final String serviceName;
      private final Iterator<? extends S> serviceIterator;
      private final Logger logger;

      private ServiceLoaderSpliterator(final Class<S> serviceType, final Iterable<? extends S> serviceLoader, final Logger logger) {
         super(Long.MAX_VALUE, 1296);
         this.serviceName = serviceType.getName();
         this.serviceIterator = serviceLoader.iterator();
         this.logger = logger;
      }

      @Override
      public boolean tryAdvance(final Consumer<? super S> action) {
         int i = 8;

         while (i-- > 0) {
            try {
               if (this.serviceIterator.hasNext()) {
                  action.accept((S)this.serviceIterator.next());
                  return true;
               }
            } catch (LinkageError | ServiceConfigurationError var4) {
               this.logger.warn("Unable to load implementation for service {}", this.serviceName, var4);
            } catch (Exception var5) {
               this.logger.warn("Unexpected exception  while loading implementation for service {}", this.serviceName, var5);
               throw var5;
            }
         }

         return false;
      }
   }
}
