package com.sun.javafx.embed.swing.newimpl;

import com.sun.javafx.embed.swing.CachingTransferable;
import com.sun.javafx.embed.swing.FXDnD;
import com.sun.javafx.embed.swing.SwingDnD;
import com.sun.javafx.embed.swing.SwingEvents;
import com.sun.javafx.embed.swing.SwingNodeHelper;
import com.sun.javafx.tk.Toolkit;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.SecondaryLoop;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.MouseDragGestureRecognizer;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import jdk.swing.interop.DragSourceContextWrapper;
import jdk.swing.interop.DropTargetContextWrapper;
import jdk.swing.interop.LightweightFrameWrapper;

public class FXDnDInteropN {
   private WeakReference<SwingNode> nodeRef = null;
   private boolean isDragSourceListenerInstalled = false;
   private MouseEvent pressEvent = null;
   private long pressTime = 0L;
   private volatile SecondaryLoop loop;
   private final Map<Component, FXDnDInteropN.FXDragGestureRecognizer> recognizers = new HashMap<>();
   private final EventHandler<MouseEvent> onMousePressHandler = var1 -> {
      this.pressEvent = var1;
      this.pressTime = System.currentTimeMillis();
   };
   private volatile FXDnDInteropN.FXDragSourceContextPeer activeDSContextPeer;
   private final EventHandler<MouseEvent> onDragStartHandler = var1 -> {
      this.activeDSContextPeer = null;
      MouseEvent var2 = this.getInitialGestureEvent();
      SwingNodeHelper.runOnEDTAndWait(this, () -> this.fireEvent((int)var2.getX(), (int)var2.getY(), this.pressTime, SwingEvents.fxMouseModsToMouseMods(var2)));
      if (this.activeDSContextPeer != null) {
         var1.consume();
         SwingNode var3 = this.getNode();
         if (var3 != null) {
            Dragboard var4 = var3.startDragAndDrop(SwingDnD.dropActionsToTransferModes(this.activeDSContextPeer.sourceActions).toArray(new TransferMode[1]));
            HashMap var5 = new HashMap();

            for (String var9 : this.activeDSContextPeer.transferable.getMimeTypes()) {
               DataFormat var10 = DataFormat.lookupMimeType(var9);
               if (var10 != null) {
                  var5.put(var10, this.activeDSContextPeer.transferable.getData(var9));
               }
            }

            boolean var11 = var4.setContent(var5);
            if (!var11 && !FXDnD.fxAppThreadIsDispatchThread) {
               this.loop.exit();
            }
         }
      }
   };
   private final EventHandler<DragEvent> onDragDoneHandler = var1 -> {
      var1.consume();
      if (!FXDnD.fxAppThreadIsDispatchThread) {
         this.loop.exit();
      }

      if (this.activeDSContextPeer != null) {
         TransferMode var2 = var1.getTransferMode();
         this.activeDSContextPeer.dragDone(var2 == null ? 0 : SwingDnD.transferModeToDropAction(var2), (int)var1.getX(), (int)var1.getY());
      }
   };
   private boolean isDropTargetListenerInstalled = false;
   private volatile FXDnDInteropN.FXDropTargetContextPeer activeDTContextPeer = null;
   private final Map<Component, DropTarget> dropTargets = new HashMap<>();
   private final EventHandler<DragEvent> onDragEnteredHandler = var1 -> {
      if (this.activeDTContextPeer == null) {
         this.activeDTContextPeer = new FXDnDInteropN.FXDropTargetContextPeer();
      }

      int var2 = this.activeDTContextPeer.postDropTargetEvent(var1);
      if (var2 != 0) {
         var1.consume();
      }
   };
   private final EventHandler<DragEvent> onDragExitedHandler = var1 -> {
      if (this.activeDTContextPeer == null) {
         this.activeDTContextPeer = new FXDnDInteropN.FXDropTargetContextPeer();
      }

      this.activeDTContextPeer.postDropTargetEvent(var1);
      this.activeDTContextPeer = null;
   };
   private final EventHandler<DragEvent> onDragOverHandler = var1 -> {
      if (this.activeDTContextPeer == null) {
         this.activeDTContextPeer = new FXDnDInteropN.FXDropTargetContextPeer();
      }

      int var2 = this.activeDTContextPeer.postDropTargetEvent(var1);
      if (var2 != 0) {
         var1.acceptTransferModes(SwingDnD.dropActionsToTransferModes(var2).toArray(new TransferMode[1]));
         var1.consume();
      }
   };
   private final EventHandler<DragEvent> onDragDroppedHandler = var1 -> {
      if (this.activeDTContextPeer == null) {
         this.activeDTContextPeer = new FXDnDInteropN.FXDropTargetContextPeer();
      }

      int var2 = this.activeDTContextPeer.postDropTargetEvent(var1);
      if (var2 != 0) {
         var1.setDropCompleted(this.activeDTContextPeer.success);
         var1.consume();
      }

      this.activeDTContextPeer = null;
   };

