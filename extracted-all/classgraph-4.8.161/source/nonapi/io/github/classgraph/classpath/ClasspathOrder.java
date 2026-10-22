package nonapi.io.github.classgraph.classpath;

import io.github.classgraph.ClassGraph;
import java.io.File;
import java.io.IOError;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClasspathOrder {
   private final ScanSpec scanSpec;
   public ReflectionUtils reflectionUtils;
   private final Set<String> classpathEntryUniqueResolvedPaths = new HashSet<>();
   private final List<ClasspathOrder.ClasspathEntry> order = new ArrayList<>();
   private static final List<String> AUTOMATIC_PACKAGE_ROOT_SUFFIXES = new ArrayList<>();
   private static final Pattern schemeMatcher = Pattern.compile("^[a-zA-Z][a-zA-Z+\\-.]+:");

   ClasspathOrder(ScanSpec scanSpec, ReflectionUtils reflectionUtils) {
      this.scanSpec = scanSpec;
      this.reflectionUtils = reflectionUtils;
   }

   public List<ClasspathOrder.ClasspathEntry> getOrder() {
      return this.order;
   }

   public Set<String> getClasspathEntryUniqueResolvedPaths() {
      return this.classpathEntryUniqueResolvedPaths;
   }

   private boolean filter(URL classpathElementURL, String classpathElementPath) {
      if (this.scanSpec.classpathElementFilters != null) {
         for (Object filterObj : this.scanSpec.classpathElementFilters) {
            if (classpathElementURL != null
                  && filterObj instanceof ClassGraph.ClasspathElementURLFilter
                  && !((ClassGraph.ClasspathElementURLFilter)filterObj).includeClasspathElement(classpathElementURL)
               || classpathElementPath != null
                  && filterObj instanceof ClassGraph.ClasspathElementFilter
                  && !((ClassGraph.ClasspathElementFilter)filterObj).includeClasspathElement(classpathElementPath)) {
               return false;
            }
         }
      }

      return true;
   }

   boolean addSystemClasspathEntry(String pathEntry, ClassLoader classLoader) {
      if (this.classpathEntryUniqueResolvedPaths.add(pathEntry)) {
         this.order.add(new ClasspathOrder.ClasspathEntry(pathEntry, classLoader));
         return true;
      } else {
         return false;
      }
   }

   private boolean addClasspathEntry(Object pathElement, String pathElementStr, ClassLoader classLoader, ScanSpec scanSpec) {
      String pathElementStrWithoutSuffix = pathElementStr;
      boolean hasSuffix = false;

      for (String suffix : AUTOMATIC_PACKAGE_ROOT_SUFFIXES) {
         if (pathElementStr.endsWith(suffix)) {
            pathElementStrWithoutSuffix = pathElementStr.substring(0, pathElementStr.length() - suffix.length());
            hasSuffix = true;
            break;
         }
      }

      if (!(pathElement instanceof URL) && !(pathElement instanceof URI) && !(pathElement instanceof Path) && !(pathElement instanceof File)) {
         String pathElementStrResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStrWithoutSuffix);
         if (scanSpec.overrideClasspath == null
            && (SystemJarFinder.getJreLibOrExtJars().contains(pathElementStrResolved) || pathElementStrResolved.equals(SystemJarFinder.getJreRtJarPath()))) {
            return false;
         }

         if (this.classpathEntryUniqueResolvedPaths.add(pathElementStrResolved)) {
            this.order.add(new ClasspathOrder.ClasspathEntry(pathElementStrResolved, classLoader));
            return true;
         }
      } else {
         Object pathElementWithoutSuffix = pathElement;
         if (hasSuffix) {
            try {
               pathElementWithoutSuffix = pathElement instanceof URL
                  ? new URL(pathElementStrWithoutSuffix)
                  : (
                     pathElement instanceof URI
                        ? new URI(pathElementStrWithoutSuffix)
                        : (pathElement instanceof Path ? Paths.get(pathElementStrWithoutSuffix) : pathElementStrWithoutSuffix)
                  );
            } catch (URISyntaxException | InvalidPathException | MalformedURLException var11) {
               try {
                  pathElementWithoutSuffix = pathElement instanceof URL
                     ? new URL("file:" + pathElementStrWithoutSuffix)
                     : (pathElement instanceof URI ? new URI("file:" + pathElementStrWithoutSuffix) : pathElementStrWithoutSuffix);
               } catch (URISyntaxException | InvalidPathException | MalformedURLException var10) {
                  return false;
               }
            }
         }

         if (this.classpathEntryUniqueResolvedPaths.add(pathElementStrWithoutSuffix)) {
            this.order.add(new ClasspathOrder.ClasspathEntry(pathElementWithoutSuffix, classLoader));
            return true;
         }
      }

      return false;
   }

   public boolean addClasspathEntry(Object pathElement, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      if (pathElement == null) {
         return false;
      } else {
         String pathElementStr;
         if (pathElement instanceof Path) {
            try {
               pathElementStr = ((Path)pathElement).toUri().toString();
               if (pathElementStr.startsWith("file:///")) {
                  pathElementStr = ((Path)pathElement).toFile().toString();
               }
            } catch (SecurityException | IOError var24) {
               pathElementStr = pathElement.toString();
            }
         } else {
            pathElementStr = pathElement.toString();
         }

         pathElementStr = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStr);
         if (pathElementStr.isEmpty()) {
            return false;
         } else {
            URL pathElementURL = null;
            boolean hasWildcardSuffix = false;
            if (pathElementStr.endsWith("/*") || pathElementStr.endsWith("\\*")) {
               hasWildcardSuffix = true;
               pathElementStr = pathElementStr.substring(0, pathElementStr.length() - 2);
            } else if (pathElementStr.equals("*")) {
               hasWildcardSuffix = true;
               pathElementStr = "";
            } else {
               Matcher m1 = schemeMatcher.matcher(pathElementStr);
               if (m1.find()) {
                  try {
                     pathElementURL = pathElement instanceof URL
                        ? (URL)pathElement
                        : (
                           pathElement instanceof URI
                              ? ((URI)pathElement).toURL()
                              : (
                                 pathElement instanceof Path
                                    ? ((Path)pathElement).toUri().toURL()
                                    : (pathElement instanceof File ? ((File)pathElement).toURI().toURL() : null)
                              )
                        );
                  } catch (IllegalArgumentException | IOError | SecurityException | MalformedURLException var23) {
                  }

                  if (pathElementURL == null) {
                     String urlStr = pathElementStr.replace("%", "%25");

                     try {
                        pathElementURL = new URL(urlStr);
                     } catch (MalformedURLException var22) {
                        try {
                           pathElementURL = new File(urlStr).toURI().toURL();
                        } catch (IllegalArgumentException | IOError | SecurityException | MalformedURLException var21) {
                           try {
                              pathElementURL = new URL(pathElementStr);
                           } catch (MalformedURLException var20) {
                           }
                        }
                     }
                  }

                  if (pathElementURL == null && log != null) {
                     log.log("Failed to convert classpath element to URL: " + pathElement);
                  }
               }
            }

            if (pathElementURL == null && !(pathElement instanceof URI) && !(pathElement instanceof File) && !(pathElement instanceof Path)) {
               if (hasWildcardSuffix) {
                  String baseDirPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStr);
                  if (!this.filter(pathElementURL, pathElementStr)
                     || !baseDirPathResolved.equals(pathElementStr) && !this.filter(pathElementURL, baseDirPathResolved)) {
                     if (log != null) {
                        log.log("Classpath element did not match filter criterion, skipping: " + pathElementStr);
                     }

                     return false;
                  } else {
                     File baseDir = new File(baseDirPathResolved);
                     if (!baseDir.exists()) {
                        if (log != null) {
                           log.log("Directory does not exist for wildcard classpath element: " + pathElementStr);
                        }

                        return false;
                     } else if (!FileUtils.canRead(baseDir)) {
                        if (log != null) {
                           log.log("Cannot read directory for wildcard classpath element: " + pathElementStr);
                        }

                        return false;
                     } else if (!baseDir.isDirectory()) {
                        if (log != null) {
                           log.log("Wildcard is appended to something other than a directory: " + pathElementStr);
                        }

                        return false;
                     } else {
                        LogNode dirLog = log == null ? null : log.log("Adding classpath elements from wildcarded directory: " + pathElementStr);
                        File[] baseDirFiles = baseDir.listFiles();
                        if (baseDirFiles != null) {
                           for (File fileInDir : baseDirFiles) {
                              String name = fileInDir.getName();
                              if (!name.equals(".") && !name.equals("..")) {
                                 String fileInDirPath = fileInDir.getPath();
                                 String fileInDirPathResolved = FastPathResolver.resolve(FileUtils.currDirPath(), fileInDirPath);
                                 if (this.addClasspathEntry(fileInDirPathResolved, fileInDirPathResolved, classLoader, scanSpec)) {
                                    if (dirLog != null) {
                                       dirLog.log(
                                          "Found classpath element: "
                                             + fileInDirPath
                                             + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved)
                                       );
                                    }
                                 } else if (dirLog != null) {
                                    dirLog.log(
                                       "Ignoring duplicate classpath element: "
                                          + fileInDirPath
                                          + (fileInDirPath.equals(fileInDirPathResolved) ? "" : " -> " + fileInDirPathResolved)
                                    );
                                 }
                              }
                           }

                           return true;
                        } else {
                           return false;
                        }
                     }
                  }
               } else if (pathElementStr.indexOf(42) >= 0) {
                  if (log != null) {
                     log.log("Wildcard classpath elements can only end with a suffix of \"/*\", can't use globs elsewhere in the path: " + pathElementStr);
                  }

                  return false;
               } else {
                  String pathElementResolved = FastPathResolver.resolve(FileUtils.currDirPath(), pathElementStr);
                  if (this.filter(pathElementURL, pathElementStr)
                     && (pathElementResolved.equals(pathElementStr) || this.filter(pathElementURL, pathElementResolved))) {
                     if (pathElementResolved.startsWith("//")) {
                        try {
                           File file = new File(pathElementResolved);
                           if (this.addClasspathEntry(file, pathElementResolved, classLoader, scanSpec)) {
                              if (log != null) {
                                 log.log("Found classpath element: " + file + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved));
                              }

                              return true;
                           }

                           if (log != null) {
                              log.log(
                                 "Ignoring duplicate classpath element: "
                                    + pathElementStr
                                    + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved)
                              );
                           }

                           return false;
                        } catch (Exception var25) {
                        }
                     }

                     if (this.addClasspathEntry(pathElementResolved, pathElementResolved, classLoader, scanSpec)) {
                        if (log != null) {
                           log.log(
                              "Found classpath element: " + pathElementStr + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved)
                           );
                        }

                        return true;
                     } else {
                        if (log != null) {
                           log.log(
                              "Ignoring duplicate classpath element: "
                                 + pathElementStr
                                 + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved)
                           );
                        }

                        return false;
                     }
                  } else {
                     if (log != null) {
                        log.log(
                           "Classpath element did not match filter criterion, skipping: "
                              + pathElementStr
                              + (pathElementStr.equals(pathElementResolved) ? "" : " -> " + pathElementResolved)
                        );
                     }

                     return false;
                  }
               }
            } else if (!this.filter(pathElementURL, pathElementStr)) {
               if (log != null) {
                  log.log("Classpath element did not match filter criterion, skipping: " + pathElementStr);
               }

               return false;
            } else {
               Object classpathElementObj = pathElement instanceof File ? pathElementStr : (pathElementURL != null ? pathElementURL : pathElement);
               if (this.addClasspathEntry(classpathElementObj, pathElementStr, classLoader, scanSpec)) {
                  if (log != null) {
                     log.log("Found classpath element: " + pathElementStr);
                  }

                  return true;
               } else {
                  if (log != null) {
                     log.log("Ignoring duplicate classpath element: " + pathElementStr);
                  }

                  return false;
               }
            }
         }
      }
   }

   public boolean addClasspathEntries(List<Object> overrideClasspath, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      if (overrideClasspath != null && !overrideClasspath.isEmpty()) {
         for (Object pathElement : overrideClasspath) {
            this.addClasspathEntry(pathElement, classLoader, scanSpec, log);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean addClasspathPathStr(String pathStr, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      if (pathStr != null && !pathStr.isEmpty()) {
         String[] parts = JarUtils.smartPathSplit(pathStr, scanSpec);
         if (parts.length == 0) {
            return false;
         } else {
            for (String pathElement : parts) {
               this.addClasspathEntry(pathElement, classLoader, scanSpec, log);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean addClasspathEntryObject(Object pathObject, ClassLoader classLoader, ScanSpec scanSpec, LogNode log) {
      boolean valid = false;
      if (pathObject != null) {
         if (pathObject instanceof URL || pathObject instanceof URI || pathObject instanceof Path || pathObject instanceof File) {
            valid |= this.addClasspathEntry(pathObject, classLoader, scanSpec, log);
         } else if (pathObject instanceof Iterable) {
            for (Object elt : (Iterable)pathObject) {
               valid |= this.addClasspathEntryObject(elt, classLoader, scanSpec, log);
            }
         } else {
            Class<?> valClass = pathObject.getClass();
            if (valClass.isArray()) {
               int j = 0;

               for (int n = Array.getLength(pathObject); j < n; j++) {
                  Object elt = Array.get(pathObject, j);
                  valid |= this.addClasspathEntryObject(elt, classLoader, scanSpec, log);
               }
            } else {
               valid |= this.addClasspathPathStr(pathObject.toString(), classLoader, scanSpec, log);
            }
         }
      }

      return valid;
   }

   static {
      for (String prefix : ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES) {
         AUTOMATIC_PACKAGE_ROOT_SUFFIXES.add("!/" + prefix.substring(0, prefix.length() - 1));
      }
   }

   public static class ClasspathEntry {
      public final Object classpathEntryObj;
      public final ClassLoader classLoader;

      public ClasspathEntry(Object classpathEntryObj, ClassLoader classLoader) {
         this.classpathEntryObj = classpathEntryObj;
         this.classLoader = classLoader;
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.classpathEntryObj);
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (!(obj instanceof ClasspathOrder.ClasspathEntry)) {
            return false;
         } else {
            ClasspathOrder.ClasspathEntry other = (ClasspathOrder.ClasspathEntry)obj;
            return Objects.equals(this.classpathEntryObj, other.classpathEntryObj);
         }
      }

      @Override
      public String toString() {
         return this.classpathEntryObj + " [" + this.classLoader + "]";
      }
   }
}
