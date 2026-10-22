package javafx.embed.swing;

import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.application.PlatformImpl.FinishListener;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.EmbeddedStageInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.embed.swing.SwingCursors;
import com.sun.javafx.embed.swing.SwingDnD;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.embed.swing.newimpl.JFXPanelInteropN;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.stage.EmbeddedWindow;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.FocusEvent.Cause;
import java.awt.im.InputMethodRequests;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Platform;
import javafx.scene.Scene;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class JFXPanel extends JComponent {
   private static final PlatformLogger log = PlatformLogger.getLogger(JFXPanel.class.getName());
   private static AtomicInteger instanceCount = new AtomicInteger(0);
   private static FinishListener finishListener;
   private transient JFXPanel.HostContainer hostContainer;
   private transient volatile EmbeddedWindow stage;
   private transient volatile Scene scene;
   private transient SwingDnD dnd;
   private transient EmbeddedStageInterface stagePeer;
   private transient EmbeddedSceneInterface scenePeer;
   private int pWidth;
   private int pHeight;
   private double scaleFactorX = 1.0;
   private double scaleFactorY = 1.0;
   private volatile int pPreferredWidth = -1;
   private volatile int pPreferredHeight = -1;
   private volatile int screenX = 0;
   private volatile int screenY = 0;
   private BufferedImage pixelsIm;
   private volatile float opacity = 1.0F;
   private AtomicInteger disableCount = new AtomicInteger(0);
   private boolean isCapturingMouse = false;
   private static boolean fxInitialized;
   private JFXPanelInteropN jfxPanelIOP;
   private transient AWTEventListener ungrabListener = var1 -> {
      if (this.jfxPanelIOP.isUngrabEvent(var1)) {
         SwingNodeHelper.runOnFxThread(() -> {
            if (this.stagePeer != null && this.getScene() != null && this.getScene().getFocusOwner() != null && this.getScene().getFocusOwner().isFocused()) {
               this.stagePeer.focusUngrab();
            }
         });
      }

      if (var1 instanceof MouseEvent && var1.getID() == 501 && var1.getSource() instanceof Component) {
         Window var2 = SwingUtilities.getWindowAncestor(this);
         Component var3 = (Component)var1.getSource();
         Window var4 = var3 instanceof Window ? (Window)var3 : SwingUtilities.getWindowAncestor(var3);
         if (var2 == var4) {
            SwingNodeHelper.runOnFxThread(() -> {
               if (this.stagePeer != null) {
                  this.stagePeer.focusUngrab();
               }
            });
         }
      }
   };

   private synchronized void registerFinishListener() {
      if (instanceCount.getAndIncrement() <= 0) {
         finishListener = new FinishListener() {
            public void idle(boolean var1) {
            }

            public void exitCalled() {
            }
         };
         PlatformImpl.addListener(finishListener);
      }
   }

   private synchronized void deregisterFinishListener() {
      if (instanceCount.decrementAndGet() <= 0) {
         PlatformImpl.removeListener(finishListener);
         finishListener = null;
      }
   }

   private static synchronized void initFx() {
      if (!fxInitialized) {
         EventQueue var0 = AccessController.doPrivileged(Toolkit.getDefaultToolkit()::getSystemEventQueue);
         if (EventQueue.isDispatchThread()) {
            SecondaryLoop var1 = var0.createSecondaryLoop();
            Throwable[] var2 = new Throwable[]{null};
            new Thread(() -> {
               try {
                  PlatformImpl.startup(() -> {
                  });
               } catch (Throwable var6) {
                  var2[0] = var6;
               } finally {
                  var1.exit();
               }
            }).start();
            var1.enter();
            if (var2[0] != null) {
               if (var2[0] instanceof RuntimeException) {
                  throw (RuntimeException)var2[0];
               }

               if (var2[0] instanceof Error) {
                  throw (Error)var2[0];
               }

               throw new RuntimeException("FX initialization failed", var2[0]);
            }
         } else {
            PlatformImpl.startup(() -> {
            });
         }

         fxInitialized = true;
      }
   }

   public JFXPanel() {
      this.jfxPanelIOP = new JFXPanelInteropN();
      initFx();
      this.hostContainer = new JFXPanel.HostContainer();
      this.enableEvents(231485L);
      this.setFocusable(true);
      this.setFocusTraversalKeysEnabled(false);
   }

   public Scene getScene() {
      return this.scene;
   }

   public void setScene(Scene var1) {
      if (com.sun.javafx.tk.Toolkit.getToolkit().isFxUserThread()) {
         this.setSceneImpl(var1);
      } else {
         EventQueue var2 = AccessController.doPrivileged(Toolkit.getDefaultToolkit()::getSystemEventQueue);
         SecondaryLoop var3 = var2.createSecondaryLoop();
         Platform.runLater(() -> {
            try {
               this.setSceneImpl(var1);
            } finally {
               var3.exit();
            }
         });
         var3.enter();
      }
   }

   private void setSceneImpl(Scene var1) {
      if (this.stage != null && var1 == null) {
         this.stage.hide();
         this.stage = null;
      }

      this.scene = var1;
      if (this.stage == null && var1 != null) {
         this.stage = new EmbeddedWindow(this.hostContainer);
      }

      if (this.stage != null) {
         this.stage.setScene(var1);
         if (!this.stage.isShowing()) {
            this.stage.show();
         }
      }
   }

   @Override
   public final void setOpaque(boolean var1) {
      if (!var1) {
         super.setOpaque(var1);
      }
   }

   @Override
   public final boolean isOpaque() {
      return false;
   }

   private void sendMouseEventToFX(MouseEvent var1) {
      if (this.scenePeer != null && this.isFxEnabled()) {
         switch (var1.getID()) {
            case 501:
            case 502:
            case 506:
               if (var1.getButton() > 5) {
                  return;
               }
            default:
               int var2 = var1.getModifiersEx();
               boolean var3 = (var2 & 1024) != 0;
               boolean var4 = (var2 & 2048) != 0;
               boolean var5 = (var2 & 4096) != 0;
               boolean var6 = (var2 & MouseEvent.getMaskForButton(4)) != 0;
               boolean var7 = (var2 & MouseEvent.getMaskForButton(5)) != 0;
               if (var1.getID() == 506) {
                  if (!this.isCapturingMouse) {
                     return;
                  }
               } else if (var1.getID() == 501) {
                  this.isCapturingMouse = true;
               } else if (var1.getID() == 502) {
                  if (!this.isCapturingMouse) {
                     return;
                  }

                  this.isCapturingMouse = var3 || var4 || var5 || var6 || var7;
               } else if (var1.getID() == 500) {
                  return;
               }

               boolean var8 = false;
               if (var1.getID() == 501 || var1.getID() == 502) {
                  var8 = var1.isPopupTrigger();
               }

               if (var1.getID() == 507) {
                  this.scenePeer
                     .scrollEvent(
                        7,
                        0.0,
                        (double)(-SwingEvents.getWheelRotation(var1)),
                        0.0,
                        0.0,
                        40.0,
                        40.0,
                        (double)var1.getX(),
                        (double)var1.getY(),
                        (double)var1.getXOnScreen(),
                        (double)var1.getYOnScreen(),
                        (var2 & 64) != 0,
                        (var2 & 128) != 0,
                        (var2 & 512) != 0,
                        (var2 & 256) != 0,
                        false
                     );
               } else {
                  this.scenePeer
                     .mouseEvent(
                        SwingEvents.mouseIDToEmbedMouseType(var1.getID()),
                        SwingEvents.mouseButtonToEmbedMouseButton(var1.getButton(), var2),
                        var3,
                        var4,
                        var5,
                        var6,
                        var7,
                        var1.getX(),
                        var1.getY(),
                        var1.getXOnScreen(),
                        var1.getYOnScreen(),
                        (var2 & 64) != 0,
                        (var2 & 128) != 0,
                        (var2 & 512) != 0,
                        (var2 & 256) != 0,
                        var8
                     );
               }

               if (var1.isPopupTrigger()) {
                  this.scenePeer.menuEvent(var1.getX(), var1.getY(), var1.getXOnScreen(), var1.getYOnScreen(), false);
               }
         }
      }
   }

   @Override
   protected void processMouseEvent(MouseEvent var1) {
      if (var1.getID() == 501 && var1.getButton() == 1 && this.isFocusable() && !this.hasFocus()) {
         this.requestFocus();
         if (this.stagePeer != null) {
            byte var2 = 0;
            this.stagePeer.setFocused(true, var2);
         }
      }

      this.sendMouseEventToFX(var1);
      super.processMouseEvent(var1);
   }

   @Override
   protected void processMouseMotionEvent(MouseEvent var1) {
      this.sendMouseEventToFX(var1);
      super.processMouseMotionEvent(var1);
   }

   @Override
   protected void processMouseWheelEvent(MouseWheelEvent var1) {
      this.sendMouseEventToFX(var1);
      super.processMouseWheelEvent(var1);
   }

   private void sendKeyEventToFX(KeyEvent var1) {
      if (this.scenePeer != null && this.isFxEnabled()) {
         char[] var2 = var1.getKeyChar() == '\uffff' ? new char[0] : new char[]{SwingEvents.keyCharToEmbedKeyChar(var1.getKeyChar())};
         this.scenePeer
            .keyEvent(
               SwingEvents.keyIDToEmbedKeyType(var1.getID()), var1.getKeyCode(), var2, SwingEvents.keyModifiersToEmbedKeyModifiers(var1.getModifiersEx())
            );
      }
   }

   @Override
   protected void processKeyEvent(KeyEvent var1) {
      this.sendKeyEventToFX(var1);
      super.processKeyEvent(var1);
   }

   private void sendResizeEventToFX() {
      if (this.stagePeer != null) {
         this.stagePeer.setSize(this.pWidth, this.pHeight);
      }

      if (this.scenePeer != null) {
         this.scenePeer.setSize(this.pWidth, this.pHeight);
      }
   }

   @Override
   protected void processComponentEvent(ComponentEvent var1) {
      switch (var1.getID()) {
         case 100:
            if (this.updateScreenLocation()) {
               this.sendMoveEventToFX();
            }
            break;
         case 101:
            this.updateComponentSize();
      }

      super.processComponentEvent(var1);
   }

   private void updateComponentSize() {
      int var1 = this.pWidth;
      int var2 = this.pHeight;
      this.pWidth = Math.max(0, this.getWidth());
      this.pHeight = Math.max(0, this.getHeight());
      if (this.getBorder() != null) {
         Insets var3 = this.getBorder().getBorderInsets(this);
         this.pWidth = this.pWidth - (var3.left + var3.right);
         this.pHeight = this.pHeight - (var3.top + var3.bottom);
      }

      double var8 = this.scaleFactorX;
      double var5 = this.scaleFactorY;
      Graphics var7 = this.getGraphics();
      var8 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
      var5 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleY();
      if (var1 != this.pWidth || var2 != this.pHeight || var8 != this.scaleFactorX || var5 != this.scaleFactorY) {
         this.createResizePixelBuffer(var8, var5);
         if (this.scenePeer != null) {
            this.scenePeer.setPixelScaleFactors((float)var8, (float)var5);
         }

         this.scaleFactorX = var8;
         this.scaleFactorY = var5;
         this.sendResizeEventToFX();
      }
   }

   private boolean updateScreenLocation() {
      synchronized (this.getTreeLock()) {
         if (this.isShowing()) {
            Point var2 = this.getLocationOnScreen();
            this.screenX = var2.x;
            this.screenY = var2.y;
            return true;
         } else {
            return false;
         }
      }
   }

   private void sendMoveEventToFX() {
      if (this.stagePeer != null) {
         this.stagePeer.setLocation(this.screenX, this.screenY);
      }
   }

   @Override
   protected void processHierarchyBoundsEvent(HierarchyEvent var1) {
      if (var1.getID() == 1401 && this.updateScreenLocation()) {
         this.sendMoveEventToFX();
      }

      super.processHierarchyBoundsEvent(var1);
   }

   @Override
   protected void processHierarchyEvent(HierarchyEvent var1) {
      if ((var1.getChangeFlags() & 4L) != 0L && this.updateScreenLocation()) {
         this.sendMoveEventToFX();
      }

      super.processHierarchyEvent(var1);
   }

   private void sendFocusEventToFX(FocusEvent var1) {
      if (this.stage != null && this.stagePeer != null && this.isFxEnabled()) {
         boolean var2 = var1.getID() == 1004;
         int var3 = var2 ? 0 : 3;
         if (var2) {
            if (var1.getCause() == Cause.TRAVERSAL_FORWARD) {
               var3 = 1;
            } else if (var1.getCause() == Cause.TRAVERSAL_BACKWARD) {
               var3 = 2;
            }
         }

         this.stagePeer.setFocused(var2, var3);
      }
   }

   @Override
   protected void processFocusEvent(FocusEvent var1) {
      this.sendFocusEventToFX(var1);
      super.processFocusEvent(var1);
   }

   private void createResizePixelBuffer(double var1, double var3) {
      if (this.scenePeer != null && this.pWidth > 0 && this.pHeight > 0) {
         BufferedImage var5 = this.pixelsIm;
         int var6 = (int)Math.ceil((double)this.pWidth * var1);
         int var7 = (int)Math.ceil((double)this.pHeight * var3);
         this.pixelsIm = new BufferedImage(var6, var7, SwingFXUtils.getBestBufferedImageType(this.scenePeer.getPixelFormat(), null, false));
         if (var5 != null) {
            double var8 = var1 / this.scaleFactorX;
            double var10 = var3 / this.scaleFactorY;
            int var12 = (int)Math.ceil((double)var5.getWidth() * var8);
            int var13 = (int)Math.ceil((double)var5.getHeight() * var10);
            Graphics var14 = this.pixelsIm.getGraphics();

            try {
               var14.drawImage(var5, 0, 0, var12, var13, null);
            } finally {
               var14.dispose();
            }
         }
      } else {
         this.pixelsIm = null;
      }
   }

   @Override
   protected void processInputMethodEvent(InputMethodEvent var1) {
      if (var1.getID() == 1100) {
         this.sendInputMethodEventToFX(var1);
      }

      super.processInputMethodEvent(var1);
   }

   private void sendInputMethodEventToFX(InputMethodEvent var1) {
      String var2 = InputMethodSupport.getTextForEvent(var1);
      int var3 = 0;
      if (var1.getCaret() != null) {
         var3 = var1.getCaret().getInsertionIndex();
      }

      this.scenePeer
         .inputMethodEvent(
            javafx.scene.input.InputMethodEvent.INPUT_METHOD_TEXT_CHANGED,
            InputMethodSupport.inputMethodEventComposed(var2, var1.getCommittedCharacterCount()),
            var2.substring(0, var1.getCommittedCharacterCount()),
            var3
         );
   }

   @Override
   protected void paintComponent(Graphics var1) {
      if (this.scenePeer != null) {
         if (this.pixelsIm == null) {
            this.createResizePixelBuffer(this.scaleFactorX, this.scaleFactorY);
            if (this.pixelsIm == null) {
               return;
            }
         }

         DataBufferInt var2 = (DataBufferInt)this.pixelsIm.getRaster().getDataBuffer();
         int[] var3 = var2.getData();
         IntBuffer var4 = IntBuffer.wrap(var3);
         if (!this.scenePeer.getPixels(var4, this.pWidth, this.pHeight)) {
         }

         Graphics var5 = null;

         try {
            var5 = var1.create();
            if (this.opacity < 1.0F && var5 instanceof Graphics2D) {
               Graphics2D var6 = (Graphics2D)var5;
               AlphaComposite var7 = AlphaComposite.getInstance(3, this.opacity);
               var6.setComposite(var7);
            }

            if (this.getBorder() != null) {
               Insets var15 = this.getBorder().getBorderInsets(this);
               var5.translate(var15.left, var15.top);
            }

            var5.drawImage(this.pixelsIm, 0, 0, this.pWidth, this.pHeight, null);
            double var16 = this.scaleFactorX;
            double var8 = this.scaleFactorY;
            var16 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleX();
            var8 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getDefaultTransform().getScaleY();
            if (this.scaleFactorX != var16 || this.scaleFactorY != var8) {
               this.createResizePixelBuffer(var16, var8);
               this.scenePeer.setPixelScaleFactors((float)var16, (float)var8);
               this.scaleFactorX = var16;
               this.scaleFactorY = var8;
            }
         } catch (Throwable var13) {
            var13.printStackTrace();
         } finally {
            if (var5 != null) {
               var5.dispose();
            }
         }
      }
   }

   @Override
   public Dimension getPreferredSize() {
      return !this.isPreferredSizeSet() && this.scenePeer != null ? new Dimension(this.pPreferredWidth, this.pPreferredHeight) : super.getPreferredSize();
   }

   private boolean isFxEnabled() {
      return this.disableCount.get() == 0;
   }

   private void setFxEnabled(boolean var1) {
      if (!var1) {
         if (this.disableCount.incrementAndGet() == 1 && this.dnd != null) {
            this.dnd.removeNotify();
         }
      } else {
         if (this.disableCount.get() == 0) {
            return;
         }

         if (this.disableCount.decrementAndGet() == 0 && this.dnd != null) {
            this.dnd.addNotify();
         }
      }
   }

   @Override
   public void addNotify() {
      super.addNotify();
      this.registerFinishListener();
      AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         this.getToolkit().addAWTEventListener(this.ungrabListener, this.jfxPanelIOP.getMask());
         return null;
      }));
      this.updateComponentSize();
      SwingNodeHelper.runOnFxThread(() -> {
         if (this.stage != null && !this.stage.isShowing()) {
            this.stage.show();
            this.sendMoveEventToFX();
         }
      });
   }

   @Override
   public InputMethodRequests getInputMethodRequests() {
      EmbeddedSceneInterface var1 = this.scenePeer;
      return var1 == null ? null : new InputMethodSupport.InputMethodRequestsAdapter(var1.getInputMethodRequests());
   }

   @Override
   public void removeNotify() {
      SwingNodeHelper.runOnFxThread(() -> {
         if (this.stage != null && this.stage.isShowing()) {
            this.stage.hide();
         }
      });
      this.pixelsIm = null;
      this.pWidth = 0;
      this.pHeight = 0;
      super.removeNotify();
      AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
         this.getToolkit().removeAWTEventListener(this.ungrabListener);
         return null;
      }));
      this.getInputContext().removeNotify(this);
      this.deregisterFinishListener();
   }

   private void invokeOnClientEDT(Runnable var1) {
      this.jfxPanelIOP.postEvent(this, new InvocationEvent(this, var1));
   }

   final BufferedImage test_getPixelsIm() {
      return this.pixelsIm;
   }

   private class HostContainer implements HostInterface {
      public void setEmbeddedStage(EmbeddedStageInterface var1) {
         JFXPanel.this.stagePeer = var1;
         if (JFXPanel.this.stagePeer != null) {
            if (JFXPanel.this.pWidth > 0 && JFXPanel.this.pHeight > 0) {
               JFXPanel.this.stagePeer.setSize(JFXPanel.this.pWidth, JFXPanel.this.pHeight);
            }

            JFXPanel.this.invokeOnClientEDT(() -> {
               if (JFXPanel.this.stagePeer != null && JFXPanel.this.isFocusOwner()) {
                  JFXPanel.this.stagePeer.setFocused(true, 0);
               }
            });
            JFXPanel.this.sendMoveEventToFX();
         }
      }

      public void setEmbeddedScene(EmbeddedSceneInterface var1) {
         if (JFXPanel.this.scenePeer != var1) {
            JFXPanel.this.scenePeer = var1;
            if (JFXPanel.this.scenePeer == null) {
               JFXPanel.this.invokeOnClientEDT(() -> {
                  if (JFXPanel.this.dnd != null) {
                     JFXPanel.this.dnd.removeNotify();
                     JFXPanel.this.dnd = null;
                  }
               });
            } else {
               if (JFXPanel.this.pWidth > 0 && JFXPanel.this.pHeight > 0) {
                  JFXPanel.this.scenePeer.setSize(JFXPanel.this.pWidth, JFXPanel.this.pHeight);
               }

               JFXPanel.this.scenePeer.setPixelScaleFactors((float)JFXPanel.this.scaleFactorX, (float)JFXPanel.this.scaleFactorY);
               JFXPanel.this.invokeOnClientEDT(() -> {
                  JFXPanel.this.dnd = new SwingDnD(JFXPanel.this, JFXPanel.this.scenePeer);
                  JFXPanel.this.dnd.addNotify();
                  if (JFXPanel.this.scenePeer != null) {
                     JFXPanel.this.scenePeer.setDragStartListener(JFXPanel.this.dnd.getDragStartListener());
                  }
               });
            }
         }
      }

      public boolean requestFocus() {
         return JFXPanel.this.requestFocusInWindow();
      }

      public boolean traverseFocusOut(boolean var1) {
         KeyboardFocusManager var2 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
         if (var1) {
            var2.focusNextComponent(JFXPanel.this);
         } else {
            var2.focusPreviousComponent(JFXPanel.this);
         }

         return true;
      }

      public void setPreferredSize(int var1, int var2) {
         JFXPanel.this.invokeOnClientEDT(() -> {
            JFXPanel.this.pPreferredWidth = var1;
            JFXPanel.this.pPreferredHeight = var2;
            JFXPanel.this.revalidate();
         });
      }

      public void repaint() {
         JFXPanel.this.invokeOnClientEDT(() -> JFXPanel.this.repaint());
      }

      public void setEnabled(boolean var1) {
         JFXPanel.this.setFxEnabled(var1);
      }

      public void setCursor(CursorFrame var1) {
         Cursor var2 = this.getPlatformCursor(var1);
         JFXPanel.this.invokeOnClientEDT(() -> JFXPanel.this.setCursor(var2));
      }

      private Cursor getPlatformCursor(CursorFrame var1) {
         Cursor var2 = (Cursor)var1.getPlatformCursor(Cursor.class);
         if (var2 != null) {
            return var2;
         } else {
            Cursor var3 = SwingCursors.embedCursorToCursor(var1);
            var1.setPlatforCursor(Cursor.class, var3);
            return var3;
         }
      }

      public boolean grabFocus() {
         if (PlatformUtil.isLinux()) {
            return true;
         } else {
            JFXPanel.this.invokeOnClientEDT(() -> {
               Window var1 = SwingUtilities.getWindowAncestor(JFXPanel.this);
               if (var1 != null) {
                  JFXPanel.this.jfxPanelIOP.grab(JFXPanel.this.getToolkit(), var1);
               }
            });
            return true;
         }
      }

      public void ungrabFocus() {
         if (!PlatformUtil.isLinux()) {
            JFXPanel.this.invokeOnClientEDT(() -> {
               Window var1 = SwingUtilities.getWindowAncestor(JFXPanel.this);
               if (var1 != null) {
                  JFXPanel.this.jfxPanelIOP.ungrab(JFXPanel.this.getToolkit(), var1);
               }
            });
         }
      }
   }
}
