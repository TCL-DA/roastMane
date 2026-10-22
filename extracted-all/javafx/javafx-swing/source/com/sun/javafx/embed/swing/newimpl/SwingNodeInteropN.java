package com.sun.javafx.embed.swing.newimpl;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.embed.swing.DisposerRecord;
import com.sun.javafx.embed.swing.FXDnD;
import com.sun.javafx.embed.swing.SwingCursors;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.util.Utils;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javax.swing.JComponent;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.LightweightContentWrapper;
import jdk.swing.interop.LightweightFrameWrapper;

public class SwingNodeInteropN {
   private volatile LightweightFrameWrapper lwFrame;
   private static SwingNodeInteropN.OptionalMethod<LightweightFrameWrapper> jlfNotifyDisplayChanged = new SwingNodeInteropN.OptionalMethod<>(
      LightweightFrameWrapper.class, "notifyDisplayChanged", double.class, double.class
   );
   private static Class lwFrameWrapperClass = null;
   private static final SwingNodeInteropN.OptionalMethod<LightweightFrameWrapper> jlfSetHostBounds;

   private static native void overrideNativeWindowHandle(Class var0, LightweightFrameWrapper var1, long var2, Runnable var4);

   public LightweightFrameWrapper createLightweightFrame() {
      this.lwFrame = new LightweightFrameWrapper();
      return this.lwFrame;
   }

   public LightweightFrameWrapper getLightweightFrame() {
      return this.lwFrame;
   }

