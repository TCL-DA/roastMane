package nonapi.io.github.classgraph.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;

public final class FileUtils {
   private static Method directByteBufferCleanerMethod;
   private static Method cleanerCleanMethod;
   private static Method attachmentMethod;
   private static Object theUnsafe;
   private static AtomicBoolean initialized = new AtomicBoolean();
   private static String currDirPath;
   public static final int MAX_BUFFER_SIZE = 2147483639;

   private FileUtils() {
   }

   public static String currDirPath() {
      if (currDirPath == null) {
         Path path = null;
         String currDirPathStr = System.getProperty("user.dir");
         if (currDirPathStr != null) {
            try {
               path = Paths.get(currDirPathStr);
            } catch (InvalidPathException var4) {
            }
         }

         if (path == null) {
            try {
               path = Paths.get("");
            } catch (InvalidPathException var3) {
            }
         }

         currDirPath = FastPathResolver.resolve(path == null ? "" : path.toString());
      }

      return currDirPath;
   }

   public static String sanitizeEntryPath(String path, boolean removeInitialSlash, boolean removeFinalSlash) {
      if (path.isEmpty()) {
         return "";
      } else {
         boolean foundSegmentToSanitize = false;
         int pathLen = path.length();
         char[] pathChars = new char[pathLen];
         path.getChars(0, pathLen, pathChars, 0);
         int lastSepIdx = -1;
         char prevC = 0;
         int i = 0;

         for (int ii = pathLen + 1; i < ii; i++) {
            char c = i == pathLen ? 0 : pathChars[i];
            if (c == '/' || c == '!' || c == 0) {
               int segmentLength = i - (lastSepIdx + 1);
               if (segmentLength == 0 && prevC == c
                  || segmentLength == 1 && pathChars[i - 1] == '.'
                  || segmentLength == 2 && pathChars[i - 2] == '.' && pathChars[i - 1] == '.') {
                  foundSegmentToSanitize = true;
               }

               lastSepIdx = i;
            }

            prevC = c;
         }

         boolean pathHasInitialSlash = pathChars[0] == '/';
         boolean pathHasInitialSlashSlash = pathHasInitialSlash && pathLen > 1 && pathChars[1] == '/';
         StringBuilder pathSanitized = new StringBuilder(pathLen + 16);
         if (foundSegmentToSanitize) {
            List<List<CharSequence>> allSectionSegments = new ArrayList<>();
            List<CharSequence> currSectionSegments = new ArrayList<>();
            allSectionSegments.add(currSectionSegments);
            int lastSepIdxx = -1;

            for (int ix = 0; ix < pathLen + 1; ix++) {
               char c = ix == pathLen ? 0 : pathChars[ix];
               if (c == '/' || c == '!' || c == 0) {
                  int segmentStartIdx = lastSepIdxx + 1;
                  int segmentLen = ix - segmentStartIdx;
                  if (segmentLen != 0 && (segmentLen != 1 || pathChars[segmentStartIdx] != '.')) {
                     if (segmentLen != 2 || pathChars[segmentStartIdx] != '.' || pathChars[segmentStartIdx + 1] != '.') {
                        currSectionSegments.add(path.subSequence(segmentStartIdx, segmentStartIdx + segmentLen));
                     } else if (!currSectionSegments.isEmpty()) {
                        currSectionSegments.remove(currSectionSegments.size() - 1);
                     }
                  }

                  if (c == '!' && !currSectionSegments.isEmpty()) {
                     currSectionSegments = new ArrayList<>();
                     allSectionSegments.add(currSectionSegments);
                  }

                  lastSepIdxx = ix;
               }
            }

            for (List<CharSequence> sectionSegments : allSectionSegments) {
               if (!sectionSegments.isEmpty()) {
                  if (pathSanitized.length() > 0) {
                     pathSanitized.append('!');
                  }

                  for (CharSequence sectionSegment : sectionSegments) {
                     pathSanitized.append('/');
                     pathSanitized.append(sectionSegment);
                  }
               }
            }

            if (pathSanitized.length() == 0 && pathHasInitialSlash) {
               pathSanitized.append('/');
            }
         } else {
            pathSanitized.append(path);
         }

         if (VersionFinder.OS == VersionFinder.OperatingSystem.Windows && pathHasInitialSlashSlash) {
            pathSanitized.insert(0, '/');
         }

         int startIdx = 0;
         if (removeInitialSlash || !pathHasInitialSlash) {
            while (startIdx < pathSanitized.length() && pathSanitized.charAt(startIdx) == '/') {
               startIdx++;
            }
         }

         if (removeFinalSlash) {
            while (pathSanitized.length() > 0 && pathSanitized.charAt(pathSanitized.length() - 1) == '/') {
               pathSanitized.setLength(pathSanitized.length() - 1);
            }
         }

         return pathSanitized.substring(startIdx);
      }
   }

