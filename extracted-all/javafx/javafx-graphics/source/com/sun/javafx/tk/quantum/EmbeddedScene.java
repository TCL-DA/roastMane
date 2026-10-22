package com.sun.javafx.tk.quantum;

import com.sun.glass.ui.Pixels;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedSceneDTInterface;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.HostDragStartListener;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.scene.input.KeyCodeMap;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.TKClipboard;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.image.PixelFormat;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.InputMethodTextRun;
import javafx.scene.input.KeyEvent;

final class EmbeddedScene extends GlassScene implements EmbeddedSceneInterface {
   private HostInterface host;
   private UploadingPainter painter;
   private PaintRenderJob paintRenderJob;
   private float renderScaleX;
   private float renderScaleY;
   private final EmbeddedSceneDnD embeddedDnD;
   private volatile IntBuffer texBits;
   private volatile int texLineStride;
   private volatile float texScaleFactorX = 1.0F;
   private volatile float texScaleFactorY = 1.0F;
   private volatile PixelFormat<?> pixelFormat;

   public EmbeddedScene(HostInterface var1, boolean var2, boolean var3) {
      super(var2, var3);
      this.sceneState = new EmbeddedState(this);
      this.host = var1;
      this.embeddedDnD = new EmbeddedSceneDnD(this);
      PaintCollector var4 = PaintCollector.getInstance();
      this.painter = new UploadingPainter(this);
      this.paintRenderJob = new PaintRenderJob(this, var4.getRendered(), this.painter);
      int var5 = Pixels.getNativeFormat();
      ByteOrder var6 = ByteOrder.nativeOrder();
      if (var5 == 1 && var6 == ByteOrder.LITTLE_ENDIAN) {
         this.pixelFormat = PixelFormat.getIntArgbPreInstance();
      } else if (var5 == 2 && var6 == ByteOrder.BIG_ENDIAN) {
         this.pixelFormat = PixelFormat.getIntArgbInstance();
      }

      assert this.pixelFormat != null;
   }

   @Override
   public void dispose() {
      assert this.host != null;

      QuantumToolkit.runWithRenderLock(() -> {
         this.host.setEmbeddedScene(null);
         this.host = null;
         this.updateSceneState();
         this.painter = null;
         this.paintRenderJob = null;
         this.texBits = null;
         return null;
      });
      super.dispose();
   }

   @Override
   void setStage(GlassStage var1) {
      super.setStage(var1);

      assert this.host != null;

      this.host.setEmbeddedScene(var1 != null ? this : null);
   }

   @Override
   protected boolean isSynchronous() {
      return false;
   }

   @Override
   public void setRoot(NGNode var1) {
      super.setRoot(var1);
      this.painter.setRoot(var1);
   }

   @Override
   public TKClipboard createDragboard(boolean var1) {
      return this.embeddedDnD.createDragboard(var1);
   }

   @Override
   public void enableInputMethodEvents(boolean var1) {
      if (QuantumToolkit.verbose) {
         System.err.println("EmbeddedScene.enableInputMethodEvents " + var1);
      }
   }

   @Override
   public void finishInputMethodComposition() {
      if (QuantumToolkit.verbose) {
         System.err.println("EmbeddedScene.finishInputMethodComposition");
      }
   }

   @Override
   public void setPixelScaleFactors(float var1, float var2) {
      this.renderScaleX = var1;
      this.renderScaleY = var2;
      this.entireSceneNeedsRepaint();
   }

   public float getRenderScaleX() {
      return this.renderScaleX;
   }

   public float getRenderScaleY() {
      return this.renderScaleY;
   }

   @Override
   public PixelFormat<?> getPixelFormat() {
      return this.pixelFormat;
   }

   void uploadPixels(Pixels var1) {
      this.texBits = (IntBuffer)var1.getPixels();
      this.texLineStride = var1.getWidthUnsafe();
      this.texScaleFactorX = var1.getScaleXUnsafe();
      this.texScaleFactorY = var1.getScaleYUnsafe();
      if (this.host != null) {
         this.host.repaint();
      }
   }

   @Override
   public void repaint() {
      Toolkit var1 = Toolkit.getToolkit();
      var1.addRenderJob(this.paintRenderJob);
   }

   @Override
   public boolean traverseOut(Direction var1) {
      if (var1 == Direction.NEXT) {
         return this.host.traverseFocusOut(true);
      } else {
         return var1 == Direction.PREVIOUS ? this.host.traverseFocusOut(false) : false;
      }
   }

