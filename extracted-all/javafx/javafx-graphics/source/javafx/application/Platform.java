package javafx.application;

import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.tk.Toolkit;
import java.util.Optional;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.input.KeyCode;

public final class Platform {
   private static ReadOnlyBooleanWrapper accessibilityActiveProperty;

   private Platform() {
   }

   public static void startup(Runnable var0) {
      PlatformImpl.startup(var0, true);
   }

   public static void runLater(Runnable var0) {
      PlatformImpl.runLater(var0);
   }

   public static void requestNextPulse() {
      Toolkit.getToolkit().requestNextPulse();
   }

   public static boolean isFxApplicationThread() {
      return PlatformImpl.isFxApplicationThread();
   }

   public static void exit() {
      PlatformImpl.exit();
   }

   public static void setImplicitExit(boolean var0) {
      PlatformImpl.setImplicitExit(var0);
   }

   public static boolean isImplicitExit() {
      return PlatformImpl.isImplicitExit();
   }

   public static boolean isSupported(ConditionalFeature var0) {
      return PlatformImpl.isSupported(var0);
   }

   public static Object enterNestedEventLoop(Object var0) {
      return Toolkit.getToolkit().enterNestedEventLoop(var0);
   }

   public static void exitNestedEventLoop(Object var0, Object var1) {
      Toolkit.getToolkit().exitNestedEventLoop(var0, var1);
   }

   public static Optional<Boolean> isKeyLocked(KeyCode var0) {
      Toolkit.getToolkit().checkFxUserThread();
      switch (var0) {
         case CAPS:
         case NUM_LOCK:
            return Toolkit.getToolkit().isKeyLocked(var0);
         default:
            throw new IllegalArgumentException("Invalid KeyCode");
      }
   }

   public static boolean isNestedLoopRunning() {
      return Toolkit.getToolkit().isNestedLoopRunning();
   }

   public static boolean isAccessibilityActive() {
      return accessibilityActiveProperty == null ? false : accessibilityActiveProperty.get();
   }

   public static ReadOnlyBooleanProperty accessibilityActiveProperty() {
      if (accessibilityActiveProperty == null) {
         accessibilityActiveProperty = new ReadOnlyBooleanWrapper(Platform.class, "accessibilityActive");
         accessibilityActiveProperty.bind(PlatformImpl.accessibilityActiveProperty());
      }

      return accessibilityActiveProperty.getReadOnlyProperty();
   }
}
