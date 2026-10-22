package org.eclipse.yasson.internal.components;

import jakarta.json.bind.JsonbException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;
import org.eclipse.yasson.spi.JsonbComponentInstanceCreator;

public class JsonbComponentInstanceCreatorFactory {
   private static final Logger LOGGER = Logger.getLogger(JsonbComponentInstanceCreator.class.getName());
   public static final String BEAN_MANAGER_NAME = "java:comp/BeanManager";
   public static final String INITIAL_CONTEXT_CLASS = "javax.naming.InitialContext";
   private static final String CDI_SPI_CLASS = "jakarta.enterprise.inject.spi.CDI";

   private JsonbComponentInstanceCreatorFactory() {
      throw new IllegalStateException("This class should never be instantiated");
   }

   public static JsonbComponentInstanceCreator getComponentInstanceCreator() {
      Object beanManager = getCdiBeanManager();
      if (beanManager == null) {
         beanManager = getJndiBeanManager();
      }

      if (beanManager == null) {
         LOGGER.finest(Messages.getMessage(MessageKeys.BEAN_MANAGER_NOT_FOUND_USING_DEFAULT));
         return new DefaultConstructorCreator();
      } else {
         return new BeanManagerInstanceCreator(beanManager);
      }
   }

   private static Object getCdiBeanManager() {
      return AccessController.doPrivileged((PrivilegedAction)(() -> {
         try {
            return getBeanManager(() -> {
               Class<?> cdiClass = Class.forName("jakarta.enterprise.inject.spi.CDI");
               Method current = cdiClass.getMethod("current");
               Method getBeanManager = cdiClass.getMethod("getBeanManager");
               Object cdiObject = current.invoke(cdiClass);
               return cdiObject == null ? null : getBeanManager.invoke(cdiObject);
            });
         } catch (ClassNotFoundException var1) {
            LOGGER.finest(Messages.getMessage(MessageKeys.NO_CDI_API_PROVIDER, "jakarta.enterprise.inject.spi.CDI"));
            return null;
         }
      }));
   }

   private static Object getJndiBeanManager() {
      return AccessController.doPrivileged((PrivilegedAction)(() -> {
         try {
            return getBeanManager(() -> {
               Class<?> initialContextClass = Class.forName("javax.naming.InitialContext");
               Method lookupMethod = initialContextClass.getMethod("lookup", String.class);
               Constructor<?> initialContextConstructor = initialContextClass.getConstructor();
               Object initialContextObject = initialContextConstructor.newInstance();
               return lookupMethod.invoke(initialContextObject, "java:comp/BeanManager");
            });
         } catch (ClassNotFoundException var1) {
            LOGGER.finest(Messages.getMessage(MessageKeys.NO_JNDI_ENVIRONMENT, "javax.naming.InitialContext"));
            return null;
         }
      }));
   }

   private static Object getBeanManager(JsonbComponentInstanceCreatorFactory.BeanManagerProvider command) throws ClassNotFoundException {
      try {
         return command.provide();
      } catch (InstantiationException | NoSuchMethodException var2) {
         throw new JsonbException(Messages.getMessage(MessageKeys.INTERNAL_ERROR, var2.getMessage()), var2);
      } catch (IllegalAccessException var3) {
         LOGGER.warning(var3.getMessage());
         LOGGER.warning(Messages.getMessage(MessageKeys.ILLEGAL_ACCESS, "lookup CDI bean manager"));
         return null;
      } catch (ClassNotFoundException var4) {
         throw var4;
      } catch (ReflectiveOperationException var5) {
         LOGGER.log(Level.FINEST, Messages.getMessage(MessageKeys.NO_CDI_ENVIRONMENT), (Throwable)var5);
         return null;
      }
   }

   private interface BeanManagerProvider {
      Object provide() throws ReflectiveOperationException;
   }
}