   @Override
   public void setSize(int var1, int var2) {
      Platform.runLater(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            if (this.sceneListener != null) {
               this.sceneListener.changedSize((float)var1, (float)var2);
            }

            return null;
         }), this.getAccessControlContext()));
   }

   @Override
   public boolean getPixels(IntBuffer var1, int var2, int var3) {
      return QuantumToolkit.runWithRenderLock(() -> {
         if (this.getRenderScaleX() == this.texScaleFactorX && this.getRenderScaleY() == this.texScaleFactorY && this.texBits != null) {
            int var4 = (int)Math.ceil((double)((float)var2 * this.texScaleFactorX));
            int var5 = (int)Math.ceil((double)((float)var3 * this.texScaleFactorY));
            var1.rewind();
            this.texBits.rewind();
            if (var1.capacity() == this.texBits.capacity()) {
               var1.put(this.texBits);
               return true;
            } else {
               int var6 = Math.min(var4, this.texLineStride);
               int var7 = Math.min(var5, this.texBits.capacity() / this.texLineStride);
               int[] var8 = new int[var6];

               for (int var9 = 0; var9 < var7; var9++) {
                  this.texBits.position(var9 * this.texLineStride);
                  this.texBits.get(var8, 0, var6);
                  var1.position(var9 * var4);
                  var1.put(var8);
               }

               return true;
            }
         } else {
            return false;
         }
      });
   }

   @Override
   protected Color getClearColor() {
      return this.fillPaint != null && this.fillPaint.getType() == Paint.Type.COLOR && ((Color)this.fillPaint).getAlpha() == 0.0F
         ? (Color)this.fillPaint
         : super.getClearColor();
   }

   @Override
   public void mouseEvent(
      int var1,
      int var2,
      boolean var3,
      boolean var4,
      boolean var5,
      boolean var6,
      boolean var7,
      int var8,
      int var9,
      int var10,
      int var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15,
      boolean var16
   ) {
      Platform.runLater(
         () -> AccessController.doPrivileged(
               (PrivilegedAction<Void>)(() -> {
                  if (this.sceneListener == null) {
                     return null;
                  } else {
                     assert var1 != 2;

                     EventType var17 = AbstractEvents.mouseIDToFXEventID(var1);
                     this.sceneListener
                        .mouseEvent(
                           var17,
                           (double)var8,
                           (double)var9,
                           (double)var10,
                           (double)var11,
                           AbstractEvents.mouseButtonToFXMouseButton(var2),
                           var16,
                           false,
                           var12,
                           var13,
                           var14,
                           var15,
                           var3,
                           var4,
                           var5,
                           var6,
                           var7
                        );
                     return null;
                  }
               }),
               this.getAccessControlContext()
            )
      );
   }

   @Override
   public void scrollEvent(
      int var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      boolean var22,
      boolean var23,
      boolean var24,
      boolean var25,
      boolean var26
   ) {
      Platform.runLater(
         () -> AccessController.doPrivileged(
               (PrivilegedAction<Void>)(() -> {
                  if (this.sceneListener == null) {
                     return null;
                  } else {
                     this.sceneListener
                        .scrollEvent(
                           AbstractEvents.scrollIDToFXEventType(var1),
                           var2,
                           var4,
                           var6,
                           var8,
                           var10,
                           var12,
                           0,
                           0,
                           0,
                           0,
                           0,
                           var14,
                           var16,
                           var18,
                           var20,
                           var22,
                           var23,
                           var24,
                           var25,
                           false,
                           var26
                        );
                     return null;
                  }
               }),
               this.getAccessControlContext()
            )
      );
   }

   @Override
   public void inputMethodEvent(EventType<InputMethodEvent> var1, ObservableList<InputMethodTextRun> var2, String var3, int var4) {
      Platform.runLater(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            if (this.sceneListener != null) {
               this.sceneListener.inputMethodEvent(var1, var2, var3, var4);
            }

            return null;
         })));
   }

   @Override
   public void menuEvent(int var1, int var2, int var3, int var4, boolean var5) {
      Platform.runLater(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            if (this.sceneListener != null) {
               this.sceneListener.menuEvent((double)var1, (double)var2, (double)var3, (double)var4, var5);
            }

            return null;
         }), this.getAccessControlContext()));
   }

   @Override
   public void keyEvent(int var1, int var2, char[] var3, int var4) {
      Platform.runLater(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            if (this.sceneListener != null) {
               boolean var5 = (var4 & 1) != 0;
               boolean var6 = (var4 & 2) != 0;
               boolean var7 = (var4 & 4) != 0;
               boolean var8 = (var4 & 8) != 0;
               String var9 = new String(var3);
               KeyEvent var11 = new KeyEvent(AbstractEvents.keyIDToFXEventType(var1), var9, var9, KeyCodeMap.valueOf(var2), var5, var6, var7, var8);
               this.sceneListener.keyEvent(var11);
            }

            return null;
         }), this.getAccessControlContext()));
   }

   @Override
   public void zoomEvent(
      int var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      boolean var18
   ) {
      Platform.runLater(
         () -> AccessController.doPrivileged(
               (PrivilegedAction<Void>)(() -> {
                  if (this.sceneListener == null) {
                     return null;
                  } else {
                     this.sceneListener
                        .zoomEvent(AbstractEvents.zoomIDToFXEventType(var1), var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, false, var18);
                     return null;
                  }
               }),
               this.getAccessControlContext()
            )
      );
   }

   @Override
   public void rotateEvent(
      int var1,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      boolean var18
   ) {
      Platform.runLater(
         () -> AccessController.doPrivileged(
               (PrivilegedAction<Void>)(() -> {
                  if (this.sceneListener == null) {
                     return null;
                  } else {
                     this.sceneListener
                        .rotateEvent(AbstractEvents.rotateIDToFXEventType(var1), var2, var4, var6, var8, var10, var12, var14, var15, var16, var17, false, var18);
                     return null;
                  }
               }),
               this.getAccessControlContext()
            )
      );
   }

   @Override
   public void swipeEvent(int var1, double var2, double var4, double var6, double var8, boolean var10, boolean var11, boolean var12, boolean var13) {
      Platform.runLater(() -> AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
            if (this.sceneListener == null) {
               return null;
            } else {
               this.sceneListener.swipeEvent(AbstractEvents.swipeIDToFXEventType(var1), 0, var2, var4, var6, var8, var10, var11, var12, var13, false);
               return null;
            }
         }), this.getAccessControlContext()));
   }

   @Override
   public void setCursor(Object var1) {
      super.setCursor(var1);
      this.host.setCursor((CursorFrame)var1);
   }

   @Override
   public void setDragStartListener(HostDragStartListener var1) {
      this.embeddedDnD.setDragStartListener(var1);
   }

   @Override
   public EmbeddedSceneDTInterface createDropTarget() {
      return this.embeddedDnD.createDropTarget();
   }

   @Override
   public InputMethodRequests getInputMethodRequests() {
      return this.inputMethodRequests;
   }
}
