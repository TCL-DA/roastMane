package javafx.embed.swing;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.embed.swing.Disposer;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.embed.swing.newimpl.SwingNodeInteropN;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGExternalNode;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.stage.FocusUngrabEvent;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.tk.TKStage;
import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowFocusListener;
import java.lang.ref.WeakReference;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.text.Font;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javax.swing.JComponent;
import javax.swing.Timer;
import jdk.swing.interop.LightweightFrameWrapper;

public class SwingNode extends Node {
   private static boolean isThreadMerged;
   private double fxWidth;
   private double fxHeight;
   private int swingPrefWidth;
   private int swingPrefHeight;
   private int swingMaxWidth;
   private int swingMaxHeight;
   private int swingMinWidth;
   private int swingMinHeight;
   private volatile JComponent content;
   private volatile Object lwFrame;
   private NGExternalNode peer;
   private final ReentrantLock paintLock = new ReentrantLock();
   private boolean skipBackwardUnrgabNotification;
   private boolean grabbed;
   private Timer deactivate;
   private SwingNodeInteropN swNodeIOP;
   private EventHandler windowHiddenHandler;
   private Window hWindow;
   private List<Runnable> peerRequests;
   private final InvalidationListener locationListener;
   private final EventHandler<FocusUngrabEvent> ungrabHandler;
   private final ChangeListener<Boolean> windowVisibleListener;
   private final ChangeListener<Window> sceneWindowListener;

   private final Object getLightweightFrame() {
      return this.lwFrame;
   }

   private ReentrantLock getPaintLock() {
      return this.paintLock;
   }

   public SwingNode() {
      SwingNodeHelper.initHelper(this);
      this.windowHiddenHandler = var1 -> {
         if (this.lwFrame != null && var1.getTarget() instanceof Window) {
            Window var2 = (Window)var1.getTarget();
            TKStage var3 = WindowHelper.getPeer(var2);
            if (var3 != null) {
               if (isThreadMerged) {
                  this.swNodeIOP.overrideNativeWindowHandle(this.lwFrame, 0L, null);
               } else {
                  var3.postponeClose();
                  SwingNodeHelper.runOnEDT(
                     () -> this.swNodeIOP.overrideNativeWindowHandle(this.lwFrame, 0L, () -> SwingNodeHelper.runOnFxThread(() -> var3.closePostponed()))
                  );
               }
            }
         }
      };
      this.hWindow = null;
      this.peerRequests = new ArrayList<>();
      this.locationListener = var1 -> this.locateLwFrame();
      this.ungrabHandler = var1 -> {
         if (!this.skipBackwardUnrgabNotification && this.lwFrame != null) {
            AccessController.doPrivileged(new SwingNode.PostEventAction(this.swNodeIOP.createUngrabEvent(this.lwFrame)));
         }
      };
      this.windowVisibleListener = (var1, var2, var3) -> {
         if (!var3) {
            this.disposeLwFrame();
         } else {
            this.setContent(this.content);
         }
      };
      this.sceneWindowListener = (var1, var2, var3) -> {
         if (var2 != null) {
            this.removeWindowListeners(var2);
         }

         this.notifyNativeHandle(var3);
         if (var3 != null) {
            this.addWindowListeners(var3);
         }
      };
      this.swNodeIOP = new SwingNodeInteropN();
      this.setFocusTraversable(true);
      this.setEventHandler(MouseEvent.ANY, new SwingNode.SwingMouseEventHandler());
      this.setEventHandler(KeyEvent.ANY, new SwingNode.SwingKeyEventHandler());
      this.setEventHandler(ScrollEvent.SCROLL, new SwingNode.SwingScrollEventHandler());
      this.focusedProperty().addListener((var1, var2, var3) -> this.activateLwFrame(var3));
      Font.getFamilies();
   }

