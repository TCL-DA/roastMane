package org.glassfish.jersey.server.wadl.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.wadl.WadlGenerator;
import org.glassfish.jersey.server.wadl.internal.generators.WadlGeneratorJAXBGrammarGenerator;

class WadlGeneratorLoader {
   private static final Logger LOGGER = Logger.getLogger(WadlGeneratorLoader.class.getName());

   static WadlGenerator loadWadlGenerators(List<WadlGenerator> wadlGenerators) throws Exception {
      WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
      if (wadlGenerators != null && !wadlGenerators.isEmpty()) {
         for (WadlGenerator generator : wadlGenerators) {
            generator.setWadlGeneratorDelegate(wadlGenerator);
            wadlGenerator = generator;
         }
      }

      wadlGenerator.init();
      return wadlGenerator;
   }

   static WadlGenerator loadWadlGeneratorDescriptions(InjectionManager injectionManager, WadlGeneratorDescription... wadlGeneratorDescriptions) throws Exception {
      List<WadlGeneratorDescription> list = wadlGeneratorDescriptions != null ? Arrays.asList(wadlGeneratorDescriptions) : null;
      return loadWadlGeneratorDescriptions(injectionManager, list);
   }

   static WadlGenerator loadWadlGeneratorDescriptions(InjectionManager injectionManager, List<WadlGeneratorDescription> wadlGeneratorDescriptions) throws Exception {
      WadlGenerator wadlGenerator = new WadlGeneratorJAXBGrammarGenerator();
      WadlGeneratorLoader.CallbackList callbacks = new WadlGeneratorLoader.CallbackList();

      try {
         if (wadlGeneratorDescriptions != null && !wadlGeneratorDescriptions.isEmpty()) {
            for (WadlGeneratorDescription wadlGeneratorDescription : wadlGeneratorDescriptions) {
               WadlGeneratorLoader.WadlGeneratorControl control = loadWadlGenerator(injectionManager, wadlGeneratorDescription, wadlGenerator);
               wadlGenerator = control.wadlGenerator;
               callbacks.add(control.callback);
            }
         }

         wadlGenerator.init();
      } finally {
         callbacks.callback();
      }

      return wadlGenerator;
   }

   private static WadlGeneratorLoader.WadlGeneratorControl loadWadlGenerator(
      InjectionManager injectionManager, WadlGeneratorDescription wadlGeneratorDescription, WadlGenerator wadlGeneratorDelegate
   ) throws Exception {
      LOGGER.info("Loading wadlGenerator " + wadlGeneratorDescription.getGeneratorClass().getName());
      WadlGenerator generator = (WadlGenerator)Injections.getOrCreate(injectionManager, wadlGeneratorDescription.getGeneratorClass());
      generator.setWadlGeneratorDelegate(wadlGeneratorDelegate);
      WadlGeneratorLoader.CallbackList callbacks = null;
      if (wadlGeneratorDescription.getProperties() != null && !wadlGeneratorDescription.getProperties().isEmpty()) {
         callbacks = new WadlGeneratorLoader.CallbackList();
         Properties wadlGeneratorProperties = wadlGeneratorDescription.getProperties();
         Class<?> osgiConfiguratorClass = wadlGeneratorDescription.getConfiguratorClass();

         for (Entry<Object, Object> entry : wadlGeneratorProperties.entrySet()) {
            WadlGeneratorLoader.Callback callback = setProperty(generator, entry.getKey().toString(), entry.getValue(), osgiConfiguratorClass);
            callbacks.add(callback);
         }
      }

      return new WadlGeneratorLoader.WadlGeneratorControl(generator, callbacks);
   }