   public Component findComponentAt(Object var1, int var2, int var3, boolean var4) {
      LightweightFrameWrapper var5 = (LightweightFrameWrapper)var1;
      return var5.findComponentAt(var5, var2, var3, false);
   }

   public boolean isCompEqual(Component var1, Object var2) {
      LightweightFrameWrapper var3 = (LightweightFrameWrapper)var2;
      return var3.isCompEqual(var1, var3);
   }

   public int convertModifiersToDropAction(int var1, int var2) {
      return DragSourceContextWrapper.convertModifiersToDropAction(var1, var2);
   }

   public Object createDragSourceContext(DragGestureEvent var1) throws InvalidDnDOperationException {
      return new FXDnDInteropN.FXDragSourceContextPeer(var1);
   }

   public <T extends DragGestureRecognizer> T createDragGestureRecognizer(DragSource var1, Component var2, int var3, DragGestureListener var4) {
      return (T)(new FXDnDInteropN.FXDragGestureRecognizer(var1, var2, var3, var4));
   }

   private void runOnFxThread(Runnable var1) {
      if (Platform.isFxApplicationThread()) {
         var1.run();
      } else {
         Platform.runLater(var1);
      }
   }

   public SwingNode getNode() {
      return this.nodeRef.get();
   }

   public void setNode(SwingNode var1) {
      this.nodeRef = new WeakReference<>(var1);
   }

   public <T> FXDnDInteropN.ComponentMapper<T> mapComponent(Map<Component, T> var1, int var2, int var3) {
      return new FXDnDInteropN.ComponentMapper<>(var1, var2, var3);
   }

   private void fireEvent(int var1, int var2, long var3, int var5) {
      FXDnDInteropN.ComponentMapper var6 = this.mapComponent(this.recognizers, var1, var2);
      FXDnDInteropN.FXDragGestureRecognizer var7 = (FXDnDInteropN.FXDragGestureRecognizer)var6.object;
      if (var7 != null) {
         var7.fireEvent(var6.x, var6.y, var3, var5);
      } else {
         SwingNodeHelper.leaveFXNestedLoop(this);
      }
   }

   private MouseEvent getInitialGestureEvent() {
      return this.pressEvent;
   }

   public void addDropTarget(DropTarget var1, SwingNode var2) {
      this.dropTargets.put(var1.getComponent(), var1);
      Platform.runLater(() -> {
         if (!this.isDropTargetListenerInstalled) {
            var2.addEventHandler(DragEvent.DRAG_ENTERED, this.onDragEnteredHandler);
            var2.addEventHandler(DragEvent.DRAG_EXITED, this.onDragExitedHandler);
            var2.addEventHandler(DragEvent.DRAG_OVER, this.onDragOverHandler);
            var2.addEventHandler(DragEvent.DRAG_DROPPED, this.onDragDroppedHandler);
            this.isDropTargetListenerInstalled = true;
         }
      });
   }

   public void removeDropTarget(DropTarget var1, SwingNode var2) {
      this.dropTargets.remove(var1.getComponent());
      Platform.runLater(() -> {
         if (this.isDropTargetListenerInstalled && this.dropTargets.isEmpty()) {
            var2.removeEventHandler(DragEvent.DRAG_ENTERED, this.onDragEnteredHandler);
            var2.removeEventHandler(DragEvent.DRAG_EXITED, this.onDragExitedHandler);
            var2.removeEventHandler(DragEvent.DRAG_OVER, this.onDragOverHandler);
            var2.removeEventHandler(DragEvent.DRAG_DROPPED, this.onDragDroppedHandler);
            this.isDropTargetListenerInstalled = false;
         }
      });
   }

   private class ComponentMapper<T> {
      public int x;
      public int y;
      public T object = (T)null;

