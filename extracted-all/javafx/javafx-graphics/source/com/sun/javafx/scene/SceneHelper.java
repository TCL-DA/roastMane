package com.sun.javafx.scene;

import com.sun.glass.ui.Accessible;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.util.Utils;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Window;

public final class SceneHelper {
   private static SceneHelper.SceneAccessor sceneAccessor;

   private SceneHelper() {
   }

   public static void enableInputMethodEvents(Scene var0, boolean var1) {
      sceneAccessor.enableInputMethodEvents(var0, var1);
   }

   public static void processKeyEvent(Scene var0, KeyEvent var1) {
      sceneAccessor.processKeyEvent(var0, var1);
   }

   public static void processMouseEvent(Scene var0, MouseEvent var1) {
      sceneAccessor.processMouseEvent(var0, var1);
   }

   public static void preferredSize(Scene var0) {
      sceneAccessor.preferredSize(var0);
   }

   public static void disposePeer(Scene var0) {
      sceneAccessor.disposePeer(var0);
   }

   public static void initPeer(Scene var0) {
      sceneAccessor.initPeer(var0);
   }

   public static void setWindow(Scene var0, Window var1) {
      sceneAccessor.setWindow(var0, var1);
   }

   public static TKScene getPeer(Scene var0) {
      return sceneAccessor.getPeer(var0);
   }

   public static void setAllowPGAccess(boolean var0) {
      sceneAccessor.setAllowPGAccess(var0);
   }

   public static void parentEffectiveOrientationInvalidated(Scene var0) {
      sceneAccessor.parentEffectiveOrientationInvalidated(var0);
   }

   public static Camera getEffectiveCamera(Scene var0) {
      return sceneAccessor.getEffectiveCamera(var0);
   }

   public static Scene createPopupScene(Parent var0) {
      return sceneAccessor.createPopupScene(var0);
   }

   public static Accessible getAccessible(Scene var0) {
      return sceneAccessor.getAccessible(var0);
   }

   public static void setSceneAccessor(SceneHelper.SceneAccessor var0) {
      if (sceneAccessor != null) {
         throw new IllegalStateException();
      } else {
         sceneAccessor = var0;
      }
   }

   public static SceneHelper.SceneAccessor getSceneAccessor() {
      if (sceneAccessor == null) {
         throw new IllegalStateException();
      } else {
         return sceneAccessor;
      }
   }

   static {
      Utils.forceInit(Scene.class);
   }

   public interface SceneAccessor {
      void enableInputMethodEvents(Scene var1, boolean var2);

      void processKeyEvent(Scene var1, KeyEvent var2);

      void processMouseEvent(Scene var1, MouseEvent var2);

      void preferredSize(Scene var1);

      void disposePeer(Scene var1);

      void initPeer(Scene var1);

      void setWindow(Scene var1, Window var2);

      TKScene getPeer(Scene var1);

      void setAllowPGAccess(boolean var1);

      void parentEffectiveOrientationInvalidated(Scene var1);

      Camera getEffectiveCamera(Scene var1);

      Scene createPopupScene(Parent var1);

      void setTransientFocusContainer(Scene var1, Node var2);

      Accessible getAccessible(Scene var1);
   }
}
