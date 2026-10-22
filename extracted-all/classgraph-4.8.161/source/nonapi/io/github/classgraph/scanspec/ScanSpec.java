package nonapi.io.github.classgraph.scanspec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ModulePathInfo;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.utils.LogNode;

public class ScanSpec {
   public AcceptReject.AcceptRejectWholeString packageAcceptReject = new AcceptReject.AcceptRejectWholeString('.');
   public AcceptReject.AcceptRejectPrefix packagePrefixAcceptReject = new AcceptReject.AcceptRejectPrefix('.');
   public AcceptReject.AcceptRejectWholeString pathAcceptReject = new AcceptReject.AcceptRejectWholeString('/');
   public AcceptReject.AcceptRejectPrefix pathPrefixAcceptReject = new AcceptReject.AcceptRejectPrefix('/');
   public AcceptReject.AcceptRejectWholeString classAcceptReject = new AcceptReject.AcceptRejectWholeString('.');
   public AcceptReject.AcceptRejectWholeString classfilePathAcceptReject = new AcceptReject.AcceptRejectWholeString('/');
   public AcceptReject.AcceptRejectWholeString classPackageAcceptReject = new AcceptReject.AcceptRejectWholeString('.');
   public AcceptReject.AcceptRejectWholeString classPackagePathAcceptReject = new AcceptReject.AcceptRejectWholeString('/');
   public AcceptReject.AcceptRejectWholeString moduleAcceptReject = new AcceptReject.AcceptRejectWholeString('.');
   public AcceptReject.AcceptRejectLeafname jarAcceptReject = new AcceptReject.AcceptRejectLeafname('/');
   public AcceptReject.AcceptRejectWholeString classpathElementResourcePathAcceptReject = new AcceptReject.AcceptRejectWholeString('/');
   public AcceptReject.AcceptRejectLeafname libOrExtJarAcceptReject = new AcceptReject.AcceptRejectLeafname('/');
   public boolean scanJars = true;
   public boolean scanNestedJars = true;
   public boolean scanDirs = true;
   public boolean scanModules = true;
   public boolean enableClassInfo;
   public boolean enableFieldInfo;
   public boolean enableMethodInfo;
   public boolean enableAnnotationInfo;
   public boolean enableStaticFinalFieldConstantInitializerValues;
   public boolean enableInterClassDependencies;
   public boolean enableExternalClasses;
   public boolean enableSystemJarsAndModules;
   public boolean ignoreClassVisibility;
   public boolean ignoreFieldVisibility;
   public boolean ignoreMethodVisibility;
   public boolean disableRuntimeInvisibleAnnotations;
   public boolean extendScanningUpwardsToExternalClasses = true;
   public Set<String> allowedURLSchemes;
   public transient List<ClassLoader> addedClassLoaders;
   public transient List<ClassLoader> overrideClassLoaders;
   public transient List<Object> addedModuleLayers;
   public transient List<Object> overrideModuleLayers;
   public List<Object> overrideClasspath;
   public transient List<Object> classpathElementFilters;
   public boolean initializeLoadedClasses;
   public boolean removeTemporaryFilesAfterScan;
   public boolean ignoreParentClassLoaders;
   public boolean ignoreParentModuleLayers;
   public ModulePathInfo modulePathInfo = new ModulePathInfo();
   public int maxBufferedJarRAMSize = 67108864;
   public boolean enableMemoryMapping;

   public void sortPrefixes() {
      for (Field field : ScanSpec.class.getDeclaredFields()) {
         if (AcceptReject.class.isAssignableFrom(field.getType())) {
            try {
               ((AcceptReject)field.get(this)).sortPrefixes();
            } catch (ReflectiveOperationException var6) {
               throw new RuntimeException("Field is not accessible: " + field, var6);
            }
         }
      }
   }

   public void addClasspathOverride(Object overrideClasspathElement) {
      if (this.overrideClasspath == null) {
         this.overrideClasspath = new ArrayList<>();
      }

      if (overrideClasspathElement instanceof ClassLoader) {
         throw new IllegalArgumentException("Need to pass ClassLoader instances to overrideClassLoaders, not overrideClasspath");
      } else {
         this.overrideClasspath
            .add(
               !(overrideClasspathElement instanceof String) && !(overrideClasspathElement instanceof URL) && !(overrideClasspathElement instanceof URI)
                  ? overrideClasspathElement.toString()
                  : overrideClasspathElement
            );
      }
   }

   public void filterClasspathElements(Object filterLambda) {
      if (!(filterLambda instanceof ClassGraph.ClasspathElementFilter) && !(filterLambda instanceof ClassGraph.ClasspathElementURLFilter)) {
         throw new IllegalArgumentException();
      } else {
         if (this.classpathElementFilters == null) {
            this.classpathElementFilters = new ArrayList<>(2);
         }

         this.classpathElementFilters.add(filterLambda);
      }
   }

