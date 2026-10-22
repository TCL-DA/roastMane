package io.github.classgraph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.VersionFinder;

public class ClassGraphClassLoader extends ClassLoader {
   private final ScanResult scanResult;
   private final boolean initializeLoadedClasses;
   private Set<ClassLoader> environmentClassLoaderDelegationOrder;
   private List<ClassLoader> overrideClassLoaders;
   private final ClassLoader classpathClassLoader;
   private Set<ClassLoader> addedClassLoaderDelegationOrder;

   ClassGraphClassLoader(ScanResult scanResult) {
      super(null);
      registerAsParallelCapable();
      this.scanResult = scanResult;
      ScanSpec scanSpec = scanResult.scanSpec;
      this.initializeLoadedClasses = scanSpec.initializeLoadedClasses;
      boolean classpathOverridden = scanSpec.overrideClasspath != null && !scanSpec.overrideClasspath.isEmpty();
      boolean classloadersOverridden = scanSpec.overrideClassLoaders != null && !scanSpec.overrideClassLoaders.isEmpty();
      boolean clasloadersAdded = scanSpec.addedClassLoaders != null && !scanSpec.addedClassLoaders.isEmpty();
      if (!classpathOverridden && !classloadersOverridden) {
         this.environmentClassLoaderDelegationOrder = new LinkedHashSet<>();
         this.environmentClassLoaderDelegationOrder.add(null);
         ClassLoader[] envClassLoaderOrder = scanResult.getClassLoaderOrderRespectingParentDelegation();
         if (envClassLoaderOrder != null) {
            this.environmentClassLoaderDelegationOrder.addAll(Arrays.asList(envClassLoaderOrder));
         }
      }

      List<URL> classpathURLs = scanResult.getClasspathURLs();
      this.classpathClassLoader = classpathURLs.isEmpty() ? null : new URLClassLoader(classpathURLs.toArray(new URL[0]));
      this.overrideClassLoaders = classloadersOverridden ? scanSpec.overrideClassLoaders : null;
      if (this.overrideClassLoaders == null && classpathOverridden && this.classpathClassLoader != null) {
         this.overrideClassLoaders = Collections.singletonList(this.classpathClassLoader);
      }

      if (clasloadersAdded) {
         this.addedClassLoaderDelegationOrder = new LinkedHashSet<>();
         this.addedClassLoaderDelegationOrder.addAll(scanSpec.addedClassLoaders);
         if (this.environmentClassLoaderDelegationOrder != null) {
            this.addedClassLoaderDelegationOrder.removeAll(this.environmentClassLoaderDelegationOrder);
         }
      }
   }

   @Override
   protected Class<?> findClass(String className) throws ClassNotFoundException, LinkageError, SecurityException {
      ClassGraphClassLoader delegateClassGraphClassLoader = this.scanResult.classpathFinder.getDelegateClassGraphClassLoader();
      LinkageError linkageError = null;
      if (delegateClassGraphClassLoader != null) {
         try {
            return Class.forName(className, this.initializeLoadedClasses, delegateClassGraphClassLoader);
         } catch (ClassNotFoundException var35) {
         } catch (LinkageError var36) {
            linkageError = var36;
         }
      }

      if (this.overrideClassLoaders != null) {
         for (ClassLoader overrideClassLoader : this.overrideClassLoaders) {
            try {
               return Class.forName(className, this.initializeLoadedClasses, overrideClassLoader);
            } catch (ClassNotFoundException var48) {
            } catch (LinkageError var49) {
               if (linkageError == null) {
                  linkageError = var49;
               }
            }
         }
      }

      if (this.overrideClassLoaders == null && this.environmentClassLoaderDelegationOrder != null && !this.environmentClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
            try {
               return Class.forName(className, this.initializeLoadedClasses, envClassLoader);
            } catch (ClassNotFoundException var46) {
            } catch (LinkageError var47) {
               if (linkageError == null) {
                  linkageError = var47;
               }
            }
         }
      }

      ClassLoader classInfoClassLoader = null;
      ClassInfo classInfo = this.scanResult.classNameToClassInfo == null ? null : this.scanResult.classNameToClassInfo.get(className);
      if (classInfo != null) {
         classInfoClassLoader = classInfo.classLoader;
         if (classInfoClassLoader != null
            && (this.environmentClassLoaderDelegationOrder == null || !this.environmentClassLoaderDelegationOrder.contains(classInfoClassLoader))) {
            try {
               return Class.forName(className, this.initializeLoadedClasses, classInfoClassLoader);
            } catch (ClassNotFoundException var44) {
            } catch (LinkageError var45) {
               if (linkageError == null) {
                  linkageError = var45;
               }
            }
         }

         if (classInfo.classpathElement instanceof ClasspathElementModule && !classInfo.isPublic()) {
            throw new ClassNotFoundException(
               "Classfile for class "
                  + className
                  + " was found in a module, but the context and system classloaders could not load the class, probably because the class is not public."
            );
         }
      }

