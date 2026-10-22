package com.sun.javafx.tk.quantum;

import com.sun.javafx.tk.FocusCause;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.TKStageListener;
import com.sun.javafx.tk.Toolkit;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

abstract class GlassStage implements TKStage {
   private static final List<GlassStage> windows = new ArrayList<>();
   private static List<TKStage> importantWindows = new ArrayList<>();
   private GlassScene scene;
   protected TKStageListener stageListener;
   private boolean visible;
   private boolean important = true;
   private AccessControlContext accessCtrlCtx = null;
   protected static final AtomicReference<GlassStage> activeFSWindow = new AtomicReference<>();

   protected GlassStage() {
      windows.add(this);
   }

   @Override
   public void close() {
      assert this.scene == null;

      windows.remove(this);
      importantWindows.remove(this);
      notifyWindowListeners();
   }

   @Override
   public void setTKStageListener(TKStageListener var1) {
      this.stageListener = var1;
   }

   protected final GlassScene getScene() {
      return this.scene;
   }

   @Override
   public void setScene(TKScene var1) {
      if (this.scene != null) {
         this.scene.setStage(null);
      }

      this.scene = (GlassScene)var1;
      if (this.scene != null) {
         this.scene.setStage(this);
      }
   }

   final AccessControlContext getAccessControlContext() {
      if (this.accessCtrlCtx == null) {
         throw new RuntimeException("Stage security context has not been set!");
      } else {
         return this.accessCtrlCtx;
      }
   }

   static AccessControlContext doIntersectionPrivilege(PrivilegedAction<AccessControlContext> var0, AccessControlContext var1, AccessControlContext var2) {
      return AccessController.doPrivileged(
         (PrivilegedAction<AccessControlContext>)(() -> AccessController.doPrivilegedWithCombiner(
               (PrivilegedAction<AccessControlContext>)(() -> AccessController.getContext()), var1
            )),
         var2
      );
   }

   public final void setSecurityContext(AccessControlContext var1) {
      if (this.accessCtrlCtx != null) {
         throw new RuntimeException("Stage security context has been already set!");
      } else {
         AccessControlContext var2 = AccessController.getContext();
         this.accessCtrlCtx = doIntersectionPrivilege(() -> AccessController.getContext(), var2, var1);
      }
   }

   @Override
   public void requestFocus() {
   }

   @Override
   public void requestFocus(FocusCause var1) {
   }

   @Override
   public void setVisible(boolean var1) {
      this.visible = var1;
      if (var1) {
         if (this.important) {
            importantWindows.add(this);
            notifyWindowListeners();
         }
      } else if (this.important) {
         importantWindows.remove(this);
         notifyWindowListeners();
      }

      if (this.scene != null) {
         this.scene.stageVisible(var1);
      }
   }

   boolean isVisible() {
      return this.visible;
   }

   protected void setPlatformEnabled(boolean var1) {
   }

   void windowsSetEnabled(boolean var1) {
      for (GlassStage var5 : windows.toArray(new GlassStage[windows.size()])) {
         if (var5 != this && windows.contains(var5)) {
            var5.setPlatformEnabled(var1);
         }
      }
   }

   @Override
   public void setImportant(boolean var1) {
      this.important = var1;
   }

   private static void notifyWindowListeners() {
      Toolkit.getToolkit().notifyWindowListeners(importantWindows);
   }

   static void requestClosingAllWindows() {
      GlassStage var0 = activeFSWindow.get();
      if (var0 != null) {
         var0.setFullScreen(false);
      }

      for (GlassStage var4 : windows.toArray(new GlassStage[windows.size()])) {
         if (windows.contains(var4) && var4.isVisible() && var4.stageListener != null) {
            AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
               var4.stageListener.closing();
               return null;
            }), var4.getAccessControlContext());
         }
      }
   }
}