   private static WadlGeneratorLoader.Callback setProperty(Object generator, String propertyName, Object propertyValue, Class<?> osgiConfigClass) throws Exception {
      WadlGeneratorLoader.Callback result = null;
      String methodName = "set" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT) + propertyName.substring(1);
      Method method = getMethodByName(methodName, generator.getClass());
      if (method.getParameterTypes().length != 1) {
         throw new RuntimeException(
            "Method " + methodName + " is no setter, it does not expect exactly one parameter, but " + method.getParameterTypes().length
         );
      } else {
         Class<?> paramClazz = method.getParameterTypes()[0];
         if (paramClazz.isAssignableFrom(propertyValue.getClass())) {
            method.invoke(generator, propertyValue);
         } else if (File.class.equals(paramClazz) && propertyValue instanceof String) {
            LOGGER.warning(
               "Configuring the "
                  + method.getDeclaringClass().getSimpleName()
                  + " with the file based property "
                  + propertyName
                  + " is deprecated and will be removed in future versions of jersey! You should use the InputStream based property instead."
            );
            String filename = propertyValue.toString();
            if (filename.startsWith("classpath:")) {
               String strippedFilename = filename.substring("classpath:".length());
               URL resource = generator.getClass().getResource(strippedFilename);
               if (resource == null) {
                  throw new RuntimeException(
                     "The file '"
                        + strippedFilename
                        + "' does not exist in the classpath. It's loaded by the generator class, so if you use a relative filename it's relative to the generator class, otherwise you might want to load it via an absolute classpath reference like classpath:/somefile.xml"
                  );
               }

               File file = new File(resource.toURI());
               method.invoke(generator, file);
            } else {
               method.invoke(generator, new File(filename));
            }
         } else if (InputStream.class.equals(paramClazz) && propertyValue instanceof String) {
            final String resource = propertyValue.toString();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader == null) {
               loader = WadlGeneratorLoader.class.getClassLoader();
            }

            final InputStream is = ReflectionHelper.getResourceAsStream(loader, osgiConfigClass, resource);
            if (is == null) {
               String message = "The resource '" + resource + "' does not exist.";
               throw new RuntimeException(message);
            }

            result = new WadlGeneratorLoader.Callback() {
               @Override
               public void callback() {
                  try {
                     is.close();
                  } catch (IOException var2) {
                     WadlGeneratorLoader.LOGGER.log(Level.WARNING, "Could not close InputStream from resource " + resource, (Throwable)var2);
                  }
               }
            };

            try {
               method.invoke(generator, is);
            } catch (Exception var12) {
               is.close();
               throw var12;
            }
         } else {
            Constructor<?> paramTypeConstructor = paramClazz.getConstructor(propertyValue.getClass());
            if (paramTypeConstructor == null) {
               throw new RuntimeException(
                  "The property '"
                     + propertyName
                     + "' could not be set because the expected parameter is neither of type "
                     + propertyValue.getClass()
                     + " nor of any type that provides a constructor expecting a "
                     + propertyValue.getClass()
                     + ". The expected parameter is of type "
                     + paramClazz.getName()
               );
            }

            Object typedPropertyValue = paramTypeConstructor.newInstance(propertyValue);
            method.invoke(generator, typedPropertyValue);
         }

         return result;
      }
   }

   private static Method getMethodByName(String methodName, Class<?> clazz) {
      for (Method method : clazz.getMethods()) {
         if (method.getName().equals(methodName)) {
            return method;
         }
      }

      throw new RuntimeException("Method '" + methodName + "' not found for class " + clazz.getName());
   }

   private interface Callback {
      void callback();
   }

   private static class CallbackList extends ArrayList<WadlGeneratorLoader.Callback> implements WadlGeneratorLoader.Callback {
      private static final long serialVersionUID = 1L;

      private CallbackList() {
      }

      @Override
      public void callback() {
         for (WadlGeneratorLoader.Callback callback : this) {
            callback.callback();
         }
      }

      public boolean add(WadlGeneratorLoader.Callback e) {
         return e != null ? super.add(e) : false;
      }
   }

   private static class WadlGeneratorControl {
      WadlGenerator wadlGenerator;
      WadlGeneratorLoader.Callback callback;

      public WadlGeneratorControl(WadlGenerator wadlGenerator, WadlGeneratorLoader.Callback callback) {
         this.wadlGenerator = wadlGenerator;
         this.callback = callback;
      }
   }
}
