package org.jvnet.hk2.internal;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import org.glassfish.hk2.api.ClassAnalyzer;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Visibility;

@Singleton
@Named("default")
@Visibility(DescriptorVisibility.LOCAL)
public class DefaultClassAnalyzer implements ClassAnalyzer {
   private final ServiceLocatorImpl locator;

   public DefaultClassAnalyzer(ServiceLocatorImpl locator) {
      this.locator = locator;
   }

   public <T> Constructor<T> getConstructor(Class<T> clazz) throws MultiException, NoSuchMethodException {
      Collector collector = new Collector();
      Constructor<T> retVal = (Constructor<T>)Utilities.findProducerConstructor(clazz, this.locator, collector);

      try {
         collector.throwIfErrors();
         return retVal;
      } catch (MultiException var7) {
         for (Throwable th : var7.getErrors()) {
            if (th instanceof NoSuchMethodException) {
               throw (NoSuchMethodException)th;
            }
         }

         throw var7;
      }
   }

   public <T> Set<Method> getInitializerMethods(Class<T> clazz) throws MultiException {
      Collector collector = new Collector();
      Set<Method> retVal = Utilities.findInitializerMethods(clazz, this.locator, collector);
      collector.throwIfErrors();
      return retVal;
   }

   public <T> Set<Field> getFields(Class<T> clazz) throws MultiException {
      Collector collector = new Collector();
      Set<Field> retVal = Utilities.findInitializerFields(clazz, this.locator, collector);
      collector.throwIfErrors();
      return retVal;
   }

   public <T> Method getPostConstructMethod(Class<T> clazz) throws MultiException {
      Collector collector = new Collector();
      Method retVal = Utilities.findPostConstruct(clazz, this.locator, collector);
      collector.throwIfErrors();
      return retVal;
   }

   public <T> Method getPreDestroyMethod(Class<T> clazz) throws MultiException {
      Collector collector = new Collector();
      Method retVal = Utilities.findPreDestroy(clazz, this.locator, collector);
      collector.throwIfErrors();
      return retVal;
   }
}
