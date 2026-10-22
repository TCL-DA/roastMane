package com.sun.javafx.application;

import com.sun.javafx.stage.StageHelper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.stage.Stage;

public class LauncherImpl {
   public static final String LAUNCH_MODE_CLASS = "LM_CLASS";
   public static final String LAUNCH_MODE_JAR = "LM_JAR";
   public static final String LAUNCH_MODE_MODULE = "LM_MODULE";
   private static final boolean trace = false;
   private static final boolean verbose = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.verbose")));
   private static final String MF_MAIN_CLASS = "Main-Class";
   private static final String MF_JAVAFX_MAIN = "JavaFX-Application-Class";
   private static final String MF_JAVAFX_PRELOADER = "JavaFX-Preloader-Class";
   private static final String MF_JAVAFX_CLASS_PATH = "JavaFX-Class-Path";
   private static final String MF_JAVAFX_ARGUMENT_PREFIX = "JavaFX-Argument-";
   private static final String MF_JAVAFX_PARAMETER_NAME_PREFIX = "JavaFX-Parameter-Name-";
   private static final String MF_JAVAFX_PARAMETER_VALUE_PREFIX = "JavaFX-Parameter-Value-";
   private static final boolean simulateSlowProgress = false;
   private static AtomicBoolean launchCalled = new AtomicBoolean(false);
   private static final AtomicBoolean toolkitStarted = new AtomicBoolean(false);
   private static volatile RuntimeException launchException = null;
   private static Preloader currentPreloader = null;
   private static Class<? extends Preloader> savedPreloaderClass = null;
   private static ClassLoader savedMainCcl = null;
   private static volatile boolean error = false;
   private static volatile Throwable pConstructorError = null;
   private static volatile Throwable pInitError = null;
   private static volatile Throwable pStartError = null;
   private static volatile Throwable pStopError = null;
   private static volatile Throwable constructorError = null;
   private static volatile Throwable initError = null;
   private static volatile Throwable startError = null;
   private static volatile Throwable stopError = null;

