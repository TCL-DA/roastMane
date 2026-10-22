package org.eclipse.jetty.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileID {
   private static final Pattern NUMBER = Pattern.compile("[0-9]+");

   public static String getBasename(Path path) {
      Path filename = path.getFileName();
      if (filename == null) {
         return "";
      } else {
         String basename = filename.toString();
         int dot = basename.lastIndexOf(46);
         if (dot >= 0) {
            basename = basename.substring(0, dot);
         }

         return basename;
      }
   }

   public static String getFileName(URI uri) {
      if (uri == null) {
         return "";
      } else {
         return uri.isOpaque() ? getFileName(uri.getSchemeSpecificPart()) : getFileName(uri.getPath());
      }
   }

   public static String getFileName(String path) {
      if (path != null && !"/".equals(path)) {
         int idx = path.lastIndexOf(47);
         if (idx >= 0) {
            if (idx == path.length() - 1) {
               int previousSlash = path.lastIndexOf(47, idx - 1);
               return previousSlash >= 0 ? path.substring(previousSlash + 1, idx) : path.substring(0, idx);
            } else {
               return path.substring(idx + 1);
            }
         } else {
            return path;
         }
      } else {
         return "";
      }
   }

   public static String getExtension(URI uri) {
      if (uri == null) {
         return null;
      } else if (uri.getScheme() == null) {
         return null;
      } else {
         String path = null;
         if (uri.getScheme().equalsIgnoreCase("jar")) {
            URI sspUri = URI.create(uri.getRawSchemeSpecificPart());
            if (!sspUri.getScheme().equalsIgnoreCase("file")) {
               return null;
            }

            path = sspUri.getPath();
         } else {
            path = uri.getPath();
         }

         int jarEnd = path.indexOf("!/");
         return jarEnd >= 0 ? getExtension(path.substring(0, jarEnd)) : getExtension(path);
      }
   }

   public static String getExtension(Path path) {
      if (path == null) {
         return null;
      } else {
         return !Files.isRegularFile(path) ? null : getExtension(path.getFileName().toString());
      }
   }

   public static String getExtension(String filename) {
      if (filename == null) {
         return null;
      } else if (!filename.endsWith("/") && !filename.endsWith("\\")) {
         int lastSlash = filename.lastIndexOf(File.separator);
         if (lastSlash >= 0) {
            filename = filename.substring(lastSlash + 1);
         }

         int lastDot = filename.lastIndexOf(46);
         return lastDot <= 0 ? null : filename.substring(lastDot + 1).toLowerCase(Locale.ENGLISH);
      } else {
         return null;
      }
   }

   public static boolean isExtension(Path path, String... extensions) {
      return matchesExtension(getExtension(path), extensions);
   }

   public static boolean isExtension(URI uri, String... extensions) {
      return matchesExtension(getExtension(uri), extensions);
   }

   public static boolean isExtension(String filename, String... extensions) {
      return matchesExtension(getExtension(filename), extensions);
   }

   private static boolean matchesExtension(String ext, String... extensions) {
      if (ext == null) {
         return false;
      } else {
         for (String extension : extensions) {
            if (ext.equals(extension)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean hasNamedPathSegment(Path path, String segmentName) {
      if (path == null) {
         return false;
      } else {
         int segmentCount = path.getNameCount();

         for (int i = segmentCount - 1; i >= 0; i--) {
            Path segment = path.getName(i);
            if (segment.getFileName().toString().equalsIgnoreCase(segmentName)) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isArchive(Path path) {
      return isExtension(path, "jar", "war", "zip");
   }

   public static boolean isArchive(String filename) {
      return isExtension(filename, "jar", "war", "zip");
   }

   public static boolean isArchive(URI uri) {
      return isExtension(uri, "jar", "war", "zip");
   }

   public static boolean isLibArchive(Path path) {
      return isExtension(path, "jar", "zip");
   }

   public static boolean isLibArchive(String filename) {
      return isExtension(filename, "jar", "zip");
   }

   public static boolean isLibArchive(URI uri) {
      return isExtension(uri, "jar", "zip");
   }

   public static boolean isClassFile(Path path) {
      Path fileNamePath = path.getFileName();
      if (fileNamePath == null) {
         return false;
      } else {
         String filename = fileNamePath.toString();
         if (!StringUtil.asciiEndsWithIgnoreCase(filename, ".class")) {
            return false;
         } else {
            int start = 0;
            int end = filename.length() - 6;
            if (end <= start) {
               return false;
            } else if (!Character.isJavaIdentifierStart(filename.charAt(0))) {
               return false;
            } else {
               for (int i = start + 1; i < end; i++) {
                  if (!Character.isJavaIdentifierPart(filename.codePointAt(i))) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public static boolean isHidden(Path path) {
      int count = path.getNameCount();

      for (int i = 0; i < count; i++) {
         Path segment = path.getName(i);
         String segmentName = segment.toString();
         if (!segmentName.isBlank()) {
            if (segmentName.charAt(0) == '.') {
               return true;
            }

            try {
               if (Files.isHidden(segment)) {
                  return true;
               }
            } catch (IOException var6) {
            }
         }
      }

      return false;
   }

   public static boolean isHidden(Path base, Path path) {
      return isHidden(base.relativize(path));
   }

   public static boolean isJavaArchive(URI uri) {
      return isExtension(uri, "jar");
   }

   public static boolean isJavaArchive(Path path) {
      return isExtension(path, "jar");
   }

   public static boolean isJavaArchive(String filename) {
      return isExtension(filename, "jar");
   }

   public static boolean isMetaInfVersions(Path path) {
      if (path.getNameCount() < 3) {
         return false;
      } else if (!StringUtil.asciiEqualsIgnoreCase("META-INF", path.getName(0).toString())) {
         return false;
      } else {
         return !StringUtil.asciiEqualsIgnoreCase("versions", path.getName(1).toString())
            ? false
            : NUMBER.matcher(path.getName(2).getFileName().toString()).matches();
      }
   }

   public static boolean isNotMetaInfVersions(Path path) {
      return !isMetaInfVersions(path);
   }

   public static boolean isModuleInfoClass(Path path) {
      Path filenameSegment = path.getFileName();
      return filenameSegment == null ? false : filenameSegment.toString().equalsIgnoreCase("module-info.class");
   }

   public static boolean isNotModuleInfoClass(Path path) {
      return !isModuleInfoClass(path);
   }

   public static boolean isTld(Path path) {
      if (path == null) {
         return false;
      } else {
         return !hasNamedPathSegment(path, "META-INF") ? false : isExtension(path, "tld");
      }
   }

   public static boolean isWebArchive(Path path) {
      return isExtension(path, "war");
   }

   public static boolean isWebArchive(URI uri) {
      return isExtension(uri, "war");
   }

   public static boolean isWebArchive(String filename) {
      return isExtension(filename, "war");
   }

   public static boolean isXml(Path path) {
      return isExtension(path, "xml");
   }

   public static boolean isXml(String filename) {
      return isExtension(filename, "xml");
   }
}