   public void addClassLoader(ClassLoader classLoader) {
      if (this.addedClassLoaders == null) {
         this.addedClassLoaders = new ArrayList<>();
      }

      if (classLoader != null) {
         this.addedClassLoaders.add(classLoader);
      }
   }

   public void enableURLScheme(String scheme) {
      if (scheme != null && scheme.length() >= 2) {
         if (this.allowedURLSchemes == null) {
            this.allowedURLSchemes = new HashSet<>();
         }

         this.allowedURLSchemes.add(scheme.toLowerCase());
      } else {
         throw new IllegalArgumentException("URL schemes must contain at least two characters");
      }
   }

   public void overrideClassLoaders(ClassLoader... overrideClassLoaders) {
      if (overrideClassLoaders.length == 0) {
         throw new IllegalArgumentException("At least one override ClassLoader must be provided");
      } else {
         this.addedClassLoaders = null;
         this.overrideClassLoaders = new ArrayList<>();

         for (ClassLoader classLoader : overrideClassLoaders) {
            if (classLoader != null) {
               this.overrideClassLoaders.add(classLoader);
            }
         }
      }
   }

   private static boolean isModuleLayer(Object moduleLayer) {
      if (moduleLayer == null) {
         throw new IllegalArgumentException("ModuleLayer references must not be null");
      } else {
         for (Class<?> currClass = moduleLayer.getClass(); currClass != null; currClass = currClass.getSuperclass()) {
            if (currClass.getName().equals("java.lang.ModuleLayer")) {
               return true;
            }
         }

         return false;
      }
   }

   public void addModuleLayer(Object moduleLayer) {
      if (!isModuleLayer(moduleLayer)) {
         throw new IllegalArgumentException("moduleLayer must be of type java.lang.ModuleLayer");
      } else {
         if (this.addedModuleLayers == null) {
            this.addedModuleLayers = new ArrayList<>();
         }

         this.addedModuleLayers.add(moduleLayer);
      }
   }

   public void overrideModuleLayers(Object... overrideModuleLayers) {
      if (overrideModuleLayers == null) {
         throw new IllegalArgumentException("overrideModuleLayers cannot be null");
      } else if (overrideModuleLayers.length == 0) {
         throw new IllegalArgumentException("At least one override ModuleLayer must be provided");
      } else {
         for (Object moduleLayer : overrideModuleLayers) {
            if (!isModuleLayer(moduleLayer)) {
               throw new IllegalArgumentException("moduleLayer must be of type java.lang.ModuleLayer");
            }
         }

         this.addedModuleLayers = null;
         this.overrideModuleLayers = new ArrayList<>();
         Collections.addAll(this.overrideModuleLayers, overrideModuleLayers);
      }
   }

   public ScanSpec.ScanSpecPathMatch dirAcceptMatchStatus(String relativePath) {
      if (this.pathAcceptReject.isRejected(relativePath) || this.pathPrefixAcceptReject.isRejected(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.HAS_REJECTED_PATH_PREFIX;
      } else if (this.pathAcceptReject.acceptIsEmpty() && this.classPackagePathAcceptReject.acceptIsEmpty()) {
         return !relativePath.isEmpty() && !relativePath.equals("/")
            ? ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX
            : ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH;
      } else if (this.pathAcceptReject.isSpecificallyAcceptedAndNotRejected(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH;
      } else if (this.classPackagePathAcceptReject.isSpecificallyAcceptedAndNotRejected(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_CLASS_PACKAGE;
      } else if (this.pathPrefixAcceptReject.isSpecificallyAccepted(relativePath)) {
         return ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX;
      } else {
         return !relativePath.equals("/")
               && !this.pathAcceptReject.acceptHasPrefix(relativePath)
               && !this.classfilePathAcceptReject.acceptHasPrefix(relativePath)
            ? ScanSpec.ScanSpecPathMatch.NOT_WITHIN_ACCEPTED_PATH
            : ScanSpec.ScanSpecPathMatch.ANCESTOR_OF_ACCEPTED_PATH;
      }
   }

   public boolean classfileIsSpecificallyAccepted(String relativePath) {
      return this.classfilePathAcceptReject.isSpecificallyAcceptedAndNotRejected(relativePath);
   }

   public boolean classOrPackageIsRejected(String className) {
      return this.classAcceptReject.isRejected(className) || this.packagePrefixAcceptReject.isRejected(className);
   }

   public void log(LogNode log) {
      if (log != null) {
         LogNode scanSpecLog = log.log("ScanSpec:");

         for (Field field : ScanSpec.class.getDeclaredFields()) {
            try {
               scanSpecLog.log(field.getName() + ": " + field.get(this));
            } catch (ReflectiveOperationException var8) {
            }
         }
      }
   }

   public static enum ScanSpecPathMatch {
      HAS_REJECTED_PATH_PREFIX,
      HAS_ACCEPTED_PATH_PREFIX,
      AT_ACCEPTED_PATH,
      ANCESTOR_OF_ACCEPTED_PATH,
      AT_ACCEPTED_CLASS_PACKAGE,
      NOT_WITHIN_ACCEPTED_PATH;
   }
}