   public static boolean isClassfile(String path) {
      int len = path.length();
      return len > 6 && path.regionMatches(true, len - 6, ".class", 0, 6);
   }

   public static boolean canRead(File file) {
      try {
         return file.canRead();
      } catch (SecurityException var2) {
         return false;
      }
   }

   public static boolean canRead(Path path) {
      try {
         if (!Files.isReadable(path)) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return Files.isRegularFile(path);
   }

   public static boolean canReadAndIsFile(File file) {
      try {
         if (!file.canRead()) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return file.isFile();
   }

   public static boolean canReadAndIsFile(Path path) {
      try {
         if (!Files.isReadable(path)) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return Files.isRegularFile(path);
   }

   public static void checkCanReadAndIsFile(File file) throws IOException {
      try {
         if (!file.canRead()) {
            throw new FileNotFoundException("File does not exist or cannot be read: " + file);
         }
      } catch (SecurityException var2) {
         throw new FileNotFoundException("File " + file + " cannot be accessed: " + var2);
      }

      if (!file.isFile()) {
         throw new IOException("Not a regular file: " + file);
      }
   }

   public static void checkCanReadAndIsFile(Path path) throws IOException {
      try {
         if (!Files.isReadable(path)) {
            throw new FileNotFoundException("Path does not exist or cannot be read: " + path);
         }
      } catch (SecurityException var2) {
         throw new FileNotFoundException("Path " + path + " cannot be accessed: " + var2);
      }

      if (!Files.isRegularFile(path)) {
         throw new IOException("Not a regular file: " + path);
      }
   }

   public static boolean canReadAndIsDir(File file) {
      try {
         if (!file.canRead()) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return file.isDirectory();
   }

   public static boolean canReadAndIsDir(Path path) {
      try {
         if (!Files.isReadable(path)) {
            return false;
         }
      } catch (SecurityException var2) {
         return false;
      }

      return Files.isDirectory(path);
   }

   public static void checkCanReadAndIsDir(File file) throws IOException {
      try {
         if (!file.canRead()) {
            throw new FileNotFoundException("Directory does not exist or cannot be read: " + file);
         }
      } catch (SecurityException var2) {
         throw new FileNotFoundException("File " + file + " cannot be accessed: " + var2);
      }

      if (!file.isDirectory()) {
         throw new IOException("Not a directory: " + file);
      }
   }

   public static String getParentDirPath(String path, char separator) {
      int lastSlashIdx = path.lastIndexOf(separator);
      return lastSlashIdx <= 0 ? "" : path.substring(0, lastSlashIdx);
   }

   public static String getParentDirPath(String path) {
      return getParentDirPath(path, '/');
   }

   private static void lookupCleanMethodPrivileged() {
      if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
         try {
            cleanerCleanMethod = Class.forName("sun.misc.Cleaner").getDeclaredMethod("clean");
            cleanerCleanMethod.setAccessible(true);
            Class<?> directByteBufferClass = Class.forName("sun.nio.ch.DirectBuffer");
            directByteBufferCleanerMethod = directByteBufferClass.getDeclaredMethod("cleaner");
            attachmentMethod = directByteBufferClass.getMethod("attachment");
            attachmentMethod.setAccessible(true);
         } catch (SecurityException var5) {
            throw new RuntimeException(
               "You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\") and ReflectPermission(\"suppressAccessChecks\")", var5
            );
         } catch (LinkageError | ReflectiveOperationException var6) {
         }
      } else {
         try {
            Class<?> unsafeClass;
            try {
               unsafeClass = Class.forName("sun.misc.Unsafe");
            } catch (LinkageError | ReflectiveOperationException var2) {
               throw new RuntimeException("Could not get class sun.misc.Unsafe", var2);
            }

            Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            theUnsafe = theUnsafeField.get(null);
            cleanerCleanMethod = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
            cleanerCleanMethod.setAccessible(true);
         } catch (SecurityException var3) {
            throw new RuntimeException(
               "You need to grant classgraph RuntimePermission(\"accessClassInPackage.sun.misc\") and ReflectPermission(\"suppressAccessChecks\")", var3
            );
         } catch (LinkageError | ReflectiveOperationException var4) {
         }
      }
   }

   private static boolean closeDirectByteBufferPrivileged(ByteBuffer byteBuffer, LogNode log) {
      if (!byteBuffer.isDirect()) {
         return true;
      } else {
         try {
            if (VersionFinder.JAVA_MAJOR_VERSION >= 9) {
               if (theUnsafe == null) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, theUnsafe == null");
                  }

                  return false;
               } else if (cleanerCleanMethod == null) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleanMethod == null");
                  }

                  return false;
               } else {
                  try {
                     cleanerCleanMethod.invoke(theUnsafe, byteBuffer);
                     return true;
                  } catch (IllegalArgumentException var4) {
                     return false;
                  }
               }
            } else if (attachmentMethod == null) {
               if (log != null) {
                  log.log("Could not unmap ByteBuffer, attachmentMethod == null");
               }

               return false;
            } else if (attachmentMethod.invoke(byteBuffer) != null) {
               return false;
            } else if (directByteBufferCleanerMethod == null) {
               if (log != null) {
                  log.log("Could not unmap ByteBuffer, cleanerMethod == null");
               }

               return false;
            } else {
               try {
                  directByteBufferCleanerMethod.setAccessible(true);
               } catch (Exception var6) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleanerMethod.setAccessible(true) failed");
                  }

                  return false;
               }

               Object cleanerInstance = directByteBufferCleanerMethod.invoke(byteBuffer);
               if (cleanerInstance == null) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleaner == null");
                  }