   private void notifyNativeHandle(Window var1) {
      if (this.hWindow != var1) {
         if (this.hWindow != null) {
            this.hWindow.removeEventHandler(WindowEvent.WINDOW_HIDDEN, this.windowHiddenHandler);
         }

         if (var1 != null) {
            var1.addEventHandler(WindowEvent.WINDOW_HIDDEN, this.windowHiddenHandler);
         }

         this.hWindow = var1;
      }

      if (this.lwFrame != null) {
         long var2 = 0L;
         if (var1 != null) {
            TKStage var4 = WindowHelper.getPeer(var1);
            if (var4 != null) {
               var2 = var4.getRawHandle();
            }
         }

         this.swNodeIOP.overrideNativeWindowHandle(this.lwFrame, var2, null);
      }
   }

   public void setContent(JComponent var1) {
      this.content = var1;
      SwingNodeHelper.runOnEDT(() -> this.setContentImpl(var1));
   }

   public JComponent getContent() {
      return this.content;
   }

   private void setContentImpl(JComponent var1) {
      if (this.lwFrame != null) {
         this.swNodeIOP.disposeFrame(this.lwFrame);
         this.lwFrame = null;
      }

      if (var1 != null) {
         this.lwFrame = this.swNodeIOP.createLightweightFrame();
         SwingNode.SwingNodeWindowFocusListener var2 = new SwingNode.SwingNodeWindowFocusListener(this);
         this.swNodeIOP.addWindowFocusListener(this.lwFrame, var2);
         if (this.getScene() != null) {
            Window var3 = this.getScene().getWindow();
            if (var3 != null) {
               this.swNodeIOP.notifyDisplayChanged(this.lwFrame, var3.getRenderScaleX(), var3.getRenderScaleY());
            }
         }

         this.swNodeIOP.setContent(this.lwFrame, this.swNodeIOP.createSwingNodeContent(var1, this));
         this.swNodeIOP.setVisible(this.lwFrame, true);
         Disposer.addRecord(this, this.swNodeIOP.createSwingNodeDisposer(this.lwFrame));
         if (this.getScene() != null) {
            this.notifyNativeHandle(this.getScene().getWindow());
         }

         SwingNodeHelper.runOnFxThread(() -> {
            this.locateLwFrame();
            if (this.focusedProperty().get()) {
               this.activateLwFrame(true);
            }
         });
      }
   }

   void setImageBuffer(int[] var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9) {
      Runnable var11 = () -> this.peer.setImageBuffer(IntBuffer.wrap(var1), var2, var3, var4, var5, (float)var4, (float)var5, var6, var7, var9);
      SwingNodeHelper.runOnFxThread(() -> {
         if (this.peer != null) {
            var11.run();
         } else {
            this.peerRequests.clear();
            this.peerRequests.add(var11);
         }
      });
   }

   void setImageBounds(int var1, int var2, int var3, int var4) {
      Runnable var5 = () -> this.peer.setImageBounds(var1, var2, var3, var4, (float)var3, (float)var4);
      SwingNodeHelper.runOnFxThread(() -> {
         if (this.peer != null) {
            var5.run();
         } else {
            this.peerRequests.add(var5);
         }
      });
   }

   void repaintDirtyRegion(int var1, int var2, int var3, int var4) {
      Runnable var5 = () -> {
         this.peer.repaintDirtyRegion(var1, var2, var3, var4);
         NodeHelper.markDirty(this, DirtyBits.NODE_CONTENTS);
      };
      SwingNodeHelper.runOnFxThread(() -> {
         if (this.peer != null) {
            var5.run();
         } else {
            this.peerRequests.add(var5);
         }
      });
   }

   public boolean isResizable() {
      return true;
   }

