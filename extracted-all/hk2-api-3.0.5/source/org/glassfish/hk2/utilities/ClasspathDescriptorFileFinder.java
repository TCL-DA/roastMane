package org.glassfish.hk2.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import org.glassfish.hk2.utilities.reflection.Logger;

public class ClasspathDescriptorFileFinder implements DescriptorFileFinder, DescriptorFileFinderInformation {
   private static final String DEBUG_DESCRIPTOR_FINDER_PROPERTY = "org.jvnet.hk2.properties.debug.descriptor.file.finder";
   private static final boolean DEBUG_DESCRIPTOR_FINDER = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.debug.descriptor.file.finder", "false"));
      }
   });
   private static final String DEFAULT_NAME = "default";
   private final ClassLoader classLoader;
   private final String[] names;
   private final List<String> identifiers = new ArrayList<>();
   private final String resourceBase;

   public ClasspathDescriptorFileFinder() {
      this(ClasspathDescriptorFileFinder.class.getClassLoader(), "default");
   }

   public ClasspathDescriptorFileFinder(ClassLoader cl) {
      this(cl, "default");
   }

   public ClasspathDescriptorFileFinder(ClassLoader cl, String... names) {
      this("META-INF/hk2-locator/", cl, names);
   }

   public ClasspathDescriptorFileFinder(String resourceBase, ClassLoader cl) {
      this(resourceBase, cl, "default");
   }

   private ClasspathDescriptorFileFinder(String resourceBase, ClassLoader cl, String... names) {
      this.resourceBase = resourceBase.endsWith("/") ? resourceBase : resourceBase + "/";
      this.classLoader = cl;
      this.names = names;
   }

   @Override
   public List<InputStream> findDescriptorFiles() throws IOException {
      this.identifiers.clear();
      List<InputStream> returnList = new ArrayList<>();

      for (String name : this.names) {
         String resourceName = this.resourceBase + name;
         Enumeration<URL> e = this.classLoader.getResources(resourceName);

         while (e.hasMoreElements()) {
            URL url = e.nextElement();
            if (DEBUG_DESCRIPTOR_FINDER) {
               Logger.getLogger().debug("Adding in URL to set being parsed: " + url + " from " + resourceName);
            }

            try {
               this.identifiers.add(url.toURI().toString());
            } catch (URISyntaxException var11) {
               throw new IOException(var11);
            }

            InputStream inputStream;
            try {
               inputStream = url.openStream();
            } catch (IOException var12) {
               if (DEBUG_DESCRIPTOR_FINDER) {
                  Logger.getLogger().debug("IOException for url " + url, var12);
               }

               throw var12;
            } catch (Throwable var13) {
               if (DEBUG_DESCRIPTOR_FINDER) {
                  Logger.getLogger().debug("Unexpected exception for url " + url, var13);
               }

               throw new IOException(var13);
            }

            if (DEBUG_DESCRIPTOR_FINDER) {
               Logger.getLogger().debug("Input stream for: " + url + " from " + resourceName + " has succesfully been opened");
            }

            returnList.add(inputStream);
         }
      }

      return returnList;
   }

   @Override
   public List<String> getDescriptorFileInformation() {
      return this.identifiers;
   }

   @Override
   public String toString() {
      return "ClasspathDescriptorFileFinder(" + this.classLoader + "," + Arrays.toString((Object[])this.names) + "," + System.identityHashCode(this) + ")";
   }
}