      private ComponentMapper(Map<Component, T> var2, int var3, int var4) {
         this.x = var3;
         this.y = var4;
         SwingNode var5 = FXDnDInteropN.this.getNode();
         if (var5 != null) {
            LightweightFrameWrapper var6 = (LightweightFrameWrapper)SwingNodeHelper.getLightweightFrame(var5);
            Object var7 = var6.findComponentAt(var6, this.x, this.y, false);
            if (var7 == null) {
               return;
            }

            synchronized (var7.getTreeLock()) {
               do {
                  this.object = (T)var2.get(var7);
               } while (this.object == null && (var7 = var7.getParent()) != null);

               if (this.object != null) {
                  while (var6.isCompEqual((Component)var7, var6) && var7 != null) {
                     this.x = this.x - var7.getX();
                     this.y = this.y - var7.getY();
                     var7 = var7.getParent();
                  }
               }
            }
         }
      }
   }

   private class FXDragGestureRecognizer extends MouseDragGestureRecognizer {
      FXDragGestureRecognizer(DragSource var2, Component var3, int var4, DragGestureListener var5) {
         super(var2, var3, var4, var5);
         if (var3 != null) {
            FXDnDInteropN.this.recognizers.put(var3, this);
         }
      }

      @Override
      public void setComponent(Component var1) {
         Component var2 = this.getComponent();
         if (var2 != null) {
            FXDnDInteropN.this.recognizers.remove(var2);
         }

         super.setComponent(var1);
         if (var1 != null) {
            FXDnDInteropN.this.recognizers.put(var1, this);
         }
      }

      @Override
      protected void registerListeners() {
         FXDnDInteropN.this.runOnFxThread(() -> {
            if (!FXDnDInteropN.this.isDragSourceListenerInstalled) {
               SwingNode var1 = FXDnDInteropN.this.getNode();
               if (var1 != null) {
                  var1.addEventHandler(MouseEvent.MOUSE_PRESSED, FXDnDInteropN.this.onMousePressHandler);
                  var1.addEventHandler(MouseEvent.DRAG_DETECTED, FXDnDInteropN.this.onDragStartHandler);
                  var1.addEventHandler(DragEvent.DRAG_DONE, FXDnDInteropN.this.onDragDoneHandler);
               }

               FXDnDInteropN.this.isDragSourceListenerInstalled = true;
            }
         });
      }

      @Override
      protected void unregisterListeners() {
         FXDnDInteropN.this.runOnFxThread(() -> {
            if (FXDnDInteropN.this.isDragSourceListenerInstalled) {
               SwingNode var1 = FXDnDInteropN.this.getNode();
               if (var1 != null) {
                  var1.removeEventHandler(MouseEvent.MOUSE_PRESSED, FXDnDInteropN.this.onMousePressHandler);
                  var1.removeEventHandler(MouseEvent.DRAG_DETECTED, FXDnDInteropN.this.onDragStartHandler);
                  var1.removeEventHandler(DragEvent.DRAG_DONE, FXDnDInteropN.this.onDragDoneHandler);
               }

               FXDnDInteropN.this.isDragSourceListenerInstalled = false;
            }
         });
      }

      private void fireEvent(int var1, int var2, long var3, int var5) {
         this.appendEvent(new java.awt.event.MouseEvent(this.getComponent(), 501, var3, var5, var1, var2, 0, false));
         int var6 = DragSourceContextWrapper.convertModifiersToDropAction(var5, this.getSourceActions());
         this.fireDragGestureRecognized(var6, new Point(var1, var2));
      }
   }

   private final class FXDragSourceContextPeer extends DragSourceContextWrapper {
      private volatile int sourceActions = 0;
      private final CachingTransferable transferable = new CachingTransferable();

      @Override
      public void startSecondaryEventLoop() {
         Toolkit.getToolkit().enterNestedEventLoop(this);
      }

      @Override
      public void quitSecondaryEventLoop() {
         assert !Platform.isFxApplicationThread();

         Platform.runLater(() -> Toolkit.getToolkit().exitNestedEventLoop(this, null));
      }

      @Override
      protected void setNativeCursor(Cursor var1, int var2) {
      }

      private void dragDone(int var1, int var2, int var3) {
         this.dragDropFinished(var1 != 0, var1, var2, var3);
      }

      FXDragSourceContextPeer(DragGestureEvent var2) {
         super(var2);
      }

