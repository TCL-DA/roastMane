package org.glassfish.jersey.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.inject.ServiceHolder;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.spi.ExceptionMappers;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

public class ExceptionMapperFactory implements ExceptionMappers {
   private static final Logger LOGGER = Logger.getLogger(ExceptionMapperFactory.class.getName());
   private final Value<Set<ExceptionMapperFactory.ExceptionMapperType>> exceptionMapperTypes;

   @Override
   public <T extends Throwable> ExceptionMapper<T> findMapping(T exceptionInstance) {
      return this.find((Class<T>)exceptionInstance.getClass(), exceptionInstance);
   }

   @Override
   public <T extends Throwable> ExceptionMapper<T> find(Class<T> type) {
      return this.find(type, null);
   }

   private <T extends Throwable> ExceptionMapper<T> find(Class<T> type, T exceptionInstance) {
      ExceptionMapper<T> mapper = null;
      int minDistance = Integer.MAX_VALUE;
      int priority = 5000;

      for (ExceptionMapperFactory.ExceptionMapperType mapperType : this.exceptionMapperTypes.get()) {
         int d = this.distance(type, mapperType.exceptionType);
         if (d >= 0 && d <= minDistance) {
            ExceptionMapper<T> candidate = mapperType.mapper.getInstance();
            int p = mapperType.mapper.getRank() > 0 ? mapperType.mapper.getRank() : 5000;
            if (this.isPreferredCandidate(exceptionInstance, candidate, d == minDistance && p >= priority)) {
               mapper = candidate;
               minDistance = d;
               priority = p;
            }
         }
      }

      return mapper;
   }

   private <T extends Throwable> boolean isPreferredCandidate(T exceptionInstance, ExceptionMapper<T> candidate, boolean sameDistance) {
      if (exceptionInstance == null) {
         return true;
      } else {
         return !(candidate instanceof ExtendedExceptionMapper)
            ? !sameDistance
            : !sameDistance && ((ExtendedExceptionMapper)candidate).isMappable(exceptionInstance);
      }
   }

   public ExceptionMapperFactory(InjectionManager injectionManager) {
      this.exceptionMapperTypes = this.createLazyExceptionMappers(injectionManager);
   }

   private LazyValue<Set<ExceptionMapperFactory.ExceptionMapperType>> createLazyExceptionMappers(InjectionManager injectionManager) {
      return Values.lazy((Value<Set<ExceptionMapperFactory.ExceptionMapperType>>)(() -> {
         Collection<ServiceHolder<ExceptionMapper>> mapperHandles = Providers.getAllServiceHolders(injectionManager, ExceptionMapper.class);
         Set<ExceptionMapperFactory.ExceptionMapperType> exceptionMapperTypes = new LinkedHashSet<>();

         for (ServiceHolder<ExceptionMapper> mapperHandle : mapperHandles) {
            ExceptionMapper mapper = mapperHandle.getInstance();
            if (!"org.glassfish.jersey.server.DefaultExceptionMapper".equals(mapper.getClass().getName())) {
               if (Proxy.isProxyClass(mapper.getClass())) {
                  SortedSet<Class<? extends ExceptionMapper>> mapperTypes = new TreeSet<>((o1, o2) -> o1.isAssignableFrom((Class<?>)o2) ? -1 : 1);

                  for (Type contract : mapperHandle.getContractTypes()) {
                     if (contract instanceof Class && ExceptionMapper.class.isAssignableFrom((Class<?>)contract) && contract != ExceptionMapper.class) {
                        mapperTypes.add((Class<? extends ExceptionMapper>)contract);
                     }
                  }

                  if (!mapperTypes.isEmpty()) {
                     Class<? extends Throwable> c = this.getExceptionType(mapperTypes.first());
                     if (c != null) {
                        exceptionMapperTypes.add(new ExceptionMapperFactory.ExceptionMapperType(mapperHandle, c));
                     }
                  }
               } else {
                  Class<? extends Throwable> c = this.getExceptionType((Class<? extends ExceptionMapper>)mapper.getClass());
                  if (c != null) {
                     exceptionMapperTypes.add(new ExceptionMapperFactory.ExceptionMapperType(mapperHandle, c));
                  }
               }
            }
         }

         return exceptionMapperTypes;
      }));
   }

   private int distance(Class<?> c, Class<?> emtc) {
      int distance = 0;
      if (!emtc.isAssignableFrom(c)) {
         return -1;
      } else {
         while (c != emtc) {
            c = c.getSuperclass();
            distance++;
         }

         return distance;
      }
   }

   private Class<? extends Throwable> getExceptionType(Class<? extends ExceptionMapper> c) {
      Class<?> t = this.getType(c);
      if (Throwable.class.isAssignableFrom(t)) {
         return (Class<? extends Throwable>)t;
      } else {
         if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(LocalizationMessages.EXCEPTION_MAPPER_SUPPORTED_TYPE_UNKNOWN(c.getName()));
         }

         return null;
      }
   }

   private Class getType(Class<? extends ExceptionMapper> clazz) {
      for (Class clazzHolder = clazz; clazzHolder != Object.class; clazzHolder = clazzHolder.getSuperclass()) {
         Class type = this.getTypeFromInterface(clazzHolder, clazz);
         if (type != null) {
            return type;
         }
      }

      throw new ProcessingException(LocalizationMessages.ERROR_FINDING_EXCEPTION_MAPPER_TYPE(clazz));
   }

   private Class getTypeFromInterface(Class<?> clazz, Class<? extends ExceptionMapper> original) {
      Type[] types = clazz.getGenericInterfaces();

      for (Type type : types) {
         if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType)type;
            if (pt.getRawType() == ExceptionMapper.class || pt.getRawType() == ExtendedExceptionMapper.class) {
               return this.getResolvedType(pt.getActualTypeArguments()[0], original, clazz);
            }
         } else if (type instanceof Class) {
            clazz = (Class<?>)type;
            if (ExceptionMapper.class.isAssignableFrom(clazz)) {
               return this.getTypeFromInterface(clazz, original);
            }
         }
      }

      return null;
   }

   private Class getResolvedType(Type t, Class c, Class dc) {
      if (t instanceof Class) {
         return (Class)t;
      } else if (t instanceof TypeVariable) {
         ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
         return ct != null ? ct.rawClass() : null;
      } else if (t instanceof ParameterizedType) {
         ParameterizedType pt = (ParameterizedType)t;
         return (Class)pt.getRawType();
      } else {
         return null;
      }
   }

   private static class ExceptionMapperType {
      ServiceHolder<ExceptionMapper> mapper;
      Class<? extends Throwable> exceptionType;

      public ExceptionMapperType(ServiceHolder<ExceptionMapper> mapper, Class<? extends Throwable> exceptionType) {
         this.mapper = mapper;
         this.exceptionType = exceptionType;
      }
   }

   public static class ExceptionMappersConfigurator implements BootstrapConfigurator {
      private ExceptionMapperFactory exceptionMapperFactory;

      @Override
      public void init(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         this.exceptionMapperFactory = new ExceptionMapperFactory(injectionManager);
         InstanceBinding<ExceptionMapperFactory> binding = Bindings.service(this.exceptionMapperFactory).to(ExceptionMappers.class);
         injectionManager.register((Binding)binding);
      }

      @Override
      public void postInit(InjectionManager injectionManager, BootstrapBag bootstrapBag) {
         bootstrapBag.setExceptionMappers(this.exceptionMapperFactory);
      }
   }
}
