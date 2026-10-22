package org.jvnet.mimepull;

import java.util.Iterator;
import java.util.ServiceLoader;

class FactoryFinder {
   static <T> T find(Class<T> factoryId) throws ClassNotFoundException, ReflectiveOperationException {
      String systemProp = System.getProperty(factoryId.getName());
      if (systemProp != null) {
         return newInstance(factoryId, systemProp);
      } else {
         Iterator<T> loader = ServiceLoader.load(factoryId).iterator();
         return loader.hasNext() ? loader.next() : null;
      }
   }

   static <T> T newInstance(Class<T> cls, String className) throws ClassNotFoundException, ReflectiveOperationException {
      Class<T> providerClass = (Class<T>)FactoryFinder.class.getClassLoader().loadClass(className);
      return providerClass.getConstructor().newInstance();
   }
}