   public void resize(double var1, double var3) {
      super.resize(var1, var3);
      if (var1 != this.fxWidth || var3 != this.fxHeight) {
         this.fxWidth = var1;
         this.fxHeight = var3;
         NodeHelper.geomChanged(this);
         NodeHelper.markDirty(this, DirtyBits.NODE_GEOMETRY);
         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.locateLwFrame();
            }
         });
      }
   }

   public double prefWidth(double var1) {
      return (double)this.swingPrefWidth;
   }

   public double prefHeight(double var1) {
      return (double)this.swingPrefHeight;
   }

   public double maxWidth(double var1) {
      return (double)this.swingMaxWidth;
   }

   public double maxHeight(double var1) {
      return (double)this.swingMaxHeight;
   }

   public double minWidth(double var1) {
      return (double)this.swingMinWidth;
   }

   public double minHeight(double var1) {
      return (double)this.swingMinHeight;
   }

   private boolean doComputeContains(double var1, double var3) {
      return true;
   }

   private void removeSceneListeners(Scene var1) {
      Window var2 = var1.getWindow();
      if (var2 != null) {
         this.removeWindowListeners(var2);
      }

      var1.windowProperty().removeListener(this.sceneWindowListener);
   }

   private void addSceneListeners(Scene var1) {
      Window var2 = var1.getWindow();
      if (var2 != null) {
         this.addWindowListeners(var2);
         this.notifyNativeHandle(var2);
      }

      var1.windowProperty().addListener(this.sceneWindowListener);
   }

   private void addWindowListeners(Window var1) {
      var1.xProperty().addListener(this.locationListener);
      var1.yProperty().addListener(this.locationListener);
      var1.widthProperty().addListener(this.locationListener);
      var1.heightProperty().addListener(this.locationListener);
      var1.renderScaleXProperty().addListener(this.locationListener);
      var1.addEventHandler(FocusUngrabEvent.FOCUS_UNGRAB, this.ungrabHandler);
      var1.showingProperty().addListener(this.windowVisibleListener);
      this.setLwFrameScale(var1.getRenderScaleX(), var1.getRenderScaleY());
   }

   private void removeWindowListeners(Window var1) {
      var1.xProperty().removeListener(this.locationListener);
      var1.yProperty().removeListener(this.locationListener);
      var1.widthProperty().removeListener(this.locationListener);
      var1.heightProperty().removeListener(this.locationListener);
      var1.renderScaleXProperty().removeListener(this.locationListener);
      var1.removeEventHandler(FocusUngrabEvent.FOCUS_UNGRAB, this.ungrabHandler);
      var1.showingProperty().removeListener(this.windowVisibleListener);
   }

   private NGNode doCreatePeer() {
      this.peer = new NGExternalNode();
      this.peer.setLock(this.paintLock);

      for (Runnable var2 : this.peerRequests) {
         var2.run();
      }

      this.peerRequests = null;
      if (this.getScene() != null) {
         this.addSceneListeners(this.getScene());
      }

      this.sceneProperty().addListener((var1, var2x, var3) -> {
         if (var2x != null) {
            this.removeSceneListeners(var2x);
            this.disposeLwFrame();
         }

         if (var3 != null) {
            if (this.content != null && this.lwFrame == null) {
               this.setContent(this.content);
            }

            this.addSceneListeners(var3);
         }
      });
      NodeHelper.treeVisibleProperty(this).addListener((var1, var2x, var3) -> this.setLwFrameVisible(var3));
      return this.peer;
   }

   private void doUpdatePeer() {
      if (NodeHelper.isDirty(this, DirtyBits.NODE_VISIBLE) || NodeHelper.isDirty(this, DirtyBits.NODE_BOUNDS)) {
         this.locateLwFrame();
      }

      if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
         this.peer.markContentDirty();
      }
   }

   private void locateLwFrame() {
      if (this.getScene() != null && this.lwFrame != null && this.getScene().getWindow() != null && this.getScene().getWindow().isShowing()) {
         Window var1 = this.getScene().getWindow();
         double var2 = var1.getRenderScaleX();
         double var4 = var1.getRenderScaleY();
         Point2D var6 = this.localToScene(0.0, 0.0);
         int var7 = (int)var1.getX();
         int var8 = (int)var1.getY();
         int var9 = (int)var1.getWidth();
         int var10 = (int)var1.getHeight();
         int var11 = (int)Math.round(var1.getX() + this.getScene().getX() + var6.getX());
         int var12 = (int)Math.round(var1.getY() + this.getScene().getY() + var6.getY());
         int var13 = (int)this.fxWidth;
         int var14 = (int)this.fxHeight;
         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.swNodeIOP.notifyDisplayChanged(this.lwFrame, var2, var4);
               this.swNodeIOP.setBounds(this.lwFrame, var11, var12, var13, var14);
               this.swNodeIOP.setHostBounds(this.lwFrame, var7, var8, var9, var10);
            }
         });
      }
   }

   private void activateLwFrame(boolean var1) {
      if (this.lwFrame != null) {
         if (PlatformUtil.isLinux()) {
            if (this.deactivate != null && this.deactivate.isRunning()) {
               this.deactivate.stop();
            } else if (!var1) {
               this.deactivate = new Timer(50, var1x -> {
                  if (this.lwFrame != null) {
                     this.swNodeIOP.emulateActivation(this.lwFrame, false);
                  }
               });
               this.deactivate.start();
               return;
            }
         }

         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.swNodeIOP.emulateActivation(this.lwFrame, var1);
            }
         });
      }
   }

   private void disposeLwFrame() {
      if (this.lwFrame != null) {
         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.swNodeIOP.disposeFrame(this.lwFrame);
               this.lwFrame = null;
            }
         });
      }
   }

   private void setLwFrameVisible(boolean var1) {
      if (this.lwFrame != null) {
         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.swNodeIOP.setVisible(this.lwFrame, var1);
            }
         });
      }
   }

   private void setLwFrameScale(double var1, double var3) {
      if (this.lwFrame != null) {
         SwingNodeHelper.runOnEDT(() -> {
            if (this.lwFrame != null) {
               this.swNodeIOP.notifyDisplayChanged(this.lwFrame, var1, var3);
            }
         });
      }
   }

   private BaseBounds doComputeGeomBounds(BaseBounds var1, BaseTransform var2) {
      var1.deriveWithNewBounds(0.0F, 0.0F, 0.0F, (float)this.fxWidth, (float)this.fxHeight, 0.0F);
      var2.transform(var1, var1);
      return var1;
   }

   private void ungrabFocus(boolean var1) {
      if (!PlatformUtil.isLinux()) {
         if (this.grabbed && this.getScene() != null && this.getScene().getWindow() != null && WindowHelper.getPeer(this.getScene().getWindow()) != null) {
            this.skipBackwardUnrgabNotification = !var1;
            WindowHelper.getPeer(this.getScene().getWindow()).ungrabFocus();
            this.skipBackwardUnrgabNotification = false;
            this.grabbed = false;
         }
      }
   }

   static {
      Object var0 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
         @Override
         public Object run() {
            SwingNode.isThreadMerged = Boolean.valueOf(System.getProperty("javafx.embed.singleThread"));
            return null;
         }
      });
      SwingNodeHelper.setSwingNodeAccessor(new SwingNodeHelper.SwingNodeAccessor() {
         @Override
         public NGNode doCreatePeer(Node var1) {
            return ((SwingNode)var1).doCreatePeer();
         }

         @Override
         public void doUpdatePeer(Node var1) {
            ((SwingNode)var1).doUpdatePeer();
         }

         @Override
         public BaseBounds doComputeGeomBounds(Node var1, BaseBounds var2, BaseTransform var3) {
            return ((SwingNode)var1).doComputeGeomBounds(var2, var3);
         }

         @Override
         public boolean doComputeContains(Node var1, double var2, double var4) {
            return ((SwingNode)var1).doComputeContains(var2, var4);
         }

         @Override
         public Object getLightweightFrame(SwingNode var1) {
            return var1.getLightweightFrame();
         }

         @Override
         public ReentrantLock getPaintLock(SwingNode var1) {
            return var1.getPaintLock();
         }

         @Override
         public void setImageBuffer(SwingNode var1, int[] var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10) {
            var1.setImageBuffer(var2, var3, var4, var5, var6, var7, var8, var10);
         }

         @Override
         public void setImageBounds(SwingNode var1, int var2, int var3, int var4, int var5) {
            var1.setImageBounds(var2, var3, var4, var5);
         }

         @Override
         public void repaintDirtyRegion(SwingNode var1, int var2, int var3, int var4, int var5) {
            var1.repaintDirtyRegion(var2, var3, var4, var5);
         }

         @Override
         public void ungrabFocus(SwingNode var1, boolean var2) {
            var1.ungrabFocus(var2);
         }

         @Override
         public void setSwingPrefWidth(SwingNode var1, int var2) {
            var1.swingPrefWidth = var2;
         }

         @Override
         public void setSwingPrefHeight(SwingNode var1, int var2) {
            var1.swingPrefHeight = var2;
         }

         @Override
         public void setSwingMaxWidth(SwingNode var1, int var2) {
            var1.swingMaxWidth = var2;
         }

         @Override
         public void setSwingMaxHeight(SwingNode var1, int var2) {
            var1.swingMaxHeight = var2;
         }

         @Override
         public void setSwingMinWidth(SwingNode var1, int var2) {
            var1.swingMinWidth = var2;
         }

         @Override
         public void setSwingMinHeight(SwingNode var1, int var2) {
            var1.swingMinHeight = var2;
         }

         @Override
         public void setGrabbed(SwingNode var1, boolean var2) {
            var1.grabbed = var2;
         }
      });
   }

   private class PostEventAction implements PrivilegedAction<Void> {
      private AWTEvent event;

      PostEventAction(AWTEvent var2) {
         this.event = var2;
      }

      public Void run() {
         EventQueue var1 = Toolkit.getDefaultToolkit().getSystemEventQueue();
         var1.postEvent(this.event);
         return null;
      }
   }

   private class SwingKeyEventHandler implements EventHandler<KeyEvent> {
      public void handle(KeyEvent var1) {
         LightweightFrameWrapper var2 = SwingNode.this.swNodeIOP.getLightweightFrame();
         if (var2 != null) {
            if (!var1.getCharacter().isEmpty()) {
               if (var1.getCode() == KeyCode.LEFT
                  || var1.getCode() == KeyCode.RIGHT
                  || var1.getCode() == KeyCode.UP
                  || var1.getCode() == KeyCode.DOWN
                  || var1.getCode() == KeyCode.TAB) {
                  var1.consume();
               }

               int var3 = SwingEvents.fxKeyEventTypeToKeyID(var1);
               if (var3 >= 0) {
                  int var4 = SwingEvents.fxKeyModsToKeyMods(var1);
                  int var5 = var1.getCode().getCode();
                  char var6 = var1.getCharacter().charAt(0);
                  if (var1.getEventType() == KeyEvent.KEY_PRESSED) {
                     String var7 = var1.getText();
                     if (var7.length() == 1) {
                        var6 = var7.charAt(0);
                     }
                  }

                  long var11 = System.currentTimeMillis();
                  java.awt.event.KeyEvent var9 = SwingNode.this.swNodeIOP.createKeyEvent(var2, var3, var11, var4, var5, var6);
                  Void var10 = AccessController.doPrivileged(SwingNode.this.new PostEventAction(var9));
               }
            }
         }
      }
   }

   private class SwingMouseEventHandler implements EventHandler<MouseEvent> {
      private final Set<MouseButton> mouseClickedAllowed = new HashSet<>();

      public void handle(MouseEvent var1) {
         LightweightFrameWrapper var2 = SwingNode.this.swNodeIOP.getLightweightFrame();
         if (var2 != null) {
            int var3 = SwingEvents.fxMouseEventTypeToMouseID(var1);
            if (var3 >= 0) {
               var1.consume();
               EventType var4 = var1.getEventType();
               if (var4 == MouseEvent.MOUSE_PRESSED) {
                  this.mouseClickedAllowed.add(var1.getButton());
               } else if (var4 != MouseEvent.MOUSE_RELEASED) {
                  if (var4 == MouseEvent.MOUSE_DRAGGED) {
                     this.mouseClickedAllowed.clear();
                  } else if (var4 == MouseEvent.MOUSE_CLICKED) {
                     if (var1.getClickCount() == 1 && !this.mouseClickedAllowed.contains(var1.getButton())) {
                        return;
                     }

                     this.mouseClickedAllowed.remove(var1.getButton());
                  }
               }

               int var5 = SwingEvents.fxMouseModsToMouseMods(var1);
               boolean var6 = var1.isPopupTrigger();
               int var7 = SwingEvents.fxMouseButtonToMouseButton(var1);
               long var8 = System.currentTimeMillis();
               int var10 = (int)Math.round(var1.getX());
               int var11 = (int)Math.round(var1.getY());
               int var12 = (int)Math.round(var1.getScreenX());
               int var13 = (int)Math.round(var1.getScreenY());
               java.awt.event.MouseEvent var14 = SwingNode.this.swNodeIOP
                  .createMouseEvent(var2, var3, var8, var5, var10, var11, var12, var13, var1.getClickCount(), var6, var7);
               Void var15 = AccessController.doPrivileged(SwingNode.this.new PostEventAction(var14));
            }
         }
      }
   }

   private static class SwingNodeWindowFocusListener implements WindowFocusListener {
      private WeakReference<SwingNode> swingNodeRef;

      SwingNodeWindowFocusListener(SwingNode var1) {
         this.swingNodeRef = new WeakReference<>(var1);
      }

      @Override
      public void windowGainedFocus(java.awt.event.WindowEvent var1) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var1x = this.swingNodeRef.get();
            if (var1x != null) {
               var1x.requestFocus();
            }
         });
      }

      @Override
      public void windowLostFocus(java.awt.event.WindowEvent var1) {
         SwingNodeHelper.runOnFxThread(() -> {
            SwingNode var1x = this.swingNodeRef.get();
            if (var1x != null) {
               var1x.ungrabFocus(true);
            }
         });
      }
   }

   private class SwingScrollEventHandler implements EventHandler<ScrollEvent> {
      public void handle(ScrollEvent var1) {
         LightweightFrameWrapper var2 = SwingNode.this.swNodeIOP.getLightweightFrame();
         if (var2 != null) {
            int var3 = SwingEvents.fxScrollModsToMouseWheelMods(var1);
            boolean var4 = (var3 & 64) != 0;
            if (!var4 && var1.getDeltaY() != 0.0) {
               this.sendMouseWheelEvent(var2, var1.getX(), var1.getY(), var3, var1.getDeltaY() / var1.getMultiplierY());
            }

            double var5 = var4 && var1.getDeltaY() != 0.0 ? var1.getDeltaY() / var1.getMultiplierY() : var1.getDeltaX() / var1.getMultiplierX();
            if (var5 != 0.0) {
               var3 |= 64;
               this.sendMouseWheelEvent(var2, var1.getX(), var1.getY(), var3, var5);
            }
         }
      }

      private void sendMouseWheelEvent(Object var1, double var2, double var4, int var6, double var7) {
         int var9 = (int)var7;
         int var10 = (int)Math.signum(var7);
         if ((double)var10 * var7 < 1.0) {
            var9 = var10;
         }

         int var11 = (int)Math.round(var2);
         int var12 = (int)Math.round(var4);
         MouseWheelEvent var13 = SwingNode.this.swNodeIOP.createMouseWheelEvent(var1, var6, var11, var12, -var9);
         Void var14 = AccessController.doPrivileged(SwingNode.this.new PostEventAction(var13));
      }
   }
}