                  return false;
               } else if (cleanerCleanMethod == null) {
                  if (log != null) {
                     log.log("Could not unmap ByteBuffer, cleanMethod == null");
                  }

                  return false;
               } else {
                  try {
                     cleanerCleanMethod.invoke(cleanerInstance);
                     return true;
                  } catch (Exception var5) {
                     if (log != null) {
                        log.log("Could not unmap ByteBuffer, cleanMethod.invoke(cleaner) failed: " + var5);
                     }

                     return false;
                  }
               }
            }
         } catch (SecurityException | ReflectiveOperationException var7) {
            if (log != null) {
               log.log("Could not unmap ByteBuffer: " + var7);
            }

            return false;
         }
      }
   }

   public static boolean closeDirectByteBuffer(final ByteBuffer byteBuffer, ReflectionUtils reflectionUtils, final LogNode log) {
      if (byteBuffer != null && byteBuffer.isDirect()) {
         if (!initialized.get()) {
            try {
               reflectionUtils.doPrivileged(new Callable<Void>() {
                  public Void call() throws Exception {
                     FileUtils.lookupCleanMethodPrivileged();
                     return null;
                  }
               });
            } catch (Throwable var5) {
               throw new RuntimeException("Cannot get buffer cleaner method", var5);
            }

            initialized.set(true);
         }

         try {
            return reflectionUtils.doPrivileged(new Callable<Boolean>() {
               public Boolean call() throws Exception {
                  return FileUtils.closeDirectByteBufferPrivileged(byteBuffer, log);
               }
            });
         } catch (Throwable var4) {
            return false;
         }
      } else {
         return false;
      }
   }
}