   public static void launchApplication(Class<? extends Application> var0, String[] var1) {
      Class var2 = savedPreloaderClass;
      if (var2 == null) {
         String var3 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("javafx.preloader")));
         if (var3 != null) {
            try {
               var2 = Class.forName(var3, false, var0.getClassLoader());
            } catch (Exception var5) {
               System.err.printf("Could not load preloader class '" + var3 + "', continuing without preloader.");
               var5.printStackTrace();
            }
         }
      }

      launchApplication(var0, var2, var1);
   }

   public static void launchApplication(Class<? extends Application> var0, Class<? extends Preloader> var1, String[] var2) {
      if (com.sun.glass.ui.Application.isEventThread()) {
         throw new IllegalStateException("Application launch must not be called on the JavaFX Application Thread");
      } else if (launchCalled.getAndSet(true)) {
         throw new IllegalStateException("Application launch must not be called more than once");
      } else if (!Application.class.isAssignableFrom(var0)) {
         throw new IllegalArgumentException("Error: " + var0.getName() + " is not a subclass of javafx.application.Application");
      } else if (var1 != null && !Preloader.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Error: " + var1.getName() + " is not a subclass of javafx.application.Preloader");
      } else {
         CountDownLatch var3 = new CountDownLatch(1);
         Thread var4 = new Thread(() -> {
            try {
               launchApplication1(var0, var1, var2);
            } catch (RuntimeException var10) {
               launchException = var10;
            } catch (Exception var11) {
               launchException = new RuntimeException("Application launch exception", var11);
            } catch (Error var12) {
               launchException = new RuntimeException("Application launch error", var12);
            } finally {
               var3.countDown();
            }
         });
         var4.setName("JavaFX-Launcher");
         var4.start();

         try {
            var3.await();
         } catch (InterruptedException var6) {
            throw new RuntimeException("Unexpected exception: ", var6);
         }

         if (launchException != null) {
            throw launchException;
         }
      }
   }

   public static void launchApplication(String var0, String var1, String[] var2) {
      if (verbose) {
         System.err.println("JavaFX launchApplication method: launchMode=" + var1);
      }

      String var3 = null;
      String var4 = null;
      String[] var5 = var2;
      ClassLoader var6 = null;
      ModuleAccess var7 = null;
      if (var1.equals("LM_JAR")) {
         Attributes var8 = getJarAttributes(var0);
         if (var8 == null) {
            abort(null, "Can't get manifest attributes from jar");
         }

         String var9 = var8.getValue("JavaFX-Class-Path");
         if (var9 != null) {
            if (var9.trim().length() == 0) {
               var9 = null;
            } else {
               if (verbose) {
                  System.err.println("WARNING: Application jar uses deprecated JavaFX-Class-Path attribute. Please use Class-Path instead.");
               }

               var6 = setupJavaFXClassLoader(new File(var0), var9);
            }
         }

         if (var2.length == 0) {
            var5 = getAppArguments(var8);
         }

         var3 = var8.getValue("JavaFX-Application-Class");
         if (var3 == null) {
            var3 = var8.getValue("Main-Class");
            if (var3 == null) {
               abort(null, "JavaFX jar manifest requires a valid JavaFX-Appliation-Class or Main-Class entry");
            }
         }

         var3 = var3.trim();
         var4 = var8.getValue("JavaFX-Preloader-Class");
         if (var4 != null) {
            var4 = var4.trim();
         }
      } else if (var1.equals("LM_CLASS")) {
         var3 = var0;
      } else if (var1.equals("LM_MODULE")) {
         int var13 = var0.indexOf(47);
         String var16;
         if (var13 == -1) {
            var16 = var0;
            var3 = null;
         } else {
            var16 = var0.substring(0, var13);
            var3 = var0.substring(var13 + 1);
         }

         var7 = ModuleAccess.load(var16);
         if (var3 == null) {
            Optional var10 = var7.getDescriptor().mainClass();
            if (!var10.isPresent()) {
               abort(null, "Module %1$s does not have a MainClass attribute, use -m <module>/<main-class>", var16);
            }

            var3 = (String)var10.get();
         }
      } else {
         abort(new IllegalArgumentException("The launchMode argument must be one of LM_CLASS, LM_JAR or LM_MODULE"), "Invalid launch mode: %1$s", var1);
      }

      if (var4 == null) {
         var4 = System.getProperty("javafx.preloader");
      }

      if (var3 == null) {
         abort(null, "No main JavaFX class to launch");
      }

      if (var6 != null) {
         try {
            Class var14 = var6.loadClass(LauncherImpl.class.getName());
            Method var17 = var14.getMethod("launchApplicationWithArgs", ModuleAccess.class, String.class, String.class, new String[0].getClass());
            Thread.currentThread().setContextClassLoader(var6);
            var17.invoke(null, null, var3, var4, var5);
         } catch (Exception var11) {
            abort(var11, "Exception while launching application");
         }
      } else {
         launchApplicationWithArgs(var7, var3, var4, var5);
      }
   }

   private static Class<?> loadClass(ModuleAccess var0, String var1) {
      Class var2 = null;
      ClassLoader var3 = Thread.currentThread().getContextClassLoader();
      if (var0 != null) {
         var2 = var0.classForName(var1);
      } else {
         try {
            var2 = Class.forName(var1, true, var3);
         } catch (NoClassDefFoundError | ClassNotFoundException var7) {
         }
      }

      if (var2 == null && System.getProperty("os.name", "").contains("OS X") && Normalizer.isNormalized(var1, Form.NFD)) {
         String var4 = Normalizer.normalize(var1, Form.NFC);
         if (var0 != null) {
            var2 = var0.classForName(var4);
         } else {
            try {
               var2 = Class.forName(var4, true, var3);
            } catch (NoClassDefFoundError | ClassNotFoundException var6) {
            }
         }
      }

      return var2;
   }

   public static void launchApplicationWithArgs(ModuleAccess var0, String var1, String var2, String[] var3) {
      try {
         startToolkit();
      } catch (InterruptedException var11) {
         abort(var11, "Toolkit initialization error", var1);
      }

      Object var5 = null;
      Class var6 = null;
      AtomicReference var7 = new AtomicReference();
      AtomicReference var8 = new AtomicReference();
      PlatformImpl.runAndWait(() -> {
         Class var5x = loadClass(var0, var1);
         if (var5x == null) {
            if (var0 != null) {
               abort(null, "Missing JavaFX application class %1$s in module %2$s", var1, var0.getName());
            } else {
               abort(null, "Missing JavaFX application class %1$s", var1);
            }
         }

         var7.set(var5x);
         if (var2 != null) {
            var5x = loadClass(null, var2);
            if (var5x == null) {
               abort(null, "Missing JavaFX preloader class %1$s", var2);
            }

            if (!Preloader.class.isAssignableFrom(var5x)) {
               abort(null, "JavaFX preloader class %1$s does not extend javafx.application.Preloader", var5x.getName());
            }

            var8.set(var5x.asSubclass(Preloader.class));
         }
      });
      var5 = (Class)var8.get();
      var6 = (Class)var7.get();
      savedPreloaderClass = (Class<? extends Preloader>)var5;
      Object var9 = null;

      try {
         Method var10 = var6.getMethod("main", new String[0].getClass());
         if (verbose) {
            System.err.println("Calling main(String[]) method");
         }

         savedMainCcl = Thread.currentThread().getContextClassLoader();
         var10.invoke(null, var3);
      } catch (IllegalAccessException | NoSuchMethodException var12) {
         savedPreloaderClass = null;
         if (verbose) {
            System.err.println("WARNING: Cannot access application main method: " + var12);
         }

         if (!Application.class.isAssignableFrom(var6)) {
            abort(var12, "JavaFX application class %1$s does not extend javafx.application.Application", var6.getName());
         }

         Class var4 = var6.asSubclass(Application.class);
         if (verbose) {
            System.err.println("Launching application directly");
         }

         launchApplication(var4, (Class<? extends Preloader>)var5, var3);
      } catch (InvocationTargetException var13) {
         var13.printStackTrace();
         abort(null, "Exception running application %1$s", var6.getName());
      }
   }

   private static URL fileToURL(File var0) throws IOException {
      return var0.getCanonicalFile().toURI().toURL();
   }

   private static ClassLoader setupJavaFXClassLoader(File var0, String var1) {
      try {
         File var2 = var0.getParentFile();
         ArrayList var3 = new ArrayList();
         String var4 = var1;
         if (var1 != null) {
            while (var4.length() > 0) {
               int var5 = var4.indexOf(" ");
               if (var5 < 0) {
                  File var13 = var2 == null ? new File(var4) : new File(var2, var4);
                  if (var13.exists()) {
                     var3.add(fileToURL(var13));
                  } else if (verbose) {
                     System.err.println("Class Path entry \"" + var4 + "\" does not exist, ignoring");
                  }
                  break;
               }

               if (var5 > 0) {
                  String var6 = var4.substring(0, var5);
                  File var7 = var2 == null ? new File(var6) : new File(var2, var6);
                  if (var7.exists()) {
                     var3.add(fileToURL(var7));
                  } else if (verbose) {
                     System.err.println("Class Path entry \"" + var6 + "\" does not exist, ignoring");
                  }
               }

               var4 = var4.substring(var5 + 1);
            }
         }

         if (!var3.isEmpty()) {
            ArrayList var10 = new ArrayList();
            var4 = System.getProperty("java.class.path");
            if (var4 != null) {
               while (var4.length() > 0) {
                  int var11 = var4.indexOf(File.pathSeparatorChar);
                  if (var11 < 0) {
                     var10.add(fileToURL(new File(var4)));
                     break;
                  }

                  if (var11 > 0) {
                     String var14 = var4.substring(0, var11);
                     var10.add(fileToURL(new File(var14)));
                  }

                  var4 = var4.substring(var11 + 1);
               }
            }

            var10.addAll(var3);
            URL[] var12 = var10.toArray(new URL[0]);
            if (verbose) {
               System.err.println("===== URL list");

               for (int var15 = 0; var15 < var12.length; var15++) {
                  System.err.println(var12[var15] + "");
               }

               System.err.println("=====");
            }

            return new URLClassLoader(var12, ClassLoader.getPlatformClassLoader());
         }
      } catch (Exception var8) {
      }

      return null;
   }

   private static String decodeBase64(String var0) throws IOException {
      return new String(Base64.getDecoder().decode(var0));
   }

   private static String[] getAppArguments(Attributes var0) {
      LinkedList var1 = new LinkedList();

      try {
         int var2 = 1;

         for (String var3 = "JavaFX-Argument-"; var0.getValue(var3 + var2) != null; var2++) {
            var1.add(decodeBase64(var0.getValue(var3 + var2)));
         }

         String var4 = "JavaFX-Parameter-Name-";
         String var5 = "JavaFX-Parameter-Value-";

         for (int var9 = 1; var0.getValue(var4 + var9) != null; var9++) {
            String var6 = decodeBase64(var0.getValue(var4 + var9));
            String var7 = null;
            if (var0.getValue(var5 + var9) != null) {
               var7 = decodeBase64(var0.getValue(var5 + var9));
            }

            var1.add("--" + var6 + "=" + (var7 != null ? var7 : ""));
         }
      } catch (IOException var8) {
         if (verbose) {
            System.err.println("Failed to extract application parameters");
         }

         var8.printStackTrace();
      }

      return var1.toArray(new String[0]);
   }

   private static void abort(Throwable var0, String var1, Object... var2) {
      String var3 = String.format(var1, var2);
      if (var3 != null) {
         System.err.println(var3);
      }

      System.exit(1);
   }

   private static Attributes getJarAttributes(String var0) {
      JarFile var1 = null;

      try {
         var1 = new JarFile(var0);
         Manifest var2 = var1.getManifest();
         if (var2 == null) {
            abort(null, "No manifest in jar file %1$s", var0);
         }

         return var2.getMainAttributes();
      } catch (IOException var13) {
         abort(var13, "Error launching jar file %1%s", var0);
      } finally {
         try {
            var1.close();
         } catch (IOException var12) {
         }
      }

      return null;
   }

   private static void startToolkit() throws InterruptedException {
      if (!toolkitStarted.getAndSet(true)) {
         CountDownLatch var0 = new CountDownLatch(1);
         PlatformImpl.startup(() -> var0.countDown());
         var0.await();
      }
   }

   private static void launchApplication1(Class<? extends Application> var0, Class<? extends Preloader> var1, String[] var2) throws Exception {
      startToolkit();
      if (savedMainCcl != null) {
         ClassLoader var3 = Thread.currentThread().getContextClassLoader();
         if (var3 != null && var3 != savedMainCcl) {
            PlatformImpl.runLater(() -> Thread.currentThread().setContextClassLoader(var3));
         }
      }

      final AtomicBoolean var21 = new AtomicBoolean(false);
      final AtomicBoolean var4 = new AtomicBoolean(false);
      final AtomicBoolean var5 = new AtomicBoolean(false);
      new AtomicBoolean(false);
      final CountDownLatch var7 = new CountDownLatch(1);
      final CountDownLatch var8 = new CountDownLatch(1);
      PlatformImpl.FinishListener var9 = new PlatformImpl.FinishListener() {
         @Override
         public void idle(boolean var1) {
            if (var1) {
               if (var4.get()) {
                  var7.countDown();
               } else if (var21.get()) {
                  var8.countDown();
               }
            }
         }

         @Override
         public void exitCalled() {
            var5.set(true);
            var7.countDown();
         }
      };
      PlatformImpl.addListener(var9);

      try {
         AtomicReference var10 = new AtomicReference();
         if (var1 != null) {
            PlatformImpl.runAndWait(() -> {
               try {
                  Constructor var3x = var1.getConstructor();
                  var10.set((Preloader)var3x.newInstance());
                  ParametersImpl.registerParameters((Application)var10.get(), new ParametersImpl(var2));
               } catch (Throwable var4x) {
                  System.err.println("Exception in Preloader constructor");
                  pConstructorError = var4x;
                  error = true;
               }
            });
         }

         currentPreloader = (Preloader)var10.get();
         if (currentPreloader != null && !error && !var5.get()) {
            try {
               currentPreloader.init();
            } catch (Throwable var19) {
               System.err.println("Exception in Preloader init method");
               pInitError = var19;
               error = true;
            }
         }

         if (currentPreloader != null && !error && !var5.get()) {
            PlatformImpl.runAndWait(() -> {
               try {
                  var21.set(true);
                  Stage var1x = new Stage();
                  StageHelper.setPrimary(var1x, true);
                  currentPreloader.start(var1x);
               } catch (Throwable var2x) {
                  System.err.println("Exception in Preloader start method");
                  pStartError = var2x;
                  error = true;
               }
            });
            if (!error && !var5.get()) {
               notifyProgress(currentPreloader, 0.0);
            }
         }

         AtomicReference var11 = new AtomicReference();
         if (!error && !var5.get()) {
            if (currentPreloader != null) {
               notifyProgress(currentPreloader, 1.0);
               notifyStateChange(currentPreloader, Preloader.StateChangeNotification.Type.BEFORE_LOAD, null);
            }

            PlatformImpl.runAndWait(() -> {
               try {
                  Constructor var3x = var0.getConstructor();
                  var11.set((Application)var3x.newInstance());
                  ParametersImpl.registerParameters((Application)var11.get(), new ParametersImpl(var2));
                  PlatformImpl.setApplicationName(var0);
               } catch (Throwable var4x) {
                  System.err.println("Exception in Application constructor");
                  constructorError = var4x;
                  error = true;
               }
            });
         }

         Application var12 = (Application)var11.get();
         if (!error && !var5.get()) {
            if (currentPreloader != null) {
               notifyStateChange(currentPreloader, Preloader.StateChangeNotification.Type.BEFORE_INIT, var12);
            }

            try {
               var12.init();
            } catch (Throwable var18) {
               System.err.println("Exception in Application init method");
               initError = var18;
               error = true;
            }
         }

         if (!error && !var5.get()) {
            if (currentPreloader != null) {
               notifyStateChange(currentPreloader, Preloader.StateChangeNotification.Type.BEFORE_START, var12);
            }

            PlatformImpl.runAndWait(() -> {
               try {
                  var4.set(true);
                  Stage var2x = new Stage();
                  StageHelper.setPrimary(var2x, true);
                  var12.start(var2x);
               } catch (Throwable var3x) {
                  System.err.println("Exception in Application start method");
                  startError = var3x;
                  error = true;
               }
            });
         }

         if (!error) {
            var7.await();
         }

         if (var4.get()) {
            PlatformImpl.runAndWait(() -> {
               try {
                  var12.stop();
               } catch (Throwable var2x) {
                  System.err.println("Exception in Application stop method");
                  stopError = var2x;
                  error = true;
               }
            });
         }

         if (error) {
            if (pConstructorError != null) {
               throw new RuntimeException("Unable to construct Preloader instance: " + var0, pConstructorError);
            }

            if (pInitError != null) {
               throw new RuntimeException("Exception in Preloader init method", pInitError);
            }

            if (pStartError != null) {
               throw new RuntimeException("Exception in Preloader start method", pStartError);
            }

            if (pStopError != null) {
               throw new RuntimeException("Exception in Preloader stop method", pStopError);
            }

            if (constructorError != null) {
               String var13 = "Unable to construct Application instance: " + var0;
               if (!notifyError(var13, constructorError)) {
                  throw new RuntimeException(var13, constructorError);
               }
            } else if (initError != null) {
               String var22 = "Exception in Application init method";
               if (!notifyError(var22, initError)) {
                  throw new RuntimeException(var22, initError);
               }
            } else if (startError != null) {
               String var23 = "Exception in Application start method";
               if (!notifyError(var23, startError)) {
                  throw new RuntimeException(var23, startError);
               }
            } else if (stopError != null) {
               String var24 = "Exception in Application stop method";
               if (!notifyError(var24, stopError)) {
                  throw new RuntimeException(var24, stopError);
               }
            }
         }
      } finally {
         PlatformImpl.removeListener(var9);
         PlatformImpl.tkExit();
      }
   }

   private static void notifyStateChange(Preloader var0, Preloader.StateChangeNotification.Type var1, Application var2) {
      PlatformImpl.runAndWait(() -> var0.handleStateChangeNotification(new Preloader.StateChangeNotification(var1, var2)));
   }

   private static void notifyProgress(Preloader var0, double var1) {
      PlatformImpl.runAndWait(() -> var0.handleProgressNotification(new Preloader.ProgressNotification(var1)));
   }

   private static boolean notifyError(String var0, Throwable var1) {
      AtomicBoolean var2 = new AtomicBoolean(false);
      PlatformImpl.runAndWait(() -> {
         if (currentPreloader != null) {
            try {
               Preloader.ErrorNotification var3 = new Preloader.ErrorNotification(null, var0, var1);
               boolean var4 = currentPreloader.handleErrorNotification(var3);
               var2.set(var4);
            } catch (Throwable var5) {
               var5.printStackTrace();
            }
         }
      });
      return var2.get();
   }

   private static void notifyCurrentPreloader(Preloader.PreloaderNotification var0) {
      PlatformImpl.runAndWait(() -> {
         if (currentPreloader != null) {
            currentPreloader.handleApplicationNotification(var0);
         }
      });
   }

   public static void notifyPreloader(Application var0, Preloader.PreloaderNotification var1) {
      if (launchCalled.get()) {
         notifyCurrentPreloader(var1);
      }
   }

   private LauncherImpl() {
      throw new InternalError();
   }
}
