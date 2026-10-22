package com.sun.glass.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.StackWalker.Option;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class NativeLibLoader {
   private static final HashSet<String> loaded = new HashSet<>();
   private static boolean verbose = false;
   private static boolean usingModules = false;
   private static File libDir = null;
   private static String libPrefix = "";
   private static String libSuffix = "";

   public static synchronized void loadLibrary(String var0) {
      if (!loaded.contains(var0)) {
         StackWalker var1 = AccessController.doPrivileged((PrivilegedAction<StackWalker>)(() -> StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)));
         Class var2 = var1.getCallerClass();
         loadLibraryInternal(var0, null, var2);
         loaded.add(var0);
      }
   }

   public static synchronized void loadLibrary(String var0, List<String> var1) {
      if (!loaded.contains(var0)) {
         StackWalker var2 = AccessController.doPrivileged((PrivilegedAction<StackWalker>)(() -> StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)));
         Class var3 = var2.getCallerClass();
         loadLibraryInternal(var0, var1, var3);
         loaded.add(var0);
      }
   }

   private static String[] initializePath(String var0) {
      String var1 = System.getProperty(var0, "");
      String var2 = File.pathSeparator;
      int var3 = var1.length();
      int var4 = var1.indexOf(var2);

      int var6;
      for (var6 = 0; var4 >= 0; var4 = var1.indexOf(var2, var4 + 1)) {
         var6++;
      }

      String[] var7 = new String[var6 + 1];
      var4 = 0;
      var6 = 0;

      for (int var5 = var1.indexOf(var2); var5 >= 0; var5 = var1.indexOf(var2, var4)) {
         if (var5 - var4 > 0) {
            var7[var6++] = var1.substring(var4, var5);
         } else if (var5 - var4 == 0) {
            var7[var6++] = ".";
         }

         var4 = var5 + 1;
      }

      var7[var6] = var1.substring(var4, var3);
      return var7;
   }

   private static void loadLibraryInternal(String var0, List<String> var1, Class var2) {
      try {
         loadLibraryFullPath(var0);
      } catch (UnsatisfiedLinkError var12) {
         if (verbose && !usingModules) {
            System.err.println("WARNING: " + var12);
         }

         if (loadLibraryFromResource(var0, var1, var2)) {
            return;
         }

         String[] var4 = initializePath("java.library.path");

         for (int var5 = 0; var5 < var4.length; var5++) {
            try {
               String var6 = var4[var5];
               if (!var6.endsWith(File.separator)) {
                  var6 = var6 + File.separator;
               }

               String var7 = System.mapLibraryName(var0);
               File var8 = new File(var6 + var7);
               System.load(var8.getAbsolutePath());
               if (verbose) {
                  System.err.println("Loaded " + var8.getAbsolutePath() + " from java.library.path");
               }

               return;
            } catch (UnsatisfiedLinkError var11) {
            }
         }

         try {
            System.loadLibrary(var0);
            if (verbose) {
               System.err.println("System.loadLibrary(" + var0 + ") succeeded");
            }
         } catch (UnsatisfiedLinkError var10) {
            if ("ios".equals(System.getProperty("os.name").toLowerCase(Locale.ROOT)) && var0.contains("-")) {
               var0 = var0.replace("-", "_");

               try {
                  System.loadLibrary(var0);
                  return;
               } catch (UnsatisfiedLinkError var9) {
                  throw var9;
               }
            }

            throw var10;
         }
      }
   }

   private static boolean loadLibraryFromResource(String var0, List<String> var1, Class var2) {
      return installLibraryFromResource(var0, var1, var2, true);
   }

   private static boolean installLibraryFromResource(String var0, List<String> var1, Class var2, boolean var3) {
      try {
         if (var1 != null) {
            for (String var5 : var1) {
               boolean var6 = installLibraryFromResource(var5, null, var2, false);
            }
         }

         String var8 = "/" + System.mapLibraryName(var0);
         InputStream var9 = var2.getResourceAsStream(var8);
         if (var9 != null) {
            String var10 = cacheLibrary(var9, var8, var2);
            if (var3) {
               System.load(var10);
               if (verbose) {
                  System.err.println("Loaded library " + var8 + " from resource");
               }
            } else if (verbose) {
               System.err.println("Unpacked library " + var8 + " from resource");
            }

            return true;
         }
      } catch (Throwable var7) {
         System.err.println("Loading library " + var0 + " from resource failed: " + var7);
         var7.printStackTrace();
      }

      return false;
   }

   private static String cacheLibrary(InputStream var0, String var1, Class var2) throws IOException {
      String var3 = System.getProperty("javafx.version", "versionless");
      String var4 = System.getProperty("javafx.cachedir", "");
      if (var4.isEmpty()) {
         var4 = System.getProperty("user.home") + "/.openjfx/cache/" + var3;
      }

      File var5 = new File(var4);
      boolean var6 = true;
      if (var5.exists()) {
         if (!var5.isDirectory()) {
            System.err.println("Cache exists but is not a directory: " + var5);
            var6 = false;
         }
      } else if (!var5.mkdirs()) {
         System.err.println("Can not create cache at " + var5);
         var6 = false;
      }

      if (!var5.canRead()) {
         var6 = false;
      }

      if (!var6) {
         String var7 = System.getProperty("user.name", "anonymous");
         String var8 = System.getProperty("java.io.tmpdir") + "/.openjfx_" + var7 + "/cache/" + var3;
         var5 = new File(var8);
         if (var5.exists()) {
            if (!var5.isDirectory()) {
               throw new IOException("Cache exists but is not a directory: " + var5);
            }
         } else if (!var5.mkdirs()) {
            throw new IOException("Can not create cache at " + var5);
         }
      }

      File var14 = new File(var5, var1);
      boolean var15 = true;
      if (var14.exists()) {
         byte[] var9;
         try {
            DigestInputStream var11 = new DigestInputStream(var0, MessageDigest.getInstance("MD5"));
            var11.getMessageDigest().reset();
            byte[] var12 = new byte[4096];

            while (var11.read(var12) != -1) {
            }

            var9 = var11.getMessageDigest().digest();
            var0.close();
            var0 = var2.getResourceAsStream(var1);
         } catch (NoSuchAlgorithmException var13) {
            var9 = new byte[1];
         }

         byte[] var10 = calculateCheckSum(var14);
         if (!Arrays.equals(var9, var10)) {
            Files.delete(var14.toPath());
         } else {
            var15 = false;
         }
      }

      if (var15) {
         Path var16 = var14.toPath();
         Files.copy(var0, var16);
      }

      return var14.getAbsolutePath();
   }

   static byte[] calculateCheckSum(File var0) {
      try {
         byte[] var4;
         try (
            FileInputStream var1 = new FileInputStream(var0);
            DigestInputStream var2 = new DigestInputStream(var1, MessageDigest.getInstance("MD5"));
         ) {
            var2.getMessageDigest().reset();
            byte[] var3 = new byte[4096];

            while (var2.read(var3) != -1) {
            }

            var4 = var2.getMessageDigest().digest();
         }

         return var4;
      } catch (NoSuchAlgorithmException | IOException | SecurityException | IllegalArgumentException var9) {
         return new byte[0];
      }
   }

   private static void loadLibraryFullPath(String var0) {
      try {
         if (usingModules) {
            throw new UnsatisfiedLinkError("ignored");
         } else {
            if (libDir == null) {
               String var1 = "NativeLibLoader.class";
               Class<NativeLibLoader> var2 = NativeLibLoader.class;
               String var3 = var2.getResource(var1).toString();
               if (var3.startsWith("jrt:")) {
                  usingModules = true;
                  throw new UnsatisfiedLinkError("ignored");
               }

               if (!var3.startsWith("jar:file:") || var3.indexOf(33) == -1) {
                  throw new UnsatisfiedLinkError("Invalid URL for class: " + var3);
               }

               String var4 = var3.substring(4, var3.lastIndexOf(33));
               int var5 = Math.max(var4.lastIndexOf(47), var4.lastIndexOf(92));
               String var6 = System.getProperty("os.name");
               String var7 = null;
               if (var6.startsWith("Windows")) {
                  var7 = "../bin";
               } else if (var6.startsWith("Mac")) {
                  var7 = ".";
               } else if (var6.startsWith("Linux")) {
                  var7 = ".";
               }

               String var8 = var4.substring(0, var5) + "/" + var7;
               libDir = new File(new URI(var8).getPath());
               if (var6.startsWith("Windows")) {
                  libPrefix = "";
                  libSuffix = ".dll";
               } else if (var6.startsWith("Mac")) {
                  libPrefix = "lib";
                  libSuffix = ".dylib";
               } else if (var6.startsWith("Linux")) {
                  libPrefix = "lib";
                  libSuffix = ".so";
               }
            }

            File var11 = new File(libDir, libPrefix + var0 + libSuffix);
            String var12 = var11.getCanonicalPath();

            try {
               System.load(var12);
               if (verbose) {
                  System.err.println("Loaded " + var11.getAbsolutePath() + " from relative path");
               }
            } catch (UnsatisfiedLinkError var9) {
               throw var9;
            }
         }
      } catch (Exception var10) {
         throw (UnsatisfiedLinkError)new UnsatisfiedLinkError().initCause(var10);
      }
   }

   static {
      Object var0 = AccessController.doPrivileged((PrivilegedAction)(() -> {
         verbose = Boolean.getBoolean("javafx.verbose");
         return null;
      }));
   }
}
