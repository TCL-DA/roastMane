package com.sun.javafx.stage;

import com.sun.javafx.tk.TKStage;
import com.sun.javafx.util.Utils;
import java.security.AccessControlContext;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.stage.Screen;
import javafx.stage.Window;

public class WindowHelper {
   private static final WindowHelper theInstance = new WindowHelper();
   private static WindowHelper.WindowAccessor windowAccessor;

   protected WindowHelper() {
   }

   private static WindowHelper getInstance() {
      return theInstance;
   }

   public static void initHelper(Window var0) {
      setHelper(var0, getInstance());
   }

   private static WindowHelper getHelper(Window var0) {
      return windowAccessor.getHelper(var0);
   }

   protected static void setHelper(Window var0, WindowHelper var1) {
      windowAccessor.setHelper(var0, var1);
   }

   public static void visibleChanging(Window var0, boolean var1) {
      getHelper(var0).visibleChangingImpl(var0, var1);
   }

   public static void visibleChanged(Window var0, boolean var1) {
      getHelper(var0).visibleChangedImpl(var0, var1);
   }

   protected void visibleChangingImpl(Window var1, boolean var2) {
      windowAccessor.doVisibleChanging(var1, var2);
   }

   protected void visibleChangedImpl(Window var1, boolean var2) {
      windowAccessor.doVisibleChanged(var1, var2);
   }

   public static TKStage getPeer(Window var0) {
      return windowAccessor.getPeer(var0);
   }

   public static void setPeer(Window var0, TKStage var1) {
      windowAccessor.setPeer(var0, var1);
   }

   public static WindowPeerListener getPeerListener(Window var0) {
      return windowAccessor.getPeerListener(var0);
   }

   public static void setPeerListener(Window var0, WindowPeerListener var1) {
      windowAccessor.setPeerListener(var0, var1);
   }

   public static void setFocused(Window var0, boolean var1) {
      windowAccessor.setFocused(var0, var1);
   }

   public static void notifyLocationChanged(Window var0, double var1, double var3) {
      windowAccessor.notifyLocationChanged(var0, var1, var3);
   }

   public static void notifySizeChanged(Window var0, double var1, double var3) {
      windowAccessor.notifySizeChanged(var0, var1, var3);
   }

   public static void notifyScaleChanged(Window var0, double var1, double var3) {
      windowAccessor.notifyScaleChanged(var0, var1, var3);
   }

   static AccessControlContext getAccessControlContext(Window var0) {
      return windowAccessor.getAccessControlContext(var0);
   }

   public static void setWindowAccessor(WindowHelper.WindowAccessor var0) {
      if (windowAccessor != null) {
         throw new IllegalStateException();
      } else {
         windowAccessor = var0;
      }
   }

   public static WindowHelper.WindowAccessor getWindowAccessor() {
      return windowAccessor;
   }

   static {
      Utils.forceInit(Window.class);
   }

   public interface WindowAccessor {
      WindowHelper getHelper(Window var1);

      void setHelper(Window var1, WindowHelper var2);

      void doVisibleChanging(Window var1, boolean var2);

      void doVisibleChanged(Window var1, boolean var2);

      TKStage getPeer(Window var1);

      void setPeer(Window var1, TKStage var2);

      WindowPeerListener getPeerListener(Window var1);

      void setPeerListener(Window var1, WindowPeerListener var2);

      void setFocused(Window var1, boolean var2);

      void notifyLocationChanged(Window var1, double var2, double var4);

      void notifySizeChanged(Window var1, double var2, double var4);

      void notifyScreenChanged(Window var1, Object var2, Object var3);

      float getPlatformScaleX(Window var1);

      float getPlatformScaleY(Window var1);

      void notifyScaleChanged(Window var1, double var2, double var4);

      ReadOnlyObjectProperty<Screen> screenProperty(Window var1);

      AccessControlContext getAccessControlContext(Window var1);
   }
}