   public MouseEvent createMouseEvent(Object var1, int var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11, int var12) {
      LightweightFrameWrapper var13 = (LightweightFrameWrapper)var1;
      return var13.createMouseEvent(var13, var2, var3, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public MouseWheelEvent createMouseWheelEvent(Object var1, int var2, int var3, int var4, int var5) {
      LightweightFrameWrapper var6 = (LightweightFrameWrapper)var1;
      return var6.createMouseWheelEvent(var6, var2, var3, var4, var5);
   }

   public KeyEvent createKeyEvent(Object var1, int var2, long var3, int var5, int var6, char var7) {
      LightweightFrameWrapper var8 = (LightweightFrameWrapper)var1;
      return var8.createKeyEvent(var8, var2, var3, var5, var6, var7);
   }

   public AWTEvent createUngrabEvent(Object var1) {
      LightweightFrameWrapper var2 = (LightweightFrameWrapper)var1;
      return var2.createUngrabEvent(var2);
   }

   public void overrideNativeWindowHandle(Object var1, long var2, Runnable var4) {
      LightweightFrameWrapper var5 = (LightweightFrameWrapper)var1;
      overrideNativeWindowHandle(lwFrameWrapperClass, var5, var2, var4);
   }

   public void notifyDisplayChanged(Object var1, double var2, double var4) {
      LightweightFrameWrapper var6 = (LightweightFrameWrapper)var1;
      if (jlfNotifyDisplayChanged.isIntegerApi()) {
         jlfNotifyDisplayChanged.invoke(var6, (int)Math.round(var2));
      } else {
         jlfNotifyDisplayChanged.invoke(var6, var2, var4);
      }
   }

   public void setHostBounds(Object var1, int var2, int var3, int var4, int var5) {
      LightweightFrameWrapper var6 = (LightweightFrameWrapper)var1;
      jlfSetHostBounds.invoke(var6, var2, var3, var4, var5);
   }

   public void setContent(Object var1, Object var2) {
      LightweightFrameWrapper var3 = (LightweightFrameWrapper)var1;
      LightweightContentWrapper var4 = (LightweightContentWrapper)var2;
      var3.setContent(var4);
   }

   public void setVisible(Object var1, boolean var2) {
      LightweightFrameWrapper var3 = (LightweightFrameWrapper)var1;
      var3.setVisible(var2);
   }

   public void setBounds(Object var1, int var2, int var3, int var4, int var5) {
      LightweightFrameWrapper var6 = (LightweightFrameWrapper)var1;
      var6.setBounds(var2, var3, var4, var5);
   }

   public LightweightContentWrapper createSwingNodeContent(JComponent var1, SwingNode var2) {
      return new SwingNodeInteropN.SwingNodeContent(var1, var2);
   }

   public DisposerRecord createSwingNodeDisposer(Object var1) {
      LightweightFrameWrapper var2 = (LightweightFrameWrapper)var1;
      return new SwingNodeInteropN.SwingNodeDisposer(var2);
   }

   public void emulateActivation(Object var1, boolean var2) {
      LightweightFrameWrapper var3 = (LightweightFrameWrapper)var1;
      var3.emulateActivation(var2);
   }

   public void disposeFrame(Object var1) {
      LightweightFrameWrapper var2 = (LightweightFrameWrapper)var1;
      var2.dispose();
   }

   public void addWindowFocusListener(Object var1, WindowFocusListener var2) {
      LightweightFrameWrapper var3 = (LightweightFrameWrapper)var1;
      var3.addWindowFocusListener(var2);
   }

   static {
      if (!jlfNotifyDisplayChanged.isSupported()) {
         jlfNotifyDisplayChanged = new SwingNodeInteropN.OptionalMethod<>(LightweightFrameWrapper.class, "notifyDisplayChanged", int.class);
      }

      try {
         lwFrameWrapperClass = Class.forName("jdk.swing.interop.LightweightFrameWrapper");
      } catch (Throwable var1) {
      }

      Utils.loadNativeSwingLibrary();
      jlfSetHostBounds = new SwingNodeInteropN.OptionalMethod<>(LightweightFrameWrapper.class, "setHostBounds", int.class, int.class, int.class, int.class);
   }

   private static final class OptionalMethod<T> {
      private final Method method;
      private final boolean isIntegerAPI;

      OptionalMethod(Class<T> var1, String var2, Class<?>... var3) {
         Method var4;
         try {
            var4 = var1.getMethod(var2, var3);
         } catch (NoSuchMethodException var6) {
            var4 = null;
         } catch (Throwable var7) {
            throw new RuntimeException("Error when calling " + var1.getName() + ".getMethod('" + var2 + "').", var7);
         }

         this.method = var4;
         this.isIntegerAPI = var3 != null && var3.length > 0 && var3[0] == int.class;
      }

      public boolean isSupported() {
         return this.method != null;
      }

      public boolean isIntegerApi() {
         return this.isIntegerAPI;
      }

      public Object invoke(T var1, Object... var2) {
         if (this.method != null) {
            try {
               return this.method.invoke(var1, var2);
            } catch (Throwable var4) {
               throw new RuntimeException("Error when calling " + var1.getClass().getName() + "." + this.method.getName() + "().", var4);
            }
         } else {
            return null;
         }
      }
   }

   private static class SwingNodeContent extends LightweightContentWrapper {
      private JComponent comp;
      private volatile FXDnD dnd;
      private WeakReference<SwingNode> swingNodeRef;

      SwingNodeContent(JComponent var1, SwingNode var2) {
         this.comp = var1;
         this.swingNodeRef = new WeakReference<>(var2);
      }

      @Override
      public JComponent getComponent() {
         return this.comp;
      }

      @Override
      public void paintLock() {
         SwingNode var1 = this.swingNodeRef.get();
         if (var1 != null) {
            SwingNodeHelper.getPaintLock(var1).lock();
         }
      }

      @Override
      public void paintUnlock() {
         SwingNode var1 = this.swingNodeRef.get();
         if (var1 != null) {
            SwingNodeHelper.getPaintLock(var1).unlock();
         }
      }

      @Override
      public void imageBufferReset(int[] var1, int var2, int var3, int var4, int var5, int var6) {
         this.imageBufferReset(var1, var2, var3, var4, var5, var6, 1);
      }

      public void imageBufferReset(int[] var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         SwingNode var8 = this.swingNodeRef.get();
         if (var8 != null) {
            SwingNodeHelper.setImageBuffer(var8, var1, var2, var3, var4, var5, var6, (double)var7, (double)var7);
         }
      }

      @Override
      public void imageBufferReset(int[] var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9) {
         SwingNode var11 = this.swingNodeRef.get();
         if (var11 != null) {
            SwingNodeHelper.setImageBuffer(var11, var1, var2, var3, var4, var5, var6, var7, var9);
         }
      }

      @Override
      public void imageReshaped(int var1, int var2, int var3, int var4) {
         SwingNode var5 = this.swingNodeRef.get();
         if (var5 != null) {
            SwingNodeHelper.setImageBounds(var5, var1, var2, var3, var4);
         }
      }

      @Override
      public void imageUpdated(int var1, int var2, int var3, int var4) {
         SwingNode var5 = this.swingNodeRef.get();
         if (var5 != null) {
            SwingNodeHelper.repaintDirtyRegion(var5, var1, var2, var3, var4);
         }
      }

      @Override
      public void focusGrabbed() {
         SwingNodeHelper.runOnFxThread(() -> {
            if (!PlatformUtil.isLinux()) {
               SwingNode var1 = this.swingNodeRef.get();
               if (var1 != null) {
                  Scene var2 = var1.getScene();
                  if (var2 != null && var2.getWindow() != null && WindowHelper.getPeer(var2.getWindow()) != null) {
                     WindowHelper.getPeer(var2.getWindow()).grabFocus();
                     SwingNodeHelper.setGrabbed(var1, true);
                  }
               }
            }
         });
      }

      @Override
      public void focusUngrabbed() {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var1 = this.swingNodeRef.get();
            if (var1 != null) {
               Scene var2 = var1.getScene();
               SwingNodeHelper.ungrabFocus(var1, false);
            }
         });
      }

      @Override
      public void preferredSizeChanged(int var1, int var2) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var3 = this.swingNodeRef.get();
            if (var3 != null) {
               SwingNodeHelper.setSwingPrefWidth(var3, var1);
               SwingNodeHelper.setSwingPrefHeight(var3, var2);
               NodeHelper.notifyLayoutBoundsChanged(var3);
            }
         });
      }

      @Override
      public void maximumSizeChanged(int var1, int var2) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var3 = this.swingNodeRef.get();
            if (var3 != null) {
               SwingNodeHelper.setSwingMaxWidth(var3, var1);
               SwingNodeHelper.setSwingMaxHeight(var3, var2);
               NodeHelper.notifyLayoutBoundsChanged(var3);
            }
         });
      }

      @Override
      public void minimumSizeChanged(int var1, int var2) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var3 = this.swingNodeRef.get();
            if (var3 != null) {
               SwingNodeHelper.setSwingMinWidth(var3, var1);
               SwingNodeHelper.setSwingMinHeight(var3, var2);
               NodeHelper.notifyLayoutBoundsChanged(var3);
            }
         });
      }

      public void setCursor(Cursor var1) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var2 = this.swingNodeRef.get();
            if (var2 != null) {
               var2.setCursor(SwingCursors.embedCursorToCursor(var1));
            }
         });
      }

      private void initDnD() {
         synchronized (this) {
            if (this.dnd == null) {
               SwingNode var2 = this.swingNodeRef.get();
               if (var2 != null) {
                  this.dnd = new FXDnD(var2);
               }
            }
         }
      }

      @Override
      public synchronized <T extends DragGestureRecognizer> T createDragGestureRecognizer(
         Class<T> var1, DragSource var2, Component var3, int var4, DragGestureListener var5
      ) {
         this.initDnD();
         return this.dnd.createDragGestureRecognizer(var1, var2, var3, var4, var5);
      }

      @Override
      public DragSourceContextWrapper createDragSourceContext(DragGestureEvent var1) throws InvalidDnDOperationException {
         this.initDnD();
         return (DragSourceContextWrapper)this.dnd.createDragSourceContext(var1);
      }

      @Override
      public void addDropTarget(DropTarget var1) {
         this.initDnD();
         this.dnd.addDropTarget(var1);
      }

      @Override
      public void removeDropTarget(DropTarget var1) {
         this.initDnD();
         this.dnd.removeDropTarget(var1);
      }
   }

   private static class SwingNodeDisposer implements DisposerRecord {
      LightweightFrameWrapper lwFrame;

      SwingNodeDisposer(LightweightFrameWrapper var1) {
         this.lwFrame = var1;
      }

      @Override
      public void dispose() {
         if (this.lwFrame != null) {
            this.lwFrame.dispose();
            this.lwFrame = null;
         }
      }
   }
}