      if (this.overrideClassLoaders == null && this.classpathClassLoader != null) {
         try {
            return Class.forName(className, this.initializeLoadedClasses, this.classpathClassLoader);
         } catch (ClassNotFoundException var42) {
         } catch (LinkageError var43) {
            if (linkageError == null) {
               linkageError = var43;
            }
         }
      }

      if (this.addedClassLoaderDelegationOrder != null && !this.addedClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
            if (additionalClassLoader != classInfoClassLoader) {
               try {
                  return Class.forName(className, this.initializeLoadedClasses, additionalClassLoader);
               } catch (ClassNotFoundException var40) {
               } catch (LinkageError var41) {
                  if (linkageError == null) {
                     linkageError = var41;
                  }
               }
            }
         }
      }

      ResourceList classfileResources = this.scanResult.getResourcesWithPath(JarUtils.classNameToClassfilePath(className));
      if (classfileResources != null) {
         for (Resource resource : classfileResources) {
            try {
               Resource resourceToClose = resource;
               Throwable theWrongName = null;

               Class var11;
               try {
                  var11 = this.defineClass(className, resourceToClose.read(), (ProtectionDomain)null);
               } catch (Throwable var34) {
                  theWrongName = var34;
                  throw var34;
               } finally {
                  if (resource != null) {
                     if (theWrongName != null) {
                        try {
                           resourceToClose.close();
                        } catch (Throwable var33) {
                           theWrongName.addSuppressed(var33);
                        }
                     } else {
                        resource.close();
                     }
                  }
               }

               return var11;
            } catch (IOException var38) {
               throw new ClassNotFoundException("Could not load classfile for class " + className + " : " + var38);
            } catch (LinkageError var39) {
               if (linkageError == null) {
                  linkageError = var39;
               }
            }
         }
      }

      if (linkageError != null) {
         if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows) {
            String msg = linkageError.getMessage();
            if (msg != null) {
               String wrongName = "(wrong name: ";
               int wrongNameIdx = msg.indexOf("(wrong name: ");
               if (wrongNameIdx > -1) {
                  String theWrongName = msg.substring(wrongNameIdx + "(wrong name: ".length(), msg.length() - 1);
                  if (theWrongName.replace('/', '.').equalsIgnoreCase(className)) {
                     throw new LinkageError(
                        "You appear to have two classfiles with the same case-insensitive name in the same directory on a case-insensitive filesystem -- this is not allowed on Windows, and therefore your code is not portable. Class name: "
                           + className,
                        linkageError
                     );
                  }
               }
            }
         }

         throw linkageError;
      } else {
         throw new ClassNotFoundException("Could not find or load classfile for class " + className);
      }
   }

   public URL[] getURLs() {
      return this.scanResult.getClasspathURLs().toArray(new URL[0]);
   }

   @Override
   public URL getResource(String path) {
      if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
            URL resource = envClassLoader.getResource(path);
            if (resource != null) {
               return resource;
            }
         }
      }

      if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
            URL resource = additionalClassLoader.getResource(path);
            if (resource != null) {
               return resource;
            }
         }
      }

      ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      return resourceList != null && !resourceList.isEmpty() ? resourceList.get(0).getURL() : super.getResource(path);
   }

   @Override
   public Enumeration<URL> getResources(String path) throws IOException {
      if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
            Enumeration<URL> resources = envClassLoader.getResources(path);
            if (resources != null && resources.hasMoreElements()) {
               return resources;
            }
         }
      }

      if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
            Enumeration<URL> resources = additionalClassLoader.getResources(path);
            if (resources != null && resources.hasMoreElements()) {
               return resources;
            }
         }
      }

      final ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      return resourceList != null && !resourceList.isEmpty() ? new Enumeration<URL>() {
         int idx;

         @Override
         public boolean hasMoreElements() {
            return this.idx < resourceList.size();
         }

         public URL nextElement() {
            return resourceList.get(this.idx++).getURL();
         }
      } : Collections.emptyEnumeration();
   }

   @Override
   public InputStream getResourceAsStream(String path) {
      if (!this.environmentClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader envClassLoader : this.environmentClassLoaderDelegationOrder) {
            InputStream inputStream = envClassLoader.getResourceAsStream(path);
            if (inputStream != null) {
               return inputStream;
            }
         }
      }

      if (!this.addedClassLoaderDelegationOrder.isEmpty()) {
         for (ClassLoader additionalClassLoader : this.addedClassLoaderDelegationOrder) {
            InputStream inputStream = additionalClassLoader.getResourceAsStream(path);
            if (inputStream != null) {
               return inputStream;
            }
         }
      }

      ResourceList resourceList = this.scanResult.getResourcesWithPath(path);
      if (resourceList != null && !resourceList.isEmpty()) {
         try {
            return resourceList.get(0).open();
         } catch (IOException var5) {
            return null;
         }
      } else {
         return super.getResourceAsStream(path);
      }
   }
}
