package com.sun.javafx.application;

import com.sun.glass.ui.Application;
import com.sun.javafx.FXPermissions;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.tk.TKListener;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Logging;
import com.sun.javafx.util.ModuleHelper;
import com.sun.javafx.util.Utils;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.module.ModuleDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.application.ConditionalFeature;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.util.FXPermission;

public class PlatformImpl {
   private static AtomicBoolean initialized = new AtomicBoolean(false);
   private static AtomicBoolean platformExit = new AtomicBoolean(false);
   private static AtomicBoolean toolkitExit = new AtomicBoolean(false);
   private static CountDownLatch startupLatch = new CountDownLatch(1);
   private static AtomicBoolean listenersRegistered = new AtomicBoolean(false);
   private static TKListener toolkitListener = null;
   private static volatile boolean implicitExit = true;
   private static boolean taskbarApplication = true;
   private static boolean contextual2DNavigation;
   private static AtomicInteger pendingRunnables = new AtomicInteger(0);
   private static AtomicInteger numWindows = new AtomicInteger(0);
   private static volatile boolean firstWindowShown = false;
   private static volatile boolean lastWindowClosed = false;
   private static AtomicBoolean reallyIdle = new AtomicBoolean(false);
   private static Set<PlatformImpl.FinishListener> finishListeners = new CopyOnWriteArraySet<>();
   private static final Object runLaterLock = new Object();
   private static Boolean isGraphicsSupported;
   private static Boolean isControlsSupported;
   private static Boolean isMediaSupported;
   private static Boolean isWebSupported;
   private static Boolean isSWTSupported;
   private static Boolean isSwingSupported;
   private static Boolean isFXMLSupported;
   private static Boolean hasTwoLevelFocus;
   private static Boolean hasVirtualKeyboard;
   private static Boolean hasTouch;
   private static Boolean hasMultiTouch;
   private static Boolean hasPointer;
   private static boolean isThreadMerged = false;
   private static String applicationType = "";
   private static BooleanProperty accessibilityActive = new SimpleBooleanProperty();
   private static CountDownLatch allNestedLoopsExitedLatch = new CountDownLatch(1);
   private static final boolean verbose = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("javafx.verbose")));
   private static final boolean DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)(() -> Boolean.getBoolean("com.sun.javafx.application.debug")));
   private static final FXPermission FXCANVAS_PERMISSION = new FXPermission("accessFXCanvasInternals");
   private static final CountDownLatch platformExitLatch = new CountDownLatch(1);
   private static boolean isModena = false;
   private static boolean isCaspian = false;
   private static String accessibilityTheme;

   public static void setTaskbarApplication(boolean var0) {
      taskbarApplication = var0;
   }

   public static boolean isTaskbarApplication() {
      return taskbarApplication;
   }

   public static void setApplicationName(Class var0) {
      runLater(() -> Application.GetApplication().setName(var0.getName()));
   }

   public static boolean isContextual2DNavigation() {
      return contextual2DNavigation;
   }

   public static void startup(Runnable var0) {
      startup(var0, false);
   }

   public static void startup(Runnable var0, boolean var1) {
      if (platformExit.get()) {
         throw new IllegalStateException("Platform.exit has been called");
      } else if (initialized.getAndSet(true)) {
         if (var1) {
            throw new IllegalStateException("Toolkit already initialized");
         } else {
            runLater(var0);
         }
      } else {
         Module var2 = PlatformImpl.class.getModule();
         ModuleDescriptor var3 = var2.getDescriptor();
         if (!var2.isNamed() || !"javafx.graphics".equals(var2.getName()) || var3 == null || var3.isAutomatic() || var3.isOpen()) {
            String var4 = "Unsupported JavaFX configuration: classes were loaded from '" + var2 + "'";
            if (var3 != null) {
               var4 = var4 + ", isAutomatic: " + var3.isAutomatic();
               var4 = var4 + ", isOpen: " + var3.isOpen();
            }

            Logging.getJavaFXLogger().warning(var4);
         }

         Void var7 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            applicationType = System.getProperty("com.sun.javafx.application.type");
            if (applicationType == null) {
               applicationType = "";
            }

            contextual2DNavigation = Boolean.getBoolean("com.sun.javafx.isContextual2DNavigation");
            String var0x = System.getProperty("com.sun.javafx.twoLevelFocus");
            if (var0x != null) {
               hasTwoLevelFocus = Boolean.valueOf(var0x);
            }

            var0x = System.getProperty("com.sun.javafx.virtualKeyboard");
            if (var0x != null) {
               if (var0x.equalsIgnoreCase("none")) {
                  hasVirtualKeyboard = false;
               } else if (var0x.equalsIgnoreCase("javafx")) {
                  hasVirtualKeyboard = true;
               } else if (var0x.equalsIgnoreCase("native")) {
                  hasVirtualKeyboard = true;
               }
            }

            var0x = System.getProperty("com.sun.javafx.touch");
            if (var0x != null) {
               hasTouch = Boolean.valueOf(var0x);
            }

            var0x = System.getProperty("com.sun.javafx.multiTouch");
            if (var0x != null) {
               hasMultiTouch = Boolean.valueOf(var0x);
            }

            var0x = System.getProperty("com.sun.javafx.pointer");
            if (var0x != null) {
               hasPointer = Boolean.valueOf(var0x);
            }

            var0x = System.getProperty("javafx.embed.singleThread");
            if (var0x != null) {
               isThreadMerged = Boolean.valueOf(var0x);
               if (isThreadMerged && !isSupported(ConditionalFeature.SWING)) {
                  isThreadMerged = false;
                  if (verbose) {
                     System.err.println("WARNING: javafx.embed.singleThread ignored (javafx.swing module not found)");
                  }
               }
            }

            return null;
         }));
         if (DEBUG) {
            System.err.println("PlatformImpl::startup : applicationType = " + applicationType);
         }

         if ("FXCanvas".equals(applicationType)) {
            initFXCanvas();
         }

         if (!taskbarApplication) {
            Void var5 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               System.setProperty("glass.taskbarApplication", "false");
               return null;
            }));
         }

         toolkitListener = new TKListener() {
            @Override
            public void changedTopLevelWindows(List<TKStage> var1) {
               PlatformImpl.numWindows.set(var1.size());
               PlatformImpl.checkIdle();
            }

            @Override
            public void exitedLastNestedLoop() {
               if (PlatformImpl.platformExit.get()) {
                  PlatformImpl.allNestedLoopsExitedLatch.countDown();
               }

               PlatformImpl.checkIdle();
            }
         };
         Toolkit.getToolkit().addTkListener(toolkitListener);
         Toolkit.getToolkit().startup(() -> {
            startupLatch.countDown();
            var0.run();
         });
         if (isThreadMerged) {
            installFwEventQueue();
         }
      }
   }

   private static void initDeviceDetailsFXCanvas() {
      long var0 = AccessController.doPrivileged((PrivilegedAction<Long>)(() -> Long.getLong("javafx.embed.eventProc", 0L)));
      if (var0 != 0L) {
         Object var2 = Application.getDeviceDetails();
         if (var2 == null) {
            var2 = new HashMap();
            Application.setDeviceDetails((Map)var2);
         }

         if (var2.get("javafx.embed.eventProc") == null) {
            var2.put("javafx.embed.eventProc", var0);
         }
      }
   }

   private static void addExportsToFXCanvas(Class<?> var0) {
      String[] var1 = new String[]{"com.sun.glass.ui", "com.sun.javafx.cursor", "com.sun.javafx.embed", "com.sun.javafx.stage", "com.sun.javafx.tk"};
      if (DEBUG) {
         System.err.println("addExportsToFXCanvas: class = " + var0);
      }

      Object var2 = ModuleHelper.getModule(PlatformImpl.class);
      Object var3 = ModuleHelper.getModule(var0);

      for (String var7 : var1) {
         if (DEBUG) {
            System.err.println("add export of " + var7 + " from " + var2 + " to " + var3);
         }

         ModuleHelper.addExports(var2, var7, var3);
      }
   }

   private static void initFXCanvas() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         try {
            var0.checkPermission(FXCANVAS_PERMISSION);
         } catch (SecurityException var5) {
            System.err.println("FXCanvas: no permission to access JavaFX internals");
            var5.printStackTrace();
            return;
         }
      }

      Predicate var1 = var0x -> !var0x.getClassName().startsWith("javafx.application.") && !var0x.getClassName().startsWith("com.sun.javafx.application.");
      StackWalker var2 = AccessController.doPrivileged((PrivilegedAction<StackWalker>)(() -> StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)));
      Optional var3 = var2.walk(var1x -> var1x.filter(var1).findFirst());
      if (var3.isPresent()) {
         Class var4 = ((StackFrame)var3.get()).getDeclaringClass();
         if (DEBUG) {
            System.err.println("callerClassName = " + var4);
         }

         if ("javafx.embed.swt.FXCanvas".equals(var4.getName())) {
            initDeviceDetailsFXCanvas();
            addExportsToFXCanvas(var4);
         }
      }
   }

   private static void installFwEventQueue() {
      invokeSwingFXUtilsMethod("installFwEventQueue");
   }

   private static void removeFwEventQueue() {
      invokeSwingFXUtilsMethod("removeFwEventQueue");
   }

   private static void invokeSwingFXUtilsMethod(String var0) {
      try {
         Class var1 = Class.forName("com.sun.javafx.embed.swing.SwingFXUtilsImpl");
         Method var2 = var1.getDeclaredMethod(var0);
         waitForStart();
         var2.invoke(null);
      } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException var3) {
         throw new RuntimeException("Property javafx.embed.singleThread is not supported");
      } catch (InvocationTargetException var4) {
         throw new RuntimeException(var4);
      }
   }

   private static void waitForStart() {
      if (startupLatch.getCount() > 0L) {
         try {
            startupLatch.await();
         } catch (InterruptedException var1) {
            var1.printStackTrace();
         }
      }
   }

   public static boolean isFxApplicationThread() {
      return Toolkit.getToolkit().isFxUserThread();
   }

   public static void runLater(Runnable var0) {
      runLater(var0, false);
   }

   private static void runLater(Runnable var0, boolean var1) {
      if (!initialized.get()) {
         throw new IllegalStateException("Toolkit not initialized");
      } else {
         pendingRunnables.incrementAndGet();
         waitForStart();
         synchronized (runLaterLock) {
            if (!var1 && toolkitExit.get()) {
               pendingRunnables.decrementAndGet();
            } else {
               AccessControlContext var3 = AccessController.getContext();
               Toolkit.getToolkit().defer(() -> {
                  try {
                     Void var2 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                        var0.run();
                        return null;
                     }), var3);
                  } finally {
                     pendingRunnables.decrementAndGet();
                     checkIdle();
                  }
               });
            }
         }
      }
   }

   public static void runAndWait(Runnable var0) {
      runAndWait(var0, false);
   }

   private static void runAndWait(Runnable var0, boolean var1) {
      if (isFxApplicationThread()) {
         try {
            var0.run();
         } catch (Throwable var5) {
            System.err.println("Exception in runnable");
            var5.printStackTrace();
         }
      } else {
         CountDownLatch var2 = new CountDownLatch(1);
         runLater(() -> {
            try {
               var0.run();
            } finally {
               var2.countDown();
            }
         }, var1);
         if (!var1 && toolkitExit.get()) {
            throw new IllegalStateException("Toolkit has exited");
         }

         try {
            var2.await();
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }
      }
   }

   public static void setImplicitExit(boolean var0) {
      implicitExit = var0;
      checkIdle();
   }

   public static boolean isImplicitExit() {
      return implicitExit;
   }

   public static void addListener(PlatformImpl.FinishListener var0) {
      listenersRegistered.set(true);
      finishListeners.add(var0);
   }

   public static void removeListener(PlatformImpl.FinishListener var0) {
      finishListeners.remove(var0);
      listenersRegistered.set(!finishListeners.isEmpty());
      if (!listenersRegistered.get()) {
         checkIdle();
      }
   }

   private static void notifyFinishListeners(boolean var0) {
      if (listenersRegistered.get()) {
         for (PlatformImpl.FinishListener var2 : finishListeners) {
            if (var0) {
               var2.exitCalled();
            } else {
               var2.idle(implicitExit);
            }
         }
      } else if (implicitExit || platformExit.get()) {
         tkExit();
      }
   }

   private static void checkIdle() {
      if (initialized.get()) {
         if (!isFxApplicationThread()) {
            runLater(() -> {
            });
         } else {
            boolean var0 = false;
            synchronized (PlatformImpl.class) {
               int var2 = numWindows.get();
               if (var2 > 0) {
                  firstWindowShown = true;
                  lastWindowClosed = false;
                  reallyIdle.set(false);
               } else if (var2 == 0 && firstWindowShown) {
                  lastWindowClosed = true;
               }

               if (lastWindowClosed && pendingRunnables.get() == 0 && (toolkitExit.get() || !Toolkit.getToolkit().isNestedLoopRunning())) {
                  if (reallyIdle.getAndSet(true)) {
                     var0 = true;
                     lastWindowClosed = false;
                  } else {
                     runLater(() -> {
                     });
                  }
               }
            }

            if (var0) {
               notifyFinishListeners(false);
            }
         }
      }
   }

   static CountDownLatch test_getPlatformExitLatch() {
      return platformExitLatch;
   }

   public static void tkExit() {
      if (!toolkitExit.getAndSet(true)) {
         if (initialized.get()) {
            if (platformExit.get()) {
               runAndWait(() -> {
                  if (Toolkit.getToolkit().isNestedLoopRunning()) {
                     Toolkit.getToolkit().exitAllNestedEventLoops();
                  } else {
                     allNestedLoopsExitedLatch.countDown();
                  }
               }, true);

               try {
                  allNestedLoopsExitedLatch.await();
               } catch (InterruptedException var1) {
                  throw new RuntimeException("Could not exit all nested event loops");
               }
            }

            runAndWait(() -> Toolkit.getToolkit().exit(), true);
            if (isThreadMerged) {
               removeFwEventQueue();
            }

            Toolkit.getToolkit().removeTkListener(toolkitListener);
            toolkitListener = null;
            platformExitLatch.countDown();
         }
      }
   }

   public static BooleanProperty accessibilityActiveProperty() {
      return accessibilityActive;
   }

   public static void exit() {
      platformExit.set(true);
      notifyFinishListeners(true);
   }

   private static Boolean checkForClass(String var0) {
      try {
         Class.forName(var0, false, PlatformImpl.class.getClassLoader());
         return Boolean.TRUE;
      } catch (ClassNotFoundException var2) {
         return Boolean.FALSE;
      }
   }

   public static boolean isSupported(ConditionalFeature var0) {
      boolean var1 = isSupportedImpl(var0);
      if (var1 && var0 == ConditionalFeature.TRANSPARENT_WINDOW) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            try {
               var2.checkPermission(FXPermissions.CREATE_TRANSPARENT_WINDOW_PERMISSION);
            } catch (SecurityException var4) {
               return false;
            }
         }

         return true;
      } else {
         return var1;
      }
   }

   public static void setDefaultPlatformUserAgentStylesheet() {
      setPlatformUserAgentStylesheet("MODENA");
   }

   public static boolean isModena() {
      return isModena;
   }

   public static boolean isCaspian() {
      return isCaspian;
   }

   public static void setPlatformUserAgentStylesheet(String var0) {
      if (isFxApplicationThread()) {
         _setPlatformUserAgentStylesheet(var0);
      } else {
         runLater(() -> _setPlatformUserAgentStylesheet(var0));
      }
   }

   public static boolean setAccessibilityTheme(String var0) {
      if (accessibilityTheme != null) {
         StyleManager.getInstance().removeUserAgentStylesheet(accessibilityTheme);
         accessibilityTheme = null;
      }

      _setAccessibilityTheme(var0);
      if (accessibilityTheme != null) {
         StyleManager.getInstance().addUserAgentStylesheet(accessibilityTheme);
         return true;
      } else {
         return false;
      }
   }

   private static void _setAccessibilityTheme(String var0) {
      String var1 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("com.sun.javafx.highContrastTheme")));
      if (isCaspian()) {
         if (var0 != null || var1 != null) {
            accessibilityTheme = "com/sun/javafx/scene/control/skin/caspian/highcontrast.css";
         }
      } else if (isModena()) {
         if (var1 != null) {
            String var2 = var1.toUpperCase();
            switch (var2) {
               case "BLACKONWHITE":
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/blackOnWhite.css";
                  break;
               case "WHITEONBLACK":
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/whiteOnBlack.css";
                  break;
               case "YELLOWONBLACK":
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/yellowOnBlack.css";
            }
         } else if (var0 != null) {
            switch (PlatformImpl.HighContrastScheme.valueOf(var0)) {
               case HIGH_CONTRAST_WHITE:
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/blackOnWhite.css";
                  break;
               case HIGH_CONTRAST_BLACK:
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/whiteOnBlack.css";
                  break;
               case HIGH_CONTRAST_1:
               case HIGH_CONTRAST_2:
                  accessibilityTheme = "com/sun/javafx/scene/control/skin/modena/yellowOnBlack.css";
            }
         }
      }
   }

   private static void _setPlatformUserAgentStylesheet(String var0) {
      isCaspian = false;
      isModena = false;
      String var1 = AccessController.doPrivileged((PrivilegedAction<String>)(() -> System.getProperty("javafx.userAgentStylesheetUrl")));
      if (var1 != null) {
         var0 = var1;
      }

      ArrayList var2 = new ArrayList();
      if ("CASPIAN".equalsIgnoreCase(var0)) {
         isCaspian = true;
         var2.add("com/sun/javafx/scene/control/skin/caspian/caspian.css");
         if (isSupported(ConditionalFeature.INPUT_TOUCH)) {
            var2.add("com/sun/javafx/scene/control/skin/caspian/embedded.css");
            if (Utils.isQVGAScreen()) {
               var2.add("com/sun/javafx/scene/control/skin/caspian/embedded-qvga.css");
            }

            if (PlatformUtil.isAndroid()) {
               var2.add("com/sun/javafx/scene/control/skin/caspian/android.css");
            }

            if (PlatformUtil.isIOS()) {
               var2.add("com/sun/javafx/scene/control/skin/caspian/ios.css");
            }
         }

         if (isSupported(ConditionalFeature.TWO_LEVEL_FOCUS)) {
            var2.add("com/sun/javafx/scene/control/skin/caspian/two-level-focus.css");
         }

         if (isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
            var2.add("com/sun/javafx/scene/control/skin/caspian/fxvk.css");
         }

         if (!isSupported(ConditionalFeature.TRANSPARENT_WINDOW)) {
            var2.add("com/sun/javafx/scene/control/skin/caspian/caspian-no-transparency.css");
         }
      } else if ("MODENA".equalsIgnoreCase(var0)) {
         isModena = true;
         var2.add("com/sun/javafx/scene/control/skin/modena/modena.css");
         if (isSupported(ConditionalFeature.INPUT_TOUCH)) {
            var2.add("com/sun/javafx/scene/control/skin/modena/touch.css");
         }

         if (PlatformUtil.isEmbedded()) {
            var2.add("com/sun/javafx/scene/control/skin/modena/modena-embedded-performance.css");
         }

         if (PlatformUtil.isAndroid()) {
            var2.add("com/sun/javafx/scene/control/skin/modena/android.css");
         }

         if (PlatformUtil.isIOS()) {
            var2.add("com/sun/javafx/scene/control/skin/modena/ios.css");
         }

         if (isSupported(ConditionalFeature.TWO_LEVEL_FOCUS)) {
            var2.add("com/sun/javafx/scene/control/skin/modena/two-level-focus.css");
         }

         if (isSupported(ConditionalFeature.VIRTUAL_KEYBOARD)) {
            var2.add("com/sun/javafx/scene/control/skin/caspian/fxvk.css");
         }

         if (!isSupported(ConditionalFeature.TRANSPARENT_WINDOW)) {
            var2.add("com/sun/javafx/scene/control/skin/modena/modena-no-transparency.css");
         }
      } else {
         var2.add(var0);
      }

      _setAccessibilityTheme(Toolkit.getToolkit().getThemeName());
      if (accessibilityTheme != null) {
         var2.add(accessibilityTheme);
      }

      Object var3 = AccessController.doPrivileged((PrivilegedAction)(() -> {
         StyleManager.getInstance().setUserAgentStylesheets(var2);
         return null;
      }));
   }

   public static void addNoTransparencyStylesheetToScene(Scene var0) {
      if (isCaspian()) {
         AccessController.doPrivileged((PrivilegedAction)(() -> {
            StyleManager.getInstance().addUserAgentStylesheet(var0, "com/sun/javafx/scene/control/skin/caspian/caspian-no-transparency.css");
            return null;
         }));
      } else if (isModena()) {
         AccessController.doPrivileged((PrivilegedAction)(() -> {
            StyleManager.getInstance().addUserAgentStylesheet(var0, "com/sun/javafx/scene/control/skin/modena/modena-no-transparency.css");
            return null;
         }));
      }
   }

   private static boolean isSupportedImpl(ConditionalFeature var0) {
      switch (var0) {
         case GRAPHICS:
            if (isGraphicsSupported == null) {
               isGraphicsSupported = checkForClass("javafx.stage.Stage");
            }

            return isGraphicsSupported;
         case CONTROLS:
            if (isControlsSupported == null) {
               isControlsSupported = checkForClass("javafx.scene.control.Control");
            }

            return isControlsSupported;
         case MEDIA:
            if (isMediaSupported == null) {
               isMediaSupported = checkForClass("javafx.scene.media.MediaView");
               if (isMediaSupported && PlatformUtil.isEmbedded()) {
                  Void var2 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                     String var0x = System.getProperty("com.sun.javafx.experimental.embedded.media", "false");
                     isMediaSupported = Boolean.valueOf(var0x);
                     return null;
                  }));
               }
            }

            return isMediaSupported;
         case WEB:
            if (isWebSupported == null) {
               isWebSupported = checkForClass("javafx.scene.web.WebView");
               if (isWebSupported && PlatformUtil.isEmbedded()) {
                  Void var1 = AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                     String var0x = System.getProperty("com.sun.javafx.experimental.embedded.web", "false");
                     isWebSupported = Boolean.valueOf(var0x);
                     return null;
                  }));
               }
            }

            return isWebSupported;
         case SWT:
            if (isSWTSupported == null) {
               isSWTSupported = checkForClass("javafx.embed.swt.FXCanvas");
            }

            return isSWTSupported;
         case SWING:
            if (isSwingSupported == null) {
               isSwingSupported = checkForClass("javax.swing.JComponent") && checkForClass("javafx.embed.swing.JFXPanel");
            }

            return isSwingSupported;
         case FXML:
            if (isFXMLSupported == null) {
               isFXMLSupported = checkForClass("javafx.fxml.FXMLLoader") && checkForClass("javax.xml.stream.XMLInputFactory");
            }

            return isFXMLSupported;
         case TWO_LEVEL_FOCUS:
            if (hasTwoLevelFocus == null) {
               return Toolkit.getToolkit().isSupported(var0);
            }

            return hasTwoLevelFocus;
         case VIRTUAL_KEYBOARD:
            if (hasVirtualKeyboard == null) {
               return Toolkit.getToolkit().isSupported(var0);
            }

            return hasVirtualKeyboard;
         case INPUT_TOUCH:
            if (hasTouch == null) {
               return Toolkit.getToolkit().isSupported(var0);
            }

            return hasTouch;
         case INPUT_MULTITOUCH:
            if (hasMultiTouch == null) {
               return Toolkit.getToolkit().isSupported(var0);
            }

            return hasMultiTouch;
         case INPUT_POINTER:
            if (hasPointer == null) {
               return Toolkit.getToolkit().isSupported(var0);
            }

            return hasPointer;
         default:
            return Toolkit.getToolkit().isSupported(var0);
      }
   }

   public interface FinishListener {
      void idle(boolean var1);

      void exitCalled();
   }

   public static enum HighContrastScheme {
      HIGH_CONTRAST_BLACK("high.contrast.black.theme"),
      HIGH_CONTRAST_WHITE("high.contrast.white.theme"),
      HIGH_CONTRAST_1("high.contrast.1.theme"),
      HIGH_CONTRAST_2("high.contrast.2.theme");

      private final String themeKey;

      private HighContrastScheme(String var3) {
         this.themeKey = var3;
      }

      public String getThemeKey() {
         return this.themeKey;
      }

      public static String fromThemeName(Function<String, String> var0, String var1) {
         if (var0 != null && var1 != null) {
            for (PlatformImpl.HighContrastScheme var5 : values()) {
               if (var1.equalsIgnoreCase((String)var0.apply(var5.getThemeKey()))) {
                  return var5.toString();
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }
}
