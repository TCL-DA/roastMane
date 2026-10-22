package jakarta.xml.bind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModuleUtil {
   private static final Logger LOGGER = Logger.getLogger("jakarta.xml.bind");

   static Class<?>[] getClassesFromContextPath(String contextPath, ClassLoader classLoader) throws JAXBException {
      List<Class<?>> classes = new ArrayList<>();
      if (contextPath != null && !contextPath.isEmpty()) {
         String[] tokens = contextPath.split(":");

         for (String pkg : tokens) {
            try {
               Class<?> o = classLoader.loadClass(pkg + ".ObjectFactory");
               classes.add(o);
            } catch (ClassNotFoundException var10) {
               try {
                  Class<?> firstByJaxbIndex = findFirstByJaxbIndex(pkg, classLoader);
                  if (firstByJaxbIndex != null) {
                     classes.add(firstByJaxbIndex);
                  }
               } catch (IOException var9) {
                  throw new JAXBException(var9);
               }
            }
         }

         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Resolved classes from context path: {0}", classes);
         }

         return classes.toArray(new Class[0]);
      } else {
         return classes.toArray(new Class[0]);
      }
   }

   static Class<?> findFirstByJaxbIndex(String pkg, ClassLoader classLoader) throws IOException, JAXBException {
      String resource = pkg.replace('.', '/') + "/jaxb.index";
      InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
      if (resourceAsStream == null) {
         return null;
      } else {
         try (BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream, "UTF-8"))) {
            for (String className = in.readLine(); className != null; className = in.readLine()) {
               className = className.trim();
               if (!className.startsWith("#") && className.length() != 0) {
                  try {
                     return classLoader.loadClass(pkg + "." + className);
                  } catch (ClassNotFoundException var8) {
                     throw new JAXBException(Messages.format("ContextFinder.ErrorLoadClass", className, pkg), var8);
                  }
               }
            }
         }

         return null;
      }
   }

   public static void delegateAddOpensToImplModule(Class<?>[] classes, Class<?> factorySPI) throws JAXBException {
      Module implModule = factorySPI.getModule();
      Module jaxbModule = JAXBContext.class.getModule();
      if (!jaxbModule.isNamed()) {
         if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.log(Level.FINE, "Using jakarta.xml.bind-api on the class path.");
         }
      } else {
         for (Class<?> cls : classes) {
            Class<?> jaxbClass = cls.isArray() ? cls.getComponentType() : cls;
            Module classModule = jaxbClass.getModule();
            String packageName = jaxbClass.getPackageName();
            if (classModule.isNamed() && !classModule.getName().equals("java.base")) {
               if (!classModule.isOpen(packageName, jaxbModule)) {
                  throw new JAXBException(Messages.format("JAXBClasses.notOpen", packageName, jaxbClass.getName(), classModule.getName()));
               }

               classModule.addOpens(packageName, implModule);
               if (LOGGER.isLoggable(Level.FINE)) {
                  LOGGER.log(
                     Level.FINE, "Propagating openness of package {0} in {1} to {2}.", new String[]{packageName, classModule.getName(), implModule.getName()}
                  );
               }
            }
         }
      }
   }
}