      @Override
      protected void startDrag(Transferable var1, long[] var2, Map var3) {
         FXDnDInteropN.this.activeDSContextPeer = this;
         this.transferable.updateData(var1, true);
         this.sourceActions = this.getDragSourceContext().getSourceActions();
         if (!FXDnD.fxAppThreadIsDispatchThread) {
            FXDnDInteropN.this.loop = java.awt.Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
            SwingNodeHelper.leaveFXNestedLoop(FXDnDInteropN.this);
            if (!FXDnDInteropN.this.loop.enter()) {
            }
         }
      }
   }

   private final class FXDropTargetContextPeer extends DropTargetContextWrapper {
      private int targetActions = 0;
      private int currentAction = 0;
      private DropTarget dt = null;
      private DropTargetContext ctx = null;
      private final CachingTransferable transferable = new CachingTransferable();
      private boolean success = false;
      private int dropAction = 0;

      @Override
      public synchronized void setTargetActions(int var1) {
         this.targetActions = var1;
      }

      @Override
      public synchronized int getTargetActions() {
         return this.targetActions;
      }

      @Override
      public synchronized DropTarget getDropTarget() {
         return this.dt;
      }

      @Override
      public synchronized boolean isTransferableJVMLocal() {
         return false;
      }

      @Override
      public synchronized DataFlavor[] getTransferDataFlavors() {
         return this.transferable.getTransferDataFlavors();
      }

      @Override
      public synchronized Transferable getTransferable() {
         return this.transferable;
      }

      @Override
      public synchronized void acceptDrag(int var1) {
         this.currentAction = var1;
      }

      @Override
      public synchronized void rejectDrag() {
         this.currentAction = 0;
      }

      @Override
      public synchronized void acceptDrop(int var1) {
         this.dropAction = var1;
      }

      @Override
      public synchronized void rejectDrop() {
         this.dropAction = 0;
      }

      @Override
      public synchronized void dropComplete(boolean var1) {
         this.success = var1;
      }

      private int postDropTargetEvent(DragEvent var1) {
         FXDnDInteropN.ComponentMapper var2 = FXDnDInteropN.this.mapComponent(FXDnDInteropN.this.dropTargets, (int)var1.getX(), (int)var1.getY());
         EventType var3 = var1.getEventType();
         Dragboard var4 = var1.getDragboard();
         this.transferable.updateData(var4, DragEvent.DRAG_DROPPED.equals(var3));
         int var5 = SwingDnD.transferModesToDropActions(var4.getTransferModes());
         int var6 = var1.getTransferMode() == null ? 0 : SwingDnD.transferModeToDropAction(var1.getTransferMode());
         DropTarget var7 = var2.object != null ? (DropTarget)var2.object : this.dt;
         SwingNodeHelper.runOnEDTAndWait(FXDnDInteropN.this, () -> {
            if (var7 != this.dt) {
               if (this.ctx != null) {
                  this.reset(this.ctx);
               }

               this.ctx = null;
               this.currentAction = this.dropAction = 0;
            }

            if (var7 != null) {
               if (this.ctx == null) {
                  this.ctx = var7.getDropTargetContext();
                  this.setDropTargetContext(this.ctx, this);
               }

               if (DragEvent.DRAG_DROPPED.equals(var3)) {
                  DropTargetDropEvent var7x = new DropTargetDropEvent(this.ctx, new Point(var2.x, var2.y), var6, var5);
                  var7.drop(var7x);
               } else {
                  DropTargetDragEvent var8 = new DropTargetDragEvent(this.ctx, new Point(var2.x, var2.y), var6, var5);
                  if (DragEvent.DRAG_OVER.equals(var3)) {
                     var7.dragOver(var8);
                  } else if (DragEvent.DRAG_ENTERED.equals(var3)) {
                     var7.dragEnter(var8);
                  } else if (DragEvent.DRAG_EXITED.equals(var3)) {
                     var7.dragExit(var8);
                  }
               }
            }

            this.dt = (DropTarget)var2.object;
            if (this.dt == null) {
               this.ctx = null;
               this.currentAction = this.dropAction = 0;
            }

            if (DragEvent.DRAG_DROPPED.equals(var3) || DragEvent.DRAG_EXITED.equals(var3)) {
               this.ctx = null;
            }

            SwingNodeHelper.leaveFXNestedLoop(FXDnDInteropN.this);
         });
         return DragEvent.DRAG_DROPPED.equals(var3) ? this.dropAction : this.currentAction;
      }
   }
}
